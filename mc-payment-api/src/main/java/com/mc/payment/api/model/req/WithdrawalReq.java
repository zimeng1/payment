package com.mc.payment.api.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

@Data
public class WithdrawalReq extends BasePaymentGatewayReq {
    @Schema(title = "商户跟踪id", description = "各个商户的每次交易操作应保证唯一", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "[商户跟踪id]不能为空")
    @Size(max = 50, message = "[商户跟踪id]长度不能超过50")
    private String trackingId;

    @Schema(title = "资产类型,[0:加密货币,1:法币]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "[资产类型]不能为空")
    @Range(min = 0, max = 1, message = "[资产类型]必须为[0:加密货币,1:法币]")
    private Integer assetType;

    @Deprecated
    @Schema(title = "用户是否可选,[0:否,1:是]", description = "已作废,默认0", requiredMode = Schema.RequiredMode.REQUIRED)
//    @NotNull(message = "[用户是否可选]不能为空")
//    @Range(min = 0, max = 1, message = "[用户是否可选]必须为[0:不可选,1:可选]")
    private Integer userSelectable = 0;

    @Schema(title = "资产名称/币种", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "[资产名称/币种]不能为空")
    @Length(max = 20, message = "[资产名称/币种]长度不能超过20")
    private String assetName;

    @Schema(title = "网络类型/支付类型")
    @NotBlank(message = "[网络类型/支付类型]不能为空")
    @Length(max = 20, message = "[网络类型/支付类型]长度不能超过20")
    private String netProtocol;

    @Schema(title = "银行代码", description = "某些币种的支付类型需要")
    //@NotBlank(message = "[bankCode]不能为空")
    @Length(max = 50, message = "[银行代码]长度不能超过50")
    private String bankCode;

    @Schema(title = "银行名称", description = "IDR币种需要")
    @Length(max = 128, message = "[银行名称]长度不能超过128")
    private String bankName;

    @Schema(title = "持卡人姓名", description = "IDR币种需要")
    @Length(max = 64, message = "[持卡人姓名]长度不能超过64")
    private String accountName;

    @Schema(title = "IFSCcode", description = "INR币种需要,叫印度金融系统代码")
    @Length(max = 30, message = "IFSCcode长度不能超过30")
    private String bankNum;

    @Schema(title = "金额", requiredMode = Schema.RequiredMode.REQUIRED, defaultValue = "1")
    @NotNull(message = "[金额]不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "[金额]必须大于零")
    @DecimalMax(value = "999999999999.99", message = "[金额]不能大于999999999999.99")
    private BigDecimal amount;

    @Schema(title = "出金地址", description = "要把钱支付给谁,如BTC地址、ETH地址、银行卡号等", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "[出金地址]不能为空")
    @Size(max = 255, message = "[出金地址]长度不能超过255")
    private String address;

    @Schema(title = "通知回调地址/webhook url", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "[通知回调地址]不能为空")
    @Size(max = 255, message = "[通知回调地址]长度不能超过255")
    private String webhookUrl;

    @Schema(title = "交易备注,不能超过255", description = "在交易中加入一些额外的数据或标识,如订单号等,接口会原样返回")
    @Size(max = 255, message = "[交易备注]长度不能超过255")
    private String remark;

    @Schema(title = "文本1", description = "仅用于收银页面显示，不参与支付", example = "1000")
    @Size(max = 40, message = "[文本1]长度不能超过40")
    private String text1;

    @Schema(title = "文本2", description = "仅用于收银页面显示，不参与支付", example = "$1000")
    @Size(max = 40, message = "[文本1]长度不能超过40")
    private String text2;

    @Schema(title = "文本3", description = "仅用于收银页面显示，不参与支付", example = "Mfedfring")
    @Size(max = 40, message = "[文本1]长度不能超过40")
    private String text3;

    @Schema(title = "文本4", description = "仅用于收银页面显示，不参与支付", example = "1USD = 1038KRW")
    @Size(max = 40, message = "[文本4]长度不能超过40")
    private String text4;

    @Schema(title = "用户id")
    @Size(max = 64, message = "[用户id]长度不能超过64")
    private String userId;

    @Schema(title = "用户ip地址")
    @Size(max = 64, message = "[用户ip地址]长度不能超过64")
    private String userIp;


    /**
     * 参数校验
     */
    public void validate() {
        if (userSelectable != 0) {
            throw new IllegalArgumentException("已作废字段,userSelectable必须为0");
        }
//        if (userSelectable == 0) {
//            if (StringUtils.isBlank(netProtocol)) {
//                throw new IllegalArgumentException("当 userSelectable == 0 时 netProtocol必填");
//            }
//        }
        // skipPage默认为0
//        skipPage = skipPage == null ? 0 : skipPage;
//        if (skipPage == 1) {
//            if (userSelectable != 0) {
//                throw new IllegalArgumentException("当 skipPage == 1 时 userSelectable必须为0");
//            }
//            if (StringUtils.isBlank(netProtocol)) {
//                throw new IllegalArgumentException("当 skipPage == 1 时 netProtocol必填");
//            }
//        }
        if (StringUtils.isBlank(netProtocol) && StringUtils.isNotBlank(bankCode)) {
            throw new IllegalArgumentException("当 bankCode 有值时 netProtocol必填");
        }
        // webhookUrl必须是url
        if (!webhookUrl.matches("^(http|https)://.*$")) {
            throw new IllegalArgumentException("webhookUrl必须是url");
        }
//        // successPageUrl必须是url
//        if (!successPageUrl.matches("^(http|https)://.*$")) {
//            throw new IllegalArgumentException("successPageUrl必须是url");
//        }
    }
}
