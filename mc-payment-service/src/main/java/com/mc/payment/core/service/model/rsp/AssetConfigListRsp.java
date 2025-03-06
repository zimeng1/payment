package com.mc.payment.core.service.model.rsp;

import com.mc.payment.core.service.entity.AssetConfigEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * @author Marty
 * @since 2024/5/10 14:24
 */

@Data
@Builder
public class AssetConfigListRsp {

    @Schema(title = "资产名称,[如:BTC]")
    private String assetName;

    @Schema(title = "资产网络")
    private String assetNet;

    @Schema(title = "网络协议")
    private String netProtocol;

    public static AssetConfigListRsp valueOf(AssetConfigEntity entity) {
        return AssetConfigListRsp.builder()
                .assetName(entity.getAssetName())
                .assetNet(entity.getAssetNet())
                .netProtocol(entity.getNetProtocol())
                .build();
    }
}
