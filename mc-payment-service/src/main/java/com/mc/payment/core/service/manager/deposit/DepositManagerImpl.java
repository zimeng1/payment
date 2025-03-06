package com.mc.payment.core.service.manager.deposit;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mc.payment.api.model.dto.DepositEventDto;
import com.mc.payment.api.model.req.DepositReq;
import com.mc.payment.api.model.rsp.DepositRsp;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.CommonConstant;
import com.mc.payment.common.exception.BusinessException;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.config.AppConfig;
import com.mc.payment.core.service.constant.WebhookEventConstants;
import com.mc.payment.core.service.entity.*;
import com.mc.payment.core.service.manager.ChannelCostManager;
import com.mc.payment.core.service.manager.CurrencyRateManager;
import com.mc.payment.core.service.model.GetAvailableWalletDto;
import com.mc.payment.core.service.model.dto.MerchantAssetDto;
import com.mc.payment.core.service.model.enums.*;
import com.mc.payment.core.service.model.req.DepositConfirmReq;
import com.mc.payment.core.service.model.req.DepositMockReq;
import com.mc.payment.core.service.model.req.GenerateWalletQRCodeReq;
import com.mc.payment.core.service.model.rsp.ConfirmRsp;
import com.mc.payment.core.service.service.*;
import com.mc.payment.core.service.util.AppSecureUtil;
import com.mc.payment.gateway.adapter.*;
import com.mc.payment.gateway.model.req.GatewayDepositReq;
import com.mc.payment.gateway.model.rsp.GatewayDepositRsp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DepositManagerImpl implements DepositManager {
    /**
     * 收银台前端页面地址
     */
    private static final String CASHIER_PAGE_URL = "/mc-payment/payment/in";


    private final IWebhookEventService webhookEventService;
    private final AppConfig appConfig;
    private final PaymentPageService paymentPageService;
    private final IDepositRecordService depositRecordService;
    private final AssetBankService assetBankService;
    private final MerchantChannelAssetService merchantChannelAssetService;
    private final MerchantWalletService merchantWalletService;
    private final ChannelWalletService channelWalletService;
    private final CheezeePayPaymentGatewayAdapter cheezeePayPaymentGatewayAdapter;
    private final EzeebillPaymentGatewayAdapter ezeebillPaymentGatewayAdapter;
    private final OfaPayPaymentGatewayAdapter ofaPayPaymentGatewayAdapter;
    private final PayPalPaymentGatewayAdapter payPalPaymentGatewayAdapter;
    private final PassToPayPaymentGatewayAdapter passToPayPaymentGatewayAdapter;
    private final ChannelAssetConfigService channelAssetConfigService;
    private final CurrencyRateManager currencyRateManager;
    private final IDepositRecordDetailService depositRecordDetailService;
    private final ChannelCostManager channelCostManager;


    @Value("${spring.profiles.active}")
    private String active;

    /**
     * 入金申请流程
     * <p>
     * 1. 校验
     * 2. 保存记录
     * 3. 执行入金
     * 4. webhook
     *
     * @param merchantId
     * @param merchantName
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = ValidateException.class)
    public RetResult<DepositRsp> requestProcess(String merchantId, String merchantName, DepositReq req) {
        log.info("DepositManagerImpl.requestProcess merchantId:{},merchantName:{},req:{}", merchantId, merchantName, req);
        RetResult<DepositRsp> retResult;
        DepositRsp rsp = new DepositRsp();
        String depositId = null;
        // 校验
        MerchantAssetDto assetDto = this.validateRequestProcess(merchantId, req);
        try {
            Optional<Integer> channelSubTypeOpt = Optional.ofNullable(assetDto).map(MerchantAssetDto::getChannelSubType);
            Integer channelSubType = channelSubTypeOpt.orElse(ChannelSubTypeEnum.UNDECIDED.getCode());
            // 保存记录
            DepositRecordEntity recordEntity = DepositRecordEntity.valueOf(req, merchantId, merchantName, channelSubType);
            if (assetDto != null) {
                recordEntity.setFeeAssetName(assetDto.getFeeAssetName());
            }
            depositRecordService.save(recordEntity);
            depositId = recordEntity.getId();
            // 生成收银页面
            // 保存支付页面信息
            PaymentPageEntity paymentPageEntity = PaymentPageEntity.valueOf(req, merchantId);
            paymentPageService.save(paymentPageEntity);
            // 跟踪id密文
            String redirectPageUrl = appConfig.getPaymentDomain() + CASHIER_PAGE_URL + "?k=" + URLEncoder.encode(AppSecureUtil.encrypt(paymentPageEntity.getId(), appConfig.getCashierKey()));
            // 执行入金
            // 是否执行入金
            String walletAddress = null;
            if (req.getSkipPage() == BooleanStatusEnum.ITEM_1.getCode()) {
                // 4. 执行入金
                ConfirmRsp ConfirmRsp = executeDeposit(recordEntity);
                redirectPageUrl = ConfirmRsp.getRedirectUrl();
                walletAddress = ConfirmRsp.getWalletAddress();
            }
            //  返回入金结果
            rsp.setRemark(req.getRemark());
            rsp.setTrackingId(req.getTrackingId());
            rsp.setWalletAddress(walletAddress);
            rsp.setExpireTimestamp(recordEntity.getExpireTimestamp());
            rsp.setRedirectPageUrl(redirectPageUrl);
            retResult = RetResult.data(rsp);
        } catch (ValidateException e) {
            log.info("入金参数异常", e);
            throw e;
        } catch (BusinessException e) {
            log.info("入金业务异常", e);
            //异常处理
            handleException(depositId, e);
            retResult = RetResult.error(e.getMessage());
        } catch (Exception e) {
            log.error("入金未知异常", e);
            //异常处理
            handleException(depositId, e);
            retResult = RetResult.error("error");
        } finally {
            log.info("req:{},rsp:{}", JSONUtil.toJsonStr(req), JSONUtil.toJsonStr(rsp));
            // 触发webhook
            webhookTrigger(depositId);
        }
        return retResult;
    }


    @Override
    @Transactional(rollbackFor = ValidateException.class)
    public RetResult<ConfirmRsp> executeProcess(DepositConfirmReq req) {
        log.info("DepositManagerImpl.executeProcess req:{}", req);
        RetResult<ConfirmRsp> retResult;
        ConfirmRsp confirmRsp = null;
        String id = AppSecureUtil.decrypt(req.getEncryptId(), appConfig.getCashierKey());
        PaymentPageEntity paymentPageEntity = paymentPageService.getById(id);
        if (paymentPageEntity == null) {
            // 页面信息不存在
            throw new ValidateException("Page information does not exist");
        }
        //查询入金记录
        DepositRecordEntity recordEntity = depositRecordService.getOne(paymentPageEntity.getMerchantId(),
                paymentPageEntity.getTrackingId());
        if (recordEntity == null) {
            log.error("入金记录不存在, trackingId:{}, merchantId:{}", paymentPageEntity.getTrackingId(), paymentPageEntity.getMerchantId());
            // 入金记录不存在
            throw new ValidateException("Deposit record does not exist");
        }
        if (recordEntity.getStatus() != DepositRecordStatusEnum.ITEM_0.getCode() && recordEntity.getStatus() != DepositRecordStatusEnum.ITEM_1.getCode()) {
            log.error("入金记录状态异常, trackingId:{}, status:{}", paymentPageEntity.getTrackingId(), recordEntity.getStatus());
            //入金记录状态异常
            throw new ValidateException("Deposit record status is abnormal");
        }
        if (recordEntity.getExpireTimestamp() < System.currentTimeMillis()) {
            // 订单已过期
            throw new ValidateException("The order has expired");
        }
        if (!StrUtil.equals(req.getAssetName(), recordEntity.getAssetName())) {
            // 资产名称不正确
            throw new ValidateException("Asset name is incorrect!");
        }
        if (recordEntity.getAmount().compareTo(req.getAmount()) != 0) {
            // 支付金额不正确
            throw new ValidateException("Payment amount is incorrect!");
        }
        // 重复提交检查
        if (recordEntity.getAssetType() == AssetTypeEnum.CRYPTO_CURRENCY.getCode()) {
            if (StrUtil.isNotBlank(recordEntity.getDestinationAddress())) {
                String walletQRCode =
                        merchantWalletService.generateWalletQRCode(new GenerateWalletQRCodeReq(recordEntity.getDestinationAddress()));
                return RetResult.data(new ConfirmRsp(recordEntity.getDestinationAddress(), walletQRCode));
            }
        } else {
            if (StrUtil.isNotBlank(paymentPageEntity.getChannelPageUrl())) {
                return RetResult.data(new ConfirmRsp(paymentPageEntity.getChannelPageUrl()));
            }
        }
        // 校验传入的参数 执行入金的流程一定要校验资产是否存在,所以isValidateAssetNull=true
        MerchantAssetDto assetDto = this.validateAssetInfo(recordEntity.getMerchantId(), recordEntity.getAssetType(),
                recordEntity.getAssetName(), req.getNetProtocol(), req.getAmount(), req.getBankCode(), true);

        // 更新入金记录,补齐协议和银行代码
        depositRecordService.update(Wrappers.lambdaUpdate(DepositRecordEntity.class)
                .eq(BaseNoLogicalDeleteEntity::getId, recordEntity.getId())
                .set(DepositRecordEntity::getNetProtocol, req.getNetProtocol())
                .set(req.getBankCode() != null, DepositRecordEntity::getBankCode, req.getBankCode())
                .set(DepositRecordEntity::getChannelSubType, assetDto.getChannelSubType())
                .set(DepositRecordEntity::getFeeAssetName, assetDto.getFeeAssetName()));
        recordEntity.setNetProtocol(req.getNetProtocol());
        recordEntity.setBankCode(req.getBankCode());
        recordEntity.setChannelSubType(assetDto.getChannelSubType());
        recordEntity.setFeeAssetName(assetDto.getFeeAssetName());

        // 执行入金
        // 执行入金
        try {
            // 补齐信息
            // 执行入金
            confirmRsp = executeDeposit(recordEntity);
            retResult = RetResult.data(confirmRsp);
        } catch (ValidateException e) {
            log.info("入金参数异常", e);
            throw e;
        } catch (BusinessException e) {
            log.info("执行入金业务异常", e);
            //异常处理
            handleException(recordEntity.getId(), e);
            retResult = RetResult.error(e.getMessage());
        } catch (Exception e) {
            log.error("执行入金未知异常", e);
            //异常处理
            handleException(recordEntity.getId(), e);
            retResult = RetResult.error(e.getMessage());
        } finally {
            log.info("recordId:{},ConfirmRsp:{}", recordEntity.getId(), confirmRsp);
            // 触发webhook
            webhookTrigger(recordEntity.getId());
        }
        return retResult;
    }

    /**
     * 模拟入金
     *
     * @param req
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void mock(DepositMockReq req) {
        log.info("DepositManagerImpl.mock req:{}", req);
        if (!CommonConstant.DEV.equals(active) && !CommonConstant.TEST.equals(active)) {
            log.error("模拟入金只能在test和dev环境使用");
            return;
        }
        DepositRecordEntity recordEntity = depositRecordService.getById(req.getId());
        if (recordEntity == null) {
            throw new ValidateException("入金记录不存在");
        }
        if (recordEntity.getStatus() != DepositRecordStatusEnum.ITEM_0.getCode()) {
            throw new ValidateException("入金记录状态异常");
        }
        if (req.getStatus() != DepositRecordStatusEnum.ITEM_1.getCode()
                && req.getStatus() != DepositRecordStatusEnum.ITEM_2.getCode()
                && req.getStatus() != DepositRecordStatusEnum.ITEM_4.getCode()) {
            throw new ValidateException("状态只能为,[1:部分入金,2:完全入金,4:请求失效]");
        }
        String newRemark = StrUtil.isNotBlank(recordEntity.getRemark()) ?
                recordEntity.getRemark() + ";" + req.getRemark() : req.getRemark();
        BigDecimal accumulatedAmount = recordEntity.getAmount();
        if (req.getStatus() == DepositRecordStatusEnum.ITEM_1.getCode()) {
            // 部分入金 remark追加
            // 计算部分入金的金额 取入金金额的一半
            int scale = recordEntity.getAssetType() == AssetTypeEnum.CRYPTO_CURRENCY.getCode() ? 20 : 5;
            accumulatedAmount = recordEntity.getAmount().divide(new BigDecimal(2), scale, RoundingMode.HALF_UP);
            depositRecordService.update(Wrappers.lambdaUpdate(DepositRecordEntity.class)
                    .eq(BaseNoLogicalDeleteEntity::getId, req.getId())
                    .set(DepositRecordEntity::getStatus, DepositRecordStatusEnum.ITEM_1.getCode())
                    .set(DepositRecordEntity::getAccumulatedAmount, accumulatedAmount)
                    .set(DepositRecordEntity::getRemark, newRemark));
        } else if (req.getStatus() == DepositRecordStatusEnum.ITEM_2.getCode()) {
            // 完全入金
            depositRecordService.update(Wrappers.lambdaUpdate(DepositRecordEntity.class)
                    .eq(BaseNoLogicalDeleteEntity::getId, req.getId())
                    .set(DepositRecordEntity::getStatus, DepositRecordStatusEnum.ITEM_2.getCode())
                    .set(DepositRecordEntity::getAccumulatedAmount, accumulatedAmount)
                    .set(DepositRecordEntity::getRemark, newRemark));
        } else if (req.getStatus() == DepositRecordStatusEnum.ITEM_4.getCode()) {
            // 请求失效
            depositRecordService.update(Wrappers.lambdaUpdate(DepositRecordEntity.class)
                    .eq(BaseNoLogicalDeleteEntity::getId, req.getId())
                    .set(DepositRecordEntity::getStatus, DepositRecordStatusEnum.ITEM_4.getCode())
                    .set(DepositRecordEntity::getRemark, newRemark));
        }
        // 生成一条模拟的入金明细
        DepositRecordDetailEntity recordDetailEntity = new DepositRecordDetailEntity();
        recordDetailEntity.setRecordId(recordEntity.getId());
        recordDetailEntity.setMerchantId(recordEntity.getMerchantId());
        recordDetailEntity.setMerchantName(recordEntity.getMerchantName());
        recordDetailEntity.setSourceAddress("模拟入金");
        recordDetailEntity.setDestinationAddress(recordEntity.getDestinationAddress());
        recordDetailEntity.setMerchantId(recordEntity.getMerchantId());
        recordDetailEntity.setTxHash("模拟_" + IdUtil.fastSimpleUUID() + System.currentTimeMillis());
        recordDetailEntity.setChannelSubType(recordEntity.getChannelSubType());
        recordDetailEntity.setAmount(accumulatedAmount);
        recordDetailEntity.setNetworkFee(BigDecimal.ZERO);
        recordDetailEntity.setAddrBalance(BigDecimal.ZERO);
        recordDetailEntity.setServiceFee(BigDecimal.ZERO);
        recordDetailEntity.setLastUpdated(System.currentTimeMillis());
        recordDetailEntity.setStatus(req.getStatus() != DepositRecordStatusEnum.ITEM_4.getCode()
                ? DepositDetailStausEnum.ITEM_3.getCode() : DepositDetailStausEnum.ITEM_7.getCode());
        recordDetailEntity.setAssetName(recordEntity.getAssetName());
        recordDetailEntity.setNetProtocol(recordEntity.getNetProtocol());

        depositRecordDetailService.save(recordDetailEntity);
        // 触发webhook
        webhookTrigger(recordEntity.getId());
    }


    //=========以下是私有方法=========

    /**
     * 执行入金
     *
     * @param recordEntity
     * @return
     */
    private ConfirmRsp executeDeposit(DepositRecordEntity recordEntity) {
        // 获取可用钱包
        MerchantWalletEntity walletEntity = getAvailableWallet(recordEntity);
        if (walletEntity == null) {
            // 没有可用的钱包,提醒五分钟后重试,定时任务会解锁超时的钱包/冷却的/以及创建新的钱包
            throw new ValidateException("There is no wallet available, please try again in 5 minutes");
        }
        // 是否是加密货币
        boolean isCrypto = recordEntity.getAssetType() == AssetTypeEnum.CRYPTO_CURRENCY.getCode();

        ImmutablePair<BigDecimal, BigDecimal> ratePair = this.calculateRate(isCrypto, recordEntity.getAssetName(),
                recordEntity.getFeeAssetName(), recordEntity.getTargetCurrency());


        // 根据平台成本计算通道费
        // 计算费用 通道费使用本币币种的汇率,它和手续费币种没关系
        BigDecimal fee = channelCostManager.channelCostCalculator(BusinessActionEnum.DEPOSIT.getCode(),
                recordEntity.getChannelSubType(),
                recordEntity.getAssetName(),
                recordEntity.getNetProtocol(),
                recordEntity.getAmount(),
                ratePair.left);
        // 更新入金记录
        // 入金申请时保存的状态是待入金,
        // 和执行入金没有区分所以不需要再更新状态  .eq(DepositRecordEntity::getStatus, DepositRecordStatusEnum.ITEM_0.getCode())
        depositRecordService.update(Wrappers.lambdaUpdate(DepositRecordEntity.class)
                .eq(BaseNoLogicalDeleteEntity::getId, recordEntity.getId())
                .set(DepositRecordEntity::getChannelFee, fee)
                .set(DepositRecordEntity::getDestinationAddress, walletEntity.getWalletAddress())
                .set(DepositRecordEntity::getAccountId, walletEntity.getAccountId())
                .set(DepositRecordEntity::getWalletId, walletEntity.getId())
                .set(DepositRecordEntity::getAddrBalance, walletEntity.getBalance())
                .set(DepositRecordEntity::getRate, ratePair.left)
                .set(DepositRecordEntity::getFeeRate, ratePair.right));

        ConfirmRsp ConfirmRsp;
        // 获取支付所需信息,组装结果
        if (isCrypto) {
            // 组装钱包地址和二维码
            String walletQRCode = merchantWalletService.generateWalletQRCode(new GenerateWalletQRCodeReq(walletEntity.getWalletAddress()));
            ConfirmRsp = new ConfirmRsp(walletEntity.getWalletAddress(), walletQRCode);
        } else {
            // 组装结果-调用上游支付通道获取支付页面地址
            // 获取最新的入金记录
            DepositRecordEntity depositRecordEntity = depositRecordService.getById(recordEntity.getId());
            String redirectPageUrl = fiatGetRedirectPageUrl(depositRecordEntity);
            ConfirmRsp = new ConfirmRsp(redirectPageUrl);
        }
        return ConfirmRsp;
    }

    /**
     * 计算当前币种和手续费币种对应美元(法币:usd,加密货币:usdt)的汇率
     *
     * @param isCrypto
     * @param assetName
     * @param feeAssetName
     * @return rate, feeRate
     */
    private ImmutablePair<BigDecimal, BigDecimal> calculateRate(boolean isCrypto, String assetName, String feeAssetName, String targetCurrency) {
        BigDecimal rate = currencyRateManager.getCurrencyRate(isCrypto, assetName, targetCurrency);
        BigDecimal feeRate = rate;
        // 如果手续费币种和当前币种不一致,则计算手续费币种对应美元(法币:usd,加密货币:usdt)的汇率
        if (!assetName.equals(feeAssetName)) {
            feeRate = currencyRateManager.getCurrencyRate(isCrypto, feeAssetName, targetCurrency);
        }
        rate = rate == null ? BigDecimal.ZERO : rate;
        feeRate = feeRate == null ? BigDecimal.ZERO : feeRate;
        return ImmutablePair.of(rate, feeRate);
    }

    /**
     * 法币入金-获取重定向页面地址
     *
     * @param depositRecordEntity
     * @return
     */
    private String fiatGetRedirectPageUrl(DepositRecordEntity depositRecordEntity) throws BusinessException {
        Integer channelSubType = depositRecordEntity.getChannelSubType();
        // 需要将gateway优化,现在直接调用对应通道的方法
        if (ChannelSubTypeEnum.CHEEZEE_PAY.getCode() == channelSubType) {
            return fiatGetRedirectPageUrlByCheezeePay(depositRecordEntity);
        } else if (ChannelSubTypeEnum.EZEEBILL.getCode() == channelSubType) {
            return fiatGetRedirectPageUrlByEzeebill(depositRecordEntity);
        } else if (ChannelSubTypeEnum.OFA_PAY.getCode() == channelSubType) {
            return fiatGetRedirectPageUrlByOfaPay(depositRecordEntity);
        } else if (ChannelSubTypeEnum.PAY_PAL.getCode() == channelSubType) {
            return fiatGetRedirectPageUrlByPayPal(depositRecordEntity);
        } else if (ChannelSubTypeEnum.PASS_TO_PAY.getCode() == channelSubType) {
            return fiatGetRedirectPageUrlByPassToPay(depositRecordEntity);
        }

        return "";
    }

    private String fiatGetRedirectPageUrlByPassToPay(DepositRecordEntity recordEntity) {
        RetResult<GatewayDepositRsp> depositRspRetResult;
        try {

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
            depositRspRetResult = passToPayPaymentGatewayAdapter.deposit(depositReq);
        } catch (Exception e) {
            throw new BusinessException("channel call fail:" + e.getMessage());
        }
        if (depositRspRetResult.isSuccess()) {
            GatewayDepositRsp resultData = depositRspRetResult.getData();
            //  更新支付网关返回的url到paymentPage
            paymentPageService.lambdaUpdate()
                    .eq(PaymentPageEntity::getMerchantId, recordEntity.getMerchantId())
                    .eq(PaymentPageEntity::getTrackingId, recordEntity.getTrackingId())
                    .set(PaymentPageEntity::getChannelPageUrl, resultData.getRedirectUrl())
                    .update();

            String channelTransactionId = resultData.getChannelTransactionId();
            depositRecordService.lambdaUpdate()
                    .eq(BaseNoLogicalDeleteEntity::getId, recordEntity.getId())
                    .set(DepositRecordEntity::getChannelTransactionId, channelTransactionId)
                    .update();
        } else {
            throw new BusinessException("channel return fail:" + depositRspRetResult.getMsg());
        }
        return depositRspRetResult.getData().getRedirectUrl();
    }

    private String fiatGetRedirectPageUrlByPayPal(DepositRecordEntity recordEntity) {
        RetResult<GatewayDepositRsp> depositRspRetResult;
        try {
            MerchantWalletEntity merchantWalletEntity = merchantWalletService.getById(recordEntity.getWalletId());
            // 查通道钱包
            ChannelWalletEntity channelWalletEntity = channelWalletService.getById(merchantWalletEntity.getChannelWalletId());
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
            depositRspRetResult = payPalPaymentGatewayAdapter.deposit(depositReq);
        } catch (Exception e) {
            throw new BusinessException("channel call fail:" + e.getMessage());
        }
        if (depositRspRetResult.isSuccess()) {
            GatewayDepositRsp resultData = depositRspRetResult.getData();

            //  更新支付网关返回的url到paymentPage
            paymentPageService.lambdaUpdate()
                    .eq(PaymentPageEntity::getMerchantId, recordEntity.getMerchantId())
                    .eq(PaymentPageEntity::getTrackingId, recordEntity.getTrackingId())
                    .set(PaymentPageEntity::getChannelPageUrl, resultData.getRedirectUrl())
                    .update();

            String channelTransactionId = resultData.getChannelTransactionId();
            depositRecordService.lambdaUpdate()
                    .eq(BaseNoLogicalDeleteEntity::getId, recordEntity.getId())
                    .set(DepositRecordEntity::getChannelTransactionId, channelTransactionId)
                    .update();
        } else {
            throw new BusinessException("channel return fail:" + depositRspRetResult.getMsg());
        }
        return depositRspRetResult.getData().getRedirectUrl();
    }

    private String fiatGetRedirectPageUrlByOfaPay(DepositRecordEntity recordEntity) {
        RetResult<GatewayDepositRsp> depositRspRetResult;
        try {
            // 组装入金请求参数
            String scode = recordEntity.getDestinationAddress().split("_")[1];
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
            depositRspRetResult = ofaPayPaymentGatewayAdapter.deposit(gatewayDepositReq);
        } catch (Exception e) {
            throw new BusinessException("channel call fail:" + e.getMessage());
        }
        if (depositRspRetResult.isSuccess()) {
            GatewayDepositRsp resultData = depositRspRetResult.getData();
            //  更新支付网关返回的url到paymentPage
            paymentPageService.lambdaUpdate()
                    .eq(PaymentPageEntity::getMerchantId, recordEntity.getMerchantId())
                    .eq(PaymentPageEntity::getTrackingId, recordEntity.getTrackingId())
                    .set(PaymentPageEntity::getChannelPageUrl, resultData.getRedirectUrl())
                    .update();

            String channelTransactionId = resultData.getChannelTransactionId();
            if (StrUtil.isNotBlank(channelTransactionId)) {
                depositRecordService.lambdaUpdate()
                        .eq(BaseNoLogicalDeleteEntity::getId, recordEntity.getId())
                        .set(DepositRecordEntity::getChannelTransactionId, channelTransactionId)
                        .update();
            }
        } else {
            throw new BusinessException("channel return fail:" + depositRspRetResult.getMsg());
        }
        return depositRspRetResult.getData().getRedirectUrl();
    }

    private String fiatGetRedirectPageUrlByEzeebill(DepositRecordEntity recordEntity) {
        RetResult<GatewayDepositRsp> depositRspRetResult;
        try {
            GatewayDepositReq depositReq = new GatewayDepositReq();
            depositReq.setTransactionId(recordEntity.getId());
            depositReq.setBusinessName(recordEntity.getBusinessName());
            depositReq.setAmount(NumberUtil.decimalFormat("#0.00", recordEntity.getAmount()));
            depositReq.setCurrency(recordEntity.getAssetName());
            depositReq.setPayType(recordEntity.getNetProtocol());
            // depositReq.setCallbackUrl() paypal 不需要通过接口设置回调地址
            //不传业务方的地址给上游服务   depositReq.setSuccessPageUrl(recordEntity.getSuccessPageUrl())
            depositReq.setSuccessPageUrl(appConfig.getPaymentRealend() + "/redirect/deposit/post/successPage?id=" + recordEntity.getId());//Ezeebill回调方式为post
            depositRspRetResult = ezeebillPaymentGatewayAdapter.deposit(depositReq);
        } catch (Exception e) {
            throw new BusinessException("channel call fail:" + e.getMessage());
        }
        if (depositRspRetResult.isSuccess()) {
            GatewayDepositRsp resultData = depositRspRetResult.getData();
            //  更新支付网关返回的url到paymentPage
            paymentPageService.lambdaUpdate()
                    .eq(PaymentPageEntity::getMerchantId, recordEntity.getMerchantId())
                    .eq(PaymentPageEntity::getTrackingId, recordEntity.getTrackingId())
                    .set(PaymentPageEntity::getChannelPageUrl, resultData.getRedirectUrl())
                    .update();

            String channelTransactionId = resultData.getChannelTransactionId();
            depositRecordService.lambdaUpdate()
                    .eq(BaseNoLogicalDeleteEntity::getId, recordEntity.getId())
                    .set(DepositRecordEntity::getChannelTransactionId, channelTransactionId)
                    .update();
        } else {
            throw new BusinessException("channel return fail:" + depositRspRetResult.getMsg());
        }
        return depositRspRetResult.getData().getRedirectUrl();

    }

    private String fiatGetRedirectPageUrlByCheezeePay(DepositRecordEntity recordEntity) throws BusinessException {
        RetResult<GatewayDepositRsp> depositRspRetResult;
        try {
            MerchantWalletEntity merchantWalletEntity = merchantWalletService.getById(recordEntity.getWalletId());
            // 查通道钱包
            ChannelWalletEntity channelWalletEntity = channelWalletService.getById(merchantWalletEntity.getChannelWalletId());
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
            depositRspRetResult = cheezeePayPaymentGatewayAdapter.deposit(depositReq);
        } catch (Exception e) {
            throw new BusinessException("channel call fail:" + e.getMessage());
        }
        if (depositRspRetResult.isSuccess()) {
            GatewayDepositRsp resultData = depositRspRetResult.getData();
            //  更新支付网关返回的url到paymentPage
            paymentPageService.lambdaUpdate()
                    .eq(PaymentPageEntity::getMerchantId, recordEntity.getMerchantId())
                    .eq(PaymentPageEntity::getTrackingId, recordEntity.getTrackingId())
                    .set(PaymentPageEntity::getChannelPageUrl, resultData.getRedirectUrl())
                    .update();

            String channelTransactionId = resultData.getChannelTransactionId();
            depositRecordService.lambdaUpdate()
                    .eq(BaseNoLogicalDeleteEntity::getId, recordEntity.getId())
                    .set(DepositRecordEntity::getChannelTransactionId, channelTransactionId)
                    .update();
        } else {
            throw new BusinessException("channel return fail:" + depositRspRetResult.getMsg());
        }
        return depositRspRetResult.getData().getRedirectUrl();
    }

    private MerchantWalletEntity getAvailableWallet(DepositRecordEntity recordEntity) {
        GetAvailableWalletDto availableWalletDto = new GetAvailableWalletDto();
        availableWalletDto.setMerchantId(recordEntity.getMerchantId());
        availableWalletDto.setAssetType(recordEntity.getAssetType());
        availableWalletDto.setChannelSubType(recordEntity.getChannelSubType());
        availableWalletDto.setAssetName(recordEntity.getAssetName());
        availableWalletDto.setNetProtocol(recordEntity.getNetProtocol());
        if (recordEntity.getAssetType() == AssetTypeEnum.CRYPTO_CURRENCY.getCode()) {
            availableWalletDto.setPurposeType(PurposeTypeEnum.DEPOSIT.getCode());
            availableWalletDto.setLock(true);
        } else {
            availableWalletDto.setPurposeType(PurposeTypeEnum.DEPOSIT_WITHDRAWAL.getCode());
            availableWalletDto.setLock(false);
        }
        return merchantWalletService.getAvailableWallet(availableWalletDto);
    }

    /**
     * 校验入金资产相关参数,并且返回资产的配置信息
     *
     * @param merchantId
     * @param req
     * @return
     */
    private MerchantAssetDto validateRequestProcess(String merchantId, DepositReq req) {
        // 校验
        req.validate();
        // 跟踪id不可重复校验
        this.validateTrackingId(merchantId, req.getTrackingId());
        // 并且用户不可选或者输入了NetProtocol时,需要校验资产为空的情况
        boolean isValidateAssetNull = req.getUserSelectable() == BooleanStatusEnum.ITEM_0.getCode() || StrUtil.isNotBlank(req.getNetProtocol());
        return this.validateAssetInfo(merchantId, req.getAssetType(), req.getAssetName(), req.getNetProtocol(), req.getAmount(), req.getBankCode(), isValidateAssetNull);
    }

    /**
     * @param merchantId
     * @param assetType
     * @param assetName
     * @param netProtocol
     * @param amount
     * @param bankCode
     * @param needValidateAssetNull 是否需要校验资产不存在
     * @return
     */
    private MerchantAssetDto validateAssetInfo(String merchantId, Integer assetType, String assetName,
                                               String netProtocol,
                                               BigDecimal amount,
                                               String bankCode, boolean needValidateAssetNull) {
        // 查询商户可用资产信息
        List<MerchantAssetDto> assetDtos = merchantChannelAssetService.queryAsset(merchantId, assetType);
        // 资产信息按照资产名称和网络协议分组 key1:资产名称 key2:网络协议
        Map<String, Map<String, MerchantAssetDto>> assetDtoMap = assetDtos.stream().collect(Collectors.groupingBy(MerchantAssetDto::getAssetName,
                Collectors.toMap(MerchantAssetDto::getNetProtocol, Function.identity())));
        Map<String, MerchantAssetDto> netProtocolMap = assetDtoMap.get(assetName);
        if (netProtocolMap == null) {
            // 您输入的资产信息不存在, 请检查
            throw new ValidateException("The assetName not available, please check");
        }
        MerchantAssetDto assetDto = netProtocolMap.get(netProtocol);
        if (needValidateAssetNull) {
            if (assetDto == null) {
                // 您输入的资产信息不存在, 请检查
                throw new ValidateException("The netProtocol not available, please check");
            }
            if (!Objects.equals(assetDto.getAssetType(), assetType)) {
                // 您输入的资产信息资产类型错误, 请检查
                throw new ValidateException("The assetType not available, please check");
            }
            if (assetDto.getDepositStatus() != BooleanStatusEnum.ITEM_1.getCode()) {
                // 该资产不支持入金,请检查
                throw new ValidateException("The asset does not support deposit, please check");
            }
        }
        if (assetDto != null) {
            this.validateAmount(assetDto, amount);
            this.validateBankCode(assetDto, bankCode);
        }
        return assetDto;
    }

    private void validateAmount(MerchantAssetDto assetDto, BigDecimal amount) {
        // 校验金额是否符合要求
        BigDecimal minDepositAmount = assetDto.getMinDepositAmount();
        BigDecimal maxDepositAmount = assetDto.getMaxDepositAmount();
        if (minDepositAmount != null && amount.compareTo(minDepositAmount) < 0) {
            // 入金金额应该大于最小入金金额,请检查
            throw new ValidateException("The deposit amount should be greater than the minimum deposit" +
                    " amount, please check:" + minDepositAmount.stripTrailingZeros().toPlainString());
        }
        if (maxDepositAmount != null && maxDepositAmount.compareTo(BigDecimal.ZERO) != 0 && amount.compareTo(maxDepositAmount) > 0) {
            // 入金金额应该小于最大入金金额,请检查
            throw new ValidateException("The deposit amount should be lesser than the max deposit" +
                    " amount, please check:" + maxDepositAmount.stripTrailingZeros().toPlainString());
        }
    }

    private void validateBankCode(MerchantAssetDto assetDto, String bankCode) {
        // 判断是否需要校验
        boolean exists = assetBankService.existDeposit(assetDto.getAssetName(), assetDto.getNetProtocol());
        if (exists) {
            if (StrUtil.isBlank(bankCode)) {
                throw new ValidateException("The bank code you entered does not exist, please " +
                        "check:bankCode");
            }
            boolean existedDepositBankCode = assetBankService.existDepositBankCode(assetDto.getAssetName(), assetDto.getNetProtocol(), bankCode);
            if (!existedDepositBankCode) {
                throw new ValidateException("The bank code you entered does not exist, please " +
                        "check:bankCode");
            }

        }
    }

    private void validateTrackingId(String merchantId, String trackingId) {
        boolean trackingIdExists = depositRecordService.lambdaQuery()
                .eq(DepositRecordEntity::getMerchantId, merchantId)
                .eq(DepositRecordEntity::getTrackingId, trackingId)
                .exists();
        if (trackingIdExists) {
            throw new ValidateException("trackingId Repeat, please check:" + trackingId);
        }
    }

    /**
     * 异常处理
     *
     * @param e
     * @param depositId
     */
    private void handleException(String depositId, Exception e) {
        depositRecordService.lambdaUpdate()
                .eq(BaseNoLogicalDeleteEntity::getId, depositId)
                .set(DepositRecordEntity::getStatus, DepositRecordStatusEnum.ITEM_4.getCode())
                .set(DepositRecordEntity::getStayReason, e.getMessage())
                .update();
    }

    private void webhookTrigger(String depositId) {
        if (depositId == null) {
            return;
        }
        DepositRecordEntity recordEntity = depositRecordService.getById(depositId);
        // 触发webhook
        WebhookEventEntity webhookEventEntity = new WebhookEventEntity();
        webhookEventEntity.setEvent(WebhookEventConstants.DEPOSIT_EVENT);
        webhookEventEntity.setData(JSONUtil.toJsonStr(new DepositEventDto(recordEntity.getTrackingId(),
                recordEntity.getStatus(), recordEntity.getAmount())));
        webhookEventEntity.setTrackingId(recordEntity.getTrackingId());
        webhookEventEntity.setWebhookUrl(recordEntity.getWebhookUrl());
        webhookEventEntity.setMerchantId(recordEntity.getMerchantId());
        webhookEventService.asyncSendWebhookEvent(webhookEventEntity);
    }
}
