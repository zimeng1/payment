package com.mc.payment.fireblocksapi.model.req.fireBlocks;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Marty
 * @since 2024/04/15 17:29
 */

@Data
//Destination")
public class TransactionDestinationPeerPathReq extends TransactionPeerPathReq{
    //目标的地址
    @NotNull(message = "[oneTimeAddress] is null")
    //The oneTimeAddress of the destination.")
    private OneTimeAddressReq oneTimeAddress;

}
