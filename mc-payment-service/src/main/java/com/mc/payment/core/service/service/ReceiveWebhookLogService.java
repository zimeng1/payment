package com.mc.payment.core.service.service;

import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.ReceiveWebhookLogEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.core.service.model.req.ReceiveWebhookLogReq;

import java.util.List;

/**
* @author Conor
* @description 针对表【mcp_receive_webhook_log(外部webhook记录表)】的数据库操作Service
* @createDate 2024-08-20 22:53:02
*/
public interface ReceiveWebhookLogService extends IService<ReceiveWebhookLogEntity> {

    void asyncSaveLog(ReceiveWebhookLogEntity receiveWebhookLogEntity);

    BasePageRsp<ReceiveWebhookLogEntity> page(ReceiveWebhookLogReq req);

}
