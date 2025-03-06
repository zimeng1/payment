package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * @author Conor
 * @since 2024/6/5 下午5:27
 */
@Data
public class QueryWalletAddressListReq {
    @Schema(title = "商户ID")
    @NotBlank(message = "[商户ID]不能为空")
    private String merchantId;

    @Schema(title = "资产名称,[如:BTC]")
    @NotBlank(message = "[资产名称]不能为空")
    private String assetName;

    @Schema(title = "网络协议")
    @NotBlank(message = "[网络协议]不能为空")
    private String netProtocol;

    @Schema(title = "账户类型,[0:入金账户,1:出金账户]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "[账户类型]必须为0或1,0:入金账户,1:出金账户")
    private String accountType;

}
