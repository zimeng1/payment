package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class DepositStatusReq {
    @Schema(title = "收银页面url参数k", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "[id]不能为空")
    @Length(max = 128, message = "[id]长度不能超过128")
    private String encryptId;
}
