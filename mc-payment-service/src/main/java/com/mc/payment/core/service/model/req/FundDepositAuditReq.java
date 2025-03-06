package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "入金审核")
public class FundDepositAuditReq {

    @Schema(title = "入金记录id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "[入金记录id]不能为空")
    private String id;

    @Schema(title = "审核结果,[1:通过,2:不通过,]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "[审核结果]不能为空")
    private Integer auditStatus;

}
