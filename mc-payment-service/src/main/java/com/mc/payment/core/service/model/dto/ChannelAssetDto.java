package com.mc.payment.core.service.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ChannelAssetDto {
    @Schema(title = "资产类型,[0:加密货币,1:法币]")
    @TableField("asset_type")
    private Integer assetType;

    @Schema(title = "资产名称")
    @TableField("asset_name")
    private String assetName;

    @Schema(title = "资产网络")
    @TableField("asset_net")
    private String assetNet;

    @Schema(title = "网络协议")
    @TableField("net_protocol")
    private String netProtocol;

    @Schema(title = "合约地址/Token地址/第三方支付通道的资产账号标识(ofapay中的scode)")
    @TableField("token_address")
    private String tokenAddress;

    @Schema(title = "通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    @TableField("channel_sub_type")
    private Integer channelSubType;

    @Schema(title = "通道资产名称")
    @TableField("channel_asset_name")
    private String channelAssetName;

    /**
     * 通道资产类型
     * fireblocks的没有资产类型,所以不存
     * ofapay的资产类型是接口特殊指定的,所以存的是对应的支付类型
     */
    @Schema(title = "通道资产类型")
    @TableField("channel_asset_type")
    private String channelAssetType;
}
