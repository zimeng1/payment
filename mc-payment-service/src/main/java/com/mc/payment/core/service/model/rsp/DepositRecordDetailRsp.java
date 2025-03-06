package com.mc.payment.core.service.model.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mc.payment.core.service.serializer.BigDecimalSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Marty
 * @since 2024/5/16 11:22
 */
@Data
public class DepositRecordDetailRsp implements Serializable {

    @Schema(title = "入金详情id")
    private String id;

    @Schema(title = "交易标识")
    private String txHash;

    @Schema(title = "资产名称,[如:BTC]")
    private String assetName;

    @Schema(title = "商户名称")
    private String merchantName;

    @Schema(title = "网络协议")
    private String netProtocol;

    @Schema(title = "来源地址")
    private String sourceAddress;

    @Schema(title = "目标地址")
    private String destinationAddress;

    @Schema(title = "审核状态 0待审核,1审核通过,2审核不通过")
    private String auditStatus;

    @JsonFormat(pattern = "#.##########", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "入金金额")
    private BigDecimal amount;

    @JsonFormat(pattern = "#.######", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "网络费")
    private BigDecimal networkFee;

    @JsonFormat(pattern = "#.######", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "服务费")
    private BigDecimal serviceFee;

    @Schema(title = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date createTime;

    @Schema(title = "手续费资产名称,[如:USDT 出金就需用到 ETH 的手续费]")
    private String feeAssetName;

    @JsonFormat(pattern = "#.######", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "目标地址-余额")
    private BigDecimal addrBalance;

    @JsonFormat(pattern = "#.######", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "目标地址-余额(U)")
    private BigDecimal addrBalanceToU;

    @JsonFormat(pattern = "#.######", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "Gas费")
    private BigDecimal gasFee;

    @JsonFormat(pattern = "#.######", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "Gas费(U)")
    private BigDecimal gasFeeToU;

    @JsonFormat(pattern = "#.######", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "入金金额(U)")
    private BigDecimal amountToU;

    @JsonFormat(pattern = "#.##########", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "当时币种转换为USDT的汇率")
    private BigDecimal rate;

    @JsonFormat(pattern = "#.##########", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "当时手续费币种转换为USDT的汇率")
    private BigDecimal feeRate;

    @Schema(title = "主网Hash跳转url")
    private String mainHashUrl;

    @Schema(title = "测试网Hash跳转url")
    private String testHashUrl;
}
