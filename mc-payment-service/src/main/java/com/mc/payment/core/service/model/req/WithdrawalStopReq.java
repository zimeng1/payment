package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Schema(description = "余额不足终止出金实体")
public class WithdrawalStopReq {

    @Schema(title = "出金记录id", requiredMode = Schema.RequiredMode.REQUIRED)
    @Length(max = 50, message = "[出金记录id]长度不能超过50")
    @NotBlank(message = "[出金记录id]不能为空")
    private String id;

}
