package com.mc.payment.api.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

@Data
public class DepositReq {

    @Schema(title = "商户跟踪id", description = "各个商户的每次交易操作应保证唯一", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "[商户跟踪id]不能为空")
    @Size(max = 50, message = "[商户跟踪id]长度不能超过50")
    private String trackingId;

    @Schema(title = "入金业务名称, 比如商品名称/业务名称 eg: xxx报名费", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "[入金业务名称]不能为空")
    @Size(max = 50, message = "[入金业务名称]长度不能超过50")
    private String businessName;

    @Schema(title = "资产类型,[0:加密货币,1:法币]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "[资产类型]不能为空")
    @Range(min = 0, max = 1, message = "[资产类型]必须为[0:加密货币,1:法币]")
    private Integer assetType;

    @Schema(title = "金额", defaultValue = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "[金额]不能为空")
    @DecimalMin(value = "0.0", message = "[金额]不能小于0")
    @DecimalMax(value = "999999999999.99", message = "[金额]不能大于999999999999.99")
    private BigDecimal amount;

    @Schema(title = "资产名称/币种")
    @NotBlank(message = "[资产名称/币种]不能为空")
    @Size(max = 20, message = "[资产名称/币种]长度不能超过20")
    private String assetName;

    @Schema(title = "用户是否可选,[0:否,1:是]", description = "可选时：网络类型/支付类型等字段可先不填，由收银页提交时指定", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "[用户是否可选]不能为空")
    @Range(min = 0, max = 1, message = "[用户是否可选]必须为[0:不可选,1:可选]")
    private Integer userSelectable;

    @Schema(title = "网络类型/支付类型", description = "userSelectable=0时必填")
    @Size(max = 20, message = "[网络类型/支付类型]长度不能超过20")
    private String netProtocol;

    @Schema(title = "银行代码", description = "某些币种的支付类型需要,userSelectable=0时校验,不能在netProtocol没有值的情况下传递")
    @Size(max = 50, message = "[银行代码]长度不能超过50")
    private String bankCode;

    @Schema(title = "通知回调地址/webhook url", requiredMode = Schema.RequiredMode.REQUIRED,
            defaultValue = "https://yourdomain.com/webhook")
    @NotBlank(message = "[通知回调地址]不能为空")
    @Size(max = 255, message = "[通知回调地址]长度不能超过255")
    private String webhookUrl;

    @Schema(title = "入金成功跳转页面地址", requiredMode = Schema.RequiredMode.REQUIRED,
            defaultValue = "https://yourdomain.com/success")
    @NotBlank(message = "[入金成功跳转页面地址]不能为空")
    @Size(max = 255, message = "[入金成功跳转页面地址]长度不能超过255")
    private String successPageUrl;

    @Schema(title = "交易备注,不能超过255", description = "在交易中加入一些额外的数据或标识,如订单号等,接口会原样返回")
    @Size(max = 255, message = "[交易备注]长度不能超过255")
    private String remark;

    @Schema(title = "有效时长,单位：毫秒", description = "0表示永久有效(实际9999-01-01过期),大于0的数值表示有限的有效时长(最大支持7天)",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "[有效时长]不能为空")
    @Range(min = 0, max = 604800000, message = "[有效时长]必须大于等0,小于等于604800000")
    private Integer activeTime;

    @Schema(title = "文本1", description = "仅用于收银页面显示，不参与支付", example = "1000")
    @Size(max = 40, message = "[文本1]长度不能超过40")
    private String text1;

    @Schema(title = "文本2", description = "仅用于收银页面显示，不参与支付", example = "$1000")
    @Size(max = 40, message = "[文本2]长度不能超过40")
    private String text2;

    @Schema(title = "文本3", description = "仅用于收银页面显示，不参与支付", example = "Mfedfring")
    @Size(max = 40, message = "[文本3]长度不能超过40")
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

    @Schema(title = "是否跳过收银页面", description = "1:跳过页面,0:不跳过页面,默认为不跳过.当选择跳过时,接口会直接返回支付所需信息,不返回收银页面地址")
    @Range(min = 0, max = 1, message = "[是否跳过收银页面]必须为[1:跳过页面,0:不跳过页面]")
    private Integer skipPage;


    /**
     * 参数校验
     */
    public void validate() {
        if (userSelectable == 0) {
            if (StringUtils.isBlank(netProtocol)) {
                throw new IllegalArgumentException("当 userSelectable == 0 时 netProtocol必填");
            }
        }
        // skipPage默认为0
        skipPage = skipPage == null ? 0 : skipPage;
        if (skipPage == 1) {
            if (userSelectable != 0) {
                throw new IllegalArgumentException("当 skipPage == 1 时 userSelectable必须为0");
            }
            if (StringUtils.isBlank(netProtocol)) {
                throw new IllegalArgumentException("当 skipPage == 1 时 netProtocol必填");
            }
        }
        if (StringUtils.isBlank(netProtocol) && StringUtils.isNotBlank(bankCode)) {
            throw new IllegalArgumentException("当 bankCode 有值时 netProtocol必填");
        }
        // webhookUrl必须是url
        if (!webhookUrl.matches("^(http|https)://.*$")) {
            throw new IllegalArgumentException("webhookUrl必须是url");
        }
        // successPageUrl必须是url
        if (!successPageUrl.matches("^(http|https)://.*$")) {
            throw new IllegalArgumentException("successPageUrl必须是url");
        }
    }

}
