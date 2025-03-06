package com.mc.payment.core.service.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mc.payment.api.model.dto.DepositEventDto;
import com.mc.payment.api.model.req.DepositReq;
import com.mc.payment.api.model.rsp.DepositRsp;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.config.AppConfig;
import com.mc.payment.core.service.constant.WebhookEventConstants;
import com.mc.payment.core.service.entity.*;
import com.mc.payment.core.service.manager.deposit.DepositTemplate;
import com.mc.payment.core.service.model.GetAvailableWalletDto;
import com.mc.payment.core.service.model.dto.MerchantAssetDto;
import com.mc.payment.core.service.model.enums.*;
import com.mc.payment.core.service.model.req.GenerateWalletQRCodeReq;
import com.mc.payment.core.service.model.req.ProcessDepositReq;
import com.mc.payment.core.service.model.rsp.ConfirmRsp;
import com.mc.payment.core.service.service.*;
import com.mc.payment.core.service.util.AppSecureUtil;
import com.mc.payment.core.service.util.CommonUtil;
import com.mc.payment.core.service.util.MonitorLogUtil;
import com.mc.payment.core.service.util.MonitorUtil;
import com.mc.payment.gateway.adapter.*;
import com.mc.payment.gateway.model.req.GatewayDepositReq;
import com.mc.payment.gateway.model.rsp.GatewayDepositRsp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 入金相关业务逻辑管理类
 *
 * @author Conor
 * @since 2024-09-21 11:29:31.694
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DepositManagerOldImpl implements DepositManagerOld {
    /**
     * 收银台前端页面地址
     */
    private static final String CASHIER_PAGE_URL = "/mc-payment/payment/in";
    /**
     * 出金确认页
     */
    private static final String CASHIER_OUT_PAGE_URL = "/mc-payment/payment/out";
    private final IDepositRecordService depositRecordService;
    private final IWebhookEventService webhookEventService;
    private final AppConfig appConfig;
    private final AssetBankService assetBankService;
    private final PaymentPageService paymentPageService;
    private final IAssetLastQuoteService assetLastQuoteService;
    private final ChannelAssetConfigService channelAssetConfigService;
    private final MerchantChannelAssetService merchantChannelAssetService;
    private final MerchantWalletService merchantWalletService;
    private final PayPalPaymentGatewayAdapter payPalPaymentGatewayAdapter;
    private final ChannelWalletService channelWalletService;
    private final OfaPayPaymentGatewayAdapter ofaPayPaymentGatewayAdapter;
    private final PassToPayPaymentGatewayAdapter passToPayPaymentGatewayAdapter;
    private final EzeebillPaymentGatewayAdapter ezeebillPaymentGatewayAdapter;
    private final CheezeePayPaymentGatewayAdapter cheezeePayPaymentGatewayAdapter;

    private final Map<String, DepositTemplate> depositStrategyMap;

    /**
     * 入金策略标识前缀
     */
    private static final String DEPOSIT_STRATEGY_PREFIX = "DepositStrategy_";


    @Override
    public DepositRsp deposit(String merchantId, String merchantName, DepositReq req) {
        ChannelSubTypeEnum channelSubTypeEnum = merchantChannelAssetService.choosePaymentChannel(merchantId, req.getAssetType(),
                req.getAssetName(), req.getNetProtocol(), true);
        ProcessDepositReq processDepositReq = ProcessDepositReq.valueOf(req);
        processDepositReq.setMerchantId(merchantId);
        processDepositReq.setMerchantName(merchantName);
        processDepositReq.setChannelSubTypeEnum(channelSubTypeEnum);
        return depositStrategyMap.get(DEPOSIT_STRATEGY_PREFIX + channelSubTypeEnum.getCode()).processDeposit(processDepositReq);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DepositRsp depositOld(String merchantId, String merchantName, DepositReq req) {
        log.info("DepositManagerImpl.deposit merchantId:{}, merchantName:{}, req:{}", merchantId, merchantName, req);
        MonitorUtil.depositRequestCounter(merchantName, req);
        // 校验
        this.depositValidate(merchantId, req);
        // 选择支付通道  可优化 和前面校验有重复
        ChannelSubTypeEnum channelSubTypeEnum = merchantChannelAssetService.choosePaymentChannel(merchantId, req.getAssetType(),
                req.getAssetName(), req.getNetProtocol(), true);
        // 保存入金记录
        DepositRecordEntity recordEntity = DepositRecordEntity.valueOf(req, merchantId, merchantName,
                channelSubTypeEnum);
        depositRecordService.save(recordEntity);
        // 保存支付页面信息
        PaymentPageEntity paymentPageEntity = PaymentPageEntity.valueOf(req, merchantId);
        paymentPageService.save(paymentPageEntity);
        // 调用支付通道
        // 返回结果
        String redirectPageUrl =
                appConfig.getPaymentDomain() + CASHIER_PAGE_URL + "?k=" + URLEncoder.encode(AppSecureUtil.encrypt(paymentPageEntity.getId(), appConfig.getCashierKey())); // 跟踪id密文

        //入金指标监控
        JSONObject depositMonitor =
                new JSONObject().put("Service", "payment").put("MonitorKey", "depositMonitor").put("Time",
                        DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
        MonitorLogUtil.log(depositMonitor);
        //资金快进快出指标监控
        JSONObject depositDiffMonitor = new JSONObject().put("Service", "payment").put("MonitorKey",
                "depositDiffMonitor").put("userId", recordEntity.getUserId()).put("amount",
                recordEntity.getAmount().multiply(recordEntity.getRate())).put("Time", DateUtil.format(new Date(),
                "yyyy-MM-dd HH:mm:ss.SSS"));
        MonitorLogUtil.log(depositDiffMonitor);
        // 触发webhook
        WebhookEventEntity webhookEventEntity = new WebhookEventEntity();
        webhookEventEntity.setEvent(WebhookEventConstants.DEPOSIT_EVENT);
        webhookEventEntity.setData(JSONUtil.toJsonStr(new DepositEventDto(recordEntity.getTrackingId(),
                recordEntity.getStatus(), recordEntity.getAmount())));
        webhookEventEntity.setTrackingId(recordEntity.getTrackingId());
        webhookEventEntity.setWebhookUrl(req.getWebhookUrl());
        webhookEventEntity.setMerchantId(merchantId);
        webhookEventService.asyncSendWebhookEvent(webhookEventEntity);

        DepositRsp rsp = new DepositRsp();
        rsp.setTrackingId(req.getTrackingId());
        rsp.setRedirectPageUrl(redirectPageUrl);
        rsp.setExpireTimestamp(recordEntity.getExpireTimestamp());
        rsp.setRemark(req.getRemark());

        // 跳过收银台页面,直接返回上游支付页面或者钱包地址
        if (req.getSkipPage() == BooleanStatusEnum.ITEM_1.getCode()) {
            ConfirmRsp ConfirmRsp = this.executeDeposit(recordEntity.getId());
            rsp.setRedirectPageUrl(ConfirmRsp.getRedirectUrl());
            rsp.setWalletAddress(ConfirmRsp.getWalletAddress());
        }

        return rsp;
    }

    /**
     * 入金校验
     *
     * @param merchantId
     * @param req
     */
    private void depositValidate(String merchantId, DepositReq req) {
        req.validate();
        //  trackingId 同一个商户不可重复
        long count = depositRecordService.count(Wrappers.lambdaQuery(DepositRecordEntity.class)
                .eq(DepositRecordEntity::getMerchantId, merchantId).eq(DepositRecordEntity::getTrackingId, req.getTrackingId()));
        if (count > 0) {
            throw new ValidateException("trackingId Repeat, please check:" + req.getTrackingId());
        }
        if (req.getUserSelectable() == BooleanStatusEnum.ITEM_0.getCode()) {
            //效验, 防止乱传没有的配置的资产信息
            MerchantAssetDto merchantAssetDto = merchantChannelAssetService.getAssetConfigOne(merchantId,
                    req.getAssetType(), req.getAssetName(), req.getNetProtocol());
            if (merchantAssetDto == null || merchantAssetDto.getDepositStatus() == BooleanStatusEnum.ITEM_0.getCode()) {
                // 您输入的资产信息不存在, 请检查
                throw new ValidateException("The assetName and netProtocol not available, please check");
            }
            // 最小入金校验  这里将单位认为是本币种的单位
            BigDecimal minDepositAmount = merchantAssetDto.getMinDepositAmount();
            BigDecimal maxDepositAmount = merchantAssetDto.getMaxDepositAmount();
            if (req.getAssetType() == AssetTypeEnum.CRYPTO_CURRENCY.getCode()) {
                String assetName = req.getAssetName();
                String feeAssetName = merchantAssetDto.getFeeAssetName();

                BigDecimal amount = req.getAmount();
                // 先查汇率, 再检查最小充值金额
                List<String> symbolList = CommonUtil.getSymbolListByNames(assetName, feeAssetName);
                Map<String, BigDecimal> symbolPriceMap = assetLastQuoteService.getSymbolAndMaxPriceBySymbol(symbolList);
                if (CommonUtil.checkMinAmount(assetName, amount, minDepositAmount, symbolPriceMap)) {
                    // 您申请的入金的金额低于最小充值金额
                    throw new ValidateException("The deposit amount should be greater than the minimum deposit" +
                            " amount, please check:" + minDepositAmount);
                }
            } else {
                if (minDepositAmount != null && req.getAmount().compareTo(minDepositAmount) < 0) {
                    // 入金金额应该大于最小入金金额,请检查
                    throw new ValidateException("The deposit amount should be greater than the minimum deposit" +
                            " amount, please check:" + minDepositAmount.stripTrailingZeros().toPlainString());
                }

                if (maxDepositAmount != null && maxDepositAmount.compareTo(BigDecimal.ZERO) != 0 && req.getAmount().compareTo(maxDepositAmount) > 0) {
                    // 入金金额应该小于最大入金金额,请检查
                    throw new ValidateException("The deposit amount should be lesser than the max deposit" +
                            " amount, please check:" + maxDepositAmount.stripTrailingZeros().toPlainString());
                }
            }
            // 校验银行代码
            // 判断是否需要校验
            boolean exists = assetBankService.existDeposit(req.getAssetName(), req.getNetProtocol());
            if (exists) {
                boolean existedDepositBankCode = assetBankService.existDepositBankCode(req.getAssetName(),
                        req.getNetProtocol(), req.getBankCode());
                if (!existedDepositBankCode) {
                    throw new ValidateException("The bank code you entered does not exist, please " +
                            "check:bankCode");
                }
            }

        } else if (req.getUserSelectable() == BooleanStatusEnum.ITEM_1.getCode()) {
            boolean exists = merchantChannelAssetService.exists(merchantId, req.getAssetType(), req.getAssetName());
            if (!exists) {
                //暂不支持该支付币种
                throw new ValidateException("This payment currency is not supported yet!");
            }
            if (req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                //入金金额需大于0
                throw new ValidateException("The deposit amount must be greater than 0!");
            }
        }
    }

    /**
     * 执行入金
     * <p>
     * 根据入金记录中的通道选择不同的入金方式
     * 比如fireblocks通道的加密货币,是本地获取一个可用的入金钱包地址返回
     * 比如paypal通道的法币,则是调用上游接口申请入金得到一个支付页面地址返回
     *
     * @param depositId
     * @return
     */
    @Override
    public ConfirmRsp executeDeposit(String depositId) {
        DepositRecordEntity recordEntity = depositRecordService.getById(depositId);
        if (recordEntity == null) {
            throw new IllegalArgumentException("The deposit record does not exist, please check:" + depositId);
        }
        if (recordEntity.getAssetType() == AssetTypeEnum.CRYPTO_CURRENCY.getCode()) {
            // 加密货币入金  设置手续费币种 有特殊情况,可能是其他币种 后面1.10.0版本会再次优化
            MerchantAssetDto merchantAssetDto = merchantChannelAssetService.getAssetConfigOne(recordEntity.getMerchantId(),
                    recordEntity.getAssetType(), recordEntity.getAssetName(), recordEntity.getNetProtocol());
            recordEntity.setFeeAssetName(merchantAssetDto.getFeeAssetName());
            depositRecordService.lambdaUpdate()
                    .eq(BaseNoLogicalDeleteEntity::getId, recordEntity.getId())
                    .set(DepositRecordEntity::getFeeAssetName, merchantAssetDto.getFeeAssetName())
                    .update();
        }

        ConfirmRsp ConfirmRsp;
        // 选择支付通道执行支付操作
        if (recordEntity.getChannelSubType() == ChannelSubTypeEnum.FIRE_BLOCKS.getCode()) {
            ConfirmRsp = this.fireBlocksDeposit(recordEntity);
        } else if (recordEntity.getChannelSubType() == ChannelSubTypeEnum.CHEEZEE_PAY.getCode()) {
            ConfirmRsp = this.cheezeePayDeposit(recordEntity);
        } else if (recordEntity.getChannelSubType() == ChannelSubTypeEnum.PAY_PAL.getCode()) {
            ConfirmRsp = this.payPalDeposit(recordEntity);
        } else if (recordEntity.getChannelSubType() == ChannelSubTypeEnum.PASS_TO_PAY.getCode()) {
            ConfirmRsp = this.passToPayDeposit(recordEntity);
        } else if (recordEntity.getChannelSubType() == ChannelSubTypeEnum.EZEEBILL.getCode()) {
            ConfirmRsp = this.ezeebillDeposit(recordEntity);
        } else if (recordEntity.getChannelSubType() == ChannelSubTypeEnum.OFA_PAY.getCode()) {
            ConfirmRsp = this.ofaPayDeposit(recordEntity);
        } else {
            // 支付通道类型不正确
            throw new IllegalArgumentException("Payment channel type is incorrect!");
        }
        return ConfirmRsp;
    }

    private ConfirmRsp fireBlocksDeposit(DepositRecordEntity recordEntity) {
        // 重复点击支付按钮,且是未支付状态,且已经申请过了,直接返回支付页面url
        if (StrUtil.isNotBlank(recordEntity.getDestinationAddress())) {
            String walletQRCode =
                    merchantWalletService.generateWalletQRCode(new GenerateWalletQRCodeReq(recordEntity.getDestinationAddress()));
            return new ConfirmRsp(recordEntity.getDestinationAddress(), walletQRCode);
        }
        GetAvailableWalletDto availableWalletDto = new GetAvailableWalletDto();
        availableWalletDto.setMerchantId(recordEntity.getMerchantId());
        availableWalletDto.setAssetType(recordEntity.getAssetType());
        availableWalletDto.setChannelSubType(recordEntity.getChannelSubType());
        availableWalletDto.setAssetName(recordEntity.getAssetName());
        availableWalletDto.setNetProtocol(recordEntity.getNetProtocol());
        availableWalletDto.setPurposeType(PurposeTypeEnum.DEPOSIT.getCode());
        availableWalletDto.setLock(true);
        MerchantWalletEntity walletEntity = merchantWalletService.getAvailableWallet(availableWalletDto);

        if (walletEntity == null) {
            // 没有可用的钱包,提醒五分钟后重试,定时任务会解锁超时的钱包/冷却的/以及创建新的钱包
            throw new IllegalArgumentException("There is no wallet available, please try again in 5 minutes");
        }
        // 计算通道费
        BigDecimal channelFee = BigDecimal.ZERO;

        String assetName = recordEntity.getAssetName();
        String feeAssetName = recordEntity.getFeeAssetName();

        // 先查汇率, 再检查最小充值金额
        List<String> symbolList = CommonUtil.getSymbolListByNames(assetName, feeAssetName);
        Map<String, BigDecimal> symbolPriceMap = assetLastQuoteService.getSymbolAndMaxPriceBySymbol(symbolList);

        BigDecimal rate = CommonUtil.getRateByNameAndMap(assetName, symbolPriceMap);
        BigDecimal feeRate = CommonUtil.getRateByNameAndMap(recordEntity.getFeeAssetName(), symbolPriceMap);

        depositRecordService.update(Wrappers.lambdaUpdate(DepositRecordEntity.class)
                .eq(BaseNoLogicalDeleteEntity::getId, recordEntity.getId())
                .eq(DepositRecordEntity::getStatus, DepositRecordStatusEnum.ITEM_0.getCode())
                .set(DepositRecordEntity::getChannelFee, channelFee)
                .set(DepositRecordEntity::getNetProtocol, recordEntity.getNetProtocol())
                .set(DepositRecordEntity::getDestinationAddress, walletEntity.getWalletAddress())
                .set(DepositRecordEntity::getAccountId, walletEntity.getAccountId())
                .set(DepositRecordEntity::getWalletId, walletEntity.getId())
                .set(DepositRecordEntity::getAddrBalance, walletEntity.getBalance())
                .set(DepositRecordEntity::getRate, rate)
                .set(DepositRecordEntity::getFeeRate, feeRate));
        String walletQRCode =
                merchantWalletService.generateWalletQRCode(new GenerateWalletQRCodeReq(walletEntity.getWalletAddress()));

        return new ConfirmRsp(walletEntity.getWalletAddress(), walletQRCode);
    }

    private ConfirmRsp ofaPayDeposit(DepositRecordEntity recordEntity) {
        PaymentPageEntity paymentPageEntity = paymentPageService.lambdaQuery().eq(PaymentPageEntity::getMerchantId,
                recordEntity.getMerchantId()).eq(PaymentPageEntity::getTrackingId, recordEntity.getTrackingId()).one();
        if (paymentPageEntity == null) {
            throw new IllegalArgumentException("The payment page information does not exist, please check:" + recordEntity.getId());
        }
        // 重复点击支付按钮,且是未支付状态,且已经申请过了,直接返回支付页面url
        if (StrUtil.isNotBlank(paymentPageEntity.getChannelPageUrl())) {
            return new ConfirmRsp(paymentPageEntity.getChannelPageUrl());
        }
        // 查询可用钱包
        GetAvailableWalletDto availableWalletDto = new GetAvailableWalletDto();
        availableWalletDto.setMerchantId(recordEntity.getMerchantId());
        availableWalletDto.setAssetType(recordEntity.getAssetType());
        availableWalletDto.setChannelSubType(recordEntity.getChannelSubType());
        availableWalletDto.setAssetName(recordEntity.getAssetName());
        availableWalletDto.setNetProtocol(recordEntity.getNetProtocol());
        availableWalletDto.setPurposeType(PurposeTypeEnum.DEPOSIT_WITHDRAWAL.getCode());
        availableWalletDto.setLock(false);
        MerchantWalletEntity walletEntity = merchantWalletService.getAvailableWallet(availableWalletDto);
        if (walletEntity == null) {
            log.info("没有可用的钱包,创建账户和钱包!,{}", availableWalletDto);
            throw new IllegalArgumentException("No wallet available");
        }
        // 更新申请单   优化点:入金流程考虑增加入金中状态
        depositRecordService.lambdaUpdate().eq(BaseNoLogicalDeleteEntity::getId, recordEntity.getId())
                .set(DepositRecordEntity::getDestinationAddress, walletEntity.getWalletAddress())
                .set(DepositRecordEntity::getAccountId, walletEntity.getAccountId())
                .set(DepositRecordEntity::getWalletId, walletEntity.getId())
                .set(DepositRecordEntity::getAddrBalance, walletEntity.getBalance())
                .update();

        // 组装入金请求参数
        String scode = walletEntity.getWalletAddress().split("_")[1];
        GatewayDepositReq gatewayDepositReq = new GatewayDepositReq();
        gatewayDepositReq.setTransactionId(recordEntity.getId());
        gatewayDepositReq.setChannelId(scode);
        gatewayDepositReq.setBusinessName(recordEntity.getBusinessName());
        gatewayDepositReq.setAmount(recordEntity.getAmount().toString());
        gatewayDepositReq.setCurrency(recordEntity.getAssetName());
        gatewayDepositReq.setPayType(recordEntity.getAssetName());
        gatewayDepositReq.setCallbackUrl(appConfig.getPaymentRealend() + "/openapi/webhook/ofaPay/deposit");
        //不传业务方的地址给上游服务   gatewayDepositReq.setSuccessPageUrl(recordEntity.getSuccessPageUrl())
        gatewayDepositReq.setSuccessPageUrl(appConfig.getPaymentRealend() + "/redirect/deposit/successPage?id=" + recordEntity.getId());
        gatewayDepositReq.setRemark(recordEntity.getRemark());
        gatewayDepositReq.setBankCode(recordEntity.getBankCode());
        RetResult<GatewayDepositRsp> depositRspRetResult = ofaPayPaymentGatewayAdapter.deposit(gatewayDepositReq);
        if (depositRspRetResult.isSuccess()) {
            //  保存支付网关返回的url到paymentPage
            paymentPageEntity.setChannelPageUrl(depositRspRetResult.getData().getRedirectUrl());
            paymentPageService.updateById(paymentPageEntity);
            return new ConfirmRsp(depositRspRetResult.getData().getRedirectUrl());
        } else {
            log.error("支付网关入金失败, trackingId:{}, merchantId:{}, depositRspRetResult:{}", recordEntity.getTrackingId(),
                    recordEntity.getMerchantId(), depositRspRetResult);
            throw new IllegalArgumentException("Payment exception," + depositRspRetResult.getMsg());
        }
    }

    private ConfirmRsp payPalDeposit(DepositRecordEntity recordEntity) {
        PaymentPageEntity paymentPageEntity = paymentPageService.lambdaQuery().eq(PaymentPageEntity::getMerchantId,
                recordEntity.getMerchantId()).eq(PaymentPageEntity::getTrackingId, recordEntity.getTrackingId()).one();
        if (paymentPageEntity == null) {
            throw new IllegalArgumentException("The payment page information does not exist, please check:" + recordEntity.getId());
        }
        // 重复点击支付按钮,且是未支付状态,且已经申请过了,直接返回支付页面url
        if (StrUtil.isNotBlank(paymentPageEntity.getChannelPageUrl())) {
            return new ConfirmRsp(paymentPageEntity.getChannelPageUrl());
        }
        // 获取可用的入金钱包
        GetAvailableWalletDto availableWalletDto = new GetAvailableWalletDto();
        availableWalletDto.setMerchantId(recordEntity.getMerchantId());
        availableWalletDto.setAssetType(recordEntity.getAssetType());
        availableWalletDto.setChannelSubType(recordEntity.getChannelSubType());
        availableWalletDto.setAssetName(recordEntity.getAssetName());
        availableWalletDto.setNetProtocol(recordEntity.getNetProtocol());
        availableWalletDto.setPurposeType(PurposeTypeEnum.DEPOSIT_WITHDRAWAL.getCode());
        availableWalletDto.setLock(false);
        MerchantWalletEntity walletEntity = merchantWalletService.getAvailableWallet(availableWalletDto);
        if (walletEntity == null) {
            log.info("没有可用的钱包,创建账户和钱包!,{}", availableWalletDto);
            throw new IllegalArgumentException("No wallet available");
        }
        depositRecordService.lambdaUpdate().eq(BaseNoLogicalDeleteEntity::getId, recordEntity.getId())
                .set(DepositRecordEntity::getDestinationAddress, walletEntity.getWalletAddress())
                .set(DepositRecordEntity::getAccountId, walletEntity.getAccountId())
                .set(DepositRecordEntity::getWalletId, walletEntity.getId())
                .set(DepositRecordEntity::getAddrBalance, walletEntity.getBalance())
                .update();
        // 查通道钱包
        ChannelWalletEntity channelWalletEntity = channelWalletService.getById(walletEntity.getChannelWalletId());

        GatewayDepositReq depositReq = new GatewayDepositReq();
        depositReq.setTransactionId(recordEntity.getId());
        depositReq.setBusinessName(recordEntity.getBusinessName());
        depositReq.setAmount(NumberUtil.decimalFormat("#0.00", recordEntity.getAmount()));
        depositReq.setCurrency(recordEntity.getAssetName());
        // depositReq.setCallbackUrl() paypal 不需要通过接口设置回调地址
        // 不传业务方的地址给上游服务   depositReq.setSuccessPageUrl(recordEntity.getSuccessPageUrl())
        // 2024年11月26日 paypal要求必须传下游商户真实域名的地址  depositReq.setSuccessPageUrl(appConfig.getPaymentRealend() +
        // "/redirect/deposit/successPage?id=" + recordEntity.getId());
        depositReq.setSuccessPageUrl(recordEntity.getSuccessPageUrl());
        Map<String, Object> extraMap = new HashMap<>();
        extraMap.put("payeeAccount", channelWalletEntity.getWalletAddress());
        depositReq.setExtraMap(extraMap);
        RetResult<GatewayDepositRsp> depositRspRetResult = payPalPaymentGatewayAdapter.deposit(depositReq);
        if (depositRspRetResult.isSuccess()) {
            GatewayDepositRsp resultData = depositRspRetResult.getData();
            //  保存支付网关返回的url到paymentPage
            paymentPageEntity.setChannelPageUrl(resultData.getRedirectUrl());
            paymentPageService.updateById(paymentPageEntity);
            String channelTransactionId = resultData.getChannelTransactionId();
            depositRecordService.lambdaUpdate().eq(BaseNoLogicalDeleteEntity::getId, recordEntity.getId())
                    .set(DepositRecordEntity::getChannelTransactionId, channelTransactionId).update();
            return new ConfirmRsp(depositRspRetResult.getData().getRedirectUrl());
        } else {
            log.error("支付网关入金失败, trackingId:{}, merchantId:{}, depositRspRetResult:{}", recordEntity.getTrackingId(),
                    recordEntity.getMerchantId(), depositRspRetResult);
            throw new IllegalArgumentException("Payment exception," + depositRspRetResult.getMsg());
        }
    }

    private ConfirmRsp passToPayDeposit(DepositRecordEntity recordEntity) {
        PaymentPageEntity paymentPageEntity = paymentPageService.lambdaQuery().eq(PaymentPageEntity::getMerchantId,
                recordEntity.getMerchantId()).eq(PaymentPageEntity::getTrackingId, recordEntity.getTrackingId()).one();
        if (paymentPageEntity == null) {
            throw new IllegalArgumentException("The payment page information does not exist, please check:" + recordEntity.getId());
        }
        // 重复点击支付按钮,且是未支付状态,且已经申请过了,直接返回支付页面url
        if (StrUtil.isNotBlank(paymentPageEntity.getChannelPageUrl())) {
            return new ConfirmRsp(paymentPageEntity.getChannelPageUrl());
        }
        // 获取可用的入金钱包
        GetAvailableWalletDto availableWalletDto = new GetAvailableWalletDto();
        availableWalletDto.setMerchantId(recordEntity.getMerchantId());
        availableWalletDto.setAssetType(recordEntity.getAssetType());
        availableWalletDto.setChannelSubType(recordEntity.getChannelSubType());
        availableWalletDto.setAssetName(recordEntity.getAssetName());
        availableWalletDto.setNetProtocol(recordEntity.getNetProtocol());
        availableWalletDto.setPurposeType(PurposeTypeEnum.DEPOSIT_WITHDRAWAL.getCode());
        availableWalletDto.setLock(false);
        MerchantWalletEntity walletEntity = merchantWalletService.getAvailableWallet(availableWalletDto);
        if (walletEntity == null) {
            log.info("没有可用的钱包,创建账户和钱包!,{}", availableWalletDto);
            throw new IllegalArgumentException("No wallet available");
        }
        depositRecordService.lambdaUpdate().eq(BaseNoLogicalDeleteEntity::getId, recordEntity.getId())
                .set(DepositRecordEntity::getDestinationAddress, walletEntity.getWalletAddress())
                .set(DepositRecordEntity::getAccountId, walletEntity.getAccountId())
                .set(DepositRecordEntity::getWalletId, walletEntity.getId()).set(DepositRecordEntity::getAddrBalance,
                        walletEntity.getBalance())
                .update();


        ChannelAssetConfigEntity channelAssetEntity = channelAssetConfigService.lambdaQuery()
                .eq(ChannelAssetConfigEntity::getChannelSubType, recordEntity.getChannelSubType())
                .eq(ChannelAssetConfigEntity::getAssetName, recordEntity.getAssetName())
                .eq(ChannelAssetConfigEntity::getNetProtocol, recordEntity.getNetProtocol())
                .eq(ChannelAssetConfigEntity::getStatus, StatusEnum.ACTIVE.getCode())
                .one();

        JSONObject jsonObject = JSONUtil.parseObj(channelAssetEntity.getChannelCredential());
        String wayCode = jsonObject.getStr("wayCode");

        GatewayDepositReq depositReq = new GatewayDepositReq();
        depositReq.setTransactionId(recordEntity.getId());
        depositReq.setBusinessName(recordEntity.getBusinessName());
        depositReq.setAmount(recordEntity.getAmount().toString());
        depositReq.setCurrency(recordEntity.getAssetName());
        depositReq.setPayType(wayCode);
        depositReq.setCallbackUrl(appConfig.getPaymentRealend() + "/openapi/webhook/passToPay/deposit");
        //不传业务方的地址给上游服务   depositReq.setSuccessPageUrl(recordEntity.getSuccessPageUrl())
        depositReq.setSuccessPageUrl(appConfig.getPaymentRealend() + "/redirect/deposit/successPage?id=" + recordEntity.getId());
        Map<String, Object> extraMap = new HashMap<>();
        extraMap.put("userId", recordEntity.getUserId());
        // 计算有效时间,当小于等于0时,默认为5分钟
        long expiredTime = (recordEntity.getExpireTimestamp() - recordEntity.getCreateTime().getTime()) / 1000;
        expiredTime = expiredTime <= 0 ? 300 : expiredTime;
        extraMap.put("expiredTime", expiredTime);
        depositReq.setExtraMap(extraMap);
        RetResult<GatewayDepositRsp> depositRspRetResult = passToPayPaymentGatewayAdapter.deposit(depositReq);
        if (depositRspRetResult.isSuccess()) {
            GatewayDepositRsp resultData = depositRspRetResult.getData();
            //  保存支付网关返回的url到paymentPage
            paymentPageEntity.setChannelPageUrl(resultData.getRedirectUrl());
            paymentPageService.updateById(paymentPageEntity);
            String channelTransactionId = resultData.getChannelTransactionId();
            depositRecordService.lambdaUpdate().eq(BaseNoLogicalDeleteEntity::getId, recordEntity.getId())
                    .set(DepositRecordEntity::getChannelTransactionId, channelTransactionId).update();
            return new ConfirmRsp(depositRspRetResult.getData().getRedirectUrl());
        } else {
            log.error("支付网关入金失败, trackingId:{}, merchantId:{}, depositRspRetResult:{}", recordEntity.getTrackingId(),
                    recordEntity.getMerchantId(), depositRspRetResult);
            throw new IllegalArgumentException("Payment exception," + depositRspRetResult.getMsg());
        }
    }

    private ConfirmRsp ezeebillDeposit(DepositRecordEntity recordEntity) {
        PaymentPageEntity paymentPageEntity = paymentPageService.lambdaQuery().eq(PaymentPageEntity::getMerchantId,
                recordEntity.getMerchantId()).eq(PaymentPageEntity::getTrackingId, recordEntity.getTrackingId()).one();
        if (paymentPageEntity == null) {
            throw new IllegalArgumentException("The payment page information does not exist, please check:" + recordEntity.getId());
        }
        // 重复点击支付按钮,且是未支付状态,且已经申请过了,直接返回支付页面url
        if (StrUtil.isNotBlank(paymentPageEntity.getChannelPageUrl())) {
            return new ConfirmRsp(paymentPageEntity.getChannelPageUrl());
        }
        // 获取可用的入金钱包
        GetAvailableWalletDto availableWalletDto = new GetAvailableWalletDto();
        availableWalletDto.setMerchantId(recordEntity.getMerchantId());
        availableWalletDto.setAssetType(recordEntity.getAssetType());
        availableWalletDto.setChannelSubType(recordEntity.getChannelSubType());
        availableWalletDto.setAssetName(recordEntity.getAssetName());
        availableWalletDto.setNetProtocol(recordEntity.getNetProtocol());
        availableWalletDto.setPurposeType(PurposeTypeEnum.DEPOSIT_WITHDRAWAL.getCode());
        availableWalletDto.setLock(false);
        MerchantWalletEntity walletEntity = merchantWalletService.getAvailableWallet(availableWalletDto);
        if (walletEntity == null) {
            log.info("没有可用的钱包,创建账户和钱包!,{}", availableWalletDto);
            throw new IllegalArgumentException("No wallet available");
        }
        depositRecordService.lambdaUpdate().eq(BaseNoLogicalDeleteEntity::getId, recordEntity.getId())
                .set(DepositRecordEntity::getDestinationAddress, walletEntity.getWalletAddress())
                .set(DepositRecordEntity::getAccountId, walletEntity.getAccountId())
                .set(DepositRecordEntity::getWalletId, walletEntity.getId())
                .set(DepositRecordEntity::getAddrBalance, walletEntity.getBalance())
                .update();

        GatewayDepositReq depositReq = new GatewayDepositReq();
        depositReq.setTransactionId(recordEntity.getId());
        depositReq.setBusinessName(recordEntity.getBusinessName());
        depositReq.setAmount(NumberUtil.decimalFormat("#0.00", recordEntity.getAmount()));
        depositReq.setCurrency(recordEntity.getAssetName());
        depositReq.setPayType(recordEntity.getNetProtocol());
        // depositReq.setCallbackUrl() paypal 不需要通过接口设置回调地址
        //不传业务方的地址给上游服务   depositReq.setSuccessPageUrl(recordEntity.getSuccessPageUrl())
        depositReq.setSuccessPageUrl(appConfig.getPaymentRealend() + "/redirect/deposit/post/successPage?id=" + recordEntity.getId());//Ezeebill回调方式为post
        RetResult<GatewayDepositRsp> depositRspRetResult = ezeebillPaymentGatewayAdapter.deposit(depositReq);
        if (depositRspRetResult.isSuccess()) {
            GatewayDepositRsp resultData = depositRspRetResult.getData();
            //  保存支付网关返回的url到paymentPage
            paymentPageEntity.setChannelPageUrl(resultData.getRedirectUrl());
            paymentPageService.updateById(paymentPageEntity);
            String channelTransactionId = resultData.getChannelTransactionId();
            depositRecordService.lambdaUpdate().eq(BaseNoLogicalDeleteEntity::getId, recordEntity.getId())
                    .set(DepositRecordEntity::getChannelTransactionId, channelTransactionId).update();
            return new ConfirmRsp(depositRspRetResult.getData().getRedirectUrl());
        } else {
            log.error("支付网关入金失败, trackingId:{}, merchantId:{}, depositRspRetResult:{}", recordEntity.getTrackingId(),
                    recordEntity.getMerchantId(), depositRspRetResult);
            throw new IllegalArgumentException("Payment exception," + depositRspRetResult.getMsg());
        }
    }


    private ConfirmRsp cheezeePayDeposit(DepositRecordEntity recordEntity) {
        PaymentPageEntity paymentPageEntity = paymentPageService.lambdaQuery().eq(PaymentPageEntity::getMerchantId,
                recordEntity.getMerchantId()).eq(PaymentPageEntity::getTrackingId, recordEntity.getTrackingId()).one();
        if (paymentPageEntity == null) {
            throw new IllegalArgumentException("The payment page information does not exist, please check:" + recordEntity.getId());
        }
        // 重复点击支付按钮,且是未支付状态,且已经申请过了,直接返回支付页面url
        if (StrUtil.isNotBlank(paymentPageEntity.getChannelPageUrl())) {
            return new ConfirmRsp(paymentPageEntity.getChannelPageUrl());
        }
        // 获取可用的入金钱包
        GetAvailableWalletDto availableWalletDto = new GetAvailableWalletDto();
        availableWalletDto.setMerchantId(recordEntity.getMerchantId());
        availableWalletDto.setAssetType(recordEntity.getAssetType());
        availableWalletDto.setChannelSubType(recordEntity.getChannelSubType());
        availableWalletDto.setAssetName(recordEntity.getAssetName());
        availableWalletDto.setNetProtocol(recordEntity.getNetProtocol());
        availableWalletDto.setPurposeType(PurposeTypeEnum.DEPOSIT_WITHDRAWAL.getCode());
        availableWalletDto.setLock(false);
        MerchantWalletEntity walletEntity = merchantWalletService.getAvailableWallet(availableWalletDto);
        if (walletEntity == null) {
            log.info("没有可用的钱包,创建账户和钱包!,{}", availableWalletDto);
            throw new IllegalArgumentException("No wallet available");
        }
        depositRecordService.lambdaUpdate().eq(BaseNoLogicalDeleteEntity::getId, recordEntity.getId())
                .set(DepositRecordEntity::getDestinationAddress, walletEntity.getWalletAddress())
                .set(DepositRecordEntity::getAccountId, walletEntity.getAccountId())
                .set(DepositRecordEntity::getWalletId, walletEntity.getId()).set(DepositRecordEntity::getAddrBalance,
                        walletEntity.getBalance())
                .update();
        // 查通道钱包
        ChannelWalletEntity channelWalletEntity = channelWalletService.getById(walletEntity.getChannelWalletId());

        GatewayDepositReq depositReq = new GatewayDepositReq();
        depositReq.setTransactionId(recordEntity.getId());
        depositReq.setBusinessName(recordEntity.getBusinessName());
        depositReq.setAmount(NumberUtil.decimalFormat("#0", recordEntity.getAmount()));
        depositReq.setCurrency(recordEntity.getAssetName());
        depositReq.setCallbackUrl(appConfig.getPaymentRealend() + "/openapi/webhook/cheezeePay/deposit");
        depositReq.setSuccessPageUrl(appConfig.getPaymentRealend() + "/redirect/deposit/successPage?id=" + recordEntity.getId());
        Map<String, Object> extraMap = new HashMap<>();
        extraMap.put("payeeAccount", channelWalletEntity.getWalletAddress());
        extraMap.put("paymentMethod", recordEntity.getNetProtocol());
        depositReq.setExtraMap(extraMap);
        RetResult<GatewayDepositRsp> depositRspRetResult = cheezeePayPaymentGatewayAdapter.deposit(depositReq);
        if (depositRspRetResult.isSuccess()) {
            GatewayDepositRsp resultData = depositRspRetResult.getData();
            //  保存支付网关返回的url到paymentPage
            paymentPageEntity.setChannelPageUrl(resultData.getRedirectUrl());
            paymentPageService.updateById(paymentPageEntity);
            String channelTransactionId = resultData.getChannelTransactionId();
            depositRecordService.lambdaUpdate().eq(BaseNoLogicalDeleteEntity::getId, recordEntity.getId())
                    .set(DepositRecordEntity::getChannelTransactionId, channelTransactionId).update();
            return new ConfirmRsp(depositRspRetResult.getData().getRedirectUrl());
        } else {
            log.error("支付网关入金失败, trackingId:{}, merchantId:{}, depositRspRetResult:{}", recordEntity.getTrackingId(),
                    recordEntity.getMerchantId(), depositRspRetResult);
            throw new IllegalArgumentException("Payment exception," + depositRspRetResult.getMsg());
        }
    }


}
