package com.mc.payment.common.rpc.model.fireBlocks.nested;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Marty
 * @since 2024/04/17 11:40
 */
@Data
public class AmountInfoVo  implements Serializable {

    // If the transfer is a withdrawal from an exchange, the actual amount that was requested to be transferred. Otherwise, it is the requested amount. This value will always be equal to the amount (number) parameter of TransactionDetails.
    private String amount;

    // The amount requested by the user
    private String requestedAmount;

    // The net amount of the transaction, after fee deduction
    private String netAmount;

    // The USD value of the requested amount
    private String amountUSD;


}
