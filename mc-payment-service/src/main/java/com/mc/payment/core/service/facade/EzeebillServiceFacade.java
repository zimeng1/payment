package com.mc.payment.core.service.facade;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.mc.payment.api.model.dto.WithdrawalEventDto;
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
import com.mc.payment.gateway.channels.ezeebill.config.EzeebillConfig;
import com.mc.payment.gateway.channels.ezeebill.model.req.EzeebillWithdrawalCallBackReq;
import com.mc.payment.gateway.channels.ezeebill.util.EzeebillUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * EzeebillServiceFacade
 *
 * @author GZM
 * @since 2024/11/19 上午10:36
 */
@Data
@RequiredArgsConstructor
@Component
@Slf4j
public class EzeebillServiceFacade implements IEzeebillServiceFacade {
	private final IWithdrawalRecordService withdrawalRecordService;
	private final IWebhookEventService webhookEventService;
	private final MerchantWalletService merchantWalletService;
	private final IWithdrawalRecordDetailService withdrawalRecordDetailService;
	private final EzeebillConfig ezeebillConfig;
	private final IPayPalServiceFacade ezeebillServiceFacade;

	public String processWithdrawalCallback(EzeebillWithdrawalCallBackReq req) {
		//1.合法性校验
		if (!isValidSignature(req)) {
			return "Signature verification failed!";
		}

		//2.匹配出金申请单
		WithdrawalRecordEntity withdrawalRecordEntity = findWithdrawalRecord(req.getMerch_order_id());
		if (withdrawalRecordEntity == null) {
			log.info("未找到匹配的出金申请单:{}", req.getMerch_order_id());
			return "Fail, No Withdrawal record";
		}

		//3.获取出金记录
		WithdrawalRecordEntity withdrawRecord = withdrawalRecordService.getById(req.getMerch_order_id());
		MerchantWalletEntity walletEntity = merchantWalletService.getById(withdrawRecord.getWalletId());

		//4.生成出金明细
		WithdrawalRecordDetailEntity withdrawalRecordDetail = getWithdrawalRecordDetailEntity(req, withdrawRecord, walletEntity);
		withdrawalRecordDetailService.save(withdrawalRecordDetail);

		//5.判断出金成功状态，处理关联表
		int eventStatus;
		String result = "";
		if (req.isSuccess()) {
			eventStatus = WithdrawalRecordStatusEnum.ITEM_4.getCode();
			handleSuccessWithdrawal(withdrawRecord, eventStatus, req.getTxn_status());
			result = "successful";
		} else {
			eventStatus = WithdrawalRecordStatusEnum.ITEM_6.getCode();
			handleFailedWithdrawal(withdrawRecord, withdrawalRecordDetail, eventStatus, req.getTxn_status());
			result = "fail";
		}

		//6.出金结果回调
		triggerWithdrawalWebhookEvent(withdrawRecord, eventStatus);
		return result;
	}

	private boolean isValidSignature(EzeebillWithdrawalCallBackReq req) {
		Map<String, Object> map = req.convertToMap();
		map.remove("secure_hash");
		Map<String, Object> sortedMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		sortedMap.putAll(map);
		String signature = EzeebillUtil.generateSignature(sortedMap, ezeebillConfig.getHashKey(map.get("currency").toString()));
		if (!StrUtil.equals(signature, req.getSecure_hash())) {
			return false;
		}
		return true;
	}

	private WithdrawalRecordEntity findWithdrawalRecord(String merchOrderId) {
		return withdrawalRecordService.lambdaQuery()
				.eq(WithdrawalRecordEntity::getId, merchOrderId)
				.eq(WithdrawalRecordEntity::getChannelSubType, ChannelSubTypeEnum.EZEEBILL.getCode())
				.eq(WithdrawalRecordEntity::getStatus, WithdrawalRecordStatusEnum.ITEM_3.getCode())
				.one();
	}

	private void handleSuccessWithdrawal(WithdrawalRecordEntity withdrawRecord, int eventStatus, String txnStatus) {
		withdrawalRecordService.lambdaUpdate()
				.set(WithdrawalRecordEntity::getStatus, eventStatus)
				.eq(WithdrawalRecordEntity::getId, withdrawRecord.getId()).update();

		withdrawalRecordService.legalTenderUnfreezeAndChangeBalance(withdrawRecord, ChangeEventTypeEnum.WITHDRAWAL_SUCCESS);
		withdrawMonitorHandle(withdrawRecord, Boolean.TRUE);

		log.info("Ezeebill出金回调成功:{}", txnStatus);
	}

	private void handleFailedWithdrawal(WithdrawalRecordEntity withdrawRecord, WithdrawalRecordDetailEntity withdrawalRecordDetail, int eventStatus, String txnStatus) {
		withdrawalRecordService.lambdaUpdate()
				.set(WithdrawalRecordEntity::getStatus, eventStatus)
				.eq(WithdrawalRecordEntity::getId, withdrawRecord.getId()).update();

		withdrawalRecordDetailService.lambdaUpdate()
				.set(WithdrawalRecordDetailEntity::getStatus, eventStatus)
				.eq(WithdrawalRecordDetailEntity::getId, withdrawalRecordDetail.getId())
				.update();

		withdrawalRecordService.legalTenderUnfreezeAndChangeBalance(withdrawRecord, ChangeEventTypeEnum.WITHDRAWAL_FAIL);
		withdrawMonitorHandle(withdrawRecord, Boolean.FALSE);

		log.error("Ezeebill出金回调异常:{}", txnStatus);
	}

	/**
	 * @param withdrawRecord
	 * @param status         出金失败：false,出金成功：true
	 */
	private void withdrawMonitorHandle(WithdrawalRecordEntity withdrawRecord, boolean status) {
		if (status) {
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
		} else {
			//出金错误状态订单增多指标监控
			JSONObject depositFailMonitor = new JSONObject().put("Service", "payment").put("MonitorKey", "depositFailMonitor")
					.put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
			MonitorLogUtil.log(depositFailMonitor);
		}
	}

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
		withdrawalRecordDetail.setSourceAddress(withdrawRecord.getSourceAddress());
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

	/**
	 * 触发WithdrawalWebhook
	 *
	 * @param withdrawRecord
	 * @param eventStatus
	 */
	private void triggerWithdrawalWebhookEvent(WithdrawalRecordEntity withdrawRecord, Integer eventStatus) {
		// 触发webhook
		WebhookEventEntity webhookEventEntity = new WebhookEventEntity();
		webhookEventEntity.setEvent(WebhookEventConstants.WITHDRAWAL_EVENT);
		webhookEventEntity.setData(JSONUtil.toJsonStr(new WithdrawalEventDto(withdrawRecord.getTrackingId()
				, eventStatus, withdrawRecord.getAmount(), null)));
		webhookEventEntity.setTrackingId(withdrawRecord.getTrackingId());
		webhookEventEntity.setWebhookUrl(withdrawRecord.getWebhookUrl());
		webhookEventEntity.setMerchantId(withdrawRecord.getMerchantId());
		webhookEventService.asyncSendWebhookEvent(webhookEventEntity);
	}

}
