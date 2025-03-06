package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentPageEstimateFeeReq {
    @Schema(title = "资产名称,[如:BTC]")
    @NotBlank(message = "[资产名称]不能为空")
    private String assetName;

    @Schema(title = "网络协议")
    @NotBlank(message = "[网络协议]不能为空")
    private String netProtocol;
}
