package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

/**
 * @author conor
 * @since 2024/7/24 下午9:56:26
 */
@Data
public class WithdrawalConfirmReq {
    @Schema(title = "收银页面url参数k", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "[id]不能为空")
    @Length(max = 128, message = "[id]长度不能超过128")
    private String encryptId;


    @Schema(title = "金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "[金额]不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "[金额]必须大于零")
    private BigDecimal amount;

    @Schema(title = "资产名称/币种", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "[资产名称/币种]不能为空")
    @Length(max = 20, message = "[资产名称/币种]长度不能超过20")
    private String assetName;

    // 以下是收银页面的表单参数,虽然是必填,但当入金申请单中的userSelectable=0（用户不可选）时，后台不会使用这几个参数，
    // 这相当于一个蜜罐，一旦这些参数与申请单中的不一致，就意味着有人在尝试篡改数据攻击支付服务，可做为风控的一种手段
    @Schema(title = "网络类型/支付类型")
    @NotBlank(message = "[网络类型/支付类型]不能为空")
    @Length(max = 20, message = "[网络类型/支付类型]长度不能超过20")
    private String netProtocol;

    @Schema(title = "银行代码", description = "某些币种的支付类型需要")
    private String bankCode;

    @Schema(title = "银行名称", description = "某些币种的支付类型需要")
    private String bankName;

    @Schema(title = "持卡人姓名", description = "某些币种的支付类型需要")
    private String accountName;

    @Schema(title = "IFSCcode", description = "INR币种需要,叫印度金融系统代码")
    private String bankNum;
}
