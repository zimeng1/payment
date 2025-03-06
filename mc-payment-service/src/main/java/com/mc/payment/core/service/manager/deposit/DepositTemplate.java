package com.mc.payment.core.service.manager.deposit;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mc.payment.api.model.dto.DepositEventDto;
import com.mc.payment.api.model.rsp.DepositRsp;
import com.mc.payment.common.exception.BusinessException;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.config.AppConfig;
import com.mc.payment.core.service.constant.AssetConstants;
import com.mc.payment.core.service.constant.WebhookEventConstants;
import com.mc.payment.core.service.entity.DepositRecordEntity;
import com.mc.payment.core.service.entity.MerchantWalletEntity;
import com.mc.payment.core.service.entity.PaymentPageEntity;
import com.mc.payment.core.service.entity.WebhookEventEntity;
import com.mc.payment.core.service.model.dto.MerchantAssetDto;
import com.mc.payment.core.service.model.enums.AssetTypeEnum;
import com.mc.payment.core.service.model.enums.BooleanStatusEnum;
import com.mc.payment.core.service.model.enums.DepositRecordStatusEnum;
import com.mc.payment.core.service.model.req.DepositConfirmReq;
import com.mc.payment.core.service.model.req.GenerateWalletQRCodeReq;
import com.mc.payment.core.service.model.req.ProcessDepositReq;
import com.mc.payment.core.service.model.rsp.ConfirmRsp;
import com.mc.payment.core.service.service.*;
import com.mc.payment.core.service.util.AppSecureUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 入金模板
 */
@Slf4j
@RequiredArgsConstructor
public abstract class DepositTemplate {
    /**
     * 收银台前端页面地址
     */
    protected static final String CASHIER_PAGE_URL = "/mc-payment/payment/in";

    protected final IWebhookEventService webhookEventService;
    protected final AppConfig appConfig;
    protected final PaymentPageService paymentPageService;
    protected final IDepositRecordService depositRecordService;
    protected final AssetBankService assetBankService;
    protected final MerchantChannelAssetService merchantChannelAssetService;
    protected final IAssetLastQuoteService assetLastQuoteService;
    protected final CurrencyRateService currencyRateService;
    protected final MerchantWalletService merchantWalletService;
    protected final ChannelWalletService channelWalletService;

    /**
     * 模板方法，定义入金申请的整体流程
     *
     * @param req
     * @return
     */
    public final DepositRsp processDeposit(ProcessDepositReq req) {
        // 改为context对象
        log.info("req:{}", JSONUtil.toJsonStr(req));
        // 1. 验证入金请求
        validateDeposit(req);
        DepositRsp rsp = new DepositRsp();
        DepositRecordEntity recordEntity = null;
        String depositId = null;
        try {
            // 3. 创建入金记录
            recordEntity = createDepositRecord(req);
            depositId = recordEntity.getId();
            // 生成收银页面记录
            String redirectPageUrl = createDepositPage(req);
            String walletAddress = null;
            if (req.getSkipPage() == BooleanStatusEnum.ITEM_1.getCode()) {
                // 4. 执行入金
                ConfirmRsp ConfirmRsp = executeDeposit(recordEntity);
                redirectPageUrl = ConfirmRsp.getRedirectUrl();
                walletAddress = ConfirmRsp.getWalletAddress();
            }
            // 6. 返回入金结果
            rsp.setRemark(req.getRemark());
            rsp.setTrackingId(req.getTrackingId());
            rsp.setWalletAddress(walletAddress);
            rsp.setExpireTimestamp(recordEntity.getExpireTimestamp());
            rsp.setRedirectPageUrl(redirectPageUrl);
        } catch (BusinessException e) {
            log.info("入金业务异常", e);
            //异常处理
            handleException(depositId, e);
        } catch (Exception e) {
            log.error("入金未知异常", e);
            //异常处理
            handleException(depositId, e);
        } finally {
            log.info("req:{},rsp:{}", JSONUtil.toJsonStr(req), JSONUtil.toJsonStr(rsp));
            // 触发webhook
            webhookTrigger(depositId);
        }
        return rsp;
    }


