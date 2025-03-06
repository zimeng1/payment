package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(title = "通道成本修改参数实体")
public class ChannelCostUpdateReq extends ChannelCostSaveReq {
    private static final long serialVersionUID = -6200304811032124199L;
    @Schema(title = "通道成本id")
    @NotBlank(message = "[通道成本id]不能为空")
    @Length(max = 20, message = "[通道成本id]长度不能超过20")
    private String id;


}
