package com.mc.payment.core.service.manager.withdrawal;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.mc.payment.api.model.dto.WithdrawalEventDto;
import com.mc.payment.api.model.req.WithdrawalReq;
import com.mc.payment.api.model.rsp.WithdrawalRsp;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ExceptionTypeEnum;
import com.mc.payment.common.exception.BusinessException;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.config.AppConfig;
import com.mc.payment.core.service.constant.WebhookEventConstants;
import com.mc.payment.core.service.entity.*;
import com.mc.payment.core.service.factory.PaymentGatewayFactory;
import com.mc.payment.core.service.manager.ChannelCostManager;
import com.mc.payment.core.service.manager.CurrencyRateManager;
import com.mc.payment.core.service.model.dto.MerchantAssetDto;
import com.mc.payment.core.service.model.enums.*;
import com.mc.payment.core.service.model.req.FundWithdrawalAuditReq;
import com.mc.payment.core.service.model.req.GenerateWalletQRCodeReq;
import com.mc.payment.core.service.model.rsp.ConfirmRsp;
import com.mc.payment.core.service.model.rsp.WithdrawalAuditRsp;
import com.mc.payment.core.service.model.rsp.WithdrawalReExecuteRsp;
import com.mc.payment.core.service.service.*;
import com.mc.payment.gateway.PaymentGateway;
import com.mc.payment.gateway.model.req.GatewayWithdrawalReq;
import com.mc.payment.gateway.model.rsp.GatewayWithdrawalRsp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class WithdrawalManagerImpl implements WithdrawalManager {
    protected final IWithdrawalRecordService withdrawalRecordService;
    protected final MerchantChannelAssetService merchantChannelAssetService;
    protected final AssetBankService assetBankService;
    private final IWebhookEventService webhookEventService;
    private final IMerchantService merchantService;
    private final MerchantWalletService merchantWalletService;
    private final AppConfig appConfig;
    private final IAccountService accountService;
    private final IWalletBlacklistService walletBlacklistService;
    private final PayProtocolService payProtocolService;
    private final CurrencyRateManager currencyRateManager;
    private final ChannelCostManager channelCostManager;

    /**
     * 出金申请流程
     * 1. 校验
     * 2. 保存记录
     * 3. 判断是否需要人工审核
     * 4. 执行出金
     * 5. webhook
     * 5. webhook
     *
     * @param merchantId
     * @param merchantName
     * @param req
     * @return
     */
    @Override
    public WithdrawalRsp requestProcess(String merchantId, String merchantName, WithdrawalReq req) {
        log.info("WithdrawalManagerImpl.requestProcess merchantId:{}, merchantName:{}, req:{}", merchantId, merchantName, req);
        WithdrawalRsp rsp = new WithdrawalRsp();
        String recordId = null;
        // 校验
        MerchantAssetDto assetDto = this.requestProcess(merchantId, req);

        try {
            Integer channelSubType = assetDto.getChannelSubType();
            // 保存记录
            WithdrawalRecordEntity recordEntity = WithdrawalRecordEntity.valueOf(req, merchantId, merchantName, channelSubType);
            recordEntity.setFeeAssetName(assetDto.getFeeAssetName());
            withdrawalRecordService.save(recordEntity);
            recordId = recordEntity.getId();
            // 判断是否需要人工审核
            ImmutablePair<Boolean, String> neededAudit = needAudit(merchantId, req);
            if (neededAudit.getLeft()) {
                // 需要审核
                withdrawalRecordService.lambdaUpdate().eq(BaseNoLogicalDeleteEntity::getId, recordId)
                        .set(WithdrawalRecordEntity::getStatus, WithdrawalRecordStatusEnum.ITEM_1.getCode())
                        .set(WithdrawalRecordEntity::getAuditStatus, WithdrawalAuditStatusEnum.ITEM_0.getCode())
                        .update();
                rsp.setRemark(req.getRemark());
                rsp.setTrackingId(req.getTrackingId());
                return rsp;
            } else {
                // 不需要审核则自动通过
                withdrawalRecordService.lambdaUpdate().eq(BaseNoLogicalDeleteEntity::getId, recordId)
                        .set(WithdrawalRecordEntity::getAuditStatus, WithdrawalAuditStatusEnum.ITEM_1.getCode())
                        .update();
            }
            // 执行出金
            ConfirmRsp ConfirmRsp = executeWithdrawal(recordEntity);
            String redirectPageUrl = ConfirmRsp.getRedirectUrl();
            String walletAddress = ConfirmRsp.getWalletAddress();
            //返回出金结果
            rsp.setRemark(req.getRemark());
            rsp.setTrackingId(req.getTrackingId());
            rsp.setWalletAddress(walletAddress);
            rsp.setRedirectPageUrl(redirectPageUrl);
        } catch (BusinessException e) {
            log.info("出金业务异常", e);
            //异常处理
            handleException(recordId, e);
        } catch (Exception e) {
            log.error("出金未知异常", e);
            //异常处理
            handleException(recordId, e);
        } finally {
            log.info("req:{},rsp:{}", JSONUtil.toJsonStr(req), JSONUtil.toJsonStr(rsp));
            // 触发webhook
            webhookTrigger(recordId);
        }
        return rsp;
    }

    /**
     * 出金审核流程
     * <p>
     * 待审核状态才可审核
     *
     * @param req
     */
    @Override
    public WithdrawalAuditRsp auditProcess(FundWithdrawalAuditReq req) {
        log.info("WithdrawalManagerImpl.auditProcess req:{}", req);
        WithdrawalAuditRsp rsp = new WithdrawalAuditRsp();
        WithdrawalRecordEntity recordEntity = withdrawalRecordService.getById(req.getId());
        if (recordEntity == null) {
            throw new BusinessException("The withdrawal record does not exist, please check");
        }
        if (recordEntity.getAuditStatus() != WithdrawalAuditStatusEnum.ITEM_0.getCode()) {
            throw new BusinessException("The withdrawal record has been audited, please check");
        }
        try {
            if (req.getAuditStatus() == WithdrawalAuditStatusEnum.ITEM_1.getCode()) {
                // 审核通过
                withdrawalRecordService.lambdaUpdate().eq(BaseNoLogicalDeleteEntity::getId, req.getId())
                        .set(WithdrawalRecordEntity::getAuditStatus, WithdrawalAuditStatusEnum.ITEM_1.getCode())
                        .update();
                // 执行出金
                ConfirmRsp confirmRsp = executeWithdrawal(recordEntity);
                rsp.setRedirectPageUrl(confirmRsp.getRedirectUrl());
                rsp.setTrackingId(recordEntity.getTrackingId());
            } else {
                // 审核不通过
                withdrawalRecordService.lambdaUpdate()
                        .eq(BaseNoLogicalDeleteEntity::getId, req.getId())
                        .set(WithdrawalRecordEntity::getStatus, WithdrawalRecordStatusEnum.ITEM_5.getCode())
                        .set(WithdrawalRecordEntity::getAuditStatus, WithdrawalAuditStatusEnum.ITEM_2.getCode())
                        .update();
            }
        } catch (BusinessException e) {
            log.info("出金业务异常", e);
            //异常处理
            handleException(recordEntity.getId(), e);
        } catch (Exception e) {
            log.error("出金未知异常", e);
            //异常处理
            handleException(recordEntity.getId(), e);
        } finally {
            // 触发webhook
            webhookTrigger(recordEntity.getId());
        }
        return rsp;
    }

    @Override
    public WithdrawalReExecuteRsp reExecuteProcess(String id) {
        log.info("WithdrawalManagerImpl.reExecuteProcess id:{}", id);
        WithdrawalRecordEntity recordEntity = withdrawalRecordService.getById(id);
        if (recordEntity == null) {
            throw new BusinessException("The withdrawal record does not exist, please check");
        }
        if (recordEntity.getStatus() != WithdrawalRecordStatusEnum.ITEM_2.getCode()) {
            throw new BusinessException("The withdrawal record is not insufficient balance, please check");
        }
        WithdrawalReExecuteRsp rsp = new WithdrawalReExecuteRsp();
        try {
            ConfirmRsp confirmRsp = executeWithdrawal(recordEntity);
            rsp.setRedirectUrl(confirmRsp.getRedirectUrl());
            rsp.setTrackingId(recordEntity.getTrackingId());
            return rsp;
        } catch (BusinessException e) {
            log.info("出金业务异常", e);
            //异常处理
            handleException(recordEntity.getId(), e);
        } catch (Exception e) {
            log.error("出金未知异常", e);
            //异常处理
            handleException(recordEntity.getId(), e);
        } finally {
            // 触发webhook
            webhookTrigger(recordEntity.getId());
        }
        return rsp;
    }


    /**
     * 终止出金流程
     * <p>
     * 余额不足才可终止出金
     *
     * @param id
     */
    @Override
    public void cancelProcess(String id) {
        log.info("WithdrawalManagerImpl.cancelProcess id:{}", id);
        WithdrawalRecordEntity recordEntity = withdrawalRecordService.getById(id);
        if (recordEntity == null) {
            throw new BusinessException("The withdrawal record does not exist, please check");
        }
        if (recordEntity.getStatus() != WithdrawalRecordStatusEnum.ITEM_2.getCode()) {
            throw new BusinessException("The withdrawal record is not insufficient balance, please check");
        }
        try {
            withdrawalRecordService.lambdaUpdate()
                    .eq(BaseNoLogicalDeleteEntity::getId, id)
                    .eq(WithdrawalRecordEntity::getStatus, WithdrawalRecordStatusEnum.ITEM_2.getCode())
                    .set(WithdrawalRecordEntity::getStatus, WithdrawalRecordStatusEnum.ITEM_7.getCode())
                    .set(WithdrawalRecordEntity::getAuditStatus, WithdrawalAuditStatusEnum.ITEM_3.getCode())
                    .update();
            // 解冻资产  终止出金不需要对资产进行解冻
            //withdrawalRecordService.unfreezeWallet(recordEntity);
        } catch (BusinessException e) {
            log.info("出金业务异常", e);
            //异常处理
            handleException(recordEntity.getId(), e);
        } catch (Exception e) {
            log.error("出金未知异常", e);
            //异常处理
            handleException(recordEntity.getId(), e);
        } finally {
            // 触发webhook
            webhookTrigger(recordEntity.getId());
        }
    }
//====

    private ConfirmRsp executeWithdrawal(WithdrawalRecordEntity recordEntity) {
        // 获取可用钱包 冻结相关资产
        MerchantWalletEntity walletEntity =
                merchantWalletService.getWithdrawWalletAndFreeze(recordEntity.getMerchantId()
                        , recordEntity.getChannelSubType(), recordEntity.getAssetType()
                        , recordEntity.getAssetName(), recordEntity.getNetProtocol(),
                        recordEntity.getAmount());
        if (walletEntity == null) {
            //余额不足触发告警
            withdrawalRecordService.balanceAlert(recordEntity);
            throw new BusinessException(ExceptionTypeEnum.INSUFFICIENT_BALANCE, "余额不足");
        }
        // 是否是加密货币
        boolean isCrypto = recordEntity.getAssetType() == AssetTypeEnum.CRYPTO_CURRENCY.getCode();
        //计算汇率
        ImmutablePair<BigDecimal, BigDecimal> ratePair = this.calculateRate(isCrypto, recordEntity.getAssetName(),
                recordEntity.getFeeAssetName(), recordEntity.getTargetCurrency());

        // 计算费用 通道费使用本币币种的汇率,它和手续费币种没关系
        BigDecimal fee = channelCostManager.channelCostCalculator(BusinessActionEnum.WITHDRAWALS.getCode(),
                recordEntity.getChannelSubType(),
                recordEntity.getAssetName(),
                recordEntity.getNetProtocol(),
                recordEntity.getAmount(),
                ratePair.left);

        // 更新出金记录
        withdrawalRecordService.lambdaUpdate()
                .eq(WithdrawalRecordEntity::getId, recordEntity.getId())
                .set(WithdrawalRecordEntity::getChannelFee, fee)
                .set(WithdrawalRecordEntity::getSourceAddress, walletEntity.getWalletAddress())
                .set(WithdrawalRecordEntity::getAccountId, walletEntity.getAccountId())
                .set(WithdrawalRecordEntity::getWalletId, walletEntity.getId())
                .set(WithdrawalRecordEntity::getAddrBalance, walletEntity.getBalance())
                .set(WithdrawalRecordEntity::getRate, ratePair.left)
                .set(WithdrawalRecordEntity::getFeeRate, ratePair.right)
                .update();
        // 获取最新记录
        recordEntity = withdrawalRecordService.getById(recordEntity.getId());

        RetResult<GatewayWithdrawalRsp> withdrawalRspRetResult;

        if (recordEntity.getChannelSubType() == ChannelSubTypeEnum.FIRE_BLOCKS.getCode()) {
            MerchantAssetDto assetDto = merchantChannelAssetService.getAssetConfigOne(recordEntity.getMerchantId(),
                    recordEntity.getAssetType(), recordEntity.getAssetName(), recordEntity.getNetProtocol());
            AccountEntity accountEntity = accountService.getById(walletEntity.getAccountId());
            try {
                withdrawalRspRetResult = withdrawalRecordService.fireblocksWithdrawal(recordEntity, assetDto.getChannelAssetName(), accountEntity);
            } catch (Exception e) {
                //任何异常都解冻钱包金额
                withdrawalRecordService.unfreezeWallet(recordEntity);
                throw e;
            }
        } else {
            // 调用上游接口申请出金
            GatewayWithdrawalReq gatewayWithdrawalReq = new GatewayWithdrawalReq();
            gatewayWithdrawalReq.setTransactionId(recordEntity.getId());
            gatewayWithdrawalReq.setChannelId(walletEntity.getWalletAddress().split("_")[1]);
            gatewayWithdrawalReq.setAmount(recordEntity.getAmount().toString());
            gatewayWithdrawalReq.setAddress(recordEntity.getDestinationAddress());
            gatewayWithdrawalReq.setBankCode(recordEntity.getBankCode());
            gatewayWithdrawalReq.setBankName(recordEntity.getBankName());
            gatewayWithdrawalReq.setAccountName(recordEntity.getAccountName());
            gatewayWithdrawalReq.setBankNum(recordEntity.getBankNum());
            gatewayWithdrawalReq.setAssetName(recordEntity.getAssetName());
            gatewayWithdrawalReq.setNetProtocol(recordEntity.getNetProtocol());
            gatewayWithdrawalReq.setBankNum(recordEntity.getBankNum());
            if (!Objects.isNull(recordEntity.getExtraMap())) {
                gatewayWithdrawalReq.setExtraMap(JSONUtil.toBean(recordEntity.getExtraMap(), Map.class));
            }
            gatewayWithdrawalReq.setCallbackUrl(appConfig.getPaymentRealend() + ChannelSubTypeEnum.getEnumByCode(recordEntity.getChannelSubType()).getCallUrl());

            PaymentGateway paymentGateway = PaymentGatewayFactory.get(recordEntity.getChannelSubType());
            try {
                withdrawalRspRetResult = paymentGateway.withdrawal(gatewayWithdrawalReq);
            } catch (Exception e) {
                //任何异常都解冻钱包金额
                withdrawalRecordService.unfreezeWallet(recordEntity);
                throw e;
            }
        }


        // 更新出金记录状态
        // 若调用上游接口失败,则解冻资产

        if (!StrUtil.equals("200", String.valueOf(withdrawalRspRetResult.getCode()))) {

            String message = withdrawalRspRetResult.getMsg();
            if (withdrawalRspRetResult.getData() != null && withdrawalRspRetResult.getData().getStatus() == 2) {
                message = "上游通道" + WithdrawalRecordStatusEnum.ITEM_2.getDesc();
                withdrawalRecordService.unfreezeWallet(recordEntity);
                throw new BusinessException(ExceptionTypeEnum.INSUFFICIENT_BALANCE, message);
            }
            withdrawalRecordService.unfreezeWallet(recordEntity);
            throw new BusinessException(message);
        } else {
            withdrawalRecordService.lambdaUpdate()
                    .set(WithdrawalRecordEntity::getStatus, WithdrawalRecordStatusEnum.ITEM_3.getCode())
                    .set(WithdrawalRecordEntity::getTransactionId, withdrawalRspRetResult.getData().getChannelTransactionId())
                    .eq(WithdrawalRecordEntity::getId, recordEntity.getId())
                    .update();
        }
        ConfirmRsp confirmRsp = new ConfirmRsp();
        confirmRsp.setRedirectUrl(withdrawalRspRetResult.getData().getRedirectPageUrl());
        confirmRsp.setWalletAddress(walletEntity.getWalletAddress());
        if (recordEntity.getAssetType() == AssetTypeEnum.CRYPTO_CURRENCY.getCode()) {
            String walletQRCode =
                    merchantWalletService.generateWalletQRCode(new GenerateWalletQRCodeReq(recordEntity.getDestinationAddress()));
            confirmRsp.setWalletQRCode(walletQRCode);
        }
        return confirmRsp;
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
        return ImmutablePair.of(rate, feeRate);
    }

    private MerchantAssetDto requestProcess(String merchantId, WithdrawalReq req) {
        req.validate();
        // 跟踪id不可重复校验
        this.validateTrackingId(merchantId, req.getTrackingId());

        MerchantAssetDto assetDto = this.validateAssetInfo(merchantId, req.getAssetType(), req.getAssetName(), req.getNetProtocol(), req.getAmount(), req.getBankCode());
        this.validateAddress(req.getAssetType(), req.getNetProtocol(), req.getAddress());
        return assetDto;
    }

    private void validateAddress(Integer assetType, String netProtocol, String address) {
        // 加密货币,需要校验地址是否合法,正则和反洗钱
        if (assetType == AssetTypeEnum.CRYPTO_CURRENCY.getCode()) {
            // 校验地址是否合法
            // 正则
            boolean matches = payProtocolService.checkAddressMatches(netProtocol, address);
            if (!matches) {
                // req.getAddress() + ",该出金地址不合法,请检查"
                throw new ValidateException("The withdrawal address is illegal, please check");
            }
            // 反洗钱功能
            boolean black = walletBlacklistService.isBlacklist(address);
            if (black) {
                // req.getAddress() + ",该出金地址被风控，无法执行出金请求，请更换地址后重试。"
                throw new ValidateException("The withdrawal address is risk controlled and cannot be executed, please change the address and try again.");
            }
        }
    }

    private MerchantAssetDto validateAssetInfo(String merchantId, Integer assetType, String assetName,
                                               String netProtocol,
                                               BigDecimal amount,
                                               String bankCode) {
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
        if (assetDto == null) {
            // 您输入的资产信息不存在, 请检查
            throw new ValidateException("The netProtocol not available, please check");
        }
        if (!Objects.equals(assetDto.getAssetType(), assetType)) {
            // 您输入的资产信息资产类型错误, 请检查
            throw new ValidateException("The assetType not available, please check");
        }
        if (assetDto.getWithdrawalStatus() != BooleanStatusEnum.ITEM_1.getCode()) {
            // 该资产不支持出金,请检查
            throw new ValidateException("The asset does not support withdrawal, please check");
        }
        this.validateAmount(assetDto, amount);
        this.validateBankCode(assetDto, bankCode);
        return assetDto;
    }

    private void validateBankCode(MerchantAssetDto assetDto, String bankCode) {
        // 校验银行代码
        // 判断是否需要校验
        boolean exists = assetBankService.existWithdraw(assetDto.getAssetName(), assetDto.getNetProtocol());
        if (exists) {
            if (StrUtil.isBlank(bankCode)) {
                throw new ValidateException("The bank code you entered does not exist, please " +
                        "check:bankCode");
            }
            boolean existedWithdrawBankCode = assetBankService.existWithdrawBankCode(assetDto.getAssetName(),
                    assetDto.getNetProtocol(), bankCode);
            if (!existedWithdrawBankCode) {
                throw new ValidateException("The bank code you entered does not exist, please " +
                        "check:bankCode");
            }
        }
    }

    private void validateAmount(MerchantAssetDto assetDto, BigDecimal amount) {
        // 校验金额是否符合要求
        BigDecimal minAmount = assetDto.getMinWithdrawalAmount();
        BigDecimal maxAmount = assetDto.getMaxWithdrawalAmount();
        if (minAmount != null && amount.compareTo(minAmount) < 0) {
            // 出金金额应该大于最小出金金额,请检查
            throw new ValidateException("The withdrawal amount should be greater than the minimum withdrawal" +
                    " amount, please check:" + minAmount.stripTrailingZeros().toPlainString());
        }
        if (maxAmount != null && maxAmount.compareTo(BigDecimal.ZERO) != 0 && amount.compareTo(maxAmount) > 0) {
            // 出金金额应该小于最大出金金额,请检查
            throw new ValidateException("The withdrawal amount should be lesser than the max withdrawal" +
                    " amount, please check:" + maxAmount.stripTrailingZeros().toPlainString());
        }
    }

    /**
     * 跟踪id不可重复校验
     *
     * @param merchantId
     * @param trackingId
     */
    private void validateTrackingId(String merchantId, String trackingId) {
        boolean trackingIdExists = withdrawalRecordService.lambdaQuery()
                .eq(WithdrawalRecordEntity::getMerchantId, merchantId)
                .eq(WithdrawalRecordEntity::getTrackingId, trackingId)
                .exists();
        if (trackingIdExists) {
            throw new ValidateException("trackingId Repeat, please check:" + trackingId);
        }
    }

    /**
     * 判断是否需要审核
     * 1. 商户配置了需要审核
     * 2. 频繁出金需要审核:同一个商户下相同用户1小时内频繁出金,需要审核
     *
     * @param req
     * @return
     */
    private ImmutablePair<Boolean, String> needAudit(String merchantId, WithdrawalReq req) {
        MerchantEntity merchantEntity = merchantService.getById(merchantId);
        boolean b = merchantEntity.getWithdrawalAudit() == BooleanStatusEnum.ITEM_1.getCode();
        if (b) {
            return ImmutablePair.of(true, "商户配置了需要审核");
        }
        if (StrUtil.isBlank(req.getUserId())) {
            // 用户id为空,无法判断是否频繁出金
            return ImmutablePair.of(false, "");
        }
        // 频繁出金需要审核
        int withdrawalNum = withdrawalRecordService.lambdaQuery()
                .eq(WithdrawalRecordEntity::getMerchantId, merchantId)
                .eq(WithdrawalRecordEntity::getUserId, req.getUserId())
                .apply("create_time >= DATE_SUB(NOW(),INTERVAL 1 HOUR)").count().intValue();
        if (withdrawalNum > 3) {
            return ImmutablePair.of(true, "出金一小时内大于3次");
        }
        return ImmutablePair.of(false, "不需要审核");
    }

    private void handleException(String recordId, Exception e) {
        int status = WithdrawalRecordStatusEnum.ITEM_6.getCode();
        if (e instanceof BusinessException businessException) {
            if (businessException.getExceptionTypeEnum() == ExceptionTypeEnum.INSUFFICIENT_BALANCE) {
                status = WithdrawalRecordStatusEnum.ITEM_2.getCode();
            }
        }
        String stayReason = e.getMessage();
        // 超过128 则截断
        if (stayReason.length() > 128) {
            log.info("stayReason:{}", stayReason);
            stayReason = stayReason.substring(0, 128);
        }
        withdrawalRecordService.lambdaUpdate()
                .eq(BaseNoLogicalDeleteEntity::getId, recordId)
                .set(WithdrawalRecordEntity::getStatus, status)
                .set(WithdrawalRecordEntity::getStayReason, stayReason)
                .update();
    }

    private void webhookTrigger(String recordId) {
        if (recordId == null) {
            return;
        }
        WithdrawalRecordEntity recordEntity = withdrawalRecordService.getById(recordId);
        WebhookEventEntity webhookEventEntity = new WebhookEventEntity();
        webhookEventEntity.setEvent(WebhookEventConstants.WITHDRAWAL_EVENT);
        webhookEventEntity.setData(JSONUtil.toJsonStr(new WithdrawalEventDto(recordEntity.getTrackingId()
                , recordEntity.getStatus(), recordEntity.getAmount(), recordEntity.getStayReason())));
        webhookEventEntity.setTrackingId(recordEntity.getTrackingId());
        webhookEventEntity.setWebhookUrl(recordEntity.getWebhookUrl());
        webhookEventEntity.setMerchantId(recordEntity.getMerchantId());
        webhookEventService.asyncSendWebhookEvent(webhookEventEntity);
    }
}
