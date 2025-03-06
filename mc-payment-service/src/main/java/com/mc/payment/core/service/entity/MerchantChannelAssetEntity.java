package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.model.enums.AssetTypeEnum;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商户支付通道资产配置
 *
 * @TableName mcp_merchant_channel_asset
 */
@TableName(value = "mcp_merchant_channel_asset")
@Data
public class MerchantChannelAssetEntity extends BaseNoLogicalDeleteEntity implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 商户id
     */
    @TableField(value = "merchant_id")
    private String merchantId;

    @TableField(value = "channel_asset_id")
    private String channelAssetId;

    @Schema(title = "资产类型,[0:加密货币,1:法币]")
    @TableField("asset_type")
    private Integer assetType;
    /**
     * 通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]
     */
    @TableField(value = "channel_sub_type")
    private Integer channelSubType;
    /**
     * 资产名称,[如:BTC]
     */
    @TableField(value = "asset_name")
    private String assetName;
    /**
     * 网络协议
     */
    @TableField(value = "net_protocol")
    private String netProtocol;
    /**
     * 是否启用告警,[0:否,1:是]
     */
    @TableField(value = "alarm_status")
    private Integer alarmStatus;
    /**
     * 备付金告警值
     */
    @TableField(value = "reserve_alarm_value")
    private BigDecimal reserveAlarmValue;


    /**
     * 是否入金可用,[0:否,1:是]
     */
    @TableField(value = "deposit_status")
    private Integer depositStatus;


    /**
     * 是否出金可用,[0:否,1:是]
     */
    @TableField(value = "withdrawal_status")
    private Integer withdrawalStatus;

    /**
     * 是否自动生成钱包,[0:否,1:是]
     */
    @TableField(value = "generate_wallet_status")
    private Integer generateWalletStatus;
    /**
     * 生成钱包小于等于阈值
     */
    @TableField(value = "generate_wallet_le_quantity")
    private Integer generateWalletLeQuantity;
    /**
     * 生成钱包数量
     */
    @TableField(value = "generate_wallet_quantity")
    private Integer generateWalletQuantity;


    // ================== 以下为非数据库字段 ==================
    @Schema(title = "是否启用告警-描述")
    public String getAlarmStatusDesc() {
        return StatusEnum.getEnumDesc(alarmStatus);
    }

    @Schema(title = "资产类型-描述")
    public String getAssetTypeDesc() {
        return AssetTypeEnum.getEnumDesc(assetType);
    }

    @Schema(title = "通道子类型-描述")
    public String getChannelSubTypeDesc() {
        return ChannelSubTypeEnum.getEnumDesc(channelSubType);
    }
}