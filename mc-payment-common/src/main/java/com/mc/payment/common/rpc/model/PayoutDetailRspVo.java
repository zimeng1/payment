package com.mc.payment.common.rpc.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Payout detail
 *
 * @since 2024/2/21 11:15
 */
@Data
public class PayoutDetailRspVo implements Serializable {
    // "The quantity of assets being sent.")
    private BigDecimal amount;
    // "The order number corresponding to your platform.")
    private String orderNo;
    // "The token identifier that the customer pays with.")
    private String symbol;
    // "To which address should the assets be sent.")
    private String toAddress;
    // "The token contract address corresponding to the assets.")
    private String tokenAddress;
}
