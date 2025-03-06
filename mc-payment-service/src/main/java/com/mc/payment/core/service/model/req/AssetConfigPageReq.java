package com.mc.payment.core.service.model.req;

import com.mc.payment.core.service.base.BasePageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "资产配置分页查询参数实体")
public class AssetConfigPageReq extends BasePageReq {
    private static final long serialVersionUID = -6200304811032124199L;

    @Schema(title = "资产id")
    private String id;

    @Schema(title = "资产名称,[如:BTC]")
    private String assetName;

    @Schema(title = "资产网络,[如:BRC20]")
    private String assetNet;

    @Schema(title = "网络协议")
    private String netProtocol;

    @Schema(title = "合约地址")
    private String tokenAddress;

    @Schema(title = "资产状态,[0:禁用,1:激活]")
    private Integer status;

    @Schema(title = "资产类型,[0:加密货币,1:法币]")
    private Integer assetType;
}
