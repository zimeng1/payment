package com.mc.payment.core.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商户可用钱包统计
 *
 * @author Conor
 * @since 2025-01-02 18:31:58.607
 */
@Data
public class MerchantAvailableWalletDto {
    private String assetName;
    private String netProtocol;
    @Schema(title = "用途类型,[0:入金,1:出金]")
    private Integer purposeType;
    private Integer walletCount;
}
