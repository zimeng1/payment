package com.mc.payment.third.party.api.model.req.fireBlocks;

import com.mc.payment.common.base.BaseReq;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Create a new transaction
 * @author Marty
 * @since 2024/04/15 16:36
 */
@EqualsAndHashCode(callSuper = true)
@Data
//Create a new transaction")
public class CreateTransactionReq extends BaseReq {

    //幂等性唯一key,24小时内同一个key的操作结果一致
    //idempotencyKey, 幂等性唯一key")
//    @NotBlank(message = "[idempotencyKey] is null")
    private String idempotencyKey;

    //TransactionRequestAmount
    //Unique ID of the End-User wallet to the API request. Required for end-user wallet operations., API 请求的最终用户钱包的唯一 ID。最终用户钱包操作所需")
    private String xEndUserWalletId;

    //自定义注释，不发送到区块链，用于描述 Fireblocks 工作区中的交易。
    //Custom note, not sent to the blockchain, to describe the transaction at your Fireblocks workspace.")
    private String note;

    //操作的默认值。将资金从一个账户转移到另一个账户。 UTXO 区块链允许多输入和多输出传输。所有其他区块链都允许使用一个源地址和一个目标地址进行传输。
    //The default value for an operation. Transfers funds from one account to another. UTXO blockchains allow multi-input and multi-output transfers. All other blockchains allow transfers with one source address and one destination address.")
    private String operation;

    // 交易的唯一 ID
    //An optional but highly recommended parameter. Fireblocks will reject future transactions with same ID., 交易的唯一 ID")
    private String externalTxId;

    //钱包id， eg:BTC
    @NotBlank(message = "[assetId] is null")
    //The ID of the asset to transfer, for TRANSFER, MINT or BURN operations. See the list of supported assets and their IDs on Fireblocks.")
    private String assetId;

    //来源
    @NotNull(message = "[source] is null")
    //The source of the transaction.")
    private TransactionPeerPathReq source;

    //目标
    @NotNull(message = "[destination] is null")
    //The destination of the transaction.")
    private TransactionDestinationPeerPathReq destination;

    //目前不支持批量交易
    //For UTXO based blockchains, you can send a single transaction to multiple destinations.")
    private List<TransactionDestinationReq> destinations;

    //交易金额
    @NotBlank(message = "[amount] is null")
    //For TRANSFER operations, the requested amount to transfer, in the asset’s unit. Fireblocks recommends using a numeric string for accurate precision. Although a number input exists, it is deprecated.")
    private String amount;

    //“设置为 时true，将从请求的金额中扣除费用。” 只有当交易的资产是基础资产（例如 ETH 或 MATIC）时才可以考虑此参数。如果该资产不能用于交易手续费，如USDC，则忽略该参数，并从源账户中的相关基础资产钱包中扣除费用。
    //When set to true, the fee will be deducted from the requested amount.")
    private Boolean treatAsGrossAmount;

    //仅适用于 Polkadot、Kusama 和 Westend 交易。当设置为 true 时，Fireblocks 将清空资产钱包。如果在源账户正好是1个DOT时设置为true，交易将会失败。任何多于或少于 1 DOT 的金额都会成功。这是 Polkadot 区块链的限制。
    //For Polkadot, Kusama and Westend transactions only. When set to true, Fireblocks will empty the asset wallet.")
    private Boolean forceSweep;

/*
    // 有认知
    private String feeLevel;

    // TransactionRequestFee 费用
    private String fee;

    // TransactionRequestFee 优先费
    private String priorityFee;

    private Boolean failOnLowFee;

    private String maxFee;

    //For EVM-based blockchains only. Units of gas required to process the transaction.    Note: Only two of the three arguments can be specified in a single transaction: gasLimit, gasPrice and networkFee. Fireblocks recommends using a numeric string for accurate precision. Although a number input exists, it is deprecated.
    private String gasLimit;

    //For non-EIP-1559, EVM-based transactions. Price per gas unit (in Ethereum this is specified in Gwei).   Note: Only two of the three arguments can be specified in a single transaction: gasLimit, gasPrice and networkFee. Fireblocks recommends using a numeric string for accurate precision. Although a number input exists, it is deprecated.
    private String gasPrice;

    //For EVM-based blockchains only. The total transaction fee in the blockchain’s largest unit.   Note: Only two of the three arguments can be specified in a single transaction: gasLimit, gasPrice and networkFee. Fireblocks recommends using a numeric string for accurate precision. Although a number input exists, it is deprecated. - The transaction blockchain fee.
    private String networkFee;

    private String replaceTxByHash;

    private Object extraParameters;

    private String customerRefId;*/

/*    private TravelRuleCreateTransactionRequest travelRuleMessage;

    private Boolean autoStaking;

    private TransactionRequestNetworkStaking networkStaking;

    private TransactionRequestNetworkStaking cpuStaking;*/

    //特殊临时标记,防止外部调度
    private String sign;

}
