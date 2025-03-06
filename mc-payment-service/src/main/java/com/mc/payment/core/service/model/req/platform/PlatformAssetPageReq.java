package com.mc.payment.core.service.model.req.platform;

import com.mc.payment.core.service.base.BasePageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PlatformAssetPageReq extends BasePageReq {
    @Schema(title = "平台资产id", description = "支持右模糊查询")
    private String id;
    @Schema(title = "资产类型,[0:加密货币,1:法币]", description = "不传则查询所有")
    private Integer assetType;
    @Schema(title = "资产名称,[如:BTC]", description = "支持模糊查询")
    private String assetName;
    @Schema(title = "资产状态,[0:禁用,1:激活]", description = "不传则查询所有")
    private Integer status;
}
