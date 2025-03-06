package com.mc.payment.core.service.model.rsp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Conor
 * @since 2024/4/13 上午11:33
 */
@Data
public class DepositRequestRsp {
    @Schema(title = "钱包地址")
    private String address;

    @Schema(title = "地址失效时间戳-精确毫秒")
    private long expireTimestamp;

    @Schema(title = "通道费")
    private BigDecimal channelFee;

    @Schema(title = "跟踪id,申请方提供唯一跟踪ID以查询处理结果，长度限制为50个字符")
    private String trackingId;
}
