package com.mc.payment.core.service.model.rsp;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 最佳手续费
 *
 * @author conor
 * @since 2024/2/20 15:30:13
 */
@Data
public class BestFeeRsp {
    /**
     * 通道成本id
     */
    private String channelCostId;
    /**
     * 费率
     */
    private BigDecimal fee;

    public BestFeeRsp() {
    }

    public BestFeeRsp(String channelCostId, BigDecimal fee) {
        this.channelCostId = channelCostId;
        this.fee = fee;
    }
}
