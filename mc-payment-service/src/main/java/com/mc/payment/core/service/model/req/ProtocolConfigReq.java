package com.mc.payment.core.service.model.req;

import com.mc.payment.core.service.base.BasePageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "加密货币正则配置实体")
public class ProtocolConfigReq extends BasePageReq {

    @Schema(title = "Id")
    private String id;

    @Schema(title = "网络协议")
    private String netProtocol;

    @Schema(title = "正则表达式")
    private String regularExpression;

}
