package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.payment.core.service.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("mcp_withdrawal_record_detail")
@Schema(title = "WithdrawalRecordDetailEntity对象", description = "WithdrawalRecordDetailEntity对象")
public class WithdrawalRecordDetailEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(title = "记录id")
    @TableField("record_id")
    private String recordId;

    @Schema(title = "TxHash")
    @TableField("tx_hash")
    private String txHash;

    @Schema(title = "通道子类型,1 FireBlocks,2 OFAPay")
    @TableField(value = "channel_sub_type")
    private Integer channelSubType;

    @Schema(title = "资产名称")
    @TableField("asset_name")
    private String assetName;

    @Schema(title = "网络协议")
    @TableField("net_protocol")
    private String netProtocol;

    @Schema(title = "来源地址")
    @TableField("source_address")
    private String sourceAddress;

    @Schema(title = "目标地址")
    @TableField("destination_address")
    private String destinationAddress;

    @Schema(title = "商户id")
    @TableField("merchant_id")
    private String merchantId;

    @Schema(title = "商户名称")
    @TableField("merchant_name")
    private String merchantName;

    @Schema(title = "金额")
    @TableField("amount")
    private BigDecimal amount;

    @Schema(title = "交易费")
    @TableField("network_fee")
    private BigDecimal networkFee;

    @Schema(title = "目标地址余额")
    @TableField("addr_balance")
    private BigDecimal addrBalance;

    @Schema(title = "出金明细状态 1未确认,2确认中,3已确认,4已取消,5已出金,6出金失败")
    @TableField("status")
    private Integer status;

    @Schema(title = "当时币种转换为USDT的汇率")
    @TableField("rate")
    private BigDecimal rate;

    @Schema(title = "当时手续费币种转换为USDT的汇率")
    @TableField("fee_rate")
    private BigDecimal feeRate;

    @Schema(title = "服务费")
    @TableField("service_fee")
    private BigDecimal serviceFee;

}
