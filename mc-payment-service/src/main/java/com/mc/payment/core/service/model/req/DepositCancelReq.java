package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Data
@Schema(title = "取消入金申请参数")
public class DepositCancelReq {
    @Schema(title = "取消类型,0:撤销申请,1:主动回收账号(用于设置了不自动回收的入金申请场景)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "[取消类型]不能为空")
    @Range(min = 0, max = 1, message = "[取消类型]必须为[0:撤销申请,1:主动回收账号]")
    private Integer cancelType;

    @Schema(title = "跟踪id,申请方提供唯一跟踪ID以查询处理结果，长度限制为50个字符", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(max = 50, message = "跟踪ID长度不能超过50个字符")
    @NotBlank(message = "[跟踪id]不能为空")
    private String trackingId;
}
