package com.mc.payment.third.party.api;

import com.mc.payment.common.base.RetResult;
import com.mc.payment.third.party.api.model.req.fireBlocks.*;
import com.mc.payment.third.party.api.model.vo.fireBlocks.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author Marty
 * @since 2024/04/13 16:48
 */
@FeignClient(value = "mc-payment-third-party-service", path = "/api/v1/fireBlocks",contextId = "third-party-Feign-FireBlocks")
public interface IFireBlocksFeignClient {

    @PostMapping("/createAccount")
    RetResult<VaultAccountVo> createAccount(@RequestBody CreateAccountReq req);

    @PostMapping("/createWallet")
    RetResult<CreateVaultAssetVo> createWallet(@RequestBody CreateWalletReq req);

    //    @GetMapping("/accounts")
    @PostMapping("/accounts")
    RetResult<VaultAccountsPagedVo> queryAccount(@RequestBody QueryAccountReq req);

//    @GetMapping("/accounts/{vaultAccountId}/{assetId}/addresses_paginated")
    @PostMapping("/accounts/addresses_paginated")
    RetResult<PaginatedAddressVo> queryAssetAddresses(@RequestBody QueryAssetAddressesReq req);

//    @GetMapping("/accounts/{vaultAccountId}/{assetId}")
    @PostMapping("/queryVaultAccountAsset")
    RetResult<VaultAssetVo> queryVaultAccountAsset(@RequestBody QueryVaultAccountAssetReq req);


    /**
     * 获取交易记录
     * @param req
     * @return
     */
//    @GetMapping("/transactions")
    @PostMapping("/queryTransactions")
    RetResult<List<TransactionVo>> queryTransactions(@RequestBody QueryTransactionsReq req);

    /**
     * 通过 Fireblocks 交易 ID 查找特定交易
     * @param req
     * @return
     */
//    @GetMapping("/transactions/{txId}") todo
    @PostMapping("/queryTransactionsById")
    RetResult<TransactionVo> queryTransactionsById(@RequestBody QueryTransactionsByIdReq req);

    /**
     * 创建新交易
     * @param req
     * @return
     */
    @PostMapping("/createTransactions")
    RetResult<CreateTransactionVo> createTransactions(@RequestBody CreateTransactionReq req);

    /**
     * 估算交易请求的交易费用。
     * @param req
     * @return
     */
    @PostMapping("/transactions/estimate_fee")
    RetResult<EstimatedTransactionFeeVo> estimateFee(@RequestBody CreateTransactionReq req);

//    @GetMapping("/supported_assets")
    @PostMapping("/querySupportedAssets")
    RetResult<List<AssetTypeVo>> querySupportedAssets();

    @PostMapping("/estimate_network_fee")
    RetResult<EstimatedNetworkFeeVo> estimateNetworkFee(@RequestBody QueryEstimateNetworkFeeReq req);

    @PostMapping("/registerNewAsset")
    RetResult<RegisterNewAssetVo> registerNewAsset(@RequestBody RegisterNewAssetReq req);

    @PostMapping("/activateAsset")
    RetResult<CreateVaultAssetVo> activateAsset(@RequestBody VaultAccountAndAssetReq req);

    @PostMapping("/resendWebhooks")
    RetResult<ResendWebhooksVo> resendWebhooks(@RequestBody ResendWebhooksReq req);
}