package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.payment.core.service.base.BaseEntity;
import com.mc.payment.core.service.model.dto.CryptoDepositEventDetailVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @author conor
 * @since 2024-04-22 17:48:32
 */
@Getter
@Setter
@TableName("mcp_deposit_record_detail")
@Schema(title = "DepositRecordDetailEntity对象", description = "")
public class DepositRecordDetailEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(title = "记录id")
    @TableField("record_id")
    private String recordId;

    @Schema(title = "资产名称,[如:BTC]")
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

    @Schema(title = "TxHash")
    @TableField("tx_hash")
    private String txHash;

    @Schema(title = "金额")
    @TableField("amount")
    private BigDecimal amount;

    @Schema(title = "网络费")
    @TableField("network_fee")
    private BigDecimal networkFee;

    @Schema(title = "服务费")
    @TableField("service_fee")
    private BigDecimal serviceFee;

    @Schema(title = "目标地址-余额")
    @TableField("addr_balance")
    private BigDecimal addrBalance;

    @Schema(title = "fireblocks回调内容的交易更新时间(精确到毫秒):The transaction’s last update date and time, in Unix timestamp. eg:1713267871232")
    @TableField("last_updated")
    private Long lastUpdated;

    @Schema(title = "当时币种转换为USDT的汇率")
    @TableField("rate")
    private BigDecimal rate;

    @Schema(title = "当时手续费币种转换为USDT的汇率")
    @TableField("fee_rate")
    private BigDecimal feeRate;

    @Schema(title = "审核状态 0待审核,1审核通过,2审核不通过")
    @TableField("audit_status")
    private Integer auditStatus;

    @Schema(title = "入金明细状态 1未确认,2确认中,3已确认,4已取消,5未支付,6交易成功,7交易失败")
    @TableField("status")
    private Integer status;

    @Schema(title = "通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    @TableField("channel_sub_type")
    private Integer channelSubType;

    public CryptoDepositEventDetailVo valueOf() {
        CryptoDepositEventDetailVo detailVo = new CryptoDepositEventDetailVo();
        detailVo.setTxHash(this.getTxHash());
        detailVo.setAssetName(this.getAssetName());
        detailVo.setNetProtocol(this.getNetProtocol());
        detailVo.setSourceAddress(this.getSourceAddress());
        detailVo.setDestinationAddress(this.getDestinationAddress());
        detailVo.setAmount(this.getAmount());
//        detailVo.setNetworkFee(this.getNetworkFee());
//        detailVo.setServiceFee(this.getServiceFee());
        detailVo.setCreateTime(this.getCreateTime());
        return detailVo;
    }
}
