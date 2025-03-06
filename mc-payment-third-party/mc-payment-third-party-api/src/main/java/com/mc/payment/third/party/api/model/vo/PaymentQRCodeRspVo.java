package com.mc.payment.third.party.api.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Payment detail
 */
@Data
public class PaymentQRCodeRspVo {
    //The token identifier that the customer pays with.
    private String symbol;
    //The quantity of assets being sent.
    private BigDecimal amount;
    //The network where the transaction takes place.
    private String network;
    //The ID of the blockchain where the transaction takes place.
    private String chainId;
    //The time when the transaction was created.
    private long createTime;
    //The ID of the merchant.
    private int merchantId;
    //The platform order number.
    private String platOrderNo;
    //The signature of the transaction.
    private String sign;
    //The ID of the transaction.
    private String txId;
    //The type of the transaction.
    private int type;
    //The status of the transaction.
    private int status;
}