package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.payment.core.service.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author conor
 * @since 2024-04-18 15:59:10
 */
@Getter
@Setter
@TableName("mcp_webhook_event")
@Schema(title = "WebhookEventEntity对象", description = "")
public class WebhookEventEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @Schema(title = "事件类型")
    @TableField("`event`")
    private String event;

    @Schema(title = "商户id")
    @TableField("merchant_id")
    private String merchantId;

    @Schema(title = "跟踪id,申请方提供唯一跟踪ID以查询处理结果")
    @TableField("tracking_id")
    private String trackingId;

    @Schema(title = "事件数据,具体参数由事件类型决定")
    @TableField("`data`")
    private String data;

    @Schema(title = "Webhook回调地址")
    @TableField("`webhook_url`")
    private String webhookUrl;

    @Schema(title = "状态,[0:处理中,1:处理成功,2:处理失败]")
    @TableField("`status`")
    private Integer status;
}
