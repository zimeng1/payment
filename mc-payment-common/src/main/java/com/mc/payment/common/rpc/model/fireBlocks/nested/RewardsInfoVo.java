package com.mc.payment.common.rpc.model.fireBlocks.nested;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Marty
 * @since 2024/04/17 12:03
 */
@Data
public class RewardsInfoVo  implements Serializable {
    // The ALGO rewards acknowledged by the source account of the transaction
    private String srcRewards;

    // The ALGO rewards acknowledged by the destination account of the transaction
    private String destRewards;

}
