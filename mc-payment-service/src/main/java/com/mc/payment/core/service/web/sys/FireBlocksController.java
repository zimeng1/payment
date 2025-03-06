package com.mc.payment.core.service.web.sys;

import com.fireblocks.sdk.ConfigurationOptions;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.fireblocksapi.FireBlocksAPI;
import com.mc.payment.fireblocksapi.model.req.fireBlocks.VaultAccountAndAssetReq;
import com.mc.payment.fireblocksapi.model.vo.fireBlocks.VaultAssetVo;
import com.mc.payment.fireblocksapi.util.FireBlocksUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Marty
 * @since 2024/6/19 14:43
 */
@Tag(name = "资产最新报价管理")
@RestController
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/fireBlocks")
public class FireBlocksController {

    private final FireBlocksAPI fireBlocksApi;
    private final FireBlocksUtil fireBlocksUtil;

    public FireBlocksController(FireBlocksAPI fireBlocksApi, FireBlocksUtil fireBlocksUtil) {
        this.fireBlocksApi = fireBlocksApi;
        this.fireBlocksUtil = fireBlocksUtil;
    }

    @GetMapping("/getHealth")
    public RetResult<String> getHealth() {
        ConfigurationOptions config = fireBlocksUtil.getConfigurationOptions();
        //检查服务是否正常启动, 配置是否正常. 查下url, apiKey前5个字符, secretKey后35个字符. 防止意外
        return RetResult.data("这是个检测服务, baseUrl: " + config.getBasePath() + ", apiKey前5个字符: " + config.getApiKey().substring(0, 5) + ", secretKey后35个字符: " + config.getSecretKey().substring(config.getSecretKey().length() - 35));
    }


    /**
     * 刷新fireblocks账户资产余额的接口, 特别说明, 该接口在fireblocks文档上备注了有频率限制, 且说明了只能在特殊场景才使用.
     * Refresh asset balance data
     * This API endpoint is subject to a strict rate limit.
     * Should be used by clients in very specific scenarios.
     *
     * @param req
     * @return
     */
    @PostMapping("/updateVaultAccountAssetBalance")
    public RetResult<VaultAssetVo> updateVaultAccountAssetBalance(@RequestBody @Validated VaultAccountAndAssetReq req) {
        return fireBlocksApi.updateVaultAccountAssetBalance(req);
    }

/*

    @PostMapping("/createAccount")
    public RetResult<VaultAccountVo> createAccount(@RequestBody @Validated CreateAccountReq req) {
        return fireBlocksApi.createAccount(req);
    }

    @PostMapping("/createWallet")
    public RetResult<CreateVaultAssetVo> createWallet(@RequestBody @Validated CreateWalletReq req) {
        return fireBlocksApi.createWallet(req);
    }

    @PostMapping("/queryAccount")
    public RetResult<VaultAccountsPagedVo> queryAccount(@RequestBody @Validated QueryAccountReq req) {
        return fireBlocksApi.queryAccount(req);
    }

    @PostMapping("/queryAssetAddresses")
    public RetResult<PaginatedAddressVo> queryAssetAddresses(@RequestBody @Validated QueryAssetAddressesReq req) {
        return fireBlocksApi.queryAssetAddresses(req);
    }

    @PostMapping("/queryVaultAccountAsset")
    public RetResult<VaultAssetVo> queryVaultAccountAsset(@RequestBody @Validated QueryVaultAccountAssetReq req) {
        return fireBlocksApi.queryVaultAccountAsset(req);
    }

    @PostMapping("/queryTransactions")
    public RetResult<List<TransactionVo>> queryTransactions(@RequestBody @Validated QueryTransactionsReq req) {
        return fireBlocksApi.queryTransactions(req);
    }

    @PostMapping("/queryTransactionsById")
    public RetResult<TransactionVo> queryTransactionsById(@RequestBody @Validated QueryTransactionsByIdReq req) {
        return fireBlocksApi.queryTransactionsById(req);
    }

    @PostMapping("/createTransactions")
    public RetResult<CreateTransactionVo> createTransactions(@RequestBody @Validated CreateTransactionReq req) {
        return fireBlocksApi.createTransactions(req);
    }

    @PostMapping("/estimateFee")
    public RetResult<EstimatedTransactionFeeVo> estimateFee(@RequestBody @Validated CreateTransactionReq req) {
        return fireBlocksApi.estimateFee(req);
    }

    @PostMapping("/querySupportedAssets")
    public RetResult<List<AssetTypeVo>> querySupportedAssets(@RequestBody @Validated CreateTransactionReq req) {
        return fireBlocksApi.querySupportedAssets();
    }
    @PostMapping("/estimateNetworkFee")
    public RetResult<EstimatedNetworkFeeVo> estimateNetworkFee(@RequestBody @Validated QueryEstimateNetworkFeeReq req) {
        return fireBlocksApi.estimateNetworkFee(req);
    }

    @PostMapping("/registerNewAsset")
    public RetResult<RegisterNewAssetVo> registerNewAsset(@RequestBody @Validated RegisterNewAssetReq req) {
        return fireBlocksApi.registerNewAsset(req);
    }

    @PostMapping("/activateAsset")
    public RetResult<CreateVaultAssetVo> activateAsset(@RequestBody @Validated VaultAccountAndAssetReq req) {
        return fireBlocksApi.activateAsset(req);
    }

    @PostMapping("/resendWebhooks")
    public RetResult<ResendWebhooksVo> resendWebhooks(@RequestBody @Validated ResendWebhooksReq req) {
        return fireBlocksApi.resendWebhooks(req);
    }

*/


}
