package com.mc.payment.third.party.api.model.vo.fireBlocks;

import com.mc.payment.third.party.api.model.vo.fireBlocks.nested.RewardsInfoVo;
import lombok.Data;

/**
 * @author Marty
 * @since 2024/04/17 16:57
 */
@Data
//Returns a wallet for a specific asset of a vault account.
public class VaultAssetVo {

    //The ID of the asset
    private String id;

    //The total wallet balance. In EOS this value includes the network balance, self staking and pending refund. For all other coins it is the balance as it appears on the blockchain.
    private String total;

    //Deprecated - replaced by total
    private String balance;

    //Funds available for transfer. Equals the blockchain balance minus any locked amounts
    private String available;

    //The cumulative balance of all transactions pending to be cleared
    private String pending;

    //The cumulative frozen balance
    private String frozen;

    //Funds in outgoing transactions that are not yet published to the network
    private String lockedAmount;

    //Staked balance
    private String staked;

    //The height (number) of the block of the balance
    private String blockHeight;

    //The hash of the block of the balance
    private String blockHash;

    //Amount that is pending for rewards
    private RewardsInfoVo rewardsInfo;

}
