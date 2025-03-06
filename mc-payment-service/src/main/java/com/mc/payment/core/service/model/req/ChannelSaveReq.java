package com.mc.payment.core.service.model.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mc.payment.common.base.BaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.util.Date;

@Data
@Schema(title = "通道保存参数实体")
public class ChannelSaveReq extends BaseReq {
    private static final long serialVersionUID = -6200304811032124199L;
    @Schema(title = "通道名称")
    @NotBlank(message = "[通道名称]不能为空")
    @Length(max = 20, message = "[通道名称]长度不能超过20")
    private String name;

    @Schema(title = "通道状态,[0:禁用,1:激活]")
    @NotNull(message = "[通道状态]不能为空")
    @Range(min = 0, max = 1, message = "[通道状态]必须为[0:禁用,1:激活]")
    private Integer status;

    @Schema(title = "通道类型,[0:虚拟货币支付,1:法币]")
    @NotNull(message = "[通道类型]不能为空")
    @Range(min = 0, max = 2, message = "[通道类型]必须为[0:虚拟货币支付,1:法币]")
    private Integer channelType;

    @Schema(title = "通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    @NotNull(message = "[通道子类型]不能为空")
    @Range(min = 1, max = 6, message = "[通道子类型]必须为[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    private Integer channelSubType;

    @Schema(title = "优先级,[分为5个等级，1为最高，5为最小]", example = "1")
    @NotNull(message = "[优先级]不能为空")
    @Range(min = 1, max = 5, message = "[优先级]必须为[分为5个等级，1为最高，5为最小]")
    private Integer priority;

    @Schema(title = "支持资产,[多个资产名称由英文逗号隔开]")
    @NotNull(message = "[支持资产]不能为空")
    @Length(max = 255, message = "[支持资产]长度不能超过255")
    private String supportAsset;

    @Deprecated
    @Schema(title = "通道参数,json字符串", example = "'{\"authURL\":\"鉴权API地址\",\"platformPublicKey\":\"平台公钥\",\"WebhookURL\":\"平台回调地址\",\"channelPublicKey\":\"通道公钥/AKSK\"}'")
//    @NotBlank(message = "[通道参数]不能为空")
//    @Pattern(regexp = "^\\{.*}$", message = "通道参数应为JSON字符串")
    private String param = "{}";

    @Schema(title = "通道有效期类型,0:一直有效,1:取通道有效期具体时间段")
    @NotNull(message = "[通道有效期类型]不能为空")
    private Integer expirationDateType;

    @Schema(title = "通道有效期-开始", example = "2020-10-10 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expirationDateStart;

    @Schema(title = "通道有效期-结束", example = "2025-10-10 23:59:59")
    //@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expirationDateEnd;

    @Schema(title = "联系人")
//    @NotBlank(message = "[联系人]不能为空")
    @Length(max = 15, message = "[联系人]长度不能超过15")
    private String contact;

    @Schema(title = "联系方式")
//    @NotBlank(message = "[联系方式]不能为空")
    @Length(max = 15, message = "[联系方式]长度不能超过15")
    private String contactTel;

    @Schema(title = "鉴权api地址")
//    @NotBlank(message = "[鉴权api地址]不能为空")
    @Length(max = 255, message = "[鉴权api地址]长度不能超过255")
    private String authUrl;

    @Schema(title = "Webhook回调地址")
//    @NotBlank(message = "[Webhook回调地址]不能为空")
    @Length(max = 255, message = "[Webhook回调地址]长度不能超过255")
    private String webhookUrl;

    @Schema(title = "通道公钥")
//    @NotBlank(message = "[通道公钥]不能为空")
    @Length(max = 32, message = "[通道公钥]长度不能超过32")
    private String channelPublicKey;

    @Schema(title = "平台公钥")
//    @NotBlank(message = "[平台公钥]不能为空")
    @Length(max = 32, message = "[平台公钥]长度不能超过32")
    private String platformPublicKey;


    public ChannelSaveReq() {
    }

    public ChannelSaveReq(String name, Integer status, Integer channelType, Integer channelSubType, Integer priority, String param, Date expirationDateStart, Date expirationDateEnd, String contact, String contactTel) {
        this.name = name;
        this.status = status;
        this.channelType = channelType;
        this.channelSubType = channelSubType;
        this.priority = priority;
        this.param = param;
        this.expirationDateStart = expirationDateStart;
        this.expirationDateEnd = expirationDateEnd;
        this.contact = contact;
        this.contactTel = contactTel;
    }


}
