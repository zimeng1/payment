package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

/**
 * @author Conor
 * @since 2024/4/29 下午3:09
 */
@Data
public class WithdrawalCheckReq {

    @Schema(title = "资产名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "[资产名称]不能为空")
    private String assetName;

    @Schema(title = "网络类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "[网络类型]不能为空")
    private String assetNet;

    @Schema(title = "网络协议")
    @NotBlank(message = "[网络协议]不能为空")
    @Length(max = 20, message = "[网络协议]长度不能超过20")
    private String netProtocol;

    @Schema(title = "金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "[金额]不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "[金额]必须大于零")
    private BigDecimal amount;

    @Schema(title = "通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    @NotNull(message = "[通道子类型]不能为空")
    @Range(min = 1, max = 6, message = "[通道子类型]必须为[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    private Integer channelSubType;

}
