package com.mc.payment.core.service.model.req.merchant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GenerateWalletAsset {
    /**
     * 资产名称,[如:BTC]
     */
    @Schema(title = "资产名称,[如:BTC]")
    @NotBlank(message = "资产名称不能为空")
    private String assetName;

    /**
     * 网络协议
     */
    @Schema(title = "网络协议")
    @NotBlank(message = "网络协议不能为空")
    private String netProtocol;

    @Schema(title = "生成钱包数量")
    @NotNull(message = "生成钱包数量不能为空")
    @Size(min = 1, max = 20, message = "生成钱包数量范围[1,20]")
    private Integer quantity;
}