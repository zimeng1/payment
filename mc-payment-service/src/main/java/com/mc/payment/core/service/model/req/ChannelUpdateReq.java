package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(title = "通道配置修改参数实体")
public class ChannelUpdateReq extends ChannelSaveReq {
    private static final long serialVersionUID = -6200304811032124199L;

    @Schema(title = "通道id")
    @NotBlank(message = "[通道id]不能为空")
    private String id;

}
