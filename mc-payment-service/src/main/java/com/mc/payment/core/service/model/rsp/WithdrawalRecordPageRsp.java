package com.mc.payment.core.service.model.rsp;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.enums.WithdrawalAuditStatusEnum;
import com.mc.payment.core.service.model.enums.WithdrawalRecordStatusEnum;
import com.mc.payment.core.service.serializer.BigDecimalSerializer;
import com.mc.payment.core.service.serializer.BigDecimalToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Marty
 * @since 2024/5/16 17:02
 */
@Data
@Schema(title = "出金-分页查询")
public class WithdrawalRecordPageRsp implements Serializable {

    private static final long serialVersionUID = 8954522309183451405L;

    @Schema(title = "出金完成时间-修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelProperty(value = "出金时间", index = 3)
    @ColumnWidth(18)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    protected Date createTime;

    @Schema(title = "出金申请时间-创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelIgnore
    protected Date updateTime;

    @Schema(title = "出金id")
    @ExcelProperty(value = "出金id", index = 1)
    @ColumnWidth(20)
    private String id;
    @Schema(title = "资产名称,[如:BTC]")
    @ExcelProperty(value = "出金资产", index = 5)
    @ColumnWidth(12)
    private String assetName;

    @Schema(title = "资产网络,[如:BRC20]")
    @ExcelIgnore
    private String assetNet;

    @Schema(title = "出金网络协议/支付类型")
    @ExcelProperty(value = "出金网络协议/支付类型", index = 6)
    @ColumnWidth(16)
    private String netProtocol;

    @Schema(title = "来源地址")
    @ExcelProperty(value = "来源地址", index = 8)
    @ColumnWidth(12)
    private String sourceAddress;

    @Schema(title = "目标地址")
    @ExcelProperty(value = "目标地址", index = 9)
    @ColumnWidth(12)
    private String destinationAddress;

    @Schema(title = "商户id")
    @ExcelIgnore
    private String merchantId;

    @Schema(title = "用户标识")
    @ExcelProperty(value = "出金用户标识", index = 4)
    @ColumnWidth(12)
    private String userId;

    @Schema(title = "用户ip")
    @ExcelIgnore
    private String userIp;

    @Schema(title = "支付通道 0 BlockATM,1 FireBlocks,2 OFAPay")
    @ExcelIgnore
    private Integer channelSubType;

    @Schema(title = "支付通道文本")
    @ExcelProperty(value = "支付通道", index = 10)
    @ColumnWidth(12)
    private String channelSubTypeText;

    @JsonFormat(pattern = "#.######", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "金额")
    @ExcelProperty(value = "出金金额", index = 7)
    @ColumnWidth(12)
    private BigDecimal amount;

    @JsonFormat(pattern = "#.######", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "Gas费")
    @ExcelProperty(value = "交易费", index = 11)
    @ColumnWidth(15)
    private BigDecimal gasFee;

    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    @Schema(title = "通道费-平台费")
    @ExcelProperty(value = "通道费", index = 12)
    @ColumnWidth(12)
    private BigDecimal channelFee;

    @Schema(title = "状态,[0:已提交,1:待审核,2:余额不足,3:出金中,4:出金完成,5:已拒绝,6:出金错误,7,终止出金]")
    @ExcelIgnore
    private Integer status;

    @Schema(title = "审核状态,[1:通过,2:不通过,3:终止执行,4:重新执行]")
    @ExcelIgnore
    private Integer auditStatus;

    @Schema(title = "审核状态描述[1:通过,2:不通过,3:终止执行,4:重新执行]")
    @ExcelIgnore
    private String auditStatusDesc;

    @ExcelProperty(value = "状态", index = 14)
    @ColumnWidth(12)
    private String statusText;

    @Schema(title = "跟踪id,申请方提供唯一跟踪ID以查询处理结果")
    @ExcelProperty(value = "商户跟踪id", index = 0)
    @ColumnWidth(16)
    private String trackingId;

    @Schema(title = "钱包id")
    @ExcelIgnore
    private String walletId;

    @JsonFormat(pattern = "#.######", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "冻结的预估手续费")
    @ExcelIgnore
    private BigDecimal freezeEsFee;

    @Schema(title = "fireblocks返回的交易id")
    @ExcelIgnore
    private String transactionId;

    @Schema(title = "手续费资产名称,[如:USDT 出金就需用到 ETH 的手续费]")
    @ExcelIgnore
    private String feeAssetName;

    @JsonFormat(pattern = "#.######", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "来源地址-余额")
    @ExcelProperty(value = "来源地址余额", index = 13)
    @ColumnWidth(16)
    private BigDecimal addrBalance;

    @JsonFormat(pattern = "#.######", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "来源地址-余额(U)")
    @ExcelIgnore
    private BigDecimal addrBalanceToU;

    @JsonFormat(pattern = "#.######", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "Gas费(U)")
    @ExcelIgnore
    private BigDecimal gasFeeToU;

    @JsonFormat(pattern = "#.######", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "金额(U)")
    @ExcelIgnore
    private BigDecimal amountToU;

    @JsonFormat(pattern = "#.######", shape = JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    @Schema(title = "冻结的预估手续费")
    @ExcelIgnore
    private BigDecimal freezeEsFeeToU;

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

    @Schema(title = "商户名称")
    @ExcelProperty(value = "出金商户", index = 2)
    @ColumnWidth(14)
    private String merchantName;

    @Schema(title = "失败原因")
    private String stayReason;

    @Schema(title = "备注")
    private String remark;

    @Schema(title = "资产类型,[0:加密货币,1:法币]")
    private Integer assetType;

    public String getChannelSubTypeText() {
        return ChannelSubTypeEnum.getEnumDesc(channelSubType);
    }

    public String getStatusText() {
        return WithdrawalRecordStatusEnum.getEnumDesc(status);
    }

    public String getAuditStatusDesc() {
        return WithdrawalAuditStatusEnum.getEnumDesc(auditStatus);
    }

}
