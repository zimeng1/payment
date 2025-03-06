package com.mc.payment.core.service.mapper;

import com.mc.payment.core.service.entity.ReceiveWebhookLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Conor
* @description 针对表【mcp_receive_webhook_log(外部webhook记录表)】的数据库操作Mapper
* @createDate 2024-08-20 22:53:02
* @Entity com.mc.payment.core.service.entity.ReceiveWebhookLogEntity
*/
public interface ReceiveWebhookLogMapper extends BaseMapper<ReceiveWebhookLogEntity> {
    List<String> webHookTypeList();
}




