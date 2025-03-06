package com.mc.payment.api.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositEventDto {
    @Schema(title = "商户跟踪id")
    private String trackingId;

    @Schema(title = "入金状态,[0:待入金,1:部分入金,2:完全入金,3:撤销入金,4:请求失败]")
    private Integer status;

    @Schema(title = "金额")
    private BigDecimal amount;
}
