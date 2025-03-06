package com.mc.payment.fireblocksapi.model.req.fireBlocks;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Marty
 * @since 2024/4/25 10:26
 */
@Data
//the ix fo the transaction")
public class QueryTransactionsByIdReq {

    //交易id
    //The txId of the transaction")
    @NotBlank(message = "[txId] is null")
    private String txId;

}
