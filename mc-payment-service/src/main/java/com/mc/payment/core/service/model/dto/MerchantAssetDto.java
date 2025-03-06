package com.mc.payment.core.service.model.dto;

import com.mc.payment.api.model.rsp.QueryAssetRsp;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商户可用的资产
 *
 * @author Conor
 * @since 2024-10-10 16:22:14.194
 */
@Data
public class MerchantAssetDto {
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

    @Schema(title = "资产网络/支付类型全称")
    private String assetNet;

    @Schema(title = "合约地址")
    private String tokenAddress;

    @Schema(title = "是否入金可用,[0:否,1:是]")
    private Integer depositStatus;

    @Schema(title = "是否出金可用,[0:否,1:是]")
    private Integer withdrawalStatus;

    @Schema(title = "最小入金金额", description = "单位:当前币种")
    private BigDecimal minDepositAmount;

    @Schema(title = "最小出金金额", description = "单位:当前币种")
    private BigDecimal minWithdrawalAmount;

    @Schema(title = "最大入金金额", description = "单位:当前币种")
    private BigDecimal maxDepositAmount;

    @Schema(title = "最大出金金额", description = "单位:当前币种")
    private BigDecimal maxWithdrawalAmount;

    @Schema(title = "手续费币种,[如:USDT 出金就需用到 ETH 的手续费]")
    private String feeAssetName;

    @Schema(title = "资产名称/币种-图标", description = "Base64编码")
    private String assetNameIcon;

    @Schema(title = "网络协议/支付类型-图标", description = "Base64编码")
    private String netProtocolIcon;

    public QueryAssetRsp convert() {
        QueryAssetRsp rsp = new QueryAssetRsp();
        rsp.setAssetType(this.assetType);
        rsp.setAssetName(this.assetName);
        rsp.setNetProtocol(this.netProtocol);
        rsp.setDepositStatus(this.depositStatus);
        rsp.setWithdrawalStatus(this.withdrawalStatus);
        rsp.setAssetNet(this.assetNet);
        rsp.setMinDepositAmount(this.minDepositAmount.stripTrailingZeros().toPlainString());
        rsp.setMinWithdrawalAmount(this.minWithdrawalAmount.stripTrailingZeros().toPlainString());
        rsp.setMaxDepositAmount(this.maxDepositAmount.stripTrailingZeros().toPlainString());
        rsp.setMaxWithdrawalAmount(this.maxWithdrawalAmount.stripTrailingZeros().toPlainString());
        rsp.setAssetNameIcon(this.assetNameIcon);
        rsp.setNetProtocolIcon(this.netProtocolIcon);
        return rsp;
    }

    public AssetDto convertAssetDto() {
        AssetDto assetDto = new AssetDto();
        assetDto.setAssetName(this.assetName);
        assetDto.setAssetNet(this.assetNet);
        assetDto.setNetProtocol(this.netProtocol);
        assetDto.setTokenAddress(this.tokenAddress);
        return assetDto;
    }

    public static MerchantAssetDto valueOf(MerchantAssetDetailDto dto) {
        MerchantAssetDto merchantAssetDto = new MerchantAssetDto();
        merchantAssetDto.setChannelSubType(dto.getChannelSubType());
        merchantAssetDto.setAssetType(dto.getAssetType());
        merchantAssetDto.setAssetName(dto.getAssetName());
        merchantAssetDto.setNetProtocol(dto.getNetProtocol());
        merchantAssetDto.setChannelAssetName(dto.getChannelAssetName());
        merchantAssetDto.setChannelNetProtocol(dto.getChannelNetProtocol());
        merchantAssetDto.setAssetNet(dto.getAssetNet());
        merchantAssetDto.setTokenAddress(dto.getTokenAddress());
        merchantAssetDto.setDepositStatus(dto.getDepositStatus());
        merchantAssetDto.setWithdrawalStatus(dto.getWithdrawalStatus());
        merchantAssetDto.setMinDepositAmount(dto.getMinDepositAmount());
        merchantAssetDto.setMinWithdrawalAmount(dto.getMinWithdrawalAmount());
        merchantAssetDto.setMaxDepositAmount(dto.getMaxDepositAmount());
        merchantAssetDto.setMaxWithdrawalAmount(dto.getMaxWithdrawalAmount());
        merchantAssetDto.setFeeAssetName(dto.getFeeAssetName());
        merchantAssetDto.setAssetNameIcon(dto.getAssetNameIcon());
        merchantAssetDto.setNetProtocolIcon(dto.getNetProtocolIcon());
        return merchantAssetDto;
    }
}
