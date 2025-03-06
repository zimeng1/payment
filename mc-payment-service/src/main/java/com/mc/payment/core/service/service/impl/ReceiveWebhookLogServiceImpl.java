package com.mc.payment.core.service.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.ReceiveWebhookLogEntity;
import com.mc.payment.core.service.mapper.ReceiveWebhookLogMapper;
import com.mc.payment.core.service.model.req.ReceiveWebhookLogReq;
import com.mc.payment.core.service.service.ReceiveWebhookLogService;
import com.mc.payment.core.service.util.ThreadTraceIdUtil;
import org.springframework.stereotype.Service;

/**
 * @author Conor
 * @description 针对表【mcp_receive_webhook_log(外部webhook记录表)】的数据库操作Service实现
 * @createDate 2024-08-20 22:53:02
 */
@Service
public class ReceiveWebhookLogServiceImpl extends ServiceImpl<ReceiveWebhookLogMapper, ReceiveWebhookLogEntity>
        implements ReceiveWebhookLogService {

    @Override
    public void asyncSaveLog(ReceiveWebhookLogEntity receiveWebhookLogEntity) {
        ThreadTraceIdUtil.execute(() -> this.save(receiveWebhookLogEntity));
    }

    @Override
    public BasePageRsp<ReceiveWebhookLogEntity> page(ReceiveWebhookLogReq req) {
        Page<ReceiveWebhookLogEntity> page = new Page<>(req.getCurrent(), req.getSize());
        this.page(page, Wrappers.lambdaQuery(ReceiveWebhookLogEntity.class)
                .eq(StrUtil.isNotBlank(req.getWebhookType()), ReceiveWebhookLogEntity::getWebhookType, req.getWebhookType())
                .like(StrUtil.isNotBlank(req.getRequestBody()), ReceiveWebhookLogEntity::getRequestBody, req.getRequestBody())
                .like(StrUtil.isNotBlank(req.getHeaders()), ReceiveWebhookLogEntity::getHeaders, req.getHeaders())
                .eq(StrUtil.isNotBlank(req.getIpAddress()), ReceiveWebhookLogEntity::getIpAddress, req.getIpAddress())
                .eq(StrUtil.isNotBlank(req.getSignature()), ReceiveWebhookLogEntity::getSignature, req.getSignature())
                .like(StrUtil.isNotBlank(req.getResponseBody()), ReceiveWebhookLogEntity::getResponseBody, req.getResponseBody())
                .gt(req.getReceiveTimeLeft() != null, ReceiveWebhookLogEntity::getReceiveTime, req.getReceiveTimeLeft())
                .lt(req.getReceiveTimeRight() != null, ReceiveWebhookLogEntity::getReceiveTime, req.getReceiveTimeRight())
                .eq(req.getExecutionTime() != null, ReceiveWebhookLogEntity::getExecutionTime, req.getExecutionTime())
                .gt(req.getCreateTimeLeft() != null, ReceiveWebhookLogEntity::getCreateTime, req.getCreateTimeLeft())
                .lt(req.getCreateTimeRight() != null, ReceiveWebhookLogEntity::getCreateTime, req.getCreateTimeRight())
                .orderByDesc(BaseNoLogicalDeleteEntity::getId));
        return BasePageRsp.valueOf(page);
    }

}