package com.mc.payment.core.service.model.rsp;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.enums.DepositAuditStatusEnum;
import com.mc.payment.core.service.model.enums.DepositRecordStatusEnum;
import com.mc.payment.core.service.serializer.BigDecimalSerializer;
import com.mc.payment.core.service.serializer.BigDecimalToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Marty
 * @since 2024/5/15 17:12
 */
@Data
@Schema(title = "入金-分页查询")
public class DepositRecordPageRsp implements Serializable {

    private static final long serialVersionUID = -7765547930446241459L;

    @Schema(title = "入金id")
    @ExcelProperty(value = "入金id", index = 1)
    @ColumnWidth(20)
    protected String id;

    @Schema(title = "入金申请时间-创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty(value = "入金申请时间", index = 2)
    @ColumnWidth(18)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    protected Date createTime;
    @Schema(title = "跟踪id")
    @ExcelProperty(value = "商户跟踪id", index = 0)
    @ColumnWidth(16)
    protected String trackingId;

    @Schema(title = "资产网络,[如:BRC20]")
    @ExcelIgnore
    private String assetNet;
    @Schema(title = "资产名称,[如:BTC]")
    @ExcelProperty(value = "入金资产", index = 4)
    @ColumnWidth(12)
    private String assetName;
    @Schema(title = "网络协议")
    @ExcelProperty(value = "入金网络协议/支付类型", index = 5)
    @ColumnWidth(16)
    private String netProtocol;
    @Schema(title = "来源地址")
    @ExcelProperty(value = "来源地址", index = 8)
    @ColumnWidth(12)
    private String sourceAddress;

    @Schema(title = "商户id")
    @ExcelIgnore
    private String merchantId;
    @Schema(title = "目标地址")
    @ExcelProperty(value = "目标地址", index = 10)
    @ColumnWidth(12)
    private String destinationAddress;

    @Schema(title = "用户ip")
    @ExcelIgnore
    private String userIp;
    @Schema(title = "入金用户标识")
    @ExcelProperty(value = "入金用户标识", index = 3)
    @ColumnWidth(16)
    private String userId;
    @Schema(title = "支付通道 0 BlockATM,1 FireBlocks,2 OFAPay")
    @ExcelIgnore
    private Integer channelSubType;
    @Schema(title = "支付通道文本")
    @ExcelProperty(value = "支付通道", index = 11)
    @ColumnWidth(12)
    private String channelSubTypeText;
    @Schema(title = "商户名称")
    @ExcelProperty(value = "入金商户", index = 9)
    @ColumnWidth(15)
    private String merchantName;
    @JsonFormat(pattern = "#.##########", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "金额(入金申请金额)")
    @ExcelProperty(value = "入金申请金额", index = 6)
    @ColumnWidth(16)
    private BigDecimal amount;
    @JsonFormat(pattern = "#.##########", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "已入金金额(入金总额)")
    @ExcelProperty(value = "入金总额", index = 7)
    @ColumnWidth(12)
    private BigDecimal accumulatedAmount;
    @JsonFormat(pattern = "#.######", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "Gas费")
    @ExcelProperty(value = "交易费", index = 12)
    @ColumnWidth(15)
    private BigDecimal gasFee;

    @Schema(title = "状态,[0:待入金,1:部分入金,2:完全入金,3:撤销入金,4:请求失败,5:待审核,6:审核不通过]")
    @ExcelIgnore
    private Integer status;

    @Schema(title = "审核状态,[1:通过,2:不通过]")
    @ExcelIgnore
    private Integer auditStatus;

    @Schema(title = "审核状态,[1:通过,2:不通过]")
    @ExcelIgnore
    private String auditStatusDesc;

    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    @Schema(title = "通道费-平台费")
    @ExcelProperty(value = "通道费", index = 13)
    @ColumnWidth(12)
    private BigDecimal channelFee;

    @Schema(title = "钱包id")
    @ExcelIgnore
    private String walletId;

    @Schema(title = "手续费资产名称,[如:USDT 出金就需用到 ETH 的手续费]")
    @ExcelIgnore
    private String feeAssetName;
    @Schema(title = "状态文本")
    @ExcelProperty(value = "状态", index = 15)
    @ColumnWidth(12)
    private String statusText;

    @JsonFormat(pattern = "#.######", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "目标地址-余额(U)")
    @ExcelIgnore
    private BigDecimal addrBalanceToU;

    @JsonFormat(pattern = "#.######", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "Gas费(U)")
    @ExcelIgnore
    private BigDecimal gasFeeToU;

    @JsonFormat(pattern = "#.######", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "金额(入金申请金额)(U)")
    @ExcelIgnore
    private BigDecimal amountToU;

    @JsonFormat(pattern = "#.######", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "已入金金额(入金总额)(U)")
    @ExcelIgnore
    private BigDecimal accumulatedAmountToU;

    @Schema(title = "地址失效时间戳-精确毫秒")
    @ExcelIgnore
    private long expireTimestamp;

    @JsonFormat(pattern = "#.##########", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "当时币种转换为USDT的汇率")
    @ExcelIgnore
    private BigDecimal rate;

    @JsonFormat(pattern = "#.##########", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "当时手续费币种转换为USDT的汇率")
    @ExcelIgnore
    private BigDecimal feeRate;
    @JsonFormat(pattern = "#.######", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "目标地址-余额")
    @ExcelProperty(value = "目标地址余额", index = 14)
    @ColumnWidth(16)
    private BigDecimal addrBalance;

    @Schema(title = "失败原因")
    private String stayReason;

    @Schema(title = "备注")
    private String remark;

    @Schema(title = "资产类型,[0:加密货币,1:法币]")
    private Integer assetType;

    public String getStatusText() {
        return DepositRecordStatusEnum.getEnumDesc(status);
    }


    public String getChannelSubTypeText() {
        return ChannelSubTypeEnum.getEnumDesc(channelSubType);
    }

    public String getAuditStatusDesc() {
        return DepositAuditStatusEnum.getEnumDesc(auditStatus);
    }
}
