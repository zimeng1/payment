package com.mc.payment.core.service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.model.enums.AssetTypeEnum;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.enums.ChannelWalletStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 通道钱包
 *
 * @TableName mcp_channel_wallet
 */
@TableName(value = "mcp_channel_wallet")
@Data
public class ChannelWalletEntity extends BaseNoLogicalDeleteEntity implements Serializable {
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
     * 网络协议/支付类型
     */
    @Schema(title = "网络协议/支付类型")
    @TableField(value = "net_protocol")
    private String netProtocol;

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
    @Schema(title = "冻结金额")
    @TableField(value = "freeze_amount")
    private BigDecimal freezeAmount;

    /**
     * 通道接口凭据加密信息Json,如API密钥
     */
    @Schema(title = "通道接口凭据加密信息Json,如API密钥")
    @TableField(value = "api_credential")
    private String apiCredential;

    /**
     * 备注
     */
    @Schema(title = "备注")
    @TableField(value = "remark")
    private String remark;

    /**
     * 状态,[0:待生成,1:生成中,2:生成失败,3:生成成功]
     */
    @Schema(title = "状态,[0:待生成,1:生成中,2:生成失败,3:生成成功]")
    @TableField(value = "status")
    private Integer status;

    /**
     * 状态描述
     */
    @Schema(title = "状态信息记录")
    @TableField(value = "status_msg")
    private String statusMsg;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


    // ================== 以下为非数据库字段 ==================
    @Schema(title = "资产状态-描述")
    public String getStatusDesc() {
        return ChannelWalletStatusEnum.getEnumDesc(status);
    }

    @Schema(title = "资产类型-描述")
    public String getAssetTypeDesc() {
        return AssetTypeEnum.getEnumDesc(assetType);
    }

    @Schema(title = "通道子类型-描述")
    public String getChannelSubTypeDesc() {
        return ChannelSubTypeEnum.getEnumDesc(channelSubType);
    }


}