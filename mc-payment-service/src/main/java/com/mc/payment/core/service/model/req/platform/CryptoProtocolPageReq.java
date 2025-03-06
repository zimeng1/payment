package com.mc.payment.core.service.model.req.platform;

import com.mc.payment.core.service.base.BasePageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CryptoProtocolPageReq extends BasePageReq {

    @Schema(title = "平台资产id", description = "支持右模糊查询")
    private String id;

    @Schema(title = "加密货币-网络协议", description = "支持模糊查询")
    private String netProtocol;

    @Schema(title = "加密货币-资产网络", description = "支持模糊查询")
    private String assetNet;

    @Schema(title = "资产状态,[0:禁用,1:激活]", description = "不传则查询所有")
    private Integer status;

    @Schema(title = "正则表达式", description = "支持模糊查询")
    private String regularExpression;

}
