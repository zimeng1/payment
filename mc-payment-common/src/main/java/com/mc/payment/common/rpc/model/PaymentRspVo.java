package com.mc.payment.common.rpc.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Payment
 *
 * @since 2024/2/21 11:14
 */
@Data
public class PaymentRspVo implements Serializable {
    // "Your unique identifier in BlockATM")
    private Integer merchantId;
    // "The token identifier that the customer pays with.")
    private String symbol;
    // "The order number corresponding to your platform.")
    private String orderNo;
    // "The quantity of assets being sent.")
    private BigDecimal amount;
    // "The customer number.")
    private String custNo;
    // "The network where the transaction takes place.")
    private String network;
    // "The ID of the blockchain where the transaction takes place.")
    private String chainId;
    // "The platform order number.")
    private String platOrderNo;
    // "The signature of the transaction.")
    private String sign;
    // "The ID of the transaction.")
    private String txId;
    // "The type of the transaction.")
    private int type;
    // "The status of the transaction.")
    private int status;
}
