package com.mc.payment.core.service.model.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mc.payment.core.service.serializer.BigDecimalSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商户分析
 *
 * @author Marty
 * @since 2024/5/8 15:07
 */
@Data
public class MerchantAnalyzeRsp implements Serializable {

    private static final long serialVersionUID = -6798046236660133895L;

    @Schema(title = "商户数")
    private int merchantCount;
    @Schema(title = "账户数")
    private int accountCount;

    @Schema(title = "入金次数")
    private int depositCount;

    @Schema(title = "出金次数")
    private int withdrawalCount;

    @JsonFormat(pattern = "#.##", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "金额-入金 类型的金额")
    private BigDecimal depositAmount;

    @JsonFormat(pattern = "#.##", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "金额-出金 类型的金额")
    private BigDecimal withdrawalAmount;

    @JsonFormat(pattern = "#.##", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "金额-通道 类型的金额")
    private BigDecimal channelFee;

    @JsonFormat(pattern = "#.##", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "金额-链上交易费 类型的金额")
    private BigDecimal gasFee;

    @JsonFormat(pattern = "#.##", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "记录余额-历史钱包总余额")
    private BigDecimal historyAddrAmount;

    @JsonFormat(pattern = "#.##", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "实时余额-钱包总余额")
    private BigDecimal addrAmount;

    @Schema(title = "商户每日出入金-条形图")
    private List<MerchantDayInOutRsp> merchantDayInOutList;

    @Schema(title = "资产分布数据-饼状图")
    private List<MerchantAssetRsp> assetDataList;

    @Schema(title = "活跃账户")
    private List<ActiveAccountAssetRsp> activeAccountList;

    @Schema(title = "资产名称")
    private String assetName;

    @Schema(title = "手续费资产名称,[如:USDT 出金就需用到 ETH 的手续费]")
    private String feeAssetName;

    @JsonFormat(pattern = "#.##########", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "实时汇率-单币种情况下才有.资产名称对USDT的汇率")
    private BigDecimal rate;


}
