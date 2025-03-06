package com.mc.payment.api.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class QueryWithdrawalReq{
    @Schema(title = "商户跟踪id",description = "各个商户的每次交易操作应保证唯一", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "[商户跟踪id]不能为空")
    @Size(max = 50, message = "[商户跟踪id]长度不能超过50")
    private String trackingId;
}
