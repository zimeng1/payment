package com.mc.payment.core.service.facade;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mc.payment.api.model.dto.WithdrawalEventDto;
import com.mc.payment.core.service.config.AppConfig;
import com.mc.payment.core.service.constant.WebhookEventConstants;
import com.mc.payment.core.service.entity.*;
import com.mc.payment.core.service.model.enums.ChangeEventTypeEnum;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.enums.WithdrawalDetailStausEnum;
import com.mc.payment.core.service.model.enums.WithdrawalRecordStatusEnum;
import com.mc.payment.core.service.service.*;
import com.mc.payment.core.service.util.MonitorLogUtil;
import com.mc.payment.gateway.channels.paypal.util.PayPalSignatureUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

/**
 * EzeebillServiceFacade
 *
 * @author GZM
 * @since 2024/11/15 下午3:38
 */
@Data
@RequiredArgsConstructor
@Component
@Slf4j
public class PayPalServiceFacade implements IPayPalServiceFacade {
	
	private final AppConfig appConfig;
	
	private final IWithdrawalRecordService withdrawalRecordService;
	
	private final MerchantWalletService merchantWalletService;
	
	private final IWithdrawalRecordDetailService withdrawalRecordDetailService;
	
	private final IWebhookEventService webhookEventService;
	
	private final ChannelWalletService channelWalletService;
	
	@Override
	public String paypalPayoutCallback(String payload, HttpServletRequest request){
		// 1. 签名验证逻辑
		if (!verifyPayPalWebhookSignature(request, payload)) {
			log.info("paypalOrderApproved: Signature verification failed!");
			return "Signature verification failed!";
		}
		
		JSONObject jsonObject = JSONUtil.parseObj(payload);
		
		// 2.事件类型校验
		if (!"PAYMENT.PAYOUTS-ITEM.SUCCEEDED".equals(jsonObject.getStr("event_type"))) {
			return "Webhook received";
		}
		
		JSONObject resource = jsonObject.getJSONObject("resource");
		String orderId = resource.getStr("payout_batch_id");
		
		// 3.获取出金申请单
		WithdrawalRecordEntity withdrawalRecordEntity = findWithdrawalRecord(orderId);
		if (withdrawalRecordEntity == null) {
			log.info("未找到匹配的出金申请单:{}", orderId);
			return "Webhook received";
		}
		
		// 4.验证金额或币种是否匹配
		if (!validatePaymentAmountAndCurrency(withdrawalRecordEntity, resource)) {
			return "Webhook received";
		}
		
		// 5.记录出金记录并发起上游webhook
		processWithdrawalRecord(withdrawalRecordEntity, jsonObject);
		
		return "Webhook received";
		
	}
	
	@Override
	public String paypalPayoutFailCallback(String payload, HttpServletRequest request) {
		// 1. 签名验证逻辑
		if (!verifyPayPalWebhookSignature(request, payload)) {
			log.info("paypalOrderApproved: Signature verification failed!");
			return "Signature verification failed!";
		}
		
		JSONObject jsonObject = JSONUtil.parseObj(payload);
		
		// 2.事件类型校验
		if (!"PAYMENT.PAYOUTS-ITEM.SUCCEEDED".equals(jsonObject.getStr("event_type"))) {
			return "Webhook received";
		}
		
		return "";
	}
	
	// 签名验证逻辑
	private boolean verifyPayPalWebhookSignature(HttpServletRequest request, String payload) {
		// 签名验证
		String webhookId = appConfig.getPaypalPayoutsItemWebhookId();
		String transmissionId = request.getHeader("paypal-transmission-id");
		String timeStamp = request.getHeader("paypal-transmission-time");
		String certUrl = request.getHeader("paypal-cert-url");
		String transmissionSig = request.getHeader("paypal-transmission-sig");
		String authAlgo = request.getHeader("paypal-auth-algo");
		return PayPalSignatureUtil.verifySignature(payload, transmissionId, timeStamp, webhookId, certUrl, transmissionSig, authAlgo);
	}
	
	// 查询出金申请单逻辑
	private WithdrawalRecordEntity findWithdrawalRecord(String orderId) {
		return withdrawalRecordService.lambdaQuery()
				.eq(WithdrawalRecordEntity::getTransactionId, orderId)
				.eq(WithdrawalRecordEntity::getChannelSubType, ChannelSubTypeEnum.PAY_PAL.getCode())
				.eq(WithdrawalRecordEntity::getStatus, WithdrawalRecordStatusEnum.ITEM_3.getCode())
				.one();
	}
	
	// 验证支付金额和币种逻辑
	private boolean validatePaymentAmountAndCurrency(WithdrawalRecordEntity withdrawalRecordEntity, JSONObject resource) {
		// 获取订单信息
		JSONObject payoutItem = resource.getJSONObject("payout_item");
		JSONObject amount = payoutItem.getJSONObject("amount");
		String value = amount.getStr("value");
		String currency = amount.getStr("currency");
		
		// 判断支付金额或币种是否匹配
		if (withdrawalRecordEntity.getAmount().compareTo(new BigDecimal(value)) != 0 || !withdrawalRecordEntity.getAssetName().equals(currency)) {
			log.error("支付金额或币种不匹配:{}", withdrawalRecordEntity.getTransactionId());
			return false;
		}
		return true;
	}
	
