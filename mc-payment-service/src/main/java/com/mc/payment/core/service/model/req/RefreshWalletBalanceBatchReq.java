package com.mc.payment.core.service.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * @author Conor
 * @since 2024/6/5 下午4:55
 */
@Data
public class RefreshWalletBalanceBatchReq {

    @Schema(title = "钱包id列表")
    @NotNull(message = "钱包id列表不能为空")
    @Size(min=1, message = "钱包id列表不能传0个元素")
    private List<String> walletIds;
}
