package com.mc.payment.core.service.model.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mc.payment.core.service.base.BasePageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * ReceiveWebhookLogReq
 *
 * @author GZM
 * @since 2024/10/13 下午10:37
 */
@Data
@Schema(title = "外部webhook日志-分页查询参数实体")
public class ReceiveWebhookLogReq extends BasePageReq {

    @Schema(title="Webhook类型")
    private String webhookType;

    @Schema(title="接收到的数据")
    private String requestBody;

    @Schema(title="请求头")
    private String headers;

    @Schema(title="发起方的ip地址")
    private String ipAddress;

    @Schema(title="签名")
    private String signature;

    @Schema(title="响应内容")
    private String responseBody;

    @Schema(title="接收时间-起始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date receiveTimeLeft;

    @Schema(title="接收时间-结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date receiveTimeRight;

    @Schema(title="执行耗时")
    private Integer executionTime;

    @Schema(title="创建时间-起始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTimeLeft;

    @Schema(title="创建时间-结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTimeRight;

}