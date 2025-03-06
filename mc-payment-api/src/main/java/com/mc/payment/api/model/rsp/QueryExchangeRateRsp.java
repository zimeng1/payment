package com.mc.payment.api.model.rsp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryExchangeRateRsp {
    @Schema(title = "汇率", example = "1.0")
    private BigDecimal exchangeRate;
}
