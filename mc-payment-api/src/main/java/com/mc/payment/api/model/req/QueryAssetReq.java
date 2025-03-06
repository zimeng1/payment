package com.mc.payment.api.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class QueryAssetReq {
    @Schema(title = "资产类型,[0:加密货币,1:法币]", description = "不传查全部")
    @Range(min = 0, max = 1, message = "[资产类型]必须为[0:加密货币,1:法币]")
    private Integer assetType;
}
