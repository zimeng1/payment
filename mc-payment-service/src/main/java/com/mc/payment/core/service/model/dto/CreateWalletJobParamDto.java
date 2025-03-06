package com.mc.payment.core.service.model.dto;

import lombok.Data;

/**
 * 创建钱包任务参数实体
 *
 * @author Conor
 * @since 2024/5/11 下午2:28
 */
@Data
public class CreateWalletJobParamDto {
    private String vaultAccountId;
    private String assetId;
    private String merchantId;
    private String accountId;
    private String assetName;
    private String netProtocol;
    private Integer channelSubType;
}