    /**
     * 模板发方法,定义执行入金的整体流程
     *
     * @param recordEntity
     * @return
     */
    public final ConfirmRsp processExecuteDeposit(DepositConfirmReq req, DepositRecordEntity recordEntity) {
        log.info("req:{}", recordEntity);
        ConfirmRsp confirmRsp = null;
        // 参数校验
        Map<String, MerchantAssetDto> netProtocolMap = validateAssetName(recordEntity.getMerchantId(), recordEntity.getAssetName(), recordEntity.getAssetType());
        MerchantAssetDto assetDto = validateAssetInfo(
                recordEntity.getAssetName(),
                req.getNetProtocol(),
                req.getAmount(),
                req.getBankCode(), netProtocolMap);
        // 更新入金记录,补齐协议和银行代码
        depositRecordService.update(Wrappers.lambdaUpdate(DepositRecordEntity.class)
                .eq(BaseNoLogicalDeleteEntity::getId, recordEntity.getId())
                .set(DepositRecordEntity::getNetProtocol, req.getNetProtocol())
                .set(DepositRecordEntity::getBankCode, recordEntity.getBankCode())
                .set(DepositRecordEntity::getChannelSubType, assetDto.getChannelSubType())
                .set(DepositRecordEntity::getFeeAssetName, assetDto.getFeeAssetName()));
        recordEntity.setNetProtocol(req.getNetProtocol());
        recordEntity.setBankCode(req.getBankCode());
        // 执行入金
        try {
            // 补齐信息
            // 执行入金
            confirmRsp = executeDeposit(recordEntity);
        } catch (BusinessException e) {
            log.info("执行入金业务异常", e);
            //异常处理
            handleException(recordEntity.getId(), e);
            throw e;
        } catch (Exception e) {
            log.error("执行入金未知异常", e);
            //异常处理
            handleException(recordEntity.getId(), e);
            throw e;
        } finally {
            log.info("recordId:{},ConfirmRsp:{}", recordEntity.getId(), confirmRsp);
            // 触发webhook
            webhookTrigger(recordEntity.getId());
        }
        return confirmRsp;
    }


    /**
     * 法币入金-获取重定向页面地址
     * todo 优化子类的实现,将调用上游通道的参数组装规则同一化,将调用上游支付网关的功能优化或者简化(现在相当于封装了两次)
     *
     * @param depositRecordEntity
     * @return 重定向页面地址
     * @throws BusinessException 说明上游通道返回错误,或者调用失败
     */
    protected abstract String fiatGetRedirectPageUrl(DepositRecordEntity depositRecordEntity) throws BusinessException;


    /**
     * 获取可用钱包
     *
     * @param recordEntity
     * @return
     */
    protected abstract MerchantWalletEntity getAvailableWallet(DepositRecordEntity recordEntity);

