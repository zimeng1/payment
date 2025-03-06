package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mc.payment.core.service.base.BaseEntity;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.enums.ChannelTypeEnum;
import com.mc.payment.core.service.model.enums.StatusEnum;
import com.mc.payment.core.service.model.req.ChannelSaveReq;
import com.mc.payment.core.service.model.req.ChannelUpdateReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * <p>
 * 通道配置
 * </p>
 *
 * @author conor
 * @since 2024-01-31 18:46:14
 */
@Getter
@Setter
@TableName("mcp_channel")
@Schema(title = "ChannelEntity对象", description = "渠道")
public class ChannelEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(title = "通道名称")
    @TableField("`name`")
    private String name;

    @Schema(title = "通道状态,[0:禁用,1:激活]")
    @TableField("`status`")
    private Integer status;

    @Schema(title = "通道类型,[0:虚拟货币支付,1:法币]")
    @TableField("channel_type")
    private Integer channelType;

    @Schema(title = "通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    @TableField("channel_sub_type")
    private Integer channelSubType;

    @Schema(title = "优先级,[分为5个等级，1为最高，5为最小]")
    @TableField("priority")
    private Integer priority;

    @Schema(title = "通道参数")
    @TableField("param")
    private String param;

    @Schema(title = "支持资产,[多个资产名称由英文逗号隔开]")
    @TableField("support_asset")
    private String supportAsset;

    @Schema(title = "通道有效期类型,0:一直有效,1:取通道有效期具体时间段")
    @TableField("expiration_date_type")
    private Integer expirationDateType;

    @Schema(title = "通道有效期-开始")
    @TableField("expiration_date_start")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expirationDateStart;

    @Schema(title = "通道有效期-结束")
    @TableField("expiration_date_end")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expirationDateEnd;

    @Schema(title = "联系人")
    @TableField("contact")
    private String contact;

    @Schema(title = "联系方式")
    @TableField("contact_tel")
    private String contactTel;

    @Schema(title = "鉴权api地址")
    @TableField("auth_url")
    private String authUrl;

    @Schema(title = "Webhook回调地址")
    @TableField("webhook_url")
    private String webhookUrl;

    @Schema(title = "通道公钥")
    @TableField("channel_public_key")
    private String channelPublicKey;

    @Schema(title = "平台公钥")
    @TableField("platform_public_key")
    private String platformPublicKey;


    // 枚举描述字段===========================

    @Schema(title = "通道类型-描述")
    public String getChannelTypeDesc() {
        return ChannelTypeEnum.getEnumDesc(channelType);
    }

    @Schema(title = "通道子类型-描述")
    public String getChannelSubTypeDesc() {
        return ChannelSubTypeEnum.getEnumDesc(channelSubType);
    }

    @Schema(title = "通道状态-描述")
    public String getStatusDesc() {
        return StatusEnum.getEnumDesc(status);
    }

    //==================
    public static ChannelEntity valueOf(ChannelSaveReq req) {
        ChannelEntity channelEntity = new ChannelEntity();
        channelEntity.setName(req.getName());
        channelEntity.setStatus(req.getStatus());
        channelEntity.setChannelType(req.getChannelType());
        channelEntity.setChannelSubType(req.getChannelSubType());
        channelEntity.setPriority(req.getPriority());
        channelEntity.setParam(req.getParam());
        channelEntity.setExpirationDateStart(req.getExpirationDateStart());
        channelEntity.setExpirationDateEnd(req.getExpirationDateEnd());
        channelEntity.setContact(req.getContact());
        channelEntity.setContactTel(req.getContactTel());
        channelEntity.setSupportAsset(req.getSupportAsset());
        channelEntity.setAuthUrl(req.getAuthUrl());
        channelEntity.setWebhookUrl(req.getWebhookUrl());
        channelEntity.setChannelPublicKey(req.getChannelPublicKey());
        channelEntity.setPlatformPublicKey(req.getPlatformPublicKey());
        channelEntity.setExpirationDateType(req.getExpirationDateType());
        return channelEntity;
    }

    public static ChannelEntity valueOf(ChannelUpdateReq req) {
        ChannelEntity channelEntity = new ChannelEntity();
        channelEntity.setName(req.getName());
        channelEntity.setStatus(req.getStatus());
        channelEntity.setChannelType(req.getChannelType());
        channelEntity.setChannelSubType(req.getChannelSubType());
        channelEntity.setPriority(req.getPriority());
        channelEntity.setParam(req.getParam());
        channelEntity.setExpirationDateStart(req.getExpirationDateStart());
        channelEntity.setExpirationDateEnd(req.getExpirationDateEnd());
        channelEntity.setContact(req.getContact());
        channelEntity.setContactTel(req.getContactTel());
        channelEntity.setSupportAsset(req.getSupportAsset());
        channelEntity.setId(req.getId());
        channelEntity.setAuthUrl(req.getAuthUrl());
        channelEntity.setWebhookUrl(req.getWebhookUrl());
        channelEntity.setChannelPublicKey(req.getChannelPublicKey());
        channelEntity.setPlatformPublicKey(req.getPlatformPublicKey());
        channelEntity.setExpirationDateType(req.getExpirationDateType());
        return channelEntity;
    }
}
