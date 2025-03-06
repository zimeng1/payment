package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.core.service.entity.WebhookEventEntity;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author conor
 * @since 2024-04-18 15:59:10
 */
public interface IWebhookEventService extends IService<WebhookEventEntity> {

    void asyncSendWebhookEvent(WebhookEventEntity webhookEventEntity);
}
