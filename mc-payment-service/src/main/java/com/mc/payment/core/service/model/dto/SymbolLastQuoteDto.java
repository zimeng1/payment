package com.mc.payment.core.service.model.dto;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 品种的最新报价
 *
 * @author Conor
 * @since 2024/5/16 上午10:48
 */
@Data
public class SymbolLastQuoteDto {
    // s (string, optional): Symbol，品种 ,
    //b (number, optional): Bid，卖价 ,
    //a (number, optional): Ask，买价 ,
    //t (string, optional): TickTime，报价更新时间

    @Alias("s")
    private String symbol;
    @Alias("b")
    private BigDecimal bid;
    @Alias("a")
    private BigDecimal ask;
    @Alias("t")
    private String tickTime;
}
