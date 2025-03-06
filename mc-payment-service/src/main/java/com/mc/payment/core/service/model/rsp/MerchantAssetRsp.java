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
public class MerchantAssetRsp implements Serializable {
    @Schema(title = "资产名称")
    private String assetName;

    // 这里说明下, v1.4.0需求, 商户分析-饼状图
    // 1. 如果是大于1的，默认显示小数点后2位；
    // 2. 如数值小于1且精度过长，仅显示小数点后6位，超过6位的进行四舍五入，例：0.123456789，则显示为：0.123457
    // 3. 如尾数为0，省略掉尾数0，仅保留有效数值，例：0.120000000，则显示为：0.12
    // 4. 如精度过长，且中间6位数都为0，则显示数值为0，例：0.000000045，则显示为：0(ps:还是6位小数位, 按四舍五入)
//    @JsonFormat(pattern = "#.##", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "币值")
    private BigDecimal balanceSum;

//    @JsonFormat(pattern = "#.##", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "币值转USDT")
    private BigDecimal balanceSumUsdt;
}
