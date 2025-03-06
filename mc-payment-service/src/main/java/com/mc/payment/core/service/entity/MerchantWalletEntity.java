package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.model.enums.AssetTypeEnum;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.enums.MerchantWalletStatusEnum;
import com.mc.payment.core.service.model.enums.PurposeTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商户钱包
 *
 * @TableName mcp_merchant_wallet
 */
@TableName(value = "mcp_merchant_wallet")
@Data
public class MerchantWalletEntity extends BaseNoLogicalDeleteEntity implements Serializable {
    /**
     * 账户签约的商户的ID
     */
    @Schema(title = "账户签约的商户的ID")
    @TableField(value = "merchant_id")
    private String merchantId;

    /**
     * 账号id
     */
    @Schema(title = "账号id")
    @TableField(value = "account_id")
    private String accountId;

    /**
     * 账号名称
     */
    @Schema(title = "账号名称")
    @TableField(value = "account_name")
    private String accountName;

    /**
     * 资产类型,[0:加密货币,1:法币]
     */
    @Schema(title = "资产类型,[0:加密货币,1:法币]")
    @TableField(value = "asset_type")
    private Integer assetType;

    /**
     * 通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]
     */
    @Schema(title = "通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    @TableField(value = "channel_sub_type")
    private Integer channelSubType;

    /**
     * 资产类型/币种
     */
    @Schema(title = "资产类型/币种")
    @TableField(value = "asset_name")
    private String assetName;

    /**
     * 网络协议/支付网络
     */
    @Schema(title = "网络协议/支付网络")
    @TableField(value = "net_protocol")
    private String netProtocol;

    /**
     * 用途类型,[0:入金,1:出金]
     */
    @Schema(title = "用途类型,[0:入金,1:出金]")
    @TableField(value = "purpose_type")
    private Integer purposeType;

    /**
     * 账户地址
     */
    @Schema(title = "账户地址")
    @TableField(value = "wallet_address")
    private String walletAddress;

    /**
     * 余额
     */
    @Schema(title = "余额")
    @TableField(value = "balance")
    private BigDecimal balance;

    /**
     * 冻结金额
     */
    @TableField(value = "freeze_amount")
    private BigDecimal freezeAmount;

    /**
     * 备注
     */
    @Schema(title = "备注")
    @TableField(value = "remark")
    private String remark;

    /**
     * 通道钱包id
     */
    @Schema(title = "通道钱包id")
    @TableField(value = "channel_wallet_id")
    private String channelWalletId;

    /**
     * 状态,[0:待生成,1:生成中,2:生成失败,3:待使用,4:锁定中,5:冷却中]
     */
    @Schema(title = "状态,[0:待生成,1:生成中,2:生成失败,3:待使用,4:锁定中,5:冷却中]")
    @TableField(value = "status")
    private Integer status;

    /**
     * 状态描述
     */
    @Schema(title = "状态信息记录")
    @TableField(value = "status_msg")
    private String statusMsg;

    // 优化时考虑是否需要将锁定和锁定结束时间从入金申请单中迁移过来
    @Schema(title = "截止时间", description = "默认为创建时间,当状态为冷却中时,截止时间为冷却开始时间加上冷却时长,当截止时间小于当前时间时,状态变为待使用")
    @TableField(value = "deadline")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    private Date deadline;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


    // ================== 以下为非数据库字段 ==================
    @Schema(title = "资产状态-描述")
    public String getStatusDesc() {
        return MerchantWalletStatusEnum.getEnumDesc(status);
    }

    @Schema(title = "资产类型-描述")
    public String getAssetTypeDesc() {
        return AssetTypeEnum.getEnumDesc(assetType);
    }

    @Schema(title = "通道子类型-描述")
    public String getChannelSubTypeDesc() {
        return ChannelSubTypeEnum.getEnumDesc(channelSubType);
    }

    @Schema(title = "用途类型-描述")
    public String getPurposeTypeDesc() {
        return PurposeTypeEnum.getEnumDesc(purposeType);
    }
}