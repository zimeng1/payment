package com.mc.payment.api.model.rsp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class QueryAssetRsp {
    @Schema(title = "资产类型,[0:加密货币,1:法币]")
    private Integer assetType;

    @Schema(title = "资产名称/币种")
    private String assetName;

    @Schema(title = "网络协议/支付类型")
    private String netProtocol;

    @Schema(title = "是否入金可用,[0:否,1:是]")
    private Integer depositStatus;

    @Schema(title = "是否出金可用,[0:否,1:是]")
    private Integer withdrawalStatus;

    @Schema(title = "资产网络/支付类型全称")
    private String assetNet;

    @Schema(title = "最小入金金额", description = "单位:当前币种;为0则不限制")
    private String minDepositAmount;

    @Schema(title = "最小出金金额", description = "单位:当前币种;为0则不限制")
    private String minWithdrawalAmount;

    @Schema(title = "最大入金金额", description = "单位:当前币种;为0则不限制")
    private String maxDepositAmount;

    @Schema(title = "最大出金金额", description = "单位:当前币种;为0则不限制")
    private String maxWithdrawalAmount;

    @Schema(title = "资产名称/币种-图标", description = "Base64编码")
    private String assetNameIcon;

    @Schema(title = "网络协议/支付类型-图标", description = "Base64编码")
    private String netProtocolIcon;
}
