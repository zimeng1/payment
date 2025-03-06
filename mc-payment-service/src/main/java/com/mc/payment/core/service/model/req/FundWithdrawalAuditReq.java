package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "资金管理出金审核")
public class FundWithdrawalAuditReq {

    @Schema(title = "出金记录id", requiredMode = Schema.RequiredMode.REQUIRED)
    @Length(max = 50, message = "[出金记录id]长度不能超过50")
    @NotBlank(message = "[出金记录id]不能为空")
    private String id;

    @Schema(title = "审核结果,[1:通过,2:不通过]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "[审核结果]不能为空")
    private Integer auditStatus;

}
