package com.mc.payment.core.service.model;

import lombok.Data;

/**
 * 获取可用钱包DTO
 *
 * @author Conor
 * @since 2024-08-14 18:07:26.038
 */
@Data
public class GetAvailableWalletDto {
    private String merchantId;
    //    资产类型,[0:加密货币,1:法币]
    private Integer assetType;
    private Integer channelSubType;
    private String assetName;
    private String netProtocol;
    //    用途类型,[0:入金,1:出金]
    private Integer purposeType;
    private boolean lock;
}