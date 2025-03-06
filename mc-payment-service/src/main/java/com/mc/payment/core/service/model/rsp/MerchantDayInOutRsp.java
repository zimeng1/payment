package com.mc.payment.core.service.model.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mc.payment.core.service.serializer.BigDecimalSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Marty
 * @since 2024/5/8 15:16
 */
@Data
public class MerchantDayInOutRsp implements Serializable {
    @Schema(title = "日期")
    private String day;

    @Schema(title = "入金次数")
    private int depositDayCount = 0;

    @Schema(title = "出金次数")
    private int withdrawalDayCount = 0;

    @JsonFormat(pattern = "#.##", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "入金金额")
    private BigDecimal depositDayAmount = BigDecimal.ZERO;

    @JsonFormat(pattern = "#.##", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "出金金额")
    private BigDecimal withdrawalDayAmount= BigDecimal.ZERO;

    @JsonFormat(pattern = "#.##", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "链上交易费")
    private BigDecimal gasFeeDayAmount= BigDecimal.ZERO;

    @JsonFormat(pattern = "#.##", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "通道费-平台费")
    private BigDecimal channelFeeDayAmount= BigDecimal.ZERO;

}
