package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.payment.core.service.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 通道资产注册表(用于支持新币种)
 * </p>
 *
 * @author Marty
 * @since 2024-06-20 14:59:26
 */
@Getter
@Setter
@TableName("mcp_channel_asset_register")
@Schema(title = "ChannelAssetRegisterEntity对象", description = "通道资产注册表(用于支持新币种)")
public class ChannelAssetRegisterEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(title = "通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    @TableField("channel_sub_type")
    private Integer channelSubType;

    @Schema(title = "通道资产名称")
    @TableField("channel_asset_name")
    private String channelAssetName;

    @Schema(title = "资产名称")
    @TableField("asset_name")
    private String assetName;

    @Schema(title = "网络协议")
    @TableField("net_protocol")
    private String netProtocol;

    @Schema(title = "资产网络")
    @TableField("asset_net")
    private String assetNet;

    @Schema(title = "原生资产ID")
    @TableField("block_chain_id")
    private String blockChainId;

    @Schema(title = "合约地址/资产地址")
    @TableField("chain_address")
    private String chainAddress;

    @Schema(title = "Asset symbol")
    @TableField("chain_symbol")
    private String chainSymbol;

    @Schema(title = "第三方返回的名称")// ps: 不知道什么意思, 是asset_net还是什么都不太清楚.
    @TableField("chain_name")
    private String chainName;

    @Schema(title = "小数位数")
    @TableField("decimals")
    private Integer decimals;

    @Schema(title = "资产类别 (NATIVE FT NFT SFT)")
    @TableField("asset_class")
    private String assetClass;

    @Schema(title = "资产的范围(Global Local)")
    @TableField("scope")
    private String scope;
}
