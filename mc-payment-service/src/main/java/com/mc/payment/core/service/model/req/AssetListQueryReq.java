package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Conor
 * @since 2024/5/21 上午11:19
 */
@Data
public class AssetListQueryReq {
    @Schema(title = "资产名称,[如:BTC]")
    private String assetName;

    @Schema(title = "网络协议")
    private String netProtocol;
}
