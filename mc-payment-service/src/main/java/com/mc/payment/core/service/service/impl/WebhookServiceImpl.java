package com.mc.payment.core.service.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mc.base.redis.util.RedisLockUtil;
import com.mc.payment.api.model.dto.DepositEventDto;
import com.mc.payment.api.model.dto.WithdrawalEventDto;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.BusinessExceptionInfoEnum;
import com.mc.payment.common.constant.RedisConstants;
import com.mc.payment.core.service.config.AppConfig;
import com.mc.payment.core.service.constant.WebhookEventConstants;
import com.mc.payment.core.service.entity.*;
import com.mc.payment.core.service.facade.CheezeePayServiceFacade;
import com.mc.payment.core.service.facade.IEzeebillServiceFacade;
import com.mc.payment.core.service.facade.IPayPalServiceFacade;
import com.mc.payment.core.service.model.enums.*;
import com.mc.payment.core.service.service.*;
import com.mc.payment.core.service.util.MonitorLogUtil;
import com.mc.payment.gateway.channels.cheezeepay.config.CheezeePayConfig;
import com.mc.payment.gateway.channels.cheezeepay.model.req.CheezeePayDepositCallBackReq;
import com.mc.payment.gateway.channels.cheezeepay.model.req.CheezeePayWithdrawalCallbackReq;
import com.mc.payment.gateway.channels.cheezeepay.utils.CheeseTradeRSAUtil;
import com.mc.payment.gateway.channels.ezeebill.config.EzeebillConfig;
import com.mc.payment.gateway.channels.ezeebill.model.req.EzeebillDepositCallBackReq;
import com.mc.payment.gateway.channels.ezeebill.model.req.EzeebillWithdrawalCallBackReq;
import com.mc.payment.gateway.channels.ezeebill.util.EzeebillUtil;
import com.mc.payment.gateway.channels.ofapay.config.OfaPayConfig;
import com.mc.payment.gateway.channels.ofapay.model.req.*;
import com.mc.payment.gateway.channels.ofapay.model.rsp.OfaPayQueryDepositRsp;
import com.mc.payment.gateway.channels.ofapay.model.rsp.OfaPayQueryWithdrawalRsp;
import com.mc.payment.gateway.channels.ofapay.service.OfaPayService;
import com.mc.payment.gateway.channels.ofapay.util.OfaPayUtil;
import com.mc.payment.gateway.channels.passtopay.model.req.PassToPayDepositCallBackReq;
import com.mc.payment.gateway.channels.passtopay.util.SignatureGenerator;
import com.mc.payment.gateway.channels.paypal.service.PaypalService;
import com.mc.payment.gateway.channels.paypal.util.PayPalSignatureUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * webhook回调服务实现类
 * todo 重构点 统一处理入金通知流程
 *
 * @author Conor
 * @since 2024-09-24 21:06:06.569
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class WebhookServiceImpl implements IWebhookService {

    private final OfaPayService ofaPayService;

    private final IDepositRecordService depositRecordService;

    private final IWithdrawalRecordService withdrawalRecordService;

    private final IWebhookEventService webhookEventService;
    private final MerchantWalletService merchantWalletService;

    private final IMerchantService merchantService;

    private final IDepositRecordDetailService depositRecordDetailService;
    private final OfaPayConfig ofaPayConfig;
    private final IWithdrawalRecordDetailService withdrawalRecordDetailService;
    private final PaypalService paypalService;
    private final AppConfig appConfig;
    private final EzeebillConfig ezeebillConfig;
    private final CheezeePayConfig cheezeePayConfig;
    private final CheezeePayServiceFacade cheezeePayServiceFacade;
    private final RedisTemplate redisTemplate;

    private final IPayPalServiceFacade payPalServiceFacade;
    private final IEzeebillServiceFacade ezeebillServiceFacade;


    String successCodeStatus = "success";

    /**
     * 构建WithdrawalRecordDetailEntity
     *
     * @param req
     * @param withdrawRecord
     * @param walletEntity
     * @return
     */
    private static @NotNull WithdrawalRecordDetailEntity getWithdrawalRecordDetailEntity(EzeebillWithdrawalCallBackReq req, WithdrawalRecordEntity withdrawRecord, MerchantWalletEntity walletEntity) {
        WithdrawalRecordDetailEntity withdrawalRecordDetail = new WithdrawalRecordDetailEntity();
        withdrawalRecordDetail.setRecordId(withdrawRecord.getId());
        withdrawalRecordDetail.setTxHash(req.getMerch_order_id());
        withdrawalRecordDetail.setChannelSubType(ChannelSubTypeEnum.EZEEBILL.getCode());
        withdrawalRecordDetail.setAssetName(withdrawRecord.getAssetName());
        withdrawalRecordDetail.setNetProtocol(withdrawRecord.getNetProtocol());
        withdrawalRecordDetail.setDestinationAddress(withdrawRecord.getDestinationAddress());
        withdrawalRecordDetail.setMerchantId(withdrawRecord.getMerchantId());
        withdrawalRecordDetail.setMerchantName(withdrawRecord.getMerchantName());
        withdrawalRecordDetail.setAmount(withdrawRecord.getAmount());
        withdrawalRecordDetail.setNetworkFee(BigDecimal.ZERO);
        withdrawalRecordDetail.setAddrBalance(walletEntity == null ? BigDecimal.ZERO : walletEntity.getBalance());
        withdrawalRecordDetail.setStatus(WithdrawalDetailStausEnum.ITEM_5.getCode());
        return withdrawalRecordDetail;
    }

    @Override
    public String depositCallBack(String payload) {
        OfaPayDepositCallbackReq callbackReq = JSONUtil.toBean(payload, OfaPayDepositCallbackReq.class);
        if (!checkSignature(callbackReq)) {
            return "Signature verification failed!";
        }
        DepositRecordEntity depositRecord = depositRecordService.getById(callbackReq.getOrderid());
        OfaPayQueryDepositReq ofaPayQueryDepositReq = new OfaPayQueryDepositReq();
        ofaPayQueryDepositReq.setOrderid(callbackReq.getOrderid());
        ofaPayQueryDepositReq.setScode(callbackReq.getScode());
        OfaPayQueryDepositRsp rsp = ofaPayService.queryDeposit(ofaPayQueryDepositReq);
        MerchantWalletEntity walletEntity = merchantWalletService.getById(depositRecord.getWalletId());
        MerchantEntity merchant = merchantService.getById(depositRecord.getMerchantId());
        //生成入金明细
        DepositRecordDetailEntity recordDetailEntity = new DepositRecordDetailEntity();
        recordDetailEntity.setRecordId(depositRecord.getId());
        recordDetailEntity.setAssetName(depositRecord.getAssetName());
        recordDetailEntity.setNetProtocol(depositRecord.getNetProtocol());
        recordDetailEntity.setMerchantId(depositRecord.getMerchantId());
        recordDetailEntity.setMerchantName(depositRecord.getMerchantName());
        recordDetailEntity.setAmount(depositRecord.getAmount());
        recordDetailEntity.setDestinationAddress(depositRecord.getDestinationAddress());
        recordDetailEntity.setNetworkFee(BigDecimal.ZERO);
        recordDetailEntity.setServiceFee(BigDecimal.ZERO);
        recordDetailEntity.setTxHash(callbackReq.getOrderid() + "_" + callbackReq.getOrderno());
        recordDetailEntity.setChannelSubType(ChannelSubTypeEnum.OFA_PAY.getCode());
        recordDetailEntity.setAddrBalance(walletEntity == null ? BigDecimal.ZERO : walletEntity.getBalance());
        recordDetailEntity.setAuditStatus(DepositAuditStatusEnum.ITEM_1.getCode());
        recordDetailEntity.setStatus(DepositDetailStausEnum.ITEM_6.getCode());
        depositRecordDetailService.save(recordDetailEntity);
        Integer eventStatus = DepositRecordStatusEnum.ITEM_2.getCode();
        if (StrUtil.equals("00", rsp.getRespcode()) || StrUtil.equals("10", rsp.getRespcode())) {
            if (merchant.getDepositAudit() == BooleanStatusEnum.ITEM_1.getCode()) {
                eventStatus = DepositRecordStatusEnum.ITEM_5.getCode();
            }
            depositRecordService.lambdaUpdate().set(DepositRecordEntity::getStatus, eventStatus)
                    .set(DepositRecordEntity::getAccumulatedAmount, rsp.getAmount())
                    .eq(DepositRecordEntity::getId, depositRecord.getId())
                    .update();
            merchantWalletService.changeBalance(ChangeEventTypeEnum.DEPOSIT, depositRecord.getId()
                    , depositRecord.getWalletId(), new BigDecimal(rsp.getAmount()), "入金成功");
            //资产入金金额超7天均值指标监控
            JSONObject deposit7AvgAmount = new JSONObject().put("Service", "payment").put("MonitorKey", "depositAmount")
                    .put("amount", depositRecord.getAmount().multiply(depositRecord.getRate()))
                    .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
            MonitorLogUtil.log(deposit7AvgAmount);
            //完全入金状态订单时间相连指标监控
            JSONObject completeDeposit = new JSONObject().put("Service", "payment").put("MonitorKey", "completeDeposit")
                    .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
            MonitorLogUtil.log(completeDeposit);
        } else {
            depositRecordService.lambdaUpdate().set(DepositRecordEntity::getStatus, DepositRecordStatusEnum.ITEM_4.getCode())
                    .eq(DepositRecordEntity::getId, depositRecord.getId())
                    .update();
            eventStatus = DepositRecordStatusEnum.ITEM_4.getCode();
            depositRecordDetailService.lambdaUpdate().set(DepositRecordDetailEntity::getStatus, DepositDetailStausEnum.ITEM_7.getCode())
                    .eq(DepositRecordDetailEntity::getId, recordDetailEntity.getId())
                    .update();
            //请求失效状态订单增多指标监控
            JSONObject failDeposit = new JSONObject().put("Service", "payment").put("MonitorKey", "failDeposit")
                    .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
            MonitorLogUtil.log(failDeposit);
        }
        // 触发webhook
        WebhookEventEntity webhookEventEntity = new WebhookEventEntity();
        webhookEventEntity.setEvent(WebhookEventConstants.DEPOSIT_EVENT);
        webhookEventEntity.setData(JSONUtil.toJsonStr(new DepositEventDto(depositRecord.getTrackingId()
                , eventStatus, new BigDecimal(rsp.getAmount()))));
        webhookEventEntity.setTrackingId(depositRecord.getTrackingId());
        webhookEventEntity.setWebhookUrl(depositRecord.getWebhookUrl());
        webhookEventEntity.setMerchantId(depositRecord.getMerchantId());
        webhookEventService.asyncSendWebhookEvent(webhookEventEntity);
        return "success";
    }

    private boolean checkSignature(OfaPayBaseReq callbackReq) {
        String apiKey = ofaPayConfig.getKeyMap().get(callbackReq.getScode());
        if (apiKey == null) {
            return false;
        }
        try {
            apiKey = ofaPayConfig.decrypt(apiKey);
        } catch (Exception e) {
            return false;
        }
        String signature = OfaPayUtil.generateSignature(callbackReq, apiKey);
        return StrUtil.equals(signature, callbackReq.getSign());
    }

    @Override
    public String withdrawCallBack(String payload) {
        OfaPayWithdrawalCallbackReq callbackReq = JSONUtil.toBean(payload, OfaPayWithdrawalCallbackReq.class);
        if (!checkSignature(callbackReq)) {
            return "Signature verification failed!";
        }
        OfaPayQueryWithdrawalReq ofaPayQueryWithdrawalReq = new OfaPayQueryWithdrawalReq();
        ofaPayQueryWithdrawalReq.setOrderid(callbackReq.getOrderid());
        ofaPayQueryWithdrawalReq.setScode(callbackReq.getScode());
        OfaPayQueryWithdrawalRsp rsp = ofaPayService.queryWithdrawal(ofaPayQueryWithdrawalReq);
        WithdrawalRecordEntity withdrawRecord = withdrawalRecordService.getById(callbackReq.getOrderid());
        MerchantWalletEntity walletEntity = merchantWalletService.getById(withdrawRecord.getWalletId());
        //生成出金明细
        WithdrawalRecordDetailEntity withdrawalRecordDetail = new WithdrawalRecordDetailEntity();
        withdrawalRecordDetail.setRecordId(withdrawRecord.getId());
        withdrawalRecordDetail.setTxHash(rsp.getOrderid() + "_" + rsp.getOrderno());
        withdrawalRecordDetail.setChannelSubType(ChannelSubTypeEnum.OFA_PAY.getCode());
        withdrawalRecordDetail.setAssetName(withdrawRecord.getAssetName());
        withdrawalRecordDetail.setNetProtocol(withdrawRecord.getNetProtocol());
        withdrawalRecordDetail.setDestinationAddress(withdrawRecord.getDestinationAddress());
        withdrawalRecordDetail.setMerchantId(withdrawRecord.getMerchantId());
        withdrawalRecordDetail.setMerchantName(withdrawRecord.getMerchantName());
        withdrawalRecordDetail.setAmount(withdrawRecord.getAmount());
        withdrawalRecordDetail.setNetworkFee(BigDecimal.ZERO);
        withdrawalRecordDetail.setAddrBalance(walletEntity == null ? BigDecimal.ZERO : walletEntity.getBalance());
        withdrawalRecordDetail.setStatus(WithdrawalDetailStausEnum.ITEM_5.getCode());
        withdrawalRecordDetailService.save(withdrawalRecordDetail);
        if (StrUtil.equals("1", rsp.getPrc()) && StrUtil.equals("S", rsp.getStatus()) && StrUtil.equals("00", rsp.getErrcode())) {
            withdrawalRecordService.lambdaUpdate()
                    .set(WithdrawalRecordEntity::getStatus, WithdrawalRecordStatusEnum.ITEM_4.getCode())
                    .eq(WithdrawalRecordEntity::getId, withdrawRecord.getId()).update();
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
                    , WithdrawalRecordStatusEnum.ITEM_4.getCode(), new BigDecimal(rsp.getMoney()), null)));
            webhookEventEntity.setTrackingId(withdrawRecord.getTrackingId());
            webhookEventEntity.setWebhookUrl(withdrawRecord.getWebhookUrl());
            webhookEventEntity.setMerchantId(withdrawRecord.getMerchantId());
            webhookEventService.asyncSendWebhookEvent(webhookEventEntity);
        } else {
            withdrawalRecordService.lambdaUpdate()
                    .set(WithdrawalRecordEntity::getStatus, WithdrawalRecordStatusEnum.ITEM_6.getCode())
                    .eq(WithdrawalRecordEntity::getId, withdrawRecord.getId()).update();
            withdrawalRecordDetailService.lambdaUpdate()
                    .set(WithdrawalRecordDetailEntity::getStatus, WithdrawalDetailStausEnum.ITEM_6.getCode())
                    .eq(WithdrawalRecordDetailEntity::getId, withdrawalRecordDetail.getId())
                    .update();
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
        //解冻钱包
        withdrawalRecordService.unfreezeWallet(withdrawRecord);
        return "success";
    }

    @Override
    public String cheezeePayWithdrawCallBack(String payload, HttpServletResponse response) {

        //参数转化
        CheezeePayWithdrawalCallbackReq callbackReq = JSONUtil.toBean(payload, CheezeePayWithdrawalCallbackReq.class);

        //获取分布式锁
        String cacheKey = RedisConstants.REDIS_PREFIX + callbackReq.getMchOrderNo();
        String cacheValue = UUID.randomUUID().toString();
        // 过期时间单位为毫秒
        int expireTime = 60000;

        try {
            boolean getLockResult = RedisLockUtil.tryLock(redisTemplate, cacheKey, cacheValue, expireTime);
            if (getLockResult) {
                return cheezeePayServiceFacade.withdrawalCallback(payload, callbackReq, response);
            } else {
                log.error("获取分布式锁失败:{}", callbackReq.getMchOrderNo());
                //此处应抛出自定义繁忙异常
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return BusinessExceptionInfoEnum.Server_Business_Exception.getMessage();
            }
        } finally {
            // 释放锁
            RedisLockUtil.releaseLock(redisTemplate, cacheKey, cacheValue);
        }
    }

    @Override
    public String paypalOrderApproved(String payload, HttpServletRequest request) {
        // 签名验证
        String webhookId = appConfig.getPaypalApprovedWebhookId();
        String transmissionId = request.getHeader("paypal-transmission-id");
        String timeStamp = request.getHeader("paypal-transmission-time");
        String certUrl = request.getHeader("paypal-cert-url");
        String transmissionSig = request.getHeader("paypal-transmission-sig");
        String authAlgo = request.getHeader("paypal-auth-algo");
        if (!PayPalSignatureUtil.verifySignature(payload, transmissionId, timeStamp, webhookId, certUrl, transmissionSig, authAlgo)) {
            log.info("paypalOrderApproved: Signature verification failed!");
            return "Signature verification failed!";
        }
        log.info("paypalOrderApproved: Signature verification success!");
        JSONObject jsonObject = JSONUtil.parseObj(payload);
        String eventType = jsonObject.getStr("event_type");
        if (!"CHECKOUT.ORDER.APPROVED".equals(eventType)) {
            // 事件类型不匹配
            return "Webhook received";
        }
        JSONObject resource = jsonObject.getJSONObject("resource");
        String orderId = resource.getStr("id");
        // 匹配入金申请单
        DepositRecordEntity depositRecordEntity = depositRecordService.lambdaQuery()
                .eq(DepositRecordEntity::getChannelTransactionId, orderId)
                .eq(DepositRecordEntity::getChannelSubType, ChannelSubTypeEnum.PAY_PAL.getCode())
                .eq(DepositRecordEntity::getStatus, DepositRecordStatusEnum.ITEM_0.getCode())
                .ge(DepositRecordEntity::getExpireTimestamp, System.currentTimeMillis())
                .one();
        if (depositRecordEntity == null) {
            log.info("未找到匹配的入金申请单:{}", orderId);
            return "Webhook received";
        }

        RetResult<String> retResult = paypalService.captureOrder(orderId);
        if (retResult.isSuccess()) {
            // 订单支付成功
            JSONObject apiJsonObject = JSONUtil.parseObj(retResult.getData());
            String status = apiJsonObject.getStr("status");
            if ("COMPLETED".equals(status)) {
                // 订单支付成功
                String captureOrderId = apiJsonObject.getStr("id");
                JSONObject paypal = apiJsonObject.getJSONObject("payment_source").getJSONObject("paypal");
                // 获取支付人信息
                String emailAddress = paypal.getStr("email_address");
                String accountId = paypal.getStr("account_id");
                JSONObject nameJsonObject = paypal.getJSONObject("name");
                String name = nameJsonObject.getStr("given_name") + " " + nameJsonObject.getStr("surname");
                // 获取订单信息
                JSONObject capture = apiJsonObject.getJSONArray("purchase_units").getJSONObject(0).getJSONObject("payments").getJSONArray("captures").getJSONObject(0);
                String captureId = capture.getStr("id");
                JSONObject sellerReceivableBreakdown = capture.getJSONObject("seller_receivable_breakdown");
                JSONObject grossAmount = sellerReceivableBreakdown.getJSONObject("gross_amount");
                String currencyCode = grossAmount.getStr("currency_code");
                String value = grossAmount.getStr("value");
                JSONObject paypalFee = sellerReceivableBreakdown.getJSONObject("paypal_fee");
                String feeCurrencyCode = paypalFee.getStr("currency_code");
                String feeValue = paypalFee.getStr("value");
                JSONObject netAmount = sellerReceivableBreakdown.getJSONObject("net_amount");
                String netCurrencyCode = netAmount.getStr("currency_code");
                String netValue = netAmount.getStr("value");
                // 判断支付金额或币种是否匹配
                if (depositRecordEntity.getAmount().compareTo(new BigDecimal(value)) != 0 || !depositRecordEntity.getAssetName().equals(currencyCode)) {
                    log.error("支付金额或币种不匹配:{}", orderId);
                    return "Webhook received";
                }

                // 生成入金明细
                MerchantWalletEntity walletEntity = merchantWalletService.getById(depositRecordEntity.getWalletId());
                DepositRecordDetailEntity depositRecordDetailEntity = new DepositRecordDetailEntity();
                depositRecordDetailEntity.setRecordId(depositRecordEntity.getId());
                depositRecordDetailEntity.setAssetName(depositRecordEntity.getAssetName());
                depositRecordDetailEntity.setNetProtocol(depositRecordEntity.getNetProtocol());
                depositRecordDetailEntity.setSourceAddress(emailAddress);
                depositRecordDetailEntity.setDestinationAddress(depositRecordEntity.getDestinationAddress());
                depositRecordDetailEntity.setMerchantId(depositRecordEntity.getMerchantId());
                depositRecordDetailEntity.setMerchantName(depositRecordEntity.getMerchantName());
                depositRecordDetailEntity.setTxHash(orderId + "_" + captureId);
                depositRecordDetailEntity.setAmount(new BigDecimal(netValue));
                depositRecordDetailEntity.setNetworkFee(new BigDecimal(feeValue));
                depositRecordDetailEntity.setServiceFee(BigDecimal.ZERO);
                depositRecordDetailEntity.setAddrBalance(walletEntity.getBalance());
                depositRecordDetailEntity.setLastUpdated(0L);
                depositRecordDetailEntity.setRate(BigDecimal.ZERO);
                depositRecordDetailEntity.setFeeRate(BigDecimal.ZERO);
                depositRecordDetailEntity.setAuditStatus(DepositAuditStatusEnum.ITEM_1.getCode());
                depositRecordDetailEntity.setStatus(DepositDetailStausEnum.ITEM_6.getCode());
                depositRecordDetailEntity.setChannelSubType(ChannelSubTypeEnum.PAY_PAL.getCode());
                // 保存入金明细
                depositRecordDetailService.save(depositRecordDetailEntity);
                // 更新入金申请单状态
                depositRecordService.lambdaUpdate().set(DepositRecordEntity::getStatus, DepositRecordStatusEnum.ITEM_2.getCode())
                        .set(DepositRecordEntity::getAccumulatedAmount, depositRecordDetailEntity.getAmount())
                        .set(DepositRecordEntity::getSourceAddress, emailAddress)
                        .set(DepositRecordEntity::getGasFee, depositRecordDetailEntity.getNetworkFee())
                        .eq(DepositRecordEntity::getId, depositRecordEntity.getId()).update();
                // 更新商户钱包余额
                merchantWalletService.changeBalance(ChangeEventTypeEnum.DEPOSIT, depositRecordEntity.getId()
                        , depositRecordEntity.getWalletId(), depositRecordDetailEntity.getAmount(), "入金成功");
                //资产入金金额超7天均值指标监控
                JSONObject deposit7AvgAmount = new JSONObject().put("Service", "payment").put("MonitorKey", "depositAmount")
                        .put("amount", depositRecordDetailEntity.getAmount())
                        .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
                MonitorLogUtil.log(deposit7AvgAmount);
                //完全入金状态订单时间相连指标监控
                JSONObject completeDeposit = new JSONObject().put("Service", "payment").put("MonitorKey", "completeDeposit")
                        .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
                MonitorLogUtil.log(completeDeposit);

                // 触发webhook
                WebhookEventEntity webhookEventEntity = new WebhookEventEntity();
                webhookEventEntity.setEvent(WebhookEventConstants.DEPOSIT_EVENT);
                webhookEventEntity.setData(JSONUtil.toJsonStr(new DepositEventDto(depositRecordEntity.getTrackingId()
                        , DepositRecordStatusEnum.ITEM_2.getCode(), depositRecordEntity.getAmount())));
                webhookEventEntity.setTrackingId(depositRecordEntity.getTrackingId());
                webhookEventEntity.setWebhookUrl(depositRecordEntity.getWebhookUrl());
                webhookEventEntity.setMerchantId(depositRecordEntity.getMerchantId());
                webhookEventService.asyncSendWebhookEvent(webhookEventEntity);
            } else {
                // 订单支付失败
                log.error("订单支付失败,状态不正确:{},status:{}", orderId, status);
            }
        } else {
            // 订单支付失败
            log.error("订单支付失败,接口调用失败:{},retResult:{}", orderId, retResult);
        }
        return "Webhook received";
    }

    @Override
    public String paypalPayoutsItemSucceeded(String payload, HttpServletRequest request) {
        //参数转化
        JSONObject jsonObject = JSONUtil.parseObj(payload);
        String orderId = jsonObject.getJSONObject("resource").getStr("payout_batch_id");

        //获取分布式锁
        String cacheKey = RedisConstants.REDIS_PREFIX + orderId;
        String cacheValue = UUID.randomUUID().toString();
        // 过期时间单位为毫秒
        int expireTime = 60000;

        String result = "";
        try {
            boolean getLockResult = RedisLockUtil.tryLock(redisTemplate, cacheKey, cacheValue, expireTime);
            if (getLockResult) {
                result = payPalServiceFacade.paypalPayoutCallback(payload, request);
            } else {
                log.error("PayPal回调获取分布式锁失败:{}", orderId);
                //此处应抛出自定义繁忙异常
                return BusinessExceptionInfoEnum.Server_Business_Exception.getMessage();
            }
        } finally {
            // 释放锁
            RedisLockUtil.releaseLock(redisTemplate, cacheKey, cacheValue);
        }
        return result;
    }

    @Override
    public String ezeebillDepositCallBack(EzeebillDepositCallBackReq req) {
        try {
            //1.合法性校验
            Map<String, Object> map = req.convertToMap();
            map.remove("secure_hash");
            Map<String, Object> sortedMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            sortedMap.putAll(map);
            String signature = EzeebillUtil.generateSignature(sortedMap, ezeebillConfig.getHashKey(map.get("currency").toString()));
            if (!StrUtil.equals(signature, req.getSecure_hash())) {
                return "Signature verification failed!";
            }
            //2.匹配入金申请单
            DepositRecordEntity depositRecordEntity = depositRecordService.lambdaQuery()
                    .eq(DepositRecordEntity::getId, req.getMerch_order_id())
                    .eq(DepositRecordEntity::getChannelSubType, ChannelSubTypeEnum.EZEEBILL.getCode())
                    .eq(DepositRecordEntity::getStatus, DepositRecordStatusEnum.ITEM_0.getCode())
                    .ge(DepositRecordEntity::getExpireTimestamp, System.currentTimeMillis())
                    .one();
            if (depositRecordEntity == null) {
                log.info("未找到匹配的入金申请单:{}", req.getMerch_order_id());
                return "Fail,No Deposit record";
            }
            //3.入金成功-生成入金明细,更新申请单状态,更新商户钱包余额
            // 付款訂單狀態
            //0-訂單已產生
            //1-付款中
            //2-付款成功
            //3-付款失敗
            //4-已取消
            //5-已退款
            //6 訂單已關閉
            int eventStatus;
            if (req.isSuccess()) {
                eventStatus = DepositRecordStatusEnum.ITEM_2.getCode();
                MerchantWalletEntity walletEntity = merchantWalletService.getById(depositRecordEntity.getWalletId());
                //将金额由分转成元,保留两位小数
                Integer amount = req.getAmount();
                BigDecimal amountYuan = BigDecimal.valueOf(amount).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                //  会出现null的情况,可能是测试环境没有真实交易的原因,需找上游确认. 已确认,当前对方还未实现该功能 2024年10月18日
//            String channelUser = req.getChannelUser() == null ? "" : req.getChannelUser();
                //生成入金明细
                DepositRecordDetailEntity depositRecordDetailEntity = new DepositRecordDetailEntity();
                depositRecordDetailEntity.setRecordId(depositRecordEntity.getId());
                depositRecordDetailEntity.setAssetName(depositRecordEntity.getAssetName());
                depositRecordDetailEntity.setNetProtocol(depositRecordEntity.getNetProtocol());
                depositRecordDetailEntity.setMerchantId(depositRecordEntity.getMerchantId());
                depositRecordDetailEntity.setMerchantName(depositRecordEntity.getMerchantName());
                depositRecordDetailEntity.setAmount(amountYuan);
                //未明确是否有返回该字段
                depositRecordDetailEntity.setSourceAddress("");
                depositRecordDetailEntity.setDestinationAddress(depositRecordEntity.getDestinationAddress());
                depositRecordDetailEntity.setNetworkFee(BigDecimal.ZERO);
                depositRecordDetailEntity.setServiceFee(BigDecimal.ZERO);
                depositRecordDetailEntity.setTxHash(req.getMerch_order_id());
                depositRecordDetailEntity.setChannelSubType(ChannelSubTypeEnum.EZEEBILL.getCode());
                depositRecordDetailEntity.setAddrBalance(walletEntity == null ? BigDecimal.ZERO : walletEntity.getBalance());
                depositRecordDetailEntity.setAuditStatus(DepositAuditStatusEnum.ITEM_1.getCode());
                depositRecordDetailEntity.setStatus(DepositDetailStausEnum.ITEM_6.getCode());

                this.handleSuccessfulDeposit(depositRecordEntity, depositRecordDetailEntity);
            } else {
                log.error("Ezeebill入金回调状态异常:{}", req.getTxn_status());
                return "fail";
            }

            //回调商户webhook
            triggerWebhookEvent(depositRecordEntity, eventStatus);
        } catch (Exception e) {
            log.error("deposit callback failed", e);
            return "Fail,deposit callback failed:" + e.getMessage();
        }
        return "success";
    }

    @Override
    public String ezeebillWithdrawalCallBack(EzeebillWithdrawalCallBackReq req) {
        //获取分布式锁
        String cacheKey = RedisConstants.REDIS_PREFIX + req.getMerch_order_id();
        String cacheValue = UUID.randomUUID().toString();
        // 过期时间单位为毫秒
        int expireTime = 60000;

        String result = "";
        try {
            boolean getLockResult = RedisLockUtil.tryLock(redisTemplate, cacheKey, cacheValue, expireTime);
            if (getLockResult) {
                result = ezeebillServiceFacade.processWithdrawalCallback(req);
            } else {
                log.error("Ezeebill回调获取分布式锁失败:{}", req.getMerch_order_id());
                //此处应抛出自定义繁忙异常
                return BusinessExceptionInfoEnum.Server_Business_Exception.getMessage();
            }
        } finally {
            // 释放锁
            RedisLockUtil.releaseLock(redisTemplate, cacheKey, cacheValue);
        }
        return result;
    }

    /**
     * passToPay入金回调
     * <p>
     * 觸發場景：
     * <p>
     * 客戶完成支付，訂單狀態為 付款成功
     * 支付訂單在指定時間內未完成支付，訂單狀態為 訂單已關閉
     * 支付失敗並且無法繼續支付，訂單狀態為 付款失敗
     * <p>
     * 付款訂單狀態
     * 0-訂單已產生
     * 1-付款中
     * 2-付款成功
     * 3-付款失敗
     * 4-已取消
     * 5-已退款
     * 6-訂單已關閉
     * <p>
     * 经过确认,4-已取消 当前对方还未实现该功能 2024年10月18日
     * 5-已退款 对方的运营平台可以操作,商户后台还无法操作;风险点: 上游可直接不经过商户退款 2024年10月18日
     *
     * <p>
     * 1.合法性校验
     * 2.匹配入金申请单
     * 3.入金成功-生成入金明细,更新申请单状态,更新商户钱包余额
     * 4.入金失败-更新申请单状态.记录失败原因
     * 5.触发webhook
     *
     * <p>
     * 遗留的问题:
     * 商户将订单取消了,但是支付成功了?
     * 当前逻辑无法处理. 可调整为接收入金回调后,先保存或更新入金明细,再更新入金申请单状态
     * 但是当前入金明细和业务有太多绑定,需要重构
     * 重构思路: 入金明细和业务解耦,入金明细只记录入金明细,不记录业务状态,现有明细状态设计不合理,需要结合入金审核重新设计
     *
     * @param payload
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String passToPayDepositCallBack(String payload) {
        //1.合法性校验
        JSONObject jsonObject = JSONUtil.parseObj(payload);
        Map map = jsonObject.toBean(Map.class);
        map.remove("sign");
        String signature = SignatureGenerator.generateSignature(map, appConfig.getPassToPaySecretKey());
        PassToPayDepositCallBackReq req = jsonObject.toBean(PassToPayDepositCallBackReq.class);
        if (!StrUtil.equals(signature, req.getSign())) {
            return "Signature verification failed!";
        }
        //2.匹配入金申请单
        DepositRecordEntity depositRecordEntity = depositRecordService.lambdaQuery()
                .eq(DepositRecordEntity::getChannelTransactionId, req.getPayOrderId())
                .eq(DepositRecordEntity::getChannelSubType, ChannelSubTypeEnum.PASS_TO_PAY.getCode())
                .eq(DepositRecordEntity::getStatus, DepositRecordStatusEnum.ITEM_0.getCode())
                .ge(DepositRecordEntity::getExpireTimestamp, System.currentTimeMillis())
                .one();
        if (depositRecordEntity == null) {
            log.info("未找到匹配的入金申请单:{}", req.getPayOrderId());
            return "success";
        }
        //3.入金成功-生成入金明细,更新申请单状态,更新商户钱包余额
        // 付款訂單狀態
        //0-訂單已產生
        //1-付款中
        //2-付款成功
        //3-付款失敗
        //4-已取消
        //5-已退款
        //6 訂單已關閉
        int eventStatus;
        if (req.isSuccess()) {
            eventStatus = DepositRecordStatusEnum.ITEM_2.getCode();
            MerchantWalletEntity walletEntity = merchantWalletService.getById(depositRecordEntity.getWalletId());
            //将金额由分转成元,保留两位小数
            Integer amount = req.getAmount();
            // 减去商户手续费 得到实际到账金额
            amount = amount - req.getMchFeeAmount();
            BigDecimal amountYuan = BigDecimal.valueOf(amount).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal feeAmountYuan = BigDecimal.valueOf(req.getMchFeeAmount()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            //  会出现null的情况,可能是测试环境没有真实交易的原因,需找上游确认. 已确认,当前对方还未实现该功能 2024年10月18日
            String channelUser = req.getChannelUser() == null ? "" : req.getChannelUser();
            //生成入金明细
            DepositRecordDetailEntity depositRecordDetailEntity = new DepositRecordDetailEntity();
            depositRecordDetailEntity.setRecordId(depositRecordEntity.getId());
            depositRecordDetailEntity.setAssetName(depositRecordEntity.getAssetName());
            depositRecordDetailEntity.setNetProtocol(depositRecordEntity.getNetProtocol());
            depositRecordDetailEntity.setMerchantId(depositRecordEntity.getMerchantId());
            depositRecordDetailEntity.setMerchantName(depositRecordEntity.getMerchantName());
            depositRecordDetailEntity.setAmount(amountYuan);
            depositRecordDetailEntity.setSourceAddress(channelUser);
            depositRecordDetailEntity.setDestinationAddress(depositRecordEntity.getDestinationAddress());
            depositRecordDetailEntity.setNetworkFee(feeAmountYuan);
            depositRecordDetailEntity.setServiceFee(BigDecimal.ZERO);
            depositRecordDetailEntity.setTxHash(req.getPayOrderId());
            depositRecordDetailEntity.setChannelSubType(ChannelSubTypeEnum.PASS_TO_PAY.getCode());
            depositRecordDetailEntity.setAddrBalance(walletEntity == null ? BigDecimal.ZERO : walletEntity.getBalance());
            depositRecordDetailEntity.setAuditStatus(DepositAuditStatusEnum.ITEM_1.getCode());
            depositRecordDetailEntity.setStatus(DepositDetailStausEnum.ITEM_6.getCode());

            this.handleSuccessfulDeposit(depositRecordEntity, depositRecordDetailEntity);
        } else if (req.isFail() || req.isClose()) {
            //4.入金失败-更新申请单状态.记录失败原因
            eventStatus = DepositRecordStatusEnum.ITEM_4.getCode();
            String stayReason = req.isFail() ? "入金失败" : "订单已关闭";
            this.handleFailedDeposit(depositRecordEntity.getId(), stayReason);
        } else {
            //  引入告警提醒
            log.error("出现非预期的入金状态:{}", req.getState());
            return "success";
        }
        triggerWebhookEvent(depositRecordEntity, eventStatus);
        return "success";
    }

    private void handleSuccessfulDeposit(DepositRecordEntity depositRecordEntity,
                                         DepositRecordDetailEntity depositRecordDetailEntity) {
        depositRecordDetailService.save(depositRecordDetailEntity);
        depositRecordService.lambdaUpdate().set(DepositRecordEntity::getStatus, DepositRecordStatusEnum.ITEM_2.getCode())
                .set(DepositRecordEntity::getAccumulatedAmount, depositRecordDetailEntity.getAmount())
                .set(DepositRecordEntity::getSourceAddress, depositRecordDetailEntity.getSourceAddress())
                .set(DepositRecordEntity::getGasFee, depositRecordDetailEntity.getNetworkFee().add(depositRecordDetailEntity.getServiceFee()))
                .eq(DepositRecordEntity::getId, depositRecordEntity.getId())
                .update();

        merchantWalletService.changeBalance(ChangeEventTypeEnum.DEPOSIT, depositRecordEntity.getId()
                , depositRecordEntity.getWalletId(), depositRecordDetailEntity.getAmount(), "入金成功");
        //资产入金金额超7天均值指标监控
        JSONObject deposit7AvgAmount = new JSONObject().put("Service", "payment").put("MonitorKey", "depositAmount")
                .put("amount", depositRecordDetailEntity.getAmount())
                .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
        MonitorLogUtil.log(deposit7AvgAmount);
        //完全入金状态订单时间相连指标监控
        JSONObject completeDeposit = new JSONObject().put("Service", "payment").put("MonitorKey", "completeDeposit")
                .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
        MonitorLogUtil.log(completeDeposit);
    }

    private void handleFailedDeposit(String depositRecordId, String stayReason) {
        depositRecordService.lambdaUpdate()
                .set(DepositRecordEntity::getStatus, DepositRecordStatusEnum.ITEM_4.getCode())
                .set(DepositRecordEntity::getStayReason, stayReason == null ? "" : stayReason)
                .eq(DepositRecordEntity::getId, depositRecordId)
                .update();
        //请求失效状态订单增多指标监控
        JSONObject failDeposit = new JSONObject().put("Service", "payment").put("MonitorKey", "failDeposit")
                .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
        MonitorLogUtil.log(failDeposit);
    }

    private void triggerWebhookEvent(DepositRecordEntity depositRecordEntity, Integer eventStatus) {
        WebhookEventEntity webhookEventEntity = new WebhookEventEntity();
        webhookEventEntity.setEvent(WebhookEventConstants.DEPOSIT_EVENT);
        webhookEventEntity.setData(JSONUtil.toJsonStr(new DepositEventDto(depositRecordEntity.getTrackingId(),
                eventStatus, depositRecordEntity.getAmount())));
        webhookEventEntity.setTrackingId(depositRecordEntity.getTrackingId());
        webhookEventEntity.setWebhookUrl(depositRecordEntity.getWebhookUrl());
        webhookEventEntity.setMerchantId(depositRecordEntity.getMerchantId());
        webhookEventService.asyncSendWebhookEvent(webhookEventEntity);
    }

    @Override
    public String cheezeePayDepositCallBack(String payload, HttpServletResponse response) {

        JSONObject jsonObject = JSONUtil.parseObj(payload);
        Map paramMap = jsonObject.toBean(Map.class);
        String PLATFORM_PUB_KEY = cheezeePayConfig.getPublicKey();
        //增加测试逻辑，跳过验证
        boolean verifyResult = false;
        try {
            //verifyResult = true;
            verifyResult = CheeseTradeRSAUtil.verifySign(paramMap, PLATFORM_PUB_KEY);

        } catch (Exception e) {
            log.error("cheezeePay callback signature verification error！", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return BusinessExceptionInfoEnum.Inner_Exception.getMessage();
        }
        //1.签名验证
        if (!verifyResult) {
            //Signature verification failed
            log.info("cheezeePay callback signature verification failed！");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return BusinessExceptionInfoEnum.Sign_Exception.getMessage();
        }

        //2.参数绑定
        CheezeePayDepositCallBackReq req = jsonObject.toBean(CheezeePayDepositCallBackReq.class);

        //3.匹配入金申请单
        DepositRecordEntity depositRecordEntity = depositRecordService.lambdaQuery()
                .eq(DepositRecordEntity::getChannelTransactionId, req.getPlatOrderNo())
                .eq(DepositRecordEntity::getChannelSubType, ChannelSubTypeEnum.CHEEZEE_PAY.getCode())
                .in(DepositRecordEntity::getStatus, DepositRecordStatusEnum.ITEM_0.getCode(), DepositRecordStatusEnum.ITEM_4.getCode())
                //ge(DepositRecordEntity::getExpireTimestamp, System.currentTimeMillis())
                .one();
        if (Objects.isNull(depositRecordEntity)) {
            log.info("未找到匹配的入金申请单:{}", req.getMchOrderNo());
            return successCodeStatus;
        }
        //判断是否超时
        Long expireTimestamp = depositRecordEntity.getExpireTimestamp();
        long currentTimestamp = System.currentTimeMillis();
        //是否超时订单
        boolean timeOutOrder = false;
        if (expireTimestamp < currentTimestamp) {
            timeOutOrder = true;
        }
        //4.入金记录
        Integer orderStatus = req.getOrderStatus();
        int eventStatus;
        //枚举：1.成功 2.退款  3.部分付款（没有部分付款这一说，文档上这么写的，支付失败不会有回调）
        if (orderStatus.equals(1)) {
            eventStatus = DepositRecordStatusEnum.ITEM_2.getCode();
            MerchantWalletEntity walletEntity = merchantWalletService.getById(depositRecordEntity.getWalletId());

            BigDecimal amount;
            BigDecimal feeAmount;
            BigDecimal realAmount;

            //只有IDR金额是整数，其他的都带2位有小数点
            amount = new BigDecimal(req.getPayAmount()).setScale(2, RoundingMode.HALF_UP);
            feeAmount = new BigDecimal(req.getFee()).setScale(2, RoundingMode.HALF_UP);
            // 减去商户手续费 得到实际到账金额
            realAmount = amount.subtract(feeAmount);
            //生成入金明细
            DepositRecordDetailEntity depositRecordDetailEntity = new DepositRecordDetailEntity();
            depositRecordDetailEntity.setRecordId(depositRecordEntity.getId());
            depositRecordDetailEntity.setAssetName(depositRecordEntity.getAssetName());
            depositRecordDetailEntity.setNetProtocol(depositRecordEntity.getNetProtocol());
            depositRecordDetailEntity.setMerchantId(depositRecordEntity.getMerchantId());
            depositRecordDetailEntity.setMerchantName(depositRecordEntity.getMerchantName());
            depositRecordDetailEntity.setAmount(realAmount);
            depositRecordDetailEntity.setSourceAddress("");
            depositRecordDetailEntity.setDestinationAddress(depositRecordEntity.getDestinationAddress());
            depositRecordDetailEntity.setNetworkFee(feeAmount);
            depositRecordDetailEntity.setServiceFee(BigDecimal.ZERO);
            depositRecordDetailEntity.setTxHash(req.getPlatOrderNo());
            depositRecordDetailEntity.setChannelSubType(ChannelSubTypeEnum.CHEEZEE_PAY.getCode());
            depositRecordDetailEntity.setAddrBalance(walletEntity == null ? BigDecimal.ZERO : walletEntity.getBalance());
            depositRecordDetailEntity.setAuditStatus(DepositAuditStatusEnum.ITEM_1.getCode());
            depositRecordDetailEntity.setStatus(DepositDetailStausEnum.ITEM_6.getCode());

            Integer status = depositRecordEntity.getStatus();
            //状态正常，同时未超时，正常处理逻辑
            if (status.equals(DepositRecordStatusEnum.ITEM_0.getCode()) && !timeOutOrder) {
                depositRecordDetailEntity.setAuditStatus(DepositAuditStatusEnum.ITEM_0.getCode());
                this.handleSuccessfulDeposit(depositRecordEntity, depositRecordDetailEntity);
            }

            if (timeOutOrder) {
                log.info("该订单超时支付，只做记录，不做任何操作:{}, 超时时间:{}", req.getMchOrderNo(), new Date(currentTimestamp - expireTimestamp));
                depositRecordDetailService.save(depositRecordDetailEntity);
                return successCodeStatus;
            }
        } else if (orderStatus.equals(2)) {
            //5.cheezeepay没有入金失败的回调，退款也只有在我们找他们退款，他们手动操作处理之后，再回调我们退款
            //eventStatus = DepositRecordStatusEnum.ITEM_4.getCode();
            log.error("出现非预期的退款状态:{}", orderStatus);
            return successCodeStatus;
        } else {
            //  引入告警提醒
            log.error("出现非预期的入金状态:{}", orderStatus);
            return successCodeStatus;
        }
        triggerWebhookEvent(depositRecordEntity, eventStatus);
        return successCodeStatus;
    }

    @Override
    public String paypalPayoutsItemFailed(String payload, HttpServletRequest request) {
        //参数转化
        JSONObject jsonObject = JSONUtil.parseObj(payload);
        String orderId = jsonObject.getJSONObject("resource").getStr("payout_batch_id");

        //获取分布式锁
        String cacheKey = RedisConstants.REDIS_PREFIX + orderId;
        String cacheValue = UUID.randomUUID().toString();
        // 过期时间单位为毫秒
        int expireTime = 60000;

        String result = "";
        try {
            boolean getLockResult = RedisLockUtil.tryLock(redisTemplate, cacheKey, cacheValue, expireTime);
            if (getLockResult) {
                result = payPalServiceFacade.paypalPayoutFailCallback(payload, request);
            } else {
                log.error("PayPal回调获取分布式锁失败:{}", orderId);
                //此处应抛出自定义繁忙异常
                return BusinessExceptionInfoEnum.Server_Business_Exception.getMessage();
            }
        } finally {
            // 释放锁
            RedisLockUtil.releaseLock(redisTemplate, cacheKey, cacheValue);
        }
        return result;
    }
}
