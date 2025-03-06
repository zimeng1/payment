package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 外部webhook记录表
 *
 * @TableName mcp_receive_webhook_log
 */
@TableName(value = "mcp_receive_webhook_log")
@Data
public class ReceiveWebhookLogEntity extends BaseNoLogicalDeleteEntity implements Serializable {
    /**
     * Webhook类型
     */
    @TableField(value = "webhook_type")
    private String webhookType;

    /**
     * 接收到的数据
     */
    @TableField(value = "request_body")
    private String requestBody;

    /**
     * 请求头
     */
    @TableField(value = "headers")
    private String headers;

    /**
     * 发起方的ip地址
     */
    @TableField(value = "ip_address")
    private String ipAddress;

    /**
     * 签名
     */
    @TableField(value = "signature")
    private String signature;

    /**
     * 响应内容
     */
    @TableField(value = "response_body")
    private String responseBody;
    /**
     * 异常信息
     */
    @TableField(value = "exception_message")
    private String exceptionMessage;

    /**
     * 接收时间
     */
    @TableField(value = "receive_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date receiveTime;

    /**
     * 执行耗时ms
     */
    @TableField(value = "execution_time")
    private Long executionTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}