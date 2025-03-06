package com.mc.payment.core.service.model.req.merchant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class GenerateWalletReq {
    @Schema(title = "商户id")
    private String merchantId;
    @Schema(title = "生成钱包资产列表")
    private List<GenerateWalletAsset> generateWalletAssets;

}
