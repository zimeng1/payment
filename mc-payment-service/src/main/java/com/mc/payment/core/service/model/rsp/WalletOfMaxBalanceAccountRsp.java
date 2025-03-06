package com.mc.payment.core.service.model.rsp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Marty
 * @since 2024/5/11 11:16
 */
@Data
public class WalletOfMaxBalanceAccountRsp implements Serializable {

    private static final long serialVersionUID = 6527015259238458269L;

    @Schema(title = "钱包id")
    private String walletId;

    @Schema(title = "钱包地址")
    private String walletAddress;

    @Schema(title = "币值")
    private BigDecimal balance;

    @Schema(title = "资产名称")
    private String assetName;

    @Schema(title = "账号id")
    private String accountId;

    @Schema(title = "外部系统账号id", description = "比如fireblocks创建去账号返回的钱包id就存这儿")
    private String externalId;

    @Schema(title = "账户名")
    private String accountName;
}
