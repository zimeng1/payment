package com.mc.payment.core.service.model.rsp;

import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.enums.MerchantWalletStatusEnum;
import com.mc.payment.core.service.model.enums.PurposeTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商户钱包导出实体
 */
@Getter
@Setter
public class MerchantWalletExportRsp {

    /**
     * 商户钱包id
     */
    @Schema(title = "商户钱包id")
    private String id;

    /**
     * 钱包关联账户
     */
    @Schema(title = "钱包关联账户")
    private String accountName;

    /**
     * 钱包所属商户
     */
    @Schema(title = "钱包所属商户")
    private String merchantName;

    /**
     * 通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]
     */
    @Schema(title = "通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    private Integer channelSubType;

    public String getChannelSubTypeText() {
        return ChannelSubTypeEnum.getEnumDesc(channelSubType);
    }

    /**
     * 账户所属通道
     */
    @Schema(title = "账户所属通道")
    private String channelSubTypeText;

    /**
     * 钱包指定资产
     */
    @Schema(title = "钱包指定资产")
    private String assetName;

    /**
     * 网络协议
     */
    @Schema(title = "网络协议")
    private String netProtocol;

    /**
     * 钱包地址
     */
    @Schema(title = "钱包地址")
    private String walletAddress;

    /**
     * 冻结金额
     */
    @Schema(title = "冻结金额")
    private BigDecimal freezeAmount;

    /**
     * 可用金额
     */
    @Schema(title = "可用金额")
    private BigDecimal availableAmount;

    /**
     * 商户资产余额
     */
    @Schema(title = "商户资产余额")
    private BigDecimal balance;

    /**
     * 状态,[0:待生成,1:生成中,2:生成失败,3:生成成功]
     */
    @Schema(title = "状态,[0:待生成,1:生成中,2:生成失败,3:待使用,4:锁定中,5:冷却中]")
    private Integer status;

    public String getStatusText() {
        return MerchantWalletStatusEnum.getEnumDesc(status);
    }

    /**
     * 状态文本
     */
    @Schema(title = "状态文本")
    private String statusText;

    /**
     * 用途类型,[0:入金,1:出金]
     */
    @Schema(title = "用途类型,[0:入金,1:出金]")
    private Integer purposeType;

    public String getPurposeTypeText() {
        return PurposeTypeEnum.getEnumDesc(purposeType);
    }

    /**
     * 用途类型
     */
    @Schema(title = "用途类型")
    private String purposeTypeText;

    /**
     * 操作时间
     */
    @Schema(title = "操作时间")
    private LocalDateTime operateTime;

    /**
     * 操作人
     */
    @Schema(title = "操作人")
    protected String operateBy;
}