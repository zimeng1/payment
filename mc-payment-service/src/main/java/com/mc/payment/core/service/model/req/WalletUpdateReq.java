package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Conor
 * @since 2024/4/15 下午2:34
 */
@Data
@Schema(title = "钱包修改参数实体")
public class WalletUpdateReq extends WalletSaveReq {
    private static final long serialVersionUID = -2088829463512905968L;
    @Schema(title = "钱包id")
    @NotBlank(message = "[钱包id]不能为空")
    private String id;
}
