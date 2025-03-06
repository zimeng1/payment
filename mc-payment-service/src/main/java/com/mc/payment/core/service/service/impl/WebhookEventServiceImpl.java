package com.mc.payment.core.service.service.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.api.util.AKSKUtil;
import com.mc.payment.core.service.entity.MerchantEntity;
import com.mc.payment.core.service.entity.WebhookEventEntity;
import com.mc.payment.core.service.mapper.WebhookEventMapper;
import com.mc.payment.core.service.service.IMerchantService;
import com.mc.payment.core.service.service.IWebhookEventService;
import com.mc.payment.core.service.util.ThreadTraceIdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author conor
 * @since 2024-04-18 15:59:10
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class WebhookEventServiceImpl extends ServiceImpl<WebhookEventMapper, WebhookEventEntity> implements IWebhookEventService {
    private final IMerchantService merchantService;

    @Override
    public void asyncSendWebhookEvent(WebhookEventEntity entity) {
        ThreadTraceIdUtil.execute(() -> {
            ThreadUtil.safeSleep(3000);
            String webhookUrl = entity.getWebhookUrl();
            String postResponse = null;
            String paramJsonStr = null;
            try {
                if (!Validator.isUrl(webhookUrl)) {
                    log.error("商户webhookUrl格式错误,merchantId:{},webhookUrl:{}", entity.getMerchantId(), webhookUrl);
                    return;
                }
                this.save(entity);
                MerchantEntity merchantEntity = merchantService.getById(entity.getMerchantId());
                // 组装webhook参数
                Map<String, Object> param = new HashMap<>();
                param.put("event", entity.getEvent());
                param.put("data", entity.getData());

                // 触发webhook
                paramJsonStr = JSONUtil.toJsonStr(param);
                postResponse = HttpUtil.createPost(webhookUrl).body(paramJsonStr).header("X-Signature", AKSKUtil.calculateHMAC(paramJsonStr, merchantEntity.getSecretKey())).execute().body();
                entity.setStatus("Webhook received".equals(postResponse) ? 1 : 2);
            } catch (Exception e) {
                log.error("触发webhook失败,URL:{},param:{}", webhookUrl, paramJsonStr, e);
                entity.setStatus(2);
            } finally {
                log.info("Send Webhook URL:{},param:{},res:{}", webhookUrl, paramJsonStr, postResponse);
                this.updateById(entity);
            }
        });
    }
}
