package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "钱包余额查询")
public class WalletBalanceReq {
    @Schema(title = "账户类型,[0:入金账户,1:出金账户]")
    private String accountType;

    @Schema(title = "资产名称")
    private String assetName;

    @Schema(title = "网络协议")
    private String netProtocol;

    @Schema(title = "钱包地址")
    private String walletAddress;
}
