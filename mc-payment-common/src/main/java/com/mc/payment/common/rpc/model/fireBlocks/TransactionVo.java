package com.mc.payment.common.rpc.model.fireBlocks;

import com.mc.payment.common.rpc.model.fireBlocks.nested.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Transaction Status Updated
 * Notification is sent when there is any change in a transaction's status or when Fireblocks detects an update to the number of confirmations.
 * url:https://developers.fireblocks.com/reference/transaction-webhooks
 *
 * @author Marty
 * @since 2024/04/15 19:31
 */
@Data
public class TransactionVo implements Serializable {

    //	The ID of the transaction.
    private String id;

    //Unique transaction ID provided by the user. Fireblocks highly recommends setting an externalTxId for every transaction created, to avoid submitting the same transaction twice.
    private String externalTxId;

    // COMPLETED-完成, 状态code: FAILED,REJECTED,BLOCKED, CANCELLED,CANCELLING,COMPLETED,CONFIRMING,BROADCASTING,PENDING_3RD_PARTY, PENDING_3RD_PARTY_MANUAL_APPROVAL,PENDING_SIGNATURE,QUEUED,PENDING_AUTHORIZATION,PENDING_AML_SCREENING,SUBMITTED
    //The current primary status of the transaction. See Primary Transaction Statuses for a detailed list.  参考 https://developers.fireblocks.com/reference/primary-transaction-statuses
    private String status;

    //	See Transaction Substatuses for a detailed list of transaction substatuses.
    private String subStatus;

    /**
     * The hash of this transaction on the blockchain. txHash is only returned for crypto assets (not fiat) when the operation type is not RAW or TYPED_MESSAGE.
     * This parameter exists if at least one of the following conditions is met:
     *
     * 1) The transaction’s source type is UNKNOWN, WHITELISTED_ADDRESS, ONE_TIME_ADDRESS, FIAT or GAS_STATION.
     * 2) The transaction’s source type is VAULT and the status is: CONFIRMING, COMPLETED, or was either status prior to changing to FAILED or REJECTED. In some instances, transactions with the status BROADCASTING will include the txHash as well.
     * 3) The transaction’s source type is EXCHANGE and the tratenantIdnsaction’s destination type is VAULT, and the status is: CONFIRMING, COMPLETED, or was either status prior to changing to FAILED.
     */
    private String txHash;

    //The transaction operation type. The default is TRANSFER. [TRANSFER, MINT, BURN, CONTRACT_CALL, TYPED_MESSAGE, RAW, ENABLE_ASSET, STAKE, UNSTAKE, WITHDRAW]
    private String operation;

    // Custom note that describes this transaction in your Fireblocks workspace. The note isn’t sent to the blockchain.
    private String note;

    // 	The ID of the transaction’s asset, for TRANSFER, MINT, BURN or ENABLE_ASSET operations. See the list of supported assets and their IDs on Fireblocks.
    private String assetId;

    //one of the following - XLM_ASSET, XDB_ASSET, TRON_TRC20, SOL_ASSET, HBAR_ERC20, FIAT, ERC721, ERC20, ERC1155, BEP20, BASE_ASSET, ALGO_ASSET
    private String assetType;

    // The transaction’s source.
    private TraPeerPathVo source;

    //For account based assets only, the source address of the transaction.
    // Note: If the status is CONFIRMING, COMPLETED, or was CONFIRMING before either FAILED or REJECTED, then this parameter will contain the source address. This parameter is empty in any other case.
    private String sourceAddress;

    // The transaction’s destination. Note: If a transaction is sent to multiple destinations, the destinations parameter is used instead of destination.
    private TraPeerPathVo destination;

    // For UTXO-based assets, all outputs are specified here.
    private List<DestinationsVo> destinations;

    // 	Address where the asset was transferred.
    private String destinationAddress;

    // 	Description of the address
    private String destinationAddressDescription;

    // 	(Optional) Destination address tag for Ripple; destination memo for EOS, Stellar, Hedera, & DigitalBits; destination note for Algorand; bank transfer description for fiat providers.
    private String destinationTag;

    //  For TRANSFER operations, all details of the transfer amount.
    private AmountInfoVo amountInfo;

    // When set to true, the fee is deducted from the requested amount for transactions initiated from this Fireblocks workspace.
    private boolean treatAsGrossAmount;

    //  Details of the transaction's fee.
    private FeeInfoVo feeInfo;

    // 	The asset type used to pay the fee (ETH for ERC-20 tokens, BTC for Omni, XLM for Stellar tokens, etc.)
    private String feeCurrency;

    // A transaction in Fireblocks can aggregate several blockchain transactions, typically as part of a contract call. Network records specify all intermediate transactions that took place on the blockchain. For single transactions, this parameter is empty.
    private List<NetworkRecordVo> networkRecords;

    // 	The transaction’s creation date and time, in Unix timestamp.
    private BigDecimal createdAt;

    //	The transaction’s last update date and time, in Unix timestamp.
    private BigDecimal lastUpdated;

    // User ID of the initiator of this transaction.
    private String createdBy;

    // User ID(s) of the signer(s) of this transaction.
    private List<String> signedBy;

    // 	User ID of the user that rejected the transaction, only if the transaction was rejected.
    private String rejectedBy;

    // Data object with information about your Transaction Authorization Policy (TAP). For more information about the TAP, refer to the Fireblocks Help Center.
    private Object authorizationInfo;

    // If the transaction originated from an exchange, this is the exchange’s ID of this transaction.
    private String exchangeTxId;

    // 	The ID for AML providers to associate the owner of the funds with the transaction.
    private String customerRefId;

    // The result of the AML screening.
    private AmlScreeningVo amlScreeningResult;

    // The hash of the replaced transaction, only If this is an RBF transaction on an EVM blockchain. Learn more about RBF transactions.
    private String replacedTxHash;

    // Parameters that are specific to some transaction operation types and blockchain networks.
    private Object  extraParameters;

    // A list of signed messages returned for raw signing.
    private List<Object> signedMessages;

    // The number of blockchain confirmations of the transaction. The number will increase until the transaction is considered completed according to the confirmation policy.
    private Integer numOfConfirmations;

    //  The hash and height of the block that the transaction was mined in.
    private BlockInfoVo blockInfo;

    // [optional] For UTXO-based assets this is the vOut, for EVM based, this is the index of the event of the contract call.
    private BigDecimal index;

    //  This field is relevant only for ALGO transactions. Both srcRewrds and destRewards appear only for vault-to-vault transactions. Otherwise, only your workspace side of the transaction is recorded.
    private RewardsInfoVo rewardsInfo;

    // A response from Fireblocks with details about the health of the current process(es). If this object is returned with data, expect potential delays or incomplete transaction statuses.
    private List<Object> systemMessages;

    //(Optional) [ ONE_TIME, WHITELISTED]
    private String addressType;
}

