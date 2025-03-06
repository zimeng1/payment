package com.mc.payment.core.service.model.rsp;

import com.mc.payment.core.service.entity.ChannelAssetConfigEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Conor
 * @since 2024/4/18 下午6:41
 */
@Data
@Builder
public class AssetListQueryRsp {
    @Schema(title = "资产名称,[如:BTC]")
    private String assetName;

    @Schema(title = "资产网络")
    private String assetNet;

    @Schema(title = "网络协议")
    private String netProtocol;

    @Schema(title = "合约地址")
    private String tokenAddress;

    @Schema(title = "最小入金金额,单位U")
    private BigDecimal minDepositAmount;

    @Schema(title = "最小出金金额,单位U")
    private BigDecimal minWithdrawalAmount;

    @Schema(title = "该币种最小入金金额参考值(参考值,单位:本币种; eg: 要入ETH币, 当时ETH转USDT汇率是3485.56,限制最小入金金额10U, 正常是限制0.0029ETH, 这里会返回0.0037ETH(10U*1.3=13U转换而来), 防止汇率波动触发最小入金限制, ps: 如果找不到该币种转U的汇率, 该值=0)")
    private BigDecimal referMinDepositAmountSelf;

    @Schema(title = "该币种最小出金金额参考值(参考值,单位:本币种; eg: 要出ETH币, 当时ETH转USDT汇率是3485.56,限制最小出金金额10U, 正常是限制0.0029ETH, 这里会返回0.0037ETH(10U*1.3=13U转换而来), 防止汇率波动触发最小出金限制, ps: 如果找不到该币种转U的汇率, 该值=0)")
    private BigDecimal referMinWithdrawalAmountSelf;


    public static AssetListQueryRsp valueOf(ChannelAssetConfigEntity entity) {
        return AssetListQueryRsp.builder()
                .assetName(entity.getAssetName())
                .assetNet(entity.getAssetNet())
                .netProtocol(entity.getNetProtocol())
                .tokenAddress(entity.getTokenAddress())
                .minDepositAmount(entity.getMinDepositAmount())
                .minWithdrawalAmount(entity.getMinWithdrawalAmount())
                .build();
    }
}
