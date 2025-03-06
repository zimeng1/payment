package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Data
public class WebhookEventQueryReq {

    @Schema(title = "事件类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "[事件类型]不能为空")
    private String event;

    @Schema(title = "跟踪id,申请方提供唯一跟踪ID以查询处理结果，长度限制为50个字符", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 50, message = "跟踪ID长度不能超过50个字符")
    @NotBlank(message = "[跟踪id]不能为空")
    private String trackingId;
}
