package com.mc.payment.api.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalEventDto {
    @Schema(title = "商户跟踪id")
    private String trackingId;

    @Schema(title = "出金状态,[0:已提交,1:待审核,2:余额不足,3:出金中,4:出金完成,5:已拒绝,6:出金错误,7,终止出金]")
    private Integer status;

    @Schema(title = "金额")
    private BigDecimal amount;

    @Schema(title = "停留原因")
    private String stayReason;
}
