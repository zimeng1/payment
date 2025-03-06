package com.mc.payment.core.service.model.rsp.merchant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MerchantListRsp {
    @Schema(title = "商户id")
    private String id;
    @Schema(title = "商户名称")
    private String name;
}
