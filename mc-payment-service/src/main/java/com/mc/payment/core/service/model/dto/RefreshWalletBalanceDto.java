package com.mc.payment.core.service.model.dto;

import lombok.Data;

/**
 * @author Conor
 * @since 2024/6/5 下午4:35
 */
@Data
public class RefreshWalletBalanceDto {
    private String walletId;
    private String accountExternalId;
    private String channelAssetName;
}
