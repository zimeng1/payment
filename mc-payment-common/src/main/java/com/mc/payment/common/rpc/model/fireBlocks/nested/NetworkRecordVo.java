package com.mc.payment.common.rpc.model.fireBlocks.nested;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Marty
 * @since 2024/04/17 11:51
 */
@Data
public class NetworkRecordVo implements Serializable {
    // Source of the transaction
    private TraPeerPathVo source;

    // Destination of the transaction
    private TraPeerPathVo destination;

    // Blockchain hash of the transaction
    private String txHash;

    // The fee paid to the network
    private Integer networkFee;

    // Transaction asset
    private String assetId;

    // The net amount of the transaction, after fee deduction
    private Integer netAmount;

    /**
     * Status of the blockchain transaction
     * DROPPED - The transaction was dropped by the network (Typically due to a low fee, or if the mempool is full).
     * BROADCASTING - Broadcasting to the blockchain.
     * CONFIRMING - Pending confirmations.
     * FAILED - The transaction has failed to complete on the blockchain.
     * CONFIRMED - Confirmed on the blockchain.
     */
    private String status;

    // Type of the operation
    private String type;

    // Destination address
    private String destinationAddress;

    // For account-based assets only, the source address of the transaction
    private String sourceAddress;

}
