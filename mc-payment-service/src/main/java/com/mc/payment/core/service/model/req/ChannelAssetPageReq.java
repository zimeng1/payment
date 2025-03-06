package com.mc.payment.core.service.model.req;

import com.mc.payment.core.service.base.BasePageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author Conor
 * @since 2024/5/22 下午3:11
 */
@Data
public class ChannelAssetPageReq extends BasePageReq {
    @Schema(title = "通道资产id")
    private String id;

    @Schema(title = "通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    private Integer channelSubType;

    @Schema(title = "通道资产名称")
    private String channelAssetName;

    @Schema(title = "资产网络")
    private String assetNet;

    @Schema(title = "网络类型/支付类型")
    private List<String> netProtocols;
}
