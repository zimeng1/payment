package com.mc.payment.core.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 阶梯费率
 *
 * @author Conor
 * @since 2025-01-10 14:10:25.203
 */
@Data
public class TieredRateDto {
    /**
     * 无限大符号
     */
    public static final String INFINITY_SYMBOL = "∞";
    /**
     * 第一个起始金额
     */
    public static final String FIRST_START_AMOUNT = "0";

    @Schema(title = "起始金额")
    private String startAmount;
    @Schema(title = "结束金额")
    private String endAmount;
    @Schema(title = "费率")
    private String rate;
}
