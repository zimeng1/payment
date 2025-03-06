package com.mc.payment.third.party.api.model.req.fireBlocks;

import lombok.Data;

/**
 * @author Marty
 * @since 2024/04/15 17:30
 */
@Data
//Destinations")
public class TransactionDestinationReq extends TransactionDestinationPeerPathReq {

    //The amount of the destinations.")
    private String amount;

}
