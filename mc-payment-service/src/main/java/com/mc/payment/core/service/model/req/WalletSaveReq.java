package com.mc.payment.core.service.model.req;

import com.mc.payment.common.base.BaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "钱包参数实体")
public class WalletSaveReq extends BaseReq {
    private static final long serialVersionUID = -6200304811032124199L;

    @Schema(title = "账号id")
    @NotBlank(message = "[账号id]不能为空")
    @Length(max = 20, message = "[账号id]长度不能超过20")
    private String accountId;

    @Schema(title = "资产名称,[如:BTC]")
    @NotBlank(message = "[资产名称]不能为空")
    @Length(max = 20, message = "[资产名称]长度不能超过20")
    private String assetName;

    @Schema(title = "钱包地址")
//    @NotBlank(message = "[钱包地址]不能为空")
    @Length(max = 255, message = "[钱包地址]长度不能超过255")
    private String walletAddress;

    @Schema(title = "余额")
//    @NotNull(message = "[余额]不能为空")
    private BigDecimal balance;

    @Schema(title = "私钥")
//    @NotBlank(message = "[私钥]不能为空")
    @Length(max = 255, message = "[私钥]长度不能超过255")
    private String privateKey;

    @Schema(title = "备注")
//    @NotBlank(message = "[备注]不能为空")
    @Length(max = 255, message = "[备注]长度不能超过255")
    private String remark;

    @Schema(title = "外部系统钱包id",description = "比如fireblocks创建去钱包返回的钱包id就存这儿")
    @Length(max = 50, message = "[外部系统钱包id]长度不能超过50")
    private String externalId;
}
