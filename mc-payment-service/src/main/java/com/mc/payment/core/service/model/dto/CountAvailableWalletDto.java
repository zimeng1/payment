package com.mc.payment.core.service.model.dto;

import lombok.Data;

@Data
public class CountAvailableWalletDto {
    private String merchantId;
    private String merchantName;
    private String assetName;
    private String netProtocol;
    private Integer walletCount;
}
