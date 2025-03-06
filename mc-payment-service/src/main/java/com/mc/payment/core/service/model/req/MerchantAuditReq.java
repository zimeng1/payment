package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(title = "商户出入金审核修改实体")
public class MerchantAuditReq {

    @Schema(title = "商户id")
    @NotBlank(message = "[商户id]不能为空")
    private String id;

    @Schema(title = "是否开启审核 0否,1是")
    @NotNull(message = "状态不能为空")
    private Integer auditStatus;
}
