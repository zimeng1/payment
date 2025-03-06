package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Conor
 * @since 2024/6/7 上午9:58
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateWalletQRCodeReq {
     @Schema(title = "钱包地址")
     @NotBlank(message = "[钱包地址]不能为空")
     private String walletAddress;
}
