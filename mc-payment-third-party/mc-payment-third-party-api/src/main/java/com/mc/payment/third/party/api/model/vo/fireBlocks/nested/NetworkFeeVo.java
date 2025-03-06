package com.mc.payment.third.party.api.model.vo.fireBlocks.nested;

import lombok.Data;

/**
 * @author Marty
 * @since 2024/4/20 10:59
 */
@Data
//NetworkFeeVo
public class NetworkFeeVo {

    //feePerByte
    private String feePerByte;

    //For non-EIP-1559, EVM-based transactions. Price per gas unit (in Ethereum this is specified in Gwei). Note: Only two of the three arguments can be specified in a single transaction:
    private String gasPrice;

    //For EVM-based blockchains only. The total transaction fee in the blockchain’s largest unit. Note: Only two of the three arguments can be specified in a single transaction: gasLimit, gasPrice and networkFee. Fireblocks recommends using a numeric string for accurate precision. Although a number input exists, it is deprecated. - The transaction blockchain fee.
    private String networkFee;

    //priorityFee
    private String priorityFee;

    //baseFee
    private String baseFee;
}
