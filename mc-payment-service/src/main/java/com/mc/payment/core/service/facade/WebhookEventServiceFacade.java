package com.mc.payment.core.service.facade;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mc.payment.api.util.AKSKUtil;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.constant.WebhookEventConstants;
import com.mc.payment.core.service.entity.*;
import com.mc.payment.core.service.model.dto.BaseWebhookEventVo;
import com.mc.payment.core.service.model.dto.CryptoDepositEventVo;
import com.mc.payment.core.service.model.dto.CryptoWithdrawalEventVo;
import com.mc.payment.core.service.model.dto.WalletBalanceEventVo;
import com.mc.payment.core.service.service.IDepositRecordDetailService;
import com.mc.payment.core.service.service.IMerchantService;
import com.mc.payment.core.service.service.IWebhookEventService;
import com.mc.payment.core.service.util.ThreadTraceIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Conor
 * @since 2024/4/18 下午4:27
 */
@Slf4j
@Component
public class WebhookEventServiceFacade {
    private final IMerchantService merchantService;
    private final IWebhookEventService webhookEventService;
    private final IDepositRecordDetailService depositRecordDetailService;


    public WebhookEventServiceFacade(IMerchantService merchantService, IWebhookEventService webhookEventService, IDepositRecordDetailService depositRecordDetailService) {
        this.merchantService = merchantService;
        this.webhookEventService = webhookEventService;
        this.depositRecordDetailService = depositRecordDetailService;
    }


    /**
     * 保存事件并且出发webhook
     *
     * @param recordEntity
     */
    private void saveAndTriggerWebhook(DepositRecordEntity recordEntity) {
        List<DepositRecordDetailEntity> list = depositRecordDetailService.list(recordEntity.getId());
        CryptoDepositEventVo eventVo = CryptoDepositEventVo.valueOf(recordEntity);
        eventVo.setDetailList(list.stream().map(DepositRecordDetailEntity::valueOf).toList());
        String event = WebhookEventConstants.DEPOSIT_EVENT;
        String merchantId = recordEntity.getMerchantId();
        String trackingId = recordEntity.getTrackingId();
        String data = JSONUtil.toJsonStr(eventVo);
        // 查询商户的WebhookURL
        extracted(merchantId, event, data, trackingId, eventVo, recordEntity.getWebhookUrl());
    }

    /**
     * 异步触发 保存事件并且出发webhook
     */
    public void asyncSaveAndTriggerWebhook(DepositRecordEntity recordEntity) {
        ThreadTraceIdUtil.execute(() -> {
            this.saveAndTriggerWebhook(recordEntity);
        });
    }

    public void asyncSaveAndTriggerWebhook(WithdrawalRecordEntity recordEntity) {
        ThreadTraceIdUtil.execute(() -> {
            this.saveAndTriggerWebhook(recordEntity);
        });
    }

    public void asyncSaveAndTriggerWebhook(String merchantId, WalletBalanceEventVo walletBalanceEventVo) {
        ThreadTraceIdUtil.execute(() -> {
            this.saveAndTriggerWebhook(merchantId, walletBalanceEventVo);
        });
    }

    public void asyncSaveAndTriggerWebhook(List<WithdrawalRecordEntity> recordEntityList) {
        ThreadTraceIdUtil.execute(() -> {
            // 触发事件
            for (WithdrawalRecordEntity entity : recordEntityList) {
                this.saveAndTriggerWebhook(entity);
            }
        });
    }

    /**
     * 保存事件并且出发webhook
     *
     * @param recordEntity
     */
    public void saveAndTriggerWebhook(WithdrawalRecordEntity recordEntity) {
        BaseWebhookEventVo eventVo = CryptoWithdrawalEventVo.valueOf(recordEntity);
        String event = WebhookEventConstants.WITHDRAWAL_EVENT;
        String merchantId = recordEntity.getMerchantId();
        String trackingId = recordEntity.getTrackingId();
        String data = JSONUtil.toJsonStr(eventVo);
        extracted(merchantId, event, data, trackingId, eventVo, recordEntity.getWebhookUrl());
    }


    private void saveAndTriggerWebhook(String merchantId, WalletBalanceEventVo walletBalanceEventVo) {
        String event = WebhookEventConstants.WALLET_BALANCE_EVENT;
        String trackingId = IdUtil.fastSimpleUUID();
        String data = JSONUtil.toJsonStr(walletBalanceEventVo);
        extracted(merchantId, event, data, trackingId, walletBalanceEventVo, null);
    }

    private void extracted(String merchantId, String event, String data, String trackingId, BaseWebhookEventVo eventVo, String webhookUrl) {
        MerchantEntity merchantEntity = merchantService.getById(merchantId);
        if (StrUtil.isBlank(webhookUrl)) {
            // 查询商户的WebhookURL
            webhookUrl = merchantEntity.getWebhookUrl();
        }
        if (!Validator.isUrl(webhookUrl)) {
            log.error("商户webhookUrl格式错误,merchantId:{},webhookUrl:{}", merchantId, webhookUrl);
            return;
        }

        // 保存事件
        WebhookEventEntity entity = new WebhookEventEntity();
        try {
            //long uuid = IdUtil.getSnowflake(eventVo.getShardIndex(), 0).nextId();
            //entity.setId(String.valueOf(uuid));// 防止重复id, 用雪花算法生成

            entity.setEvent(event);
            entity.setData(data);
            entity.setMerchantId(merchantId);
            entity.setTrackingId(trackingId);
            entity.setWebhookUrl(webhookUrl);
            webhookEventService.save(entity);
        } catch (Exception e) {
            log.error("保存webhook事件失败,merchantId:{},event:{},data:{},trackingId:{},webhookUrl:{}", merchantId, event, data, trackingId, webhookUrl, e);
        }
        // 组装webhook参数
        Map<String, Object> param = new HashMap<>();
        param.put("event", event);
        param.put("data", eventVo);
        // 触发webhook
        String postResponse = null;
        String paramJsonStr = null;
        try {
            paramJsonStr = JSONUtil.toJsonStr(param);
//            postResponse = HttpUtil.post(webhookUrl, paramJsonStr);
            postResponse = HttpUtil.createPost(webhookUrl).body(paramJsonStr).header("X-Signature",
                    AKSKUtil.calculateHMAC(paramJsonStr, merchantEntity.getSecretKey())).execute().body();

            entity.setStatus("Webhook received".equals(postResponse) ? 1 : 2);
        } catch (Exception e) {
            log.error("触发webhook失败,URL:{},param:{}", webhookUrl, paramJsonStr, e);
            entity.setStatus(2);
        } finally {
            log.info("Webhook URL:{},param:{},res:{}", webhookUrl, paramJsonStr, postResponse);
            webhookEventService.update(Wrappers.lambdaUpdate(WebhookEventEntity.class).eq(BaseNoLogicalDeleteEntity::getId, entity.getId()).set(WebhookEventEntity::getStatus, entity.getStatus()));
        }
    }
}