    /**
     * 执行入金
     *
     * @param recordEntity
     * @return
     */
    protected ConfirmRsp executeDeposit(DepositRecordEntity recordEntity) {
        // 获取可用钱包
        MerchantWalletEntity walletEntity = getAvailableWallet(recordEntity);
        if (walletEntity == null) {
            // 没有可用的钱包,提醒五分钟后重试,定时任务会解锁超时的钱包/冷却的/以及创建新的钱包
            throw new ValidateException("There is no wallet available, please try again in 5 minutes");
        }
        // 是否是加密货币入金
        boolean isCrypto = recordEntity.getAssetType() == AssetTypeEnum.CRYPTO_CURRENCY.getCode();

        String assetName = recordEntity.getAssetName();
        String feeAssetName = recordEntity.getFeeAssetName();
        // 先查汇率
        BigDecimal rate = BigDecimal.ONE;
        BigDecimal feeRate = BigDecimal.ONE;
        // 是否同种币
        boolean isSame = assetName.equals(feeAssetName);
        if (isCrypto) {
            // assetName 是否为USDT
            boolean isUsdt = assetName.equals(AssetConstants.AN_USDT);
            // feeAssetName 是否为USDT
            boolean isFeeUsdt = feeAssetName.equals(AssetConstants.AN_USDT);
            if (!isUsdt) {
                rate = assetLastQuoteService.getExchangeRate(assetName, AssetConstants.AN_USDT);
            }
            if (isSame) {
                feeRate = rate;
            } else {
                if (!isFeeUsdt) {
                    feeRate = assetLastQuoteService.getExchangeRate(feeAssetName, AssetConstants.AN_USDT);
                }
            }
        } else {
            // assetName 是否为USD
            boolean isUsd = assetName.equals(AssetConstants.AN_USD);
            // feeAssetName 是否为USD
            boolean isFeeUsd = feeAssetName.equals(AssetConstants.AN_USD);
            if (!isUsd) {
                rate = currencyRateService.getCurrencyRate(assetName, AssetConstants.AN_USD);
            }
            if (isSame) {
                feeRate = rate;
            } else {
                if (!isFeeUsd) {
                    feeRate = currencyRateService.getCurrencyRate(feeAssetName, AssetConstants.AN_USD);
                }
            }
        }

        // 根据平台成本计算通道费
        // 计算费用
        BigDecimal fee = calculateFee(recordEntity);
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
                .set(DepositRecordEntity::getRate, rate)
                .set(DepositRecordEntity::getFeeRate, feeRate));

