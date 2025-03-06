package com.mc.payment.core.service.model.dto;

import lombok.Data;

/**
 * @author conor
 * @since 2024/8/21 21:13:37
 */
@Data
public class FireBlocksWalletSyncInfoDto {
    private String walletId;
    private String channelWalletId;
    private String externalId;
    private String channelAssetName;

}
