package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.model.dto.AssetSimpleDto;
import lombok.Data;

/**
 * 成本规则支持的资产
 *
 * @TableName mcp_channel_cost_asset
 */
@TableName(value = "mcp_channel_cost_asset")
@Data
public class ChannelCostAssetEntity extends BaseNoLogicalDeleteEntity {
    /**
     * 成本规则id
     */
    @TableField(value = "cost_id")
    private String costId;

    /**
     * 业务动作,[0:入金,1:出金]
     */
    @TableField(value = "business_action")
    private Integer businessAction;

    /**
     * 通道子类型
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

    public static ChannelCostAssetEntity valueOf(AssetSimpleDto assetSimpleDto, String costId, Integer channelSubType, Integer businessAction) {
        ChannelCostAssetEntity channelCostAssetEntity = new ChannelCostAssetEntity();
        channelCostAssetEntity.setAssetName(assetSimpleDto.getAssetName());
        channelCostAssetEntity.setNetProtocol(assetSimpleDto.getNetProtocol());
        channelCostAssetEntity.setCostId(costId);
        channelCostAssetEntity.setBusinessAction(businessAction);
        channelCostAssetEntity.setChannelSubType(channelSubType);
        return channelCostAssetEntity;
    }

    public AssetSimpleDto convert() {
        AssetSimpleDto assetSimpleDto = new AssetSimpleDto();
        assetSimpleDto.setAssetName(this.assetName);
        assetSimpleDto.setNetProtocol(this.netProtocol);
        return assetSimpleDto;
    }
}