package com.mc.payment.core.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商户资产详细信息,包含通道资产数据
 *
 * @author Conor
 * @since 2025-01-02 19:28:34.925
 */
@Data
public class MerchantAssetDetailDto extends MerchantAssetDto {
    /**
     * 是否自动生成钱包,[0:否,1:是]
     */
    @Schema(title = "是否自动生成钱包,[0:否,1:是]")
    private Integer generateWalletStatus;

    /**
     * 生成钱包小于等于阈值
     */
    @Schema(title = "生成钱包小于等于阈值")
    private Integer generateWalletLeQuantity;
    /**
     * 生成钱包数量
     */
    @Schema(title = "生成钱包数量")
    private Integer generateWalletQuantity;

}
