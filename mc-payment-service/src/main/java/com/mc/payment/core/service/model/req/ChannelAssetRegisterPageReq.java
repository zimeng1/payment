package com.mc.payment.core.service.model.req;

import com.mc.payment.core.service.base.BasePageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Marty
 * @since 2024-06-20 14:59:26
 */
@Data
public class ChannelAssetRegisterPageReq extends BasePageReq {
    @Schema(title = "资产id")
    private String id;

    @Schema(title = "资产名称")
    private String assetName;

    @Schema(title = "通道资产名称")
    private String channelAssetName;

    @Schema(title = "合约地址/资产地址")
    private String chainAddress;

}
