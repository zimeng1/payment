package com.mc.payment.core.service.model.req;

import com.mc.payment.common.base.BaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Marty
 * @since 2024/4/28 15:07
 */
@Data
public class WithdrawalRecordIdReq extends BaseReq {

    @NotBlank(message = "[出金记录id]不能为空")
    @Schema(title = "出金记录id")
    private String id;

}
