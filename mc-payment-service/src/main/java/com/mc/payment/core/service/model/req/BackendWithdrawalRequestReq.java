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
public class BackendWithdrawalRequestReq {

    @Schema(title = "商户ID")
    @NotBlank(message = "[商户ID]不能为空")
    private String merchantId;

    @Schema(title = "资产类型,[0:加密货币,1:法币]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "[资产类型]不能为空")
    @Range(min = 0, max = 1, message = "[资产类型]必须为[0:加密货币,1:法币]")
    private Integer assetType;
    @Schema(title = "资产名称/币种", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "[资产名称/币种]不能为空")
    @Length(max = 20, message = "[资产名称/币种]长度不能超过20")
    private String assetName;

    @Schema(title = "网络类型/支付类型")
    @Length(max = 20, message = "[网络类型/支付类型]长度不能超过20")
    private String netProtocol;

    @Schema(title = "银行代码", description = "某些币种的支付类型需要")
    //@NotBlank(message = "[bankCode]不能为空")
    @Length(max = 50, message = "[银行代码]长度不能超过50")
    private String bankCode;

    @Schema(title = "银行名称", description = "IDR币种需要")
    private String bankName;

    @Schema(title = "持卡人姓名", description = "IDR币种需要")
    private String accountName;

    @Schema(title = "IFSCcode", description = "INR币种需要,叫印度金融系统代码")
    @Length(max = 30, message = "IFSCcode长度不能超过30")
    private String bankNum;

    @Schema(title = "金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "[金额]不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "[金额]必须大于零")
    private BigDecimal amount;

    @Schema(title = "出金地址", description = "要把钱支付给谁,如BTC地址、ETH地址、银行卡号等", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "[出金地址]不能为空")
    @Size(max = 255, message = "[出金地址]长度不能超过255")
    private String address;

    @Schema(title = "pixType", description = "assetName=BRL 且 netProtocol=PIX时需要,CPF:巴西的个人身份证号码;" +
            "CNPJ:巴西的法人税号，由14位数字组成;PHONE:巴西手机号(+55开头,号码10-11位);EMAIL:邮箱号;EVP:虚拟支付账号(校验的正则：^[0-9 a-z]{8}-[0-9 a-z]{4}-[0-9 a-z]{4}-[0-9 a-z]{4}-[0-9 a-z]{12}$)")
    private String pixType;

    @Schema(title = "pixAccount", description = "assetName=BRL 且 netProtocol=PIX时需要,以pixType为准,传递对应的账号即可")
    private String pixAccount;

    @Schema(title = "taxNumber", description = "assetName=BRL 且 netProtocol=PIX时需要,税号CPF/CNPJ税号")
    private String taxNumber;
    
}
