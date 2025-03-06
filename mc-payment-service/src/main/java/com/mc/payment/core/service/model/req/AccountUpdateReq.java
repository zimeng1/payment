package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author conor
 * @since 2024/2/2 15:33:24
 */
@Data
@Schema(title = "账户-修改参数实体")
public class AccountUpdateReq extends AccountSaveReq {
    private static final long serialVersionUID = 8607841364328432414L;

    @Schema(title = "账户id")
    @NotBlank(message = "[账户id]不能为空")
    private String id;

}
