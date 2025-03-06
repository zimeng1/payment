package com.mc.payment.core.service.facade;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mc.payment.api.model.dto.WithdrawalEventDto;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ExceptionTypeEnum;
import com.mc.payment.common.exception.BusinessException;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.config.AppConfig;
import com.mc.payment.core.service.constant.WebhookEventConstants;
import com.mc.payment.core.service.entity.*;
import com.mc.payment.core.service.factory.PaymentGatewayFactory;
import com.mc.payment.core.service.manager.DepositManagerOld;
import com.mc.payment.core.service.manager.deposit.DepositManager;
import com.mc.payment.core.service.manager.deposit.DepositTemplate;
import com.mc.payment.core.service.model.dto.AssetDto;
import com.mc.payment.core.service.model.dto.MerchantAssetDto;
import com.mc.payment.core.service.model.enums.AssetTypeEnum;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.enums.DepositRecordStatusEnum;
import com.mc.payment.core.service.model.enums.WithdrawalRecordStatusEnum;
import com.mc.payment.core.service.model.req.DepositConfirmReq;
import com.mc.payment.core.service.model.req.DepositStatusReq;
import com.mc.payment.core.service.model.req.GenerateWalletQRCodeReq;
import com.mc.payment.core.service.model.req.WithdrawalConfirmReq;
import com.mc.payment.core.service.model.rsp.ConfirmRsp;
import com.mc.payment.core.service.model.rsp.DepositInfoRsp;
import com.mc.payment.core.service.model.rsp.WithDrawalInfoRsp;
import com.mc.payment.core.service.service.*;
import com.mc.payment.core.service.util.AppSecureUtil;
import com.mc.payment.core.service.util.MonitorLogUtil;
import com.mc.payment.gateway.PaymentGateway;
import com.mc.payment.gateway.model.req.GatewayWithdrawalReq;
import com.mc.payment.gateway.model.rsp.GatewayWithdrawalRsp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentPageServiceFacadeImpl implements IPaymentPageServiceFacade {
    private final AppConfig appConfig;
    private final IDepositRecordService depositRecordService;
    private final PaymentPageService paymentPageService;
    private final IWithdrawalRecordService withdrawalRecordService;
    private final MerchantWalletService merchantWalletService;
    private final IWebhookEventService webhookEventService;
    private final AssetBankService assetBankService;
    private final MerchantChannelAssetService merchantChannelAssetService;
    private final DepositManagerOld depositManagerOld;
    private final DepositManager depositManager;
    /**
     * 入金策略标识前缀
     */
    private static final String DEPOSIT_STRATEGY_PREFIX = "DepositStrategy_";
    private final Map<String, DepositTemplate> depositStrategyMap;

    @Override
    public RetResult<DepositInfoRsp> depositInfo(String encryptId) {
        String id = AppSecureUtil.decrypt(encryptId, appConfig.getCashierKey());
        PaymentPageEntity paymentPageEntity = paymentPageService.getById(id);
        if (paymentPageEntity == null) {
            // 页面信息不存在
            throw new IllegalArgumentException("page not exist");
        }
        DepositRecordEntity recordEntity = getDepositRecordEntity(paymentPageEntity.getTrackingId(),
                paymentPageEntity.getMerchantId());

        // 3.返回收银信息
        DepositInfoRsp rsp = new DepositInfoRsp();
        rsp.setAmount(recordEntity.getAmount());
        rsp.setAssetName(recordEntity.getAssetName());
        rsp.setNetProtocol(recordEntity.getNetProtocol());
        rsp.setAssetNet(recordEntity.getAssetNet());
        rsp.setExpireTimestamp(recordEntity.getExpireTimestamp());
        rsp.setPageTextJson(paymentPageEntity.getPageTextJson());
        rsp.setAssetType(recordEntity.getAssetType());
        rsp.setSuccessPageUrl(recordEntity.getSuccessPageUrl());
        rsp.setBankCode(recordEntity.getBankCode());

        // 查询入金需要的资产列表和银行列表
        Integer assetType = recordEntity.getAssetType();
        List<AssetDto> assetDtos = merchantChannelAssetService.queryAssetList(recordEntity.getMerchantId(), assetType,
                true);

        rsp.setAssetList(assetDtos);
        return RetResult.data(rsp);
    }

    private @NotNull DepositRecordEntity getDepositRecordEntity(String trackingId, String merchantId) {

        // 2.查询入金记录
        DepositRecordEntity recordEntity = depositRecordService.getOne(merchantId, trackingId);
        if (recordEntity == null) {
            log.error("入金记录不存在, trackingId:{}, merchantId:{}", trackingId, merchantId);
            // 入金记录不存在
            throw new IllegalArgumentException("Deposit record does not exist");
        }
        if (recordEntity.getStatus() != DepositRecordStatusEnum.ITEM_0.getCode() && recordEntity.getStatus() != DepositRecordStatusEnum.ITEM_1.getCode()) {
            log.error("入金记录状态异常, trackingId:{}, status:{}", trackingId, recordEntity.getStatus());
            //入金记录状态异常
            throw new IllegalArgumentException("Deposit record status is abnormal");
        }
        return recordEntity;
    }

    @Override
    public RetResult<ConfirmRsp> depositConfirmOld(DepositConfirmReq req) {
        String id = AppSecureUtil.decrypt(req.getEncryptId(), appConfig.getCashierKey());
        PaymentPageEntity paymentPageEntity = paymentPageService.getById(id);
        if (paymentPageEntity == null) {
            // 页面信息不存在
            throw new IllegalArgumentException("Page information does not exist");
        }
        // 重复支付校验
        DepositRecordEntity recordEntity = getDepositRecordEntity(paymentPageEntity.getTrackingId(),
                paymentPageEntity.getMerchantId());
        if (recordEntity.getStatus().equals(DepositRecordStatusEnum.ITEM_4.getCode())) {
            // 下单失败的的不让重试
            throw new IllegalArgumentException("The order has failed and cannot be retried");
        }
        if (recordEntity.getExpireTimestamp() < System.currentTimeMillis()) {
            // 订单已过期
            throw new IllegalArgumentException("The order has expired");
        }
        if (!StrUtil.equals(req.getAssetName(), recordEntity.getAssetName())) {
            // 资产名称不正确
            throw new IllegalArgumentException("Asset name is incorrect!");
        }
        if (recordEntity.getAmount().compareTo(req.getAmount()) != 0) {
            // 支付金额不正确
            throw new IllegalArgumentException("Payment amount is incorrect!");
        }
        // 补全入金记录信息
        if (StrUtil.isBlank(recordEntity.getNetProtocol())) {
            recordEntity.setNetProtocol(req.getNetProtocol());
            // 选择支付通道
            if (recordEntity.getChannelSubType() == ChannelSubTypeEnum.UNDECIDED.getCode()) {
                ChannelSubTypeEnum channelSubTypeEnum = merchantChannelAssetService.choosePaymentChannel(recordEntity.getMerchantId(),
                        recordEntity.getAssetType(),
                        req.getAssetName(),
                        req.getNetProtocol(), true);
                recordEntity.setChannelSubType(channelSubTypeEnum.getCode());
            }

            depositRecordService.lambdaUpdate().eq(BaseNoLogicalDeleteEntity::getId, recordEntity.getId())
                    .set(DepositRecordEntity::getNetProtocol, req.getNetProtocol())
                    .set(DepositRecordEntity::getChannelSubType, recordEntity.getChannelSubType())
                    .update();
        }
        if (StrUtil.isBlank(recordEntity.getBankCode())) {
            // 判断是否需要校验
            boolean exists = assetBankService.existDeposit(req.getAssetName(), req.getNetProtocol());
            if (exists) {
                // 校验银行代码
                boolean existedDepositBankCode = assetBankService.existDepositBankCode(req.getAssetName(),
                        req.getNetProtocol(), req.getBankCode());
                if (!existedDepositBankCode) {
                    throw new IllegalArgumentException("The bank code you entered does not exist, please " +
                            "check:bankCode");
                }
                recordEntity.setBankCode(req.getBankCode());
                depositRecordService.lambdaUpdate().eq(BaseNoLogicalDeleteEntity::getId, recordEntity.getId())
                        .set(DepositRecordEntity::getBankCode, req.getBankCode())
                        .update();
            }
        }

        // 选择支付通道执行支付操作
        ConfirmRsp ConfirmRsp = null;
        boolean failFlag = false;
        try {
            ConfirmRsp = depositManagerOld.executeDeposit(recordEntity.getId());
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            /**
             * 说明：
             * 1.判断是否是三方支付请求失败异常
             * 2.正确的做法：需要自定义业务异常，通过业务异常code做判断，而不是通过异常信息做判断
             */
            if (StringUtils.isNotBlank(message) && message.contains("Payment exception")) {
                log.error("depositConfirm fail:", e);
                failFlag = true;
            } else {
                throw e;
            }
        }

        if (failFlag) {
            //记录失败原因
            depositRecordService.lambdaUpdate().eq(DepositRecordEntity::getTrackingId, paymentPageEntity.getTrackingId())
                    .eq(DepositRecordEntity::getMerchantId, paymentPageEntity.getMerchantId())
                    .set(DepositRecordEntity::getStatus, DepositRecordStatusEnum.ITEM_4.getCode())
                    .update();
            log.info("depositConfirm fail, trackingId:{} ", paymentPageEntity.getTrackingId());
            throw new IllegalArgumentException("第三方接口报错， Payment fail!");
        }

        return RetResult.data(ConfirmRsp);
    }

    @Override
    public RetResult<ConfirmRsp> depositConfirm(DepositConfirmReq req) {
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


        Integer channelSubType = recordEntity.getChannelSubType();

        ConfirmRsp confirmRsp = depositStrategyMap.get(DEPOSIT_STRATEGY_PREFIX + channelSubType)
                .processExecuteDeposit(req, recordEntity);
        return RetResult.data(confirmRsp);
    }

    @Override
    public RetResult<ConfirmRsp> withdrawConfirm(WithdrawalConfirmReq req) {
        String id = AppSecureUtil.decrypt(req.getEncryptId(), appConfig.getCashierKey());
        PaymentPageEntity paymentPageEntity = paymentPageService.getById(id);
        if (paymentPageEntity == null) {
            throw new IllegalArgumentException("页面信息不存在!");
        }
        // 重复支付校验
        WithdrawalRecordEntity recordEntity = getWithdrawalRecordEntity(paymentPageEntity.getTrackingId(),
                paymentPageEntity.getMerchantId());

        // 重复点击支付按钮,且是未支付状态,且已经申请过了,直接返回支付页面url
        if (StrUtil.isNotBlank(paymentPageEntity.getChannelPageUrl())) {
            return RetResult.data(new ConfirmRsp(paymentPageEntity.getChannelPageUrl()));
        }
        recordEntity.setBankCode(req.getBankCode());
        if (recordEntity.getAssetType() == AssetTypeEnum.CRYPTO_CURRENCY.getCode()) {
            return RetResult.error("暂不支持加密货币支付!");
        }
        //支付类型校验

        MerchantAssetDto merchantAssetDto = merchantChannelAssetService.getAssetConfigOne(recordEntity.getMerchantId(),
                recordEntity.getAssetType(), req.getAssetName(), req.getNetProtocol());
        if (merchantAssetDto == null) {
            // 您输入的资产信息不可用, 请检查
            throw new IllegalArgumentException("The assetName and netProtocol not available, please check");
        }
        //更新出金申请单
        recordEntity.setAssetNet(merchantAssetDto.getAssetNet());
        recordEntity.setNetProtocol(req.getNetProtocol());
        withdrawalRecordService.updateById(recordEntity);
        String redirectPageUrl = this.fundWithDrawal(recordEntity);
        return RetResult.data(new ConfirmRsp(redirectPageUrl));
    }

    @Override
    public RetResult<WithDrawalInfoRsp> withdrawInfo(String encryptId) {
        String id = AppSecureUtil.decrypt(encryptId, appConfig.getCashierKey());
        PaymentPageEntity paymentPageEntity = paymentPageService.getById(id);
        if (paymentPageEntity == null) {
            throw new IllegalArgumentException("页面信息不存在");
        }
        WithdrawalRecordEntity recordEntity = getWithdrawalRecordEntity(paymentPageEntity.getTrackingId(),
                paymentPageEntity.getMerchantId());

        // 3.返回收银信息
        WithDrawalInfoRsp rsp = new WithDrawalInfoRsp();
        rsp.setAmount(recordEntity.getAmount());
        rsp.setAssetName(recordEntity.getAssetName());
        rsp.setNetProtocol(recordEntity.getNetProtocol());
        rsp.setAssetNet(recordEntity.getAssetNet());
        rsp.setPageTextJson(paymentPageEntity.getPageTextJson());
        // 查询出金需要的资产列表和银行列表
        Integer assetType = recordEntity.getAssetType();
        List<AssetDto> assetDtos = merchantChannelAssetService.queryAssetList(recordEntity.getMerchantId(), assetType
                , false);
        rsp.setAssetList(assetDtos);
        return RetResult.data(rsp);
    }


    private @NotNull WithdrawalRecordEntity getWithdrawalRecordEntity(String trackingId, String merchantId) {

        // 2.查询出金记录
        WithdrawalRecordEntity recordEntity = withdrawalRecordService.getOne(merchantId, trackingId);
        if (recordEntity == null) {
            log.error("出金记录不存在, trackingId:{}, merchantId:{}", trackingId, merchantId);
            throw new IllegalArgumentException("出金记录不存在!");
        }
        if (recordEntity.getStatus() != WithdrawalRecordStatusEnum.ITEM_0.getCode()) {
            log.error("出金记录状态异常, trackingId:{}, status:{}", trackingId, recordEntity.getStatus());
            throw new IllegalArgumentException("出金记录状态异常!");
        }
        return recordEntity;
    }

    @Override
    public String fundWithDrawal(WithdrawalRecordEntity withdrawalRecord) {
        log.info("出金接口withdrawalRecord:{}", withdrawalRecord);
        // 获取可用的钱包并冻结出金金额
        MerchantWalletEntity walletEntity =
                merchantWalletService.getWithdrawWalletAndFreeze(withdrawalRecord.getMerchantId()
                        , withdrawalRecord.getChannelSubType(), withdrawalRecord.getAssetType()
                        , withdrawalRecord.getAssetName(), withdrawalRecord.getNetProtocol(),
                        withdrawalRecord.getAmount());
        if (walletEntity == null) {
            log.error("Insufficient balance!");
            //余额不足触发告警
            withdrawalRecordService.balanceAlert(withdrawalRecord);
            withdrawalRecordService.lambdaUpdate()
                    .set(WithdrawalRecordEntity::getStatus, WithdrawalRecordStatusEnum.ITEM_2.getCode())
                    .eq(WithdrawalRecordEntity::getId, withdrawalRecord.getId())
                    .update();
            this.legalDrawalWebHook(withdrawalRecord);
            //余额不足状态订单增多指标监控
            JSONObject InsBalanceMonitor = new JSONObject().put("Service", "payment").put("MonitorKey",
                            "InsBalanceMonitor")
                    .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
            MonitorLogUtil.log(InsBalanceMonitor);
            throw new BusinessException(ExceptionTypeEnum.INSUFFICIENT_BALANCE, "余额不足");
        }

        //更新出金记录
        withdrawalRecordService.lambdaUpdate().set(WithdrawalRecordEntity::getSourceAddress, walletEntity.getWalletAddress())
                .set(WithdrawalRecordEntity::getAccountId, walletEntity.getAccountId())
                .set(WithdrawalRecordEntity::getWalletId, walletEntity.getId())
                .eq(WithdrawalRecordEntity::getId, withdrawalRecord.getId())
                .update();
        withdrawalRecord = withdrawalRecordService.getById(withdrawalRecord.getId());

        Integer channelSubType = withdrawalRecord.getChannelSubType();

        //构建出金req对象
        GatewayWithdrawalReq gatewayWithdrawalReq = generateGatewayWithdrawalReq(withdrawalRecord, walletEntity);

        RetResult<GatewayWithdrawalRsp> withdrawalRspRetResult;

        PaymentGateway paymentGateway = PaymentGatewayFactory.get(channelSubType);

        try {
            gatewayWithdrawalReq.setCallbackUrl(appConfig.getPaymentRealend() + ChannelSubTypeEnum.getEnumByCode(channelSubType).getCallUrl());
            //出金通道具体逻辑
            withdrawalRspRetResult = paymentGateway.withdrawal(gatewayWithdrawalReq);
        } catch (Exception e) {
            //任何异常都解冻钱包金额
            withdrawalRecordService.unfreezeWallet(withdrawalRecord);
            throw e;
        }

        if (!StrUtil.equals("200", String.valueOf(withdrawalRspRetResult.getCode()))) {

            String message = withdrawalRspRetResult.getMsg();
            //更新出金申请单
            int withdrawalStatus = WithdrawalRecordStatusEnum.ITEM_6.getCode();
            if (withdrawalRspRetResult.getData() != null && withdrawalRspRetResult.getData().getStatus() == 2) {
                withdrawalStatus = WithdrawalRecordStatusEnum.ITEM_2.getCode();
                message = "上游通道" + WithdrawalRecordStatusEnum.ITEM_2.getDesc();
            }
            withdrawalRecordService.lambdaUpdate()
                    .set(WithdrawalRecordEntity::getStatus, withdrawalStatus)
                    .set(WithdrawalRecordEntity::getStayReason, withdrawalRspRetResult.getMsg() == null ? "" : withdrawalRspRetResult.getMsg())
                    .eq(WithdrawalRecordEntity::getId, withdrawalRecord.getId())
                    .update();

            withdrawalRecordService.unfreezeWallet(withdrawalRecord);
            this.legalDrawalWebHook(withdrawalRecord);
            throw new BusinessException(message);
        } else {
            withdrawalRecordService.lambdaUpdate()
                    .set(WithdrawalRecordEntity::getStatus, WithdrawalRecordStatusEnum.ITEM_3.getCode())
                    .set(WithdrawalRecordEntity::getTransactionId, withdrawalRspRetResult.getData().getChannelTransactionId())
                    .eq(WithdrawalRecordEntity::getId, withdrawalRecord.getId())
                    .update();
        }
        this.legalDrawalWebHook(withdrawalRecord);
        return withdrawalRspRetResult.getData().getRedirectPageUrl();
    }

    @Override
    public String fundWithDrawalHandle(WithdrawalRecordEntity withdrawalRecord) {
        return "";
    }

    private @NotNull GatewayWithdrawalReq generateGatewayWithdrawalReq(WithdrawalRecordEntity withdrawalRecord, MerchantWalletEntity walletEntity) {
        GatewayWithdrawalReq gatewayWithdrawalReq = new GatewayWithdrawalReq();
        gatewayWithdrawalReq.setTransactionId(withdrawalRecord.getId());
        gatewayWithdrawalReq.setChannelId(walletEntity.getWalletAddress().split("_")[1]);
        gatewayWithdrawalReq.setAmount(withdrawalRecord.getAmount().toString());
        gatewayWithdrawalReq.setAddress(withdrawalRecord.getDestinationAddress());
        gatewayWithdrawalReq.setBankCode(withdrawalRecord.getBankCode());
        gatewayWithdrawalReq.setBankName(withdrawalRecord.getBankName());
        gatewayWithdrawalReq.setAccountName(withdrawalRecord.getAccountName());
        gatewayWithdrawalReq.setBankNum(withdrawalRecord.getBankNum());
        gatewayWithdrawalReq.setAssetName(withdrawalRecord.getAssetName());
        gatewayWithdrawalReq.setNetProtocol(withdrawalRecord.getNetProtocol());
        gatewayWithdrawalReq.setBankNum(withdrawalRecord.getBankNum());
        if (!Objects.isNull(withdrawalRecord.getExtraMap())) {
            gatewayWithdrawalReq.setExtraMap(JSONUtil.toBean(withdrawalRecord.getExtraMap(), Map.class));
        }
        return gatewayWithdrawalReq;
    }

    private @NotNull GatewayWithdrawalReq generateGatewayWithdrawalReq(WithdrawalRecordEntity withdrawalRecord, MerchantWalletEntity walletEntity, Map<String, Object> extraMap) {
        GatewayWithdrawalReq gatewayWithdrawalReq = new GatewayWithdrawalReq();
        gatewayWithdrawalReq.setTransactionId(withdrawalRecord.getId());
        gatewayWithdrawalReq.setChannelId(walletEntity.getWalletAddress().split("_")[1]);
        gatewayWithdrawalReq.setAmount(withdrawalRecord.getAmount().toString());
        gatewayWithdrawalReq.setAddress(withdrawalRecord.getDestinationAddress());
        gatewayWithdrawalReq.setBankCode(withdrawalRecord.getBankCode());
        gatewayWithdrawalReq.setBankName(withdrawalRecord.getBankName());
        gatewayWithdrawalReq.setAccountName(withdrawalRecord.getAccountName());
        gatewayWithdrawalReq.setBankNum(withdrawalRecord.getBankNum());
        gatewayWithdrawalReq.setAssetName(withdrawalRecord.getAssetName());
        gatewayWithdrawalReq.setNetProtocol(withdrawalRecord.getNetProtocol());
        gatewayWithdrawalReq.setBankNum(withdrawalRecord.getBankNum());
        gatewayWithdrawalReq.setExtraMap(JSONUtil.toBean(withdrawalRecord.getExtraMap(), Map.class));
        return gatewayWithdrawalReq;
    }

    //触发法币出金webhook
    public void legalDrawalWebHook(WithdrawalRecordEntity withdrawalRecord) {
        WebhookEventEntity webhookEventEntity = new WebhookEventEntity();
        webhookEventEntity.setEvent(WebhookEventConstants.WITHDRAWAL_EVENT);
        webhookEventEntity.setData(JSONUtil.toJsonStr(new WithdrawalEventDto(withdrawalRecord.getTrackingId()
                , withdrawalRecord.getStatus(), withdrawalRecord.getAmount(), withdrawalRecord.getStayReason())));
        webhookEventEntity.setTrackingId(withdrawalRecord.getTrackingId());
        webhookEventEntity.setWebhookUrl(withdrawalRecord.getWebhookUrl());
        webhookEventEntity.setMerchantId(withdrawalRecord.getMerchantId());
        webhookEventService.asyncSendWebhookEvent(webhookEventEntity);
    }

    /**
     * 入金状态查询
     *
     * @param req
     * @return
     */
    @Override
    public Integer depositStatus(DepositStatusReq req) {
        String id = AppSecureUtil.decrypt(req.getEncryptId(), appConfig.getCashierKey());
        PaymentPageEntity paymentPageEntity = paymentPageService.getById(id);
        if (paymentPageEntity == null) {
            return null;
        }
        DepositRecordEntity recordEntity = depositRecordService.getOne(paymentPageEntity.getMerchantId(),
                paymentPageEntity.getTrackingId());
        if (recordEntity == null) {
            return null;
        }
        return recordEntity.getStatus();
    }
}
