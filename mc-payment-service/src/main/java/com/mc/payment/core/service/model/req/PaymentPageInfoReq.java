package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author conor
 * @since 2024/7/24 下午9:56:26
 */
@Data
public class PaymentPageInfoReq {
    @Schema(title = "支付确认页面id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "[encryptId]不能为空")
    private String encryptId;

}
