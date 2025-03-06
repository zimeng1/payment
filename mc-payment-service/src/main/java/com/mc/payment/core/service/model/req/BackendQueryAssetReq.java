package com.mc.payment.core.service.model.req;

import com.mc.payment.api.model.req.QueryAssetReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BackendQueryAssetReq extends QueryAssetReq {
    @Schema(title = "商户ID")
    @NotBlank(message = "[商户ID]不能为空")
    private String merchantId;
}
