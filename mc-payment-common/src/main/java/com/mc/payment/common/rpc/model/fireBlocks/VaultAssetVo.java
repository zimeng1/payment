package com.mc.payment.common.rpc.model.fireBlocks;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Notification is sent when a vault account's asset balance is updated.
 * url:https://developers.fireblocks.com/reference/vault-webhooks
 *
 * @author Marty
 * @since 2024/04/15 19:47
 */
@Data
public class VaultAssetVo implements Serializable {
    // The ID of the asset
    private String id;

    // Total wallet balance
    private String total;

    // Deprecated - replaced by "total"
    private String balance;

    // 	Funds that are available for transfer, equal to the blockchain balance minus any locked amount
    private String available;

    // 	The cumulative balance of all pending transactions to be cleared
    private String pending;

    // 	Staked funds; returned only for DOT
    private String staked;

    // Frozen by your workspace's AML policies
    private String frozen;

    // Funds in outgoing transactions not yet published to the network
    private String lockedAmount;

    // 	The height (number) of the block of the balance
    private String blockHeight;

    // The hash of the block of the balance
    private String blockHash;
}

