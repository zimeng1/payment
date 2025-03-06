package com.mc.payment.core.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MerchantGenerateWalletAssetDto {
    @Schema(title = "通道子类型")
    private Integer channelSubType;

    @Schema(title = "资产类型,[0:加密货币,1:法币]")
    private Integer assetType;

    @Schema(title = "资产名称/币种")
    private String assetName;

    @Schema(title = "网络协议/支付类型")
    private String netProtocol;

    @Schema(title = "通道资产名称")
    private String channelAssetName;

    @Schema(title = "通道资产网络协议/支付类型")
    private String channelNetProtocol;
    /**
     * 是否自动生成钱包,[0:否,1:是]
     */
    @Schema(title = "是否自动生成钱包,[0:否,1:是]")
    private Integer generateWalletStatus;

    /**
     * 生成钱包小于等于阈值
     */
    @Schema(title = "生成钱包小于等于阈值")
    private Integer generateWalletLeQuantity;
    /**
     * 生成钱包数量
     */
    @Schema(title = "生成钱包数量")
    private Integer generateWalletQuantity;

    @Schema(title = "商户id")
    private String merchantId;

    @Schema(title = "商户名称")
    private String merchantName;
}
