package com.mc.payment.fireblocksapi.model.vo.fireBlocks.nested;

import lombok.Data;

/**
 * @author Marty
 * @since 2024/04/17 18:38
 */
@Data
//transaction fee
public class TransactionFeeVo {
    //feePerByte
    private String feePerByte;

    //For non-EIP-1559, EVM-based transactions. Price per gas unit (in Ethereum this is specified in Gwei). Note: Only two of the three arguments can be specified in a single transaction:
    private String gasPrice;

    //For EVM-based blockchains only. Units of gas required to process the transaction.    Note: Only two of the three arguments can be specified in a single transaction: gasLimit, gasPrice and networkFee. Fireblocks recommends using a numeric string for accurate precision. Although a number input exists, it is deprecated.
    private String gasLimit;

    //For EVM-based blockchains only. The total transaction fee in the blockchain’s largest unit. Note: Only two of the three arguments can be specified in a single transaction: gasLimit, gasPrice and networkFee. Fireblocks recommends using a numeric string for accurate precision. Although a number input exists, it is deprecated. - The transaction blockchain fee.
    private String networkFee;

    //(optional) Base Fee according to EIP-1559 (ETH assets)
    private String baseFee;

    //(optional) Priority Fee according to EIP-1559 (ETH assets)
    private String priorityFee;



}
