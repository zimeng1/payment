package com.mc.payment.core.service.model.req.platform;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class PlatformAssetListReq {
    @Schema(title = "资产类型,[0:加密货币,1:法币]", description = "不传则查询所有")
    @Range(min = 0, max = 1, message = "[资产类型]必须为[0:加密货币,1:法币]")
    private Integer assetType;

    @Schema(title = "资产名称,[如:BTC]", description = "不传则查询所有,支持模糊查询")
    @Size(max = 20, message = "[资产名称/币种]长度不能超过20")
    private String assetName;

    @Schema(title = "资产状态,[0:禁用,1:激活]", description = "不传则查询所有")
    @Range(min = 0, max = 1, message = "[资产状态]必须为0或1,0:禁用,1:激活")
    private Integer status;

}
