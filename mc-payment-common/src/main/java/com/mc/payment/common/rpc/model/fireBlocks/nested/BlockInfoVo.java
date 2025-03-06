package com.mc.payment.common.rpc.model.fireBlocks.nested;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Marty
 * @since 2024/04/17 12:01
 */
@Data
public class BlockInfoVo  implements Serializable {
    // The height (number) of the block the transaction was mined in
    private String blockHeight;

    // The hash of the block the transaction was mined in
    private String blockHash;

}
