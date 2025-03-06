package com.mc.payment.core.service.model.rsp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Conor
 * @since 2024/4/29 下午3:36
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalCheckRsp {
    @Schema(title = "是否可以出金,[0:否,1:是]")
    private Integer canWithdrawal;
}
