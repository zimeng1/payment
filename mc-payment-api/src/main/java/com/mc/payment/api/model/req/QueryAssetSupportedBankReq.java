package com.mc.payment.api.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class QueryAssetSupportedBankReq {

    @Schema(title = "支付类型,[0:入金,1:出金,0,1:出入金]")
    @NotBlank(message = "[支付类型]必须为0或1或0,1;0:入金,1:出金,0,1:出入金")
    @Length(max = 3, message = "[支付类型]长度不能超过3")
    private String paymentType;

    @Schema(title = "资产名称/币种")
    @NotBlank(message = "[资产名称/币种]不能为空")
    @Length(max = 20, message = "[资产名称/币种]长度不能超过20")
    private String assetName;

    @Schema(title = "网络类型/支付类型")
    @NotBlank(message = "[网络类型/支付类型]不能为空")
    @Length(max = 20, message = "[网络类型/支付类型]长度不能超过20")
    private String netProtocol;
}
