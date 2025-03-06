package com.mc.payment.common.rpc.model.fireBlocks.nested;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Marty
 * @since 2024/04/17 11:41
 */
@Data
public class FeeInfoVo implements Serializable {

    // The fee paid to the network
    private String networkFee;

    // The total fee deducted by the exchange from the actual requested amount (serviceFee = amount - netAmount)
    private String serviceFee;

}
