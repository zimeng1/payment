package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DepositMockReq {
    @Schema(title = "入金记录id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "[入金记录id]不能为空")
    private String id;

    @Schema(title = "状态,[1:部分入金,2:完全入金,4:请求失效]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "[状态]不能为空")
    private Integer status;

    @Schema(title = "操作原因")
    @Size(max = 20, message = "[操作原因]最大长度为20")
    private String remark;
}
