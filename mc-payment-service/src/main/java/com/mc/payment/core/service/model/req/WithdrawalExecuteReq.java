package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

/**
 * @author Conor
 * @since 2024/6/6 上午10:56
 */
@Data
public class WithdrawalExecuteReq {
    @Schema(title = "商户id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "[商户id]不能为空")
    private String merchantId;

    @Schema(title = "商户名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "[商户名称]不能为空")
    private String merchantName;

    @Schema(title = "资产名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "[资产名称]不能为空")
    private String assetName;

    @Schema(title = "网络协议")
    @NotBlank(message = "[网络协议]不能为空")
    @Length(max = 20, message = "[网络协议]长度不能超过20")
    private String netProtocol;

    @Schema(title = "收款地址/出金地址/目标地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "[收款地址]不能为空")
    private String address;

    @Schema(title = "金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "[金额]不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "[金额]必须大于零")
    private BigDecimal amount;

}
