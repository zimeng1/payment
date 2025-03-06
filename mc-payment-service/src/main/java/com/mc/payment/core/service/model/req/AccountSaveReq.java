package com.mc.payment.core.service.model.req;

import com.mc.payment.common.base.BaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

/**
 * @author conor
 * @since 2024/2/2 15:33:15
 */
@Slf4j
@Data
@Schema(title = "账户-保存参数实体")
public class AccountSaveReq extends BaseReq {
    private static final long serialVersionUID = -5394991997353219674L;
    @Schema(title = "账户名")
    @NotBlank(message = "[账户名]不能为空")
    @Length(max = 50, message = "[账户名]长度不能超过50")
    private String name;


    @Schema(title = "通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    @NotNull(message = "[通道子类型]不能为空")
    @Range(min = 1, max = 6, message = "[通道子类型]必须为[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    private Integer channelSubType;

    @Schema(title = "账户签约的商户的ID")
    @NotBlank(message = "[账户签约的商户的ID]不能为空")
    @Length(max = 20, message = "[账户签约的商户的ID]长度不能超过20")
    private String merchantId;

    @Schema(title = "账户类型,[0:入金账户,1:出金账户]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "[账户类型]必须为0或1,0:入金账户,1:出金账户")
    @Range(min = 0, max = 1, message = "[账户类型]必须为0或1,0:入金账户,1:出金账户")
    private Integer accountType;

    @Schema(title = "外部系统账号id", description = "比如fireblocks创建去账号返回的钱包id就存这儿")
    @Length(max = 50, message = "[外部系统账号id]长度不能超过50")
    private String externalId;

    public AccountSaveReq() {
    }

    public AccountSaveReq(AccountSaveReq req) {
        this.name = req.getName();
        this.channelSubType = req.getChannelSubType();
        this.merchantId = req.getMerchantId();
    }


}