	// 处理出金申请单逻辑
	private void processWithdrawalRecord(WithdrawalRecordEntity withdrawalRecordEntity, JSONObject jsonObject) {
		JSONObject resource = jsonObject.getJSONObject("resource");
		JSONObject payoutItem = resource.getJSONObject("payout_item");
		JSONObject amount = payoutItem.getJSONObject("amount");
		String value = amount.getStr("value");
		String orderId = resource.getStr("payout_batch_id");
		// 获取支付人信息
		String emailAddress = payoutItem.getStr("receiver");
		String captureId = jsonObject.getStr("id");
		
		JSONObject payoutItemFee = resource.getJSONObject("payout_item_fee");
		String feeValue = payoutItemFee.getStr("value");
		
		// 生成出金明细
		MerchantWalletEntity walletEntity = merchantWalletService.getById(withdrawalRecordEntity.getWalletId());
		WithdrawalRecordDetailEntity withdrawalRecordDetailEntity = new WithdrawalRecordDetailEntity();
		withdrawalRecordDetailEntity.setRecordId(withdrawalRecordEntity.getId());
		withdrawalRecordDetailEntity.setAssetName(withdrawalRecordEntity.getAssetName());
		withdrawalRecordDetailEntity.setNetProtocol(withdrawalRecordEntity.getNetProtocol());
		withdrawalRecordDetailEntity.setSourceAddress(withdrawalRecordEntity.getSourceAddress());
		withdrawalRecordDetailEntity.setDestinationAddress(emailAddress);
		withdrawalRecordDetailEntity.setMerchantId(withdrawalRecordEntity.getMerchantId());
		withdrawalRecordDetailEntity.setMerchantName(withdrawalRecordEntity.getMerchantName());
		withdrawalRecordDetailEntity.setTxHash(orderId + "_" + captureId);
		withdrawalRecordDetailEntity.setAmount(new BigDecimal(value));
		withdrawalRecordDetailEntity.setNetworkFee(new BigDecimal(feeValue));
		withdrawalRecordDetailEntity.setServiceFee(BigDecimal.ZERO);
		withdrawalRecordDetailEntity.setAddrBalance(walletEntity.getBalance());
		withdrawalRecordDetailEntity.setRate(BigDecimal.ZERO);
		withdrawalRecordDetailEntity.setFeeRate(BigDecimal.ZERO);
		withdrawalRecordDetailEntity.setStatus(WithdrawalDetailStausEnum.ITEM_5.getCode());
		withdrawalRecordDetailEntity.setChannelSubType(ChannelSubTypeEnum.PAY_PAL.getCode());
		
		// 保存出金明细
		withdrawalRecordDetailService.save(withdrawalRecordDetailEntity);
		
		// 更新出金申请单状态
		withdrawalRecordService.lambdaUpdate().set(WithdrawalRecordEntity::getStatus, WithdrawalRecordStatusEnum.ITEM_4.getCode())
				.set(WithdrawalRecordEntity::getGasFee, withdrawalRecordDetailEntity.getNetworkFee())
				.eq(WithdrawalRecordEntity::getId, withdrawalRecordEntity.getId()).update();
		
		// 解冻并扣除出金金额
		withdrawalRecordService.legalTenderUnfreezeAndChangeBalance(withdrawalRecordEntity, ChangeEventTypeEnum.WITHDRAWAL_SUCCESS);
		
		//监控
		monitorWithdrawal(withdrawalRecordDetailEntity);
		
		//webhook上游回调
		triggerWebhookEvent(withdrawalRecordEntity);
	}
	
	// 监控出金情况
	private void monitorWithdrawal(WithdrawalRecordDetailEntity withdrawalRecordDetailEntity) {
		//资产入金金额超7天均值指标监控
		JSONObject withdrawal7AvgAmount = new JSONObject().put("Service", "payment").put("MonitorKey", "withdrawalAmount")
				.put("amount", withdrawalRecordDetailEntity.getAmount())
				.put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
		MonitorLogUtil.log(withdrawal7AvgAmount);
		//完全入金状态订单时间相连指标监控
		JSONObject completeWithdrawal = new JSONObject().put("Service", "payment").put("MonitorKey", "completeWithdrawal")
				.put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
		MonitorLogUtil.log(completeWithdrawal);
	}
	
	//触发上游回调
	private void triggerWebhookEvent(WithdrawalRecordEntity withdrawalRecordEntity) {
		WebhookEventEntity webhookEventEntity = new WebhookEventEntity();
		webhookEventEntity.setEvent(WebhookEventConstants.WITHDRAWAL_EVENT);
		webhookEventEntity.setData(JSONUtil.toJsonStr(new WithdrawalEventDto(
				withdrawalRecordEntity.getTrackingId(),
				WithdrawalRecordStatusEnum.ITEM_4.getCode(),
				withdrawalRecordEntity.getAmount(),
				withdrawalRecordEntity.getStayReason()
		)));
		webhookEventEntity.setTrackingId(withdrawalRecordEntity.getTrackingId());
		webhookEventEntity.setWebhookUrl(withdrawalRecordEntity.getWebhookUrl());
		webhookEventEntity.setMerchantId(withdrawalRecordEntity.getMerchantId());
		webhookEventService.asyncSendWebhookEvent(webhookEventEntity);
	}
	
}
