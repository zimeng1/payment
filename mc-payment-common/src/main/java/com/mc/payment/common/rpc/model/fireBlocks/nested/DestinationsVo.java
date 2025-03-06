package com.mc.payment.common.rpc.model.fireBlocks.nested;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Marty
 * @since 2024/04/17 11:29
 */
@Data
public class DestinationsVo implements Serializable {

    // The amount to be sent to this destination
    private String amount;

    // Destination of the transaction
    private TraPeerPathVo destination;

    // The USD value of the requested amount
    private Long amountUSD;

    // Address where the asset was transferred
    private String destinationAddress;

    // Description of the address
    private String destinationAddressDescription;

    // The result of the AML screening
    private AmlScreeningVo amlScreeningResult;

    // The ID for AML providers to associate the owner of funds with transactions
    private String customerRefId;


}
