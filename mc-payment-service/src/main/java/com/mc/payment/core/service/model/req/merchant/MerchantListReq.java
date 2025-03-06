package com.mc.payment.core.service.model.req.merchant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MerchantListReq {
    @Schema(title = "商户名称", description = "支持模糊查询")
    private String name;
}