        ConfirmRsp ConfirmRsp;
        // 获取支付所需信息,组装结果
        if (isCrypto) {
            // 组装钱包地址和二维码
            String walletQRCode = merchantWalletService.generateWalletQRCode(new GenerateWalletQRCodeReq(recordEntity.getDestinationAddress()));
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
     * 创建收银页面
     *
     * @return 收银页面地址
     */
    protected String createDepositPage(ProcessDepositReq req) {
        // 保存支付页面信息
        PaymentPageEntity paymentPageEntity = PaymentPageEntity.valueOf(req);
        paymentPageService.save(paymentPageEntity);
        // 跟踪id密文
        return appConfig.getPaymentDomain() + CASHIER_PAGE_URL + "?k=" + URLEncoder.encode(AppSecureUtil.encrypt(paymentPageEntity.getId(), appConfig.getCashierKey()));
    }

    /**
     * 计算费用
     *
     * @param recordEntity
     * @return
     */
    protected BigDecimal calculateFee(DepositRecordEntity recordEntity) {
        return BigDecimal.ZERO;
    }

    /**
     * 入金参数验证
     *
     * @param req
     */
    protected void validateDeposit(ProcessDepositReq req) throws ValidateException {
        req.validate();
        //  trackingId 同一个商户不可重复
        boolean trackingIdExists = depositRecordService.lambdaQuery()
                .eq(DepositRecordEntity::getMerchantId, req.getMerchantId())
                .eq(DepositRecordEntity::getTrackingId, req.getTrackingId())
                .exists();
        if (trackingIdExists) {
            throw new ValidateException("trackingId Repeat, please check:" + req.getTrackingId());
        }
        Map<String, MerchantAssetDto> netProtocolMap = validateAssetName(req.getMerchantId(), req.getAssetName(), req.getAssetType());

        // 可选
        if (req.getUserSelectable() == BooleanStatusEnum.ITEM_1.getCode()) {
            // netProtocol 和 bankCode 允许先不传值,不传则不校验,传了则校验
            if (StrUtil.isNotBlank((req.getNetProtocol()))) {
                validateAssetInfo(req.getAssetName(),
                        req.getNetProtocol(),
                        req.getAmount(),
                        req.getBankCode(), netProtocolMap);
            }
        } else {
            // 不可选
            // netProtocol 必须传值 bankCode 看情况有需要则要传值 ,且必须存在
            validateAssetInfo(req.getAssetName(),
                    req.getNetProtocol(),
                    req.getAmount(),
                    req.getBankCode(), netProtocolMap);
        }
    }

    @NotNull
    private Map<String, MerchantAssetDto> validateAssetName(String merchantId, String assetName, Integer assetType) {
        List<MerchantAssetDto> assetDtos = merchantChannelAssetService.queryAsset(merchantId, assetType);
        if (assetDtos == null || assetDtos.isEmpty()) {
            // 说明商户没有配置资产信息
            throw new ValidateException("The merchant has not configured the asset information, please check");
        }
        // 资产信息按照资产名称和网络协议分组 key1:资产名称 key2:网络协议
        Map<String, Map<String, MerchantAssetDto>> assetDtoMap = assetDtos.stream().collect(Collectors.groupingBy(MerchantAssetDto::getAssetName,
                Collectors.toMap(MerchantAssetDto::getNetProtocol, Function.identity())));
        Map<String, MerchantAssetDto> netProtocolMap = assetDtoMap.get(assetName);
        if (netProtocolMap == null) {
            // 您输入的资产信息不存在, 请检查
            throw new ValidateException("The assetName not available, please check");
        }
        return netProtocolMap;
    }


    /**
     * 校验入金资产相关参数,并且返回资产的配置信息
     * <p>
     * 校验网络协议
     * 校验是否支持入金
     * 校验是否需要校验银行代码
     * 校验银行代码
     * 校验金额是否符合要求
     *
     * @param assetName
     * @param netProtocol
     * @param amount
     * @param bankCode
     */
    private MerchantAssetDto validateAssetInfo(String assetName,
                                               String netProtocol,
                                               BigDecimal amount,
                                               String bankCode,
                                               Map<String, MerchantAssetDto> netProtocolMap) {

        MerchantAssetDto assetDto = netProtocolMap.get(netProtocol);
        if (assetDto == null) {
            // 您输入的资产信息不存在, 请检查
            throw new ValidateException("The netProtocol not available, please check");
        }
        if (assetDto.getDepositStatus() != BooleanStatusEnum.ITEM_1.getCode()) {
            // 该资产不支持入金,请检查
            throw new ValidateException("The asset does not support deposit, please check");
        }

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
        // 校验银行代码
        // 判断是否需要校验
        boolean exists = assetBankService.existDeposit(assetName, netProtocol);
        if (exists) {
            if (StrUtil.isBlank(bankCode)) {
                throw new ValidateException("The bank code you entered does not exist, please " +
                        "check:bankCode");
            }
            boolean existedDepositBankCode = assetBankService.existDepositBankCode(assetName, netProtocol, bankCode);
            if (!existedDepositBankCode) {
                throw new ValidateException("The bank code you entered does not exist, please " +
                        "check:bankCode");
            }
        }
        return assetDto;
    }

    /**
     * 创建入金记录
     *
     * @param req
     * @return 入金记录ID
     */
    protected DepositRecordEntity createDepositRecord(ProcessDepositReq req) {
        // 保存入金记录
        DepositRecordEntity recordEntity = DepositRecordEntity.valueOf(req);
        depositRecordService.save(recordEntity);
        return recordEntity;
    }

    /**
     * 异常处理
     *
     * @param e
     * @param depositId
     */
    protected void handleException(String depositId, Exception e) {
        depositRecordService.lambdaUpdate()
                .eq(BaseNoLogicalDeleteEntity::getId, depositId)
                .set(DepositRecordEntity::getStatus, DepositRecordStatusEnum.ITEM_4.getCode())
                .set(DepositRecordEntity::getStayReason, e.getMessage())
                .update();
    }

    protected void webhookTrigger(String depositId) {
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
