package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商户钱包
 *
 * @TableName mcp_merchant_wallet
 */
@TableName(value = "mcp_merchant_wallet_snapshot")
@Data
public class MerchantWalletSnapshotEntity extends BaseNoLogicalDeleteEntity implements Serializable {
    /**
     * 账户签约的商户的ID
     */
    @Schema(title = "账户签约的商户的ID")
    @TableField(value = "merchant_id")
    private String merchantId;

    /**
     * 资产类型,[0:加密货币,1:法币]
     */
    @Schema(title = "资产类型,[0:加密货币,1:法币]")
    @TableField(value = "asset_type")
    private Integer assetType;

    /**
     * 资产类型/币种
     */
    @Schema(title = "资产类型/币种")
    @TableField(value = "asset_name")
    private String assetName;

    /**
     * 网络协议/支付网络
     */
    @Schema(title = "网络协议/支付网络")
    @TableField(value = "net_protocol")
    private String netProtocol;

    /**
     * 用途类型,[0:入金,1:出金]
     */
    @Schema(title = "用途类型,[0:入金,1:出金]")
    @TableField(value = "purpose_type")
    private Integer purposeType;

    /**
     * 账户地址
     */
    @Schema(title = "账户地址")
    @TableField(value = "wallet_address")
    private String walletAddress;

    /**
     * 余额
     */
    @Schema(title = "余额")
    @TableField(value = "balance")
    private BigDecimal balance;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}