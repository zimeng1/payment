package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.model.dto.AssetConfigExcelDto;
import com.mc.payment.core.service.model.req.ChannelAssetSaveReq;
import com.mc.payment.fireblocksapi.model.vo.fireBlocks.AssetTypeVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author conor
 * @since 2024-04-24 14:43:44
 */
@Data
@TableName("mcp_channel_asset")
@Schema(title = "ChannelAssetEntity对象", description = "")
public class ChannelAssetEntity extends BaseNoLogicalDeleteEntity {

    private static final long serialVersionUID = 1L;

    @Schema(title = "通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    @TableField("channel_sub_type")
    private Integer channelSubType;

    /**
     * 给第三方支付通道接口使用的资产标识
     * 在支付网关中统一叫currency/payType
     * fireblocks的资产标识是assetId,所以存的是assetId
     * ofapay的资产标识是币种和支付类型,所以存的是币种_支付类型
     */
    @Schema(title = "通道资产名称")
    @TableField("channel_asset_name")
    private String channelAssetName;

    @Schema(title = "资产名称")
    @TableField("asset_name")
    private String assetName;

    @Schema(title = "资产网络")
    @TableField("asset_net")
    private String assetNet;

    @Schema(title = "网络协议")
    @TableField("net_protocol")
    private String netProtocol;

    @Schema(title = "通道凭据Json数据", description = "存储一些通道接口所需的参数值")
    @TableField("channel_credential")
    private String channelCredential;

    public static ChannelAssetEntity valueOf(AssetConfigExcelDto assetConfigExcelDto, Integer channelSubType) {
        ChannelAssetEntity channelAssetEntity = new ChannelAssetEntity();
        channelAssetEntity.setAssetName(assetConfigExcelDto.getAssetName());
        channelAssetEntity.setNetProtocol(assetConfigExcelDto.getNetProtocol());
        channelAssetEntity.setChannelAssetName(assetConfigExcelDto.getChannelAssetName());
        channelAssetEntity.setChannelSubType(channelSubType);
        return channelAssetEntity;
    }

    // ChannelAssetSaveReq
    public static ChannelAssetEntity valueOf(ChannelAssetSaveReq req) {
        ChannelAssetEntity channelAssetEntity = new ChannelAssetEntity();
        channelAssetEntity.setAssetName(req.getAssetName());
        channelAssetEntity.setNetProtocol(req.getNetProtocol());
        channelAssetEntity.setChannelAssetName(req.getChannelAssetName());
        channelAssetEntity.setChannelSubType(req.getChannelSubType());
        channelAssetEntity.setAssetNet(req.getAssetNet());
        return channelAssetEntity;
    }

    public static ChannelAssetEntity valueOf(AssetTypeVo vo) {
        ChannelAssetEntity channelAssetEntity = new ChannelAssetEntity();
        channelAssetEntity.setChannelAssetName(vo.getId());
        channelAssetEntity.setNetProtocol(vo.getType());
        return channelAssetEntity;
    }
}
