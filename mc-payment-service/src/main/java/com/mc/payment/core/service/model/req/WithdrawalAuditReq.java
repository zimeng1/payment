package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "出金审核")
public class WithdrawalAuditReq {

    @Schema(title = "跟踪id,申请方提供唯一跟踪ID以查询处理结果，长度限制为50个字符", requiredMode = Schema.RequiredMode.REQUIRED)
    @Length(max = 50, message = "[跟踪id]长度不能超过50")
    @NotBlank(message = "[跟踪id]不能为空")
    private String trackingId;

    @Schema(title = "审核结果,[1:通过,2:不通过]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "[审核结果]不能为空")
    @Range(min = 0, max = 1, message = "[审核结果]必须为[1:通过,2:不通过]")
    private Integer auditStatus;


    @Schema(title = "审核备注,长度不能超过255")
    @Length(max = 255, message = "[审核备注]长度不能超过255")
    private String remark;


}
