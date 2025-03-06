package com.mc.payment.fireblocksapi;

import com.mc.payment.common.base.RetResult;
import com.mc.payment.fireblocksapi.model.req.fireBlocks.*;
import com.mc.payment.fireblocksapi.model.vo.fireBlocks.*;

import java.util.List;

/**
 * @author Marty
 * @since 2024/04/13 16:48
 */
public interface FireBlocksAPI {

    RetResult<VaultAccountVo> createAccount(CreateAccountReq req);

    RetResult<CreateVaultAssetVo> createWallet(CreateWalletReq req);

    RetResult<VaultAccountsPagedVo> queryAccountPage(QueryAccountReq req);

    RetResult<PaginatedAddressVo> queryAssetAddresses(QueryAssetAddressesReq req);

    RetResult<VaultAssetVo> queryVaultAccountAsset(QueryVaultAccountAssetReq req);

    RetResult<VaultAccountVo> queryAccount(String vaultAccountId);


    /**
     * 获取交易记录
     *
     * @param req
     * @return
     */
    RetResult<List<TransactionVo>> queryTransactions(QueryTransactionsReq req);

    /**
     * 通过 Fireblocks 交易 ID 查找特定交易
     *
     * @param req  txid用的是fireblocks.transactions().createTransaction返回的id
     * @return
     */
    RetResult<TransactionVo> queryTransactionsById(QueryTransactionsByIdReq req);

    /**
     * 创建新交易
     *
     * @param req
     * @return
     */
    RetResult<CreateTransactionVo> createTransactions(CreateTransactionReq req);

    /**
     * 估算交易请求的交易费用。
     *
     * @param req
     * @return
     */
    RetResult<EstimatedTransactionFeeVo> estimateFee(CreateTransactionReq req);

    RetResult<List<AssetTypeVo>> querySupportedAssets();

    RetResult<EstimatedNetworkFeeVo> estimateNetworkFee(QueryEstimateNetworkFeeReq req);

    RetResult<RegisterNewAssetVo> registerNewAsset(RegisterNewAssetReq req);

    RetResult<CreateVaultAssetVo> activateAsset(VaultAccountAndAssetReq req);

    RetResult<ResendWebhooksVo> resendWebhooks(ResendWebhooksReq req);

    RetResult<VaultAssetVo> updateVaultAccountAssetBalance(VaultAccountAndAssetReq req);
}