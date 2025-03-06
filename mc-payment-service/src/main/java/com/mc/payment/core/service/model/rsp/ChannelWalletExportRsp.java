package com.mc.payment.core.service.model.rsp;

import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.enums.ChannelWalletStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 通道钱包导出实体
 */
@Getter
@Setter
public class ChannelWalletExportRsp {

    /**
     * 通道钱包id
     */
    @Schema(title = "通道钱包id")
    private String id;

    /**
     * 通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]
     */
    @Schema(title = "通道子类型,[1:FireBlocks,2:OFAPay,3:PayPal,4:PassToPay,5:Ezeebill,6:CheezeePay]")
    private Integer channelSubType;

    public String getChannelSubTypeText() {
        return ChannelSubTypeEnum.getEnumDesc(channelSubType);
    }

    /**
     * 通道子类型文本
     */
    @Schema(title = "通道子类型文本")
    private String channelSubTypeText;

    /**
     * 资产类型/币种
     */
    @Schema(title = "资产类型/币种")
    private String assetName;

    /**
     * 网络协议/支付类型
     */
    @Schema(title = "网络协议/支付类型")
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
     * 余额
     */
    @Schema(title = "余额")
    private BigDecimal balance;

    public String getStatusText() {
        return ChannelWalletStatusEnum.getEnumDesc(status);
    }

    /**
     * 状态,[0:待生成,1:生成中,2:生成失败,3:生成成功]
     */
    @Schema(title = "状态,[0:待生成,1:生成中,2:生成失败,3:生成成功]")
    private Integer status;

    /**
     * 状态文本
     */
    @Schema(title = "状态文本")
    private String statusText;

    @Schema(title = "操作时间")
    private LocalDateTime operateTime;

    @Schema(title = "操作人")
    protected String operateBy;
}