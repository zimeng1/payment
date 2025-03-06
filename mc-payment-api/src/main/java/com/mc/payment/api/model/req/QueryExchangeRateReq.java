package com.mc.payment.api.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class QueryExchangeRateReq {
    @Schema(title = "资产类型,[0:加密货币,1:法币]", description = "默认为1,法币,乱传则默认为加密货币")
    private Integer assetType;
    @Schema(title = "原始币种", example = "BTC")
    @NotBlank(message = "[原始币种]不能为空")
    @Size(max = 20, message = "[原始币种]长度不能超过50")
    private String baseCurrency;
    @Schema(title = "目标币种", example = "USDT")
    @NotBlank(message = "[目标币种]不能为空")
    @Size(max = 20, message = "[目标币种]长度不能超过50")
    private String targetCurrency;
}
