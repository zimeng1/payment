package com.mc.payment.core.service.model.rsp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Conor
 * @since 2024/4/25 下午4:25
 */
@Data
public class EstimateFeeRsp {

    @Schema(title = "预估费,计算完已换汇率 :chainTransactionFee * exchangeRate")
    private BigDecimal estimateFee;

    @Schema(title = "链上交易费 计算完未转换汇率")
    private BigDecimal chainTransactionFee;

    @Deprecated
    @Schema(title = "汇率")
    private BigDecimal exchangeRate;
}
