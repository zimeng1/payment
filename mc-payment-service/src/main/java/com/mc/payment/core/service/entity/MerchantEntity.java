package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mc.payment.core.service.base.BaseEntity;
import com.mc.payment.core.service.model.dto.MerchantParamVo;
import com.mc.payment.core.service.model.enums.BusinessScopeEnum;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.enums.StatusEnum;
import com.mc.payment.core.service.model.rsp.merchant.MerchantConfigGetByIdRsp;
import com.mc.payment.core.service.serializer.BigDecimalToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @author conor
 * @since 2024-01-31 18:46:14
 */
@Getter
@Setter
@TableName("mcp_merchant")
@Schema(title = "MerchantEntity对象", description = "")
public class MerchantEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(title = "商户名称")
    @TableField("`name`")
    private String name;
    /**
     * @see MerchantParamVo
     * @deprecated 1.9.0 版本后废弃
     * jsonStr
     */
    @Deprecated
    @Schema(title = "商户参数")
    @TableField("param")
    private String param;

    @Schema(title = "商户状态,[0:禁用,1:激活]")
    @TableField("status")
    private Integer status;

    @Schema(title = "业务范围,[字典编码:BUSINESS_SCOPE 由英文逗号隔开]")
    @TableField("business_scope")
    private String businessScope;

    @Schema(title = "结算主体")
    @TableField("settlement_subject")
    private String settlementSubject;

    @Schema(title = "结算信息")
    @TableField("settlement_info")
    private String settlementInfo;

    @Schema(title = "结算对接人邮箱,[英文逗号隔开]")
    @TableField("settlement_email")
    private String settlementEmail;

    @Schema(title = "商户联系人")
    @TableField("contact")
    private String contact;

    @Schema(title = "商户联系方式")
    @TableField("contact_tel")
    private String contactTel;

    @Schema(title = "告警邮箱,[英文逗号隔开]")
    @TableField("alarm_email")
    private String alarmEmail;

    @Schema(title = "Access Key")
    @TableField("access_key")
    private String accessKey;

    @Schema(title = "Secret Key")
    @TableField("secret_key")
    private String secretKey;

    @Schema(title = "回调地址")
    @TableField("webhook_url")
    private String webhookUrl;

    @Schema(title = "ip白名单,[英文逗号隔开]")
    @TableField("ip_whitelist")
    private String ipWhitelist;

    @Schema(title = "通道子类型集合,[通道子类型:channel_sub_type 由英文逗号隔开] 0:BlockATM,1:FireBlocks")
    @TableField("channel_sub_types")
    private String channelSubTypes;

    @Schema(title = "入金审核,[0:否,1:是]")
    @TableField("deposit_audit")
    private Integer depositAudit;

    @Schema(title = "出金审核,[0:否,1:是]")
    @TableField("withdrawal_audit")
    private Integer withdrawalAudit;

    @Schema(title = "商户结算机制,[0:基础费率/笔,1:累加费用/笔,2:固定费率/笔,3:阶梯费率/月交易金额]")
    @TableField("billing_type")
    private Integer billingType;

    @Schema(title = "累加费用/固定费率")
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    @TableField("additional_fee")
    private BigDecimal additionalFee;

    @Schema(title = "费用的货币单位,[例如“USD”或“SOL”]")
    @TableField("currency")
    private String currency;

    @Schema(title = "阶梯费率的配置，包含范围和对应费率的jsonarray字符串,例子:[{{\"range\": \"0-1000\", \"rate\": \"3%\"}, {\"range\": \"1000-2000\", \"rate\": \"2%\"},{\"range\": \"2000-∞\", \"rate\": \"1%\"}}]")
    @TableField("tiered_rate_json")
    private String tieredRateJson;


    //=================
    @Schema(title = "通道子类型-描述")
    public String getChannelNameDescs() {
        return ChannelSubTypeEnum.getEnumDescByString(channelSubTypes);
    }

    @Schema(title = "业务范围-描述")
    public String getBusinessScopeDescs() {
        return BusinessScopeEnum.getEnumDescByString(businessScope);
    }


    @Schema(title = "商户状态-描述")
    public String getStatusDesc() {
        return StatusEnum.getEnumDesc(status);
    }

//=================

    public MerchantConfigGetByIdRsp convert() {
        MerchantConfigGetByIdRsp rsp = new MerchantConfigGetByIdRsp();
        rsp.setId(this.getId());
        rsp.setName(this.getName());
        rsp.setAccessKey(this.getAccessKey());
        rsp.setSecretKey(this.getSecretKey());
        rsp.setAlarmEmail(this.getAlarmEmail());
        rsp.setIpWhitelist(this.getIpWhitelist());
        return rsp;
    }
}
