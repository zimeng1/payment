package com.mc.payment.core.service.model.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.enums.DepositDetailStausEnum;
import com.mc.payment.core.service.serializer.BigDecimalToStringSerializer;
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
public class DepositDetailRsp implements Serializable {

    @Schema(title = "入金明细id")
    private String id;

    @Schema(title = "关联商户跟踪id")
    private String trackingId;

    @Schema(title = "入金商户")
    private String merchantName;

    @Schema(title = "入金申请时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date applyTime;

    @Schema(title = "入金申请金额")
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    private BigDecimal applyAmount;

    @Schema(title = "TxHash")
    private String txHash;

    @Schema(title = "入金确认时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date confirmTime;

    @Schema(title = "入金资产")
    private String assetName;

    @Schema(title = "入金网络协议/支付类型")
    private String netProtocol;

    @Schema(title = "入金金额")
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    private BigDecimal amount;

    @Schema(title = "来源地址")
    private String sourceAddress;

    @Schema(title = "目标地址")
    private String destinationAddress;

    @Schema(title = "支付通道")
    private Integer channelSubType;

    @Schema(title = "支付通道文本")
    private String channelSubTypeText;

    @Schema(title = "交易费")
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    private BigDecimal networkFee;

    @Schema(title = "通道费")
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    private BigDecimal channelFee;

    @Schema(title = "目标地址余额")
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    private BigDecimal addrBalance;

    @Schema(title = "状态 1未确认,2确认中,3已确认,4已取消,5未支付,6交易成功,7交易失败")
    private Integer status;

    @Schema(title = "审核状态 1通过 2不通过")
    private Integer auditStatus;


    @Schema(title = "状态文本")
    private String statusText;

    @Schema(title = "主网Hash跳转url")
    private String mainHashUrl;

    @Schema(title = "测试网Hash跳转url")
    private String testHashUrl;

    @Schema(title = "gasFee费")
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    private BigDecimal gasFee;

    @Schema(title = "gas费(U)")
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    private BigDecimal gasFeeToU;

    @Schema(title = "入金金额(U)")
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    private BigDecimal amountToU;

    @Schema(title = "目标地址余额(U)")
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    private BigDecimal addrBalanceToU;

    @Schema(title = "手续费资产名称,[如:USDT 出金就需用到 ETH 的手续费]")
    private String feeAssetName;

    @Schema(title = "当时币种转换为USDT的汇率")
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    private BigDecimal rate;

    @Schema(title = "当时手续费币种转换为USDT的汇率")
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    private BigDecimal feeRate;

    public String getStatusText() {
        return DepositDetailStausEnum.getEnumDesc(status);
    }

    public String getChannelSubTypeText() {
        return ChannelSubTypeEnum.getEnumDesc(channelSubType);
    }

}
