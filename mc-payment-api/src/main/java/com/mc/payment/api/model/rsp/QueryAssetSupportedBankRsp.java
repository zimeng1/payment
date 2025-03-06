package com.mc.payment.api.model.rsp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class QueryAssetSupportedBankRsp {

    @Schema(title = "银行代码")
    private String bankCode;
    @Schema(title = "银行名称")
    private String bankName;
}
