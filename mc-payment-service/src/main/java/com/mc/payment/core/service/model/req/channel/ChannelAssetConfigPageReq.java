package com.mc.payment.core.service.model.req.channel;

import com.mc.payment.core.service.base.BasePageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Data
public class ChannelAssetConfigPageReq extends BasePageReq {
    @Schema(title = "通道资产id", description = "支持右模糊查询")
    private String id;

    @Schema(title = "资产类型,[0:加密货币,1:法币]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "[资产类型]不能为空")
    @Range(min = 0, max = 1, message = "[资产类型]必须为[0:加密货币,1:法币]")
    private Integer assetType;

    @Schema(title = "通道子类型")
    private Integer channelSubType;

    @Schema(title = "通道资产名称", description = "支持模糊查询")
    private String channelAssetName;

    @Schema(title = "通道资产网络协议/支付类型", description = "支持模糊查询")
    private String channelNetProtocol;

    @Schema(title = "资产状态,[0:禁用,1:激活]", description = "不传则查询所有")
    @Range(min = 0, max = 1, message = "[资产状态]必须为0或1,0:禁用,1:激活")
    private Integer status;

    @Schema(title = "资产名称-集合")
    private List<String> assetNames;

    @Schema(title = "加密货币网络协议/法币支付类型-集合")
    private List<String> netProtocols;
}
