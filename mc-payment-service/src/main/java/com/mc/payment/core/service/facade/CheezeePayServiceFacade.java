package com.mc.payment.core.service.facade;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.mc.payment.api.model.dto.WithdrawalEventDto;
import com.mc.payment.common.constant.BusinessExceptionInfoEnum;
import com.mc.payment.common.constant.CommonConstant;
import com.mc.payment.core.service.constant.WebhookEventConstants;
import com.mc.payment.core.service.entity.MerchantWalletEntity;
import com.mc.payment.core.service.entity.WebhookEventEntity;
import com.mc.payment.core.service.entity.WithdrawalRecordDetailEntity;
import com.mc.payment.core.service.entity.WithdrawalRecordEntity;
import com.mc.payment.core.service.model.enums.ChangeEventTypeEnum;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.enums.WithdrawalDetailStausEnum;
import com.mc.payment.core.service.model.enums.WithdrawalRecordStatusEnum;
import com.mc.payment.core.service.service.IWebhookEventService;
import com.mc.payment.core.service.service.IWithdrawalRecordDetailService;
import com.mc.payment.core.service.service.IWithdrawalRecordService;
import com.mc.payment.core.service.service.MerchantWalletService;
import com.mc.payment.core.service.util.MonitorLogUtil;
import com.mc.payment.gateway.channels.cheezeepay.config.CheezeePayConfig;
import com.mc.payment.gateway.channels.cheezeepay.model.req.CheezeePayWithdrawalCallbackReq;
import com.mc.payment.gateway.channels.cheezeepay.service.CheezeePayService;
import com.mc.payment.gateway.channels.cheezeepay.utils.CheeseTradeRSAUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class CheezeePayServiceFacade implements ICheezeePayServiceFacade {

    private final CheezeePayConfig cheezeePayConfig;

    private final CheezeePayService cheezeePayService;

    private final IWithdrawalRecordService withdrawalRecordService;

    private final MerchantWalletService merchantWalletService;

    private final IWithdrawalRecordDetailService withdrawalRecordDetailService;

    private final IWebhookEventService webhookEventService;

    @Autowired
    @Lazy
    private CheezeePayServiceFacade iCheezeePayServiceFacade;


    //出金回调枚举
    private Integer SUCCESS_CODE = 1;
    private Integer REFUND_CODE = 2;
    private Integer FAIL_CODE = 4;


    /**
     * 出金回调
     */
    public String withdrawalCallback(String payload, CheezeePayWithdrawalCallbackReq callbackReq, HttpServletResponse response) {

        //1.验签
        String result = verifySign(payload, response, cheezeePayConfig.getPublicKey());

        //此处应该用自定义异常去代替
        if (StringUtils.isNotBlank(result)) {
            return result;
        }

        //2.校验状态
        String validResult = validStatus(callbackReq);

        if (StringUtils.isNotBlank(validResult)) {
            return validResult;
        }

        //3.业务逻辑
        WithdrawalRecordEntity withdrawalRecordEntity = iCheezeePayServiceFacade.withdrawalCallBackHandle(callbackReq);

        //4.监控日志 与 hook回调
        asyncSendWebhookEvent(withdrawalRecordEntity, callbackReq.getOrderStatus(), callbackReq.getPayAmount());

        return CommonConstant.SUCCESS_STATUS;
    }


    private String verifySign(String payload, HttpServletResponse response, String platformPubKey) {
        JSONObject jsonObject = JSONUtil.parseObj(payload);
        Map paramMap = jsonObject.toBean(Map.class);

        boolean verifyResult = false;
        try {
            verifyResult = CheeseTradeRSAUtil.verifySign(paramMap, platformPubKey);

        } catch (Exception e) {
            log.error("cheezeePay payout callback signature verification error！", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return BusinessExceptionInfoEnum.Inner_Exception.getMessage();
        }
        //1.签名验证
        if (!verifyResult) {
            //Signature verification failed
            log.info("cheezeePay payout callback signature verification failed！");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return BusinessExceptionInfoEnum.Sign_Exception.getMessage();
        }
        return null;
    }


    private String validStatus(CheezeePayWithdrawalCallbackReq callbackReq) {

        Boolean success = SUCCESS_CODE == callbackReq.getOrderStatus() ? true : false;

        WithdrawalRecordEntity withdrawRecord = withdrawalRecordService.getById(callbackReq.getMchOrderNo());
        Integer withdrawRecordStatus = withdrawRecord.getStatus();

        if (success) {
            //已经处理完成，直接返回不处理
            if (withdrawRecordStatus.equals(WithdrawalRecordStatusEnum.ITEM_4.getCode())) {
                return CommonConstant.SUCCESS_STATUS;
            }
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public WithdrawalRecordEntity withdrawalCallBackHandle(CheezeePayWithdrawalCallbackReq callbackReq) {

        Integer orderStatus = callbackReq.getOrderStatus();
        LambdaUpdateWrapper<WithdrawalRecordEntity> updateWrapper = new LambdaUpdateWrapper<>();

        WithdrawalRecordEntity withdrawRecord = withdrawalRecordService.getById(callbackReq.getMchOrderNo());
        MerchantWalletEntity walletEntity = merchantWalletService.getById(withdrawRecord.getWalletId());


        //生成出金明细
        WithdrawalRecordDetailEntity withdrawalRecordDetail = new WithdrawalRecordDetailEntity();
        withdrawalRecordDetail.setRecordId(withdrawRecord.getId());
        withdrawalRecordDetail.setTxHash(callbackReq.getMchOrderNo() + "_" + callbackReq.getPlatOrderNo());
        withdrawalRecordDetail.setChannelSubType(ChannelSubTypeEnum.CHEEZEE_PAY.getCode());
        withdrawalRecordDetail.setAssetName(withdrawRecord.getAssetName());
        withdrawalRecordDetail.setNetProtocol(withdrawRecord.getNetProtocol());
        withdrawalRecordDetail.setDestinationAddress(withdrawRecord.getDestinationAddress());
        withdrawalRecordDetail.setMerchantId(withdrawRecord.getMerchantId());
        withdrawalRecordDetail.setMerchantName(withdrawRecord.getMerchantName());
        withdrawalRecordDetail.setServiceFee(BigDecimal.ZERO);
        withdrawalRecordDetail.setAmount(withdrawRecord.getAmount());
        //通道收取的整体费用应该是： networkFee + serviceFee（虚拟货币特有）
        withdrawalRecordDetail.setNetworkFee(new BigDecimal(callbackReq.getFee()));
        withdrawalRecordDetail.setAddrBalance(walletEntity == null ? BigDecimal.ZERO : walletEntity.getBalance());
        withdrawalRecordDetail.setSourceAddress(withdrawRecord.getSourceAddress());


        if (SUCCESS_CODE.equals(orderStatus) || FAIL_CODE.equals(orderStatus)) {
            //成功
            if (SUCCESS_CODE.equals(orderStatus)) {

                withdrawalRecordDetail.setStatus(WithdrawalDetailStausEnum.ITEM_5.getCode());
                updateWrapper.set(WithdrawalRecordEntity::getStatus, WithdrawalRecordStatusEnum.ITEM_4.getCode());
                //成功更新手续费
                updateWrapper.set(WithdrawalRecordEntity::getFeeAssetName, callbackReq.getFeeCurrency());
                updateWrapper.set(WithdrawalRecordEntity::getGasFee, new BigDecimal(callbackReq.getFee()));
            } else {
                //失败
                if (FAIL_CODE.equals(orderStatus)) {
                    withdrawalRecordDetail.setStatus(WithdrawalDetailStausEnum.ITEM_6.getCode());
                    updateWrapper.set(WithdrawalRecordEntity::getStatus, WithdrawalRecordStatusEnum.ITEM_6.getCode());
                }
            }
            //保存出金明细
            withdrawalRecordDetailService.save(withdrawalRecordDetail);
        }

        //退款
        if (REFUND_CODE.equals(orderStatus)) {
            updateWrapper.set(WithdrawalRecordEntity::getStayReason, "支付回调成功,又回调退款");
            //退款不改回调成功的detail
            //withdrawalRecordDetailService.lambdaUpdate().set()
        }

        //更新总记录
        updateWrapper.eq(WithdrawalRecordEntity::getId, withdrawRecord.getId());
        withdrawalRecordService.update(updateWrapper);


        //只有成功或者失败才解冻钱包
        if (SUCCESS_CODE.equals(orderStatus) || FAIL_CODE.equals(orderStatus)) {
            ChangeEventTypeEnum changeEventTypeEnum = SUCCESS_CODE == callbackReq.getOrderStatus() ? ChangeEventTypeEnum.WITHDRAWAL_SUCCESS : ChangeEventTypeEnum.WITHDRAWAL_FAIL;
            withdrawalRecordService.legalTenderUnfreezeAndChangeBalance(withdrawRecord, changeEventTypeEnum);
        }

        return withdrawRecord;
    }


    private void asyncSendWebhookEvent(WithdrawalRecordEntity withdrawRecord, int orderStatus, String amount) {
        Boolean success = SUCCESS_CODE == orderStatus ? true : false;
        if (SUCCESS_CODE.equals(orderStatus)) {
            //出金指标监控
            JSONObject withdrawalMonitor = new JSONObject().put("Service", "payment").put("MonitorKey", "withdrawalMonitor")
                    .put("address", withdrawRecord.getDestinationAddress()).put("amount", withdrawRecord.getAmount().multiply(withdrawRecord.getRate()))
                    .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
            MonitorLogUtil.log(withdrawalMonitor);
            //资产出金金额超7天均值指标监控
            JSONObject withdrawal7AvgAmount = new JSONObject().put("Service", "payment").put("MonitorKey", "withdrawalAmount")
                    .put("amount", withdrawRecord.getAmount().multiply(withdrawRecord.getRate()))
                    .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
            MonitorLogUtil.log(withdrawal7AvgAmount);
            //出金成功状态订单时间相连指标监控
            JSONObject depositCompleteMonitor = new JSONObject().put("Service", "payment").put("MonitorKey", "depositCompleteMonitor")
                    .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
            MonitorLogUtil.log(depositCompleteMonitor);

            // 触发webhook
            WebhookEventEntity webhookEventEntity = new WebhookEventEntity();
            webhookEventEntity.setEvent(WebhookEventConstants.WITHDRAWAL_EVENT);
            webhookEventEntity.setData(JSONUtil.toJsonStr(new WithdrawalEventDto(withdrawRecord.getTrackingId()
                    , WithdrawalRecordStatusEnum.ITEM_4.getCode(), new BigDecimal(amount), null)));
            webhookEventEntity.setTrackingId(withdrawRecord.getTrackingId());
            webhookEventEntity.setWebhookUrl(withdrawRecord.getWebhookUrl());
            webhookEventEntity.setMerchantId(withdrawRecord.getMerchantId());
            webhookEventService.asyncSendWebhookEvent(webhookEventEntity);
        }

        if (FAIL_CODE.equals(orderStatus)) {
            //出金错误状态订单增多指标监控
            JSONObject depositFailMonitor = new JSONObject().put("Service", "payment").put("MonitorKey", "depositFailMonitor")
                    .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
            MonitorLogUtil.log(depositFailMonitor);
            // 触发webhook
            WebhookEventEntity webhookEventEntity = new WebhookEventEntity();
            webhookEventEntity.setEvent(WebhookEventConstants.WITHDRAWAL_EVENT);
            webhookEventEntity.setData(JSONUtil.toJsonStr(new WithdrawalEventDto(withdrawRecord.getTrackingId()
                    , WithdrawalRecordStatusEnum.ITEM_6.getCode(), withdrawRecord.getAmount(), null)));
            webhookEventEntity.setTrackingId(withdrawRecord.getTrackingId());
            webhookEventEntity.setWebhookUrl(withdrawRecord.getWebhookUrl());
            webhookEventEntity.setMerchantId(withdrawRecord.getMerchantId());
            webhookEventService.asyncSendWebhookEvent(webhookEventEntity);
        }
        if (REFUND_CODE.equals(orderStatus)) {
            //出金成功有回调退款的，在那时不发通知，只做记录留存
            log.info("cheezeepay withdrawalCallback not send event, orderStatus: {}, trackingId: {}", success, orderStatus, withdrawRecord.getTrackingId());
        }

    }


}
