package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.model.dto.AssetSimpleDto;
import com.mc.payment.core.service.model.enums.AssetTypeEnum;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.enums.StatusEnum;
import com.mc.payment.core.service.serializer.BigDecimalToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 通道资产配置(1.9.0基于mcp_channel_asset和mcp_asset_config迁移)
 *
 * @TableName mcp_channel_asset_config
 */
@TableName(value = "mcp_channel_asset_config")
@Data
public class ChannelAssetConfigEntity extends BaseNoLogicalDeleteEntity implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 通道子类型
     */
    @Schema(title = "通道子类型")
    @TableField(value = "channel_sub_type")
    private Integer channelSubType;
    /**
     * 资产类型,[0:加密货币,1:法币]
     */
    @Schema(title = "资产类型,[0:加密货币,1:法币]")
    @TableField(value = "asset_type")
    private Integer assetType;
    /**
     * 通道资产名称
     */
    @Schema(title = "通道资产名称")
    @TableField(value = "channel_asset_name")
    private String channelAssetName;
    /**
     * 通道资产网络协议
     */
    @Schema(title = "通道资产网络协议/支付类型")
    @TableField(value = "channel_net_protocol")
    private String channelNetProtocol;
    /**
     * 资产名称
     */
    @Schema(title = "资产名称")
    @TableField(value = "asset_name")
    private String assetName;
    /**
     * 加密货币网络协议/法币支付类型
     */
    @Schema(title = "加密货币网络协议/法币支付类型")
    @TableField(value = "net_protocol")
    private String netProtocol;
    /**
     * 资产网络
     */
    @Schema(title = "资产网络")
    @TableField(value = "asset_net")
    private String assetNet;
    /**
     * 最小入金金额
     */
    @Schema(title = "最小入金金额")
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    @TableField(value = "min_deposit_amount")
    private BigDecimal minDepositAmount;
    /**
     * 最小出金金额
     */
    @Schema(title = "最小出金金额")
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    @TableField(value = "min_withdrawal_amount")
    private BigDecimal minWithdrawalAmount;
    /**
     * 最大入金金额
     */
    @Schema(title = "最大入金金额")
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    @TableField(value = "max_deposit_amount")
    private BigDecimal maxDepositAmount;
    /**
     * 最大出金金额
     */
    @Schema(title = "最大出金金额")
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    @TableField(value = "max_withdrawal_amount")
    private BigDecimal maxWithdrawalAmount;
    /**
     * 合约地址
     */
    @Schema(title = "合约地址")
    @TableField(value = "token_address")
    private String tokenAddress;
    /**
     * 哈希查询地址(Testnet)
     */
    @Schema(title = "哈希查询地址(Testnet)")
    @TableField(value = "test_hash_url")
    private String testHashUrl;
    /**
     * 哈希查询地址(Mainnet)
     */
    @Schema(title = "哈希查询地址(Mainnet)")
    @TableField(value = "main_hash_url")
    private String mainHashUrl;
    /**
     * 手续费币种/资产名称,[如:BTC]
     */
    @Schema(title = "手续费币种/资产名称,[如:BTC]")
    @TableField(value = "fee_asset_name")
    private String feeAssetName;
    /**
     * 预估费,[单位:当前币种]
     */
    @Schema(title = "预估费,[单位:当前币种]")
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    @TableField(value = "estimate_fee")
    private BigDecimal estimateFee;
    /**
     * 未转换汇率预估费,[单位:手续费币种]
     */
    @Schema(title = "未转换汇率预估费,[单位:手续费币种]")
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    @TableField(value = "un_estimate_fee")
    private BigDecimal unEstimateFee;
    /**
     * 默认预估费,[单位:当前币种]
     */
    @Schema(title = "默认预估费,[单位:当前币种]")
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    @TableField(value = "default_estimate_fee")
    private BigDecimal defaultEstimateFee;
    /**
     * 通道凭据信息
     * 1.9.0之前的旧版本设计,用于与上游通道接口交互时使用的凭据信息 新版考虑使用通道资产名称和协议代替或者是配置文件代替
     * 后期版本逐步废弃,已有的逐步迁移
     */
    @Schema(title = "通道凭据信息")
    @TableField(value = "channel_credential")
    private String channelCredential;
    /**
     * 资产状态,[0:禁用,1:激活]
     */
    @Schema(title = "资产状态,[0:禁用,1:激活]")
    @TableField(value = "status")
    private Integer status;

    // ================== 以下为非数据库字段 ==================
    @Schema(title = "资产状态-描述")
    public String getStatusDesc() {
        return StatusEnum.getEnumDesc(status);
    }

    @Schema(title = "资产类型-描述")
    public String getAssetTypeDesc() {
        return AssetTypeEnum.getEnumDesc(assetType);
    }

    @Schema(title = "通道子类型-描述")
    public String getChannelSubTypeDesc() {
        return ChannelSubTypeEnum.getEnumDesc(channelSubType);
    }


    public AssetSimpleDto convert() {
        AssetSimpleDto assetSimpleDto = new AssetSimpleDto();
        assetSimpleDto.setAssetName(this.assetName);
        assetSimpleDto.setNetProtocol(this.netProtocol);
        return assetSimpleDto;
    }
}