package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

@Data
public class BackendDepositRequestReq {

    @Schema(title = "商户ID")
    @NotBlank(message = "[商户ID]不能为空")
    private String merchantId;

    @Schema(title = "资产类型,[0:加密货币,1:法币]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "[资产类型]不能为空")
    @Range(min = 0, max = 1, message = "[资产类型]必须为[0:加密货币,1:法币]")
    private Integer assetType;

    @Schema(title = "金额")
    @NotNull(message = "[金额]不能为空")
    @DecimalMin(value = "0.0", message = "[金额]不能小于0")
    private BigDecimal amount;

    @Schema(title = "资产名称/币种")
    @NotBlank(message = "[资产名称/币种]不能为空")
    @Size(max = 20, message = "[资产名称/币种]长度不能超过20")
    private String assetName;

    @Schema(title = "网络类型/支付类型")
    @NotBlank(message = "[网络类型/支付类型]不能为空")
    @Length(max = 20, message = "[网络类型/支付类型]长度不能超过20")
    private String netProtocol;

    @Schema(title = "银行代码")
    private String bankCode;

}
