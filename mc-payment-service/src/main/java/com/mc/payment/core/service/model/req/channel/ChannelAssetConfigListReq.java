package com.mc.payment.core.service.model.req.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class ChannelAssetConfigListReq {
    @Schema(title = "通道子类型", description = "不传则查询所有")
    @Range(min = 1, max = 6, message = "[通道子类型]必须为[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    private Integer channelSubType;

    @Schema(title = "资产类型,[0:加密货币,1:法币]", description = "不传则查询所有")
    @Range(min = 0, max = 1, message = "[资产类型]必须为[0:加密货币,1:法币]")
    private Integer assetType;

    @Schema(title = "资产状态,[0:禁用,1:激活]", description = "不传则查询所有")
    @Range(min = 0, max = 1, message = "[通道资产状态]必须为[0:禁用,1:激活]")
    private Integer status;

    public ChannelAssetConfigListReq() {
    }

    public ChannelAssetConfigListReq(Integer channelSubType, Integer assetType, Integer status) {
        this.channelSubType = channelSubType;
        this.assetType = assetType;
        this.status = status;
    }
}
