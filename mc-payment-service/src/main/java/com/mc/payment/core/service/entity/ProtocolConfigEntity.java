package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.model.req.ProtocolConfigReq;
import lombok.Data;

import java.io.Serializable;

/**
 * 协议钱包地址正则表达式配置表
 * @TableName mcp_protocol_config
 */
@TableName(value ="mcp_protocol_config")
@Data
public class ProtocolConfigEntity extends BaseNoLogicalDeleteEntity implements Serializable {
    /**
     * 网络协议
     */
    @TableField(value = "net_protocol")
    private String netProtocol;

    /**
     * 正则表达式
     */
    @TableField(value = "regular_expression")
    private String regularExpression;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public static ProtocolConfigEntity valueOf(ProtocolConfigReq protocolConfigReq) {
        ProtocolConfigEntity protocolConfigEntity = new ProtocolConfigEntity();
        protocolConfigEntity.setId(protocolConfigReq.getId());
        protocolConfigEntity.setNetProtocol(protocolConfigReq.getNetProtocol());
        protocolConfigEntity.setRegularExpression(protocolConfigReq.getRegularExpression());
        return protocolConfigEntity;
    }

}