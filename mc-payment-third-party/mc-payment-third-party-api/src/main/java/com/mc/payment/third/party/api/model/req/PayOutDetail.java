package com.mc.payment.third.party.api.model.req;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author conor
 * @since 2024/2/22 21:06:44
 */
@Data
public class PayOutDetail implements Serializable {
    private static final long serialVersionUID = -4877118147902732001L;
    private String tokenAddress;
    private BigDecimal amount;
    private String toAddress;
    private String orderNo;

}
