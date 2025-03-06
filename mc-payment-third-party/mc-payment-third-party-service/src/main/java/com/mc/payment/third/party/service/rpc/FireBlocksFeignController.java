package com.mc.payment.third.party.service.rpc;

import cn.hutool.core.bean.BeanUtil;
import com.fireblocks.sdk.ApiResponse;
import com.fireblocks.sdk.ConfigurationOptions;
import com.fireblocks.sdk.Fireblocks;
import com.fireblocks.sdk.model.*;
import com.google.gson.Gson;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.third.party.api.IFireBlocksFeignClient;
import com.mc.payment.third.party.api.model.constant.FireBlocksConstant;
import com.mc.payment.third.party.api.model.req.fireBlocks.*;
import com.mc.payment.third.party.api.model.vo.fireBlocks.*;
import com.mc.payment.third.party.service.util.CommonUtil;
import com.mc.payment.third.party.service.util.FireBlocksUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Marty
 * @since 2024/04/13 17:16
 */
@Slf4j
@RestController
@RequestMapping("/fireBlocks")
public class FireBlocksFeignController implements IFireBlocksFeignClient {

    @Resource
    private FireBlocksUtil fireBlocksUtil;

    @GetMapping("/getTest")
    public RetResult<String> getTest() {
        ConfigurationOptions config = fireBlocksUtil.getConfigurationOptions();
        //检查服务是否正常启动, 配置是否正常. 查下url, apiKey前5个字符, secretKey后35个字符. 防止意外
        return RetResult.data("这是个检测服务, baseUrl: " + config.getBasePath() + ", apiKey前5个字符: " + config.getApiKey().substring(0, 5) + ", secretKey后35个字符: " + config.getSecretKey().substring(config.getSecretKey().length() - 35));
    }

    @Override
    @PostMapping("/createAccount")
    public RetResult<VaultAccountVo> createAccount(@Valid @RequestBody CreateAccountReq req) {
        log.info("[createAccount] req :{}", req);
        try {
            Fireblocks fireblocks = fireBlocksUtil.getFireBlocks();
            CreateVaultAccountRequest createRequest = new CreateVaultAccountRequest();
            createRequest.setAutoFuel(req.getAutoFuel());
            createRequest.setName(req.getName());
            createRequest.setCustomerRefId(req.getCustomerRefId());
            createRequest.setHiddenOnUI(req.getHiddenOnUI());
            CompletableFuture<ApiResponse<VaultAccount>> result = fireblocks.vaults().createVaultAccount(createRequest, req.getName() + req.getCustomerRefId());
            try {
                // 同步获取异步操作的结果，如果发生异常，则捕获异常
                ApiResponse<VaultAccount> response = result.get();
                VaultAccountVo vo = new VaultAccountVo();
                // 之后考虑用原生的get,set方法，或者MapStruct映射处理
                BeanUtil.copyProperties(response.getData(), vo);
                return RetResult.data(vo);
            } catch (Exception e) {
                log.error("[createAccount] API调度异常: req={}, errorMsg={}", req, e.getMessage());
                return RetResult.error(e.getMessage());
            }
        } catch (Exception e) {
            log.error("[createAccount] has bean error, req:{}", req, e);
            return RetResult.error();
        }
    }

    @Override
    @PostMapping("/createWallet")
    public RetResult<CreateVaultAssetVo> createWallet(@Valid @RequestBody CreateWalletReq req) {
        log.info("[createWallet] req :{}", req);
        try {
            Fireblocks fireblocks = fireBlocksUtil.getFireBlocks();
            String vaultAccountId = req.getVaultAccountId();
            String assetId = req.getAssetId();

            //如果assetId有包含$符号, 就走特殊处理
            if (StringUtils.isNotBlank(assetId) && assetId.contains("$")) {
                try {
                    String result = fireBlocksUtil.doFireBlocksApiByUrl(String.format("/vault/accounts/%s/%s", vaultAccountId, assetId), "POST");
                    if (StringUtils.isNotBlank(result) && !(result.contains("message") && result.contains("code"))) {
                        //将response.body().string()转出CreateVaultAssetVo对象接收
                        Gson gson = new Gson();
                        CreateVaultAssetVo createVaultAssetVo = gson.fromJson(result, CreateVaultAssetVo.class);
                        return RetResult.data(createVaultAssetVo);
                    } else {
                        log.error("[createWallet] API调度失败: req={}, errorMsg={}", req, result);
                        return RetResult.error(result);
                    }
                } catch (Exception e) {
                    log.error("[createWallet] API调度异常: req={}, errorMsg={}", req, e.getMessage());
                    return RetResult.error(e.getMessage());
                }
            }

            CreateAssetsRequest createAssetsRequest = new CreateAssetsRequest();
            String idempotencyKey = vaultAccountId + "_" + assetId + System.currentTimeMillis();;

            CompletableFuture<ApiResponse<CreateVaultAssetResponse>> result = fireblocks.vaults().createVaultAccountAsset(vaultAccountId, assetId, createAssetsRequest, idempotencyKey);
            try {
                ApiResponse<CreateVaultAssetResponse> response = result.get();
                CreateVaultAssetVo vo = new CreateVaultAssetVo();
                BeanUtil.copyProperties(response.getData(), vo);
                return RetResult.data(vo);
            } catch (Exception e) {
                log.error("[createWallet] API调度异常: req={}, errorMsg={}", req, e.getMessage());
                return RetResult.error(e.getMessage());
            }
        } catch (Exception e) {
            log.error("[createWallet] has bean error, req:{}", req, e);
            return RetResult.error();
        }
    }

    @Override
//    @GetMapping("/accounts")
    @PostMapping("/queryAccount")
    public RetResult<VaultAccountsPagedVo> queryAccount(@Valid @RequestBody QueryAccountReq req) {
//        log.info("[queryAccount] req :{}", req);
        try {
            Fireblocks fireblocks = fireBlocksUtil.getFireBlocks();
            // 该接口没人用, 不做特殊处理
            CompletableFuture<ApiResponse<VaultAccountsPagedResponse>> result =
                    fireblocks.vaults().getPagedVaultAccounts(req.getNamePrefix(), req.getNameSuffix(),
                            req.getMinAmountThreshold(), req.getAssetId(), req.getOrderBy(), req.getBefore(), req.getAfter(), req.getLimit());
            try {
                ApiResponse<VaultAccountsPagedResponse> response = result.get();
                VaultAccountsPagedVo vo = new VaultAccountsPagedVo();
                BeanUtil.copyProperties(response.getData(), vo);
                return RetResult.data(vo);
            } catch (Exception e) {
                log.error("[queryAccount] API调度异常: req={}, errorMsg={}", req, e.getMessage());
                return RetResult.error(e.getMessage());
            }
        } catch (Exception e) {
            log.error("[queryAccount] has bean error, req:{}", req, e);
            return RetResult.error();
        }
    }

    @Override
//    @GetMapping("/accounts/{vaultAccountId}/{assetId}/addresses_paginated")
    @PostMapping("/accounts/addresses_paginated")
    public RetResult<PaginatedAddressVo> queryAssetAddresses(@Valid @RequestBody QueryAssetAddressesReq req) {
//        log.info("[createAccount] vaultAccountId :{}, assetId:{}, req :{}", vaultAccountId, assetId, req);
        String vaultAccountId = req.getVaultAccountId();
        String assetId = req.getAssetId();
        try {
            Fireblocks fireblocks = fireBlocksUtil.getFireBlocks();
            //如果assetId有包含$符号, 就走特殊处理
            if (StringUtils.isNotBlank(assetId) && assetId.contains("$")) {
                try {
                    // /vault/accounts/{vaultAccountId}/{assetId}/addresses_paginated
                    String result = fireBlocksUtil.doFireBlocksApiByUrl(String.format("/vault/accounts/%s/%s/addresses_paginated", vaultAccountId, assetId), "GET");
                    if (StringUtils.isNotBlank(result) && !(result.contains("message") && result.contains("code"))) {
                        Gson gson = new Gson();
                        PaginatedAddressVo vo = gson.fromJson(result, PaginatedAddressVo.class);
                        return RetResult.data(vo);
                    } else {
                        log.error("[queryAssetAddresses] API调度失败: req={}, errorMsg={}", req, result);
                        return RetResult.error(result);
                    }
                } catch (Exception e) {
                    log.error("[queryAssetAddresses] API调度异常: req={}, errorMsg={}", req, e.getMessage());
                    return RetResult.error(e.getMessage());
                }
            }

            CompletableFuture<ApiResponse<PaginatedAddressResponse>> result =
                    fireblocks.vaults().getVaultAccountAssetAddressesPaginated(
                            vaultAccountId, assetId, req.getLimit(), req.getBefore(), req.getAfter());
            try {
                // 这里有枚举.addressFormat, 不过因为返回空,所有可以不用管.
                ApiResponse<PaginatedAddressResponse> response = result.get();
                PaginatedAddressVo vo = new PaginatedAddressVo();
                BeanUtil.copyProperties(response.getData(), vo);
                return RetResult.data(vo);
            } catch (Exception e) {
                log.error("[queryAssetAddresses] API调度异常: vaultAccountId={}, assetId={}, req={}, errorMsg={}", vaultAccountId, assetId, req, e.getMessage());
                return RetResult.error(e.getMessage());
            }
        } catch (Exception e) {
            log.error("[queryAssetAddresses] has bean error, req:{}", req, e);
            return RetResult.error();
        }
    }

    @Override
    @PostMapping("/queryVaultAccountAsset")
    public RetResult<VaultAssetVo> queryVaultAccountAsset(@Valid @RequestBody QueryVaultAccountAssetReq req ) {
//    public RetResult<VaultAssetVo> queryVaultAccountAsset(@PathVariable("vaultAccountId") String vaultAccountId, @PathVariable("assetId") String assetId)
        String vaultAccountId = req.getVaultAccountId();
        String assetId = req.getAssetId();
        try {
            Fireblocks fireblocks = fireBlocksUtil.getFireBlocks();
            //如果assetId有包含$符号, 就走特殊处理
            if (StringUtils.isNotBlank(assetId) && assetId.contains("$")) {
                try {
                    // /vault/accounts/{vaultAccountId}/{assetId}
                    String result = fireBlocksUtil.doFireBlocksApiByUrl(String.format("/vault/accounts/%s/%s", vaultAccountId, assetId), "GET");
                    if (StringUtils.isNotBlank(result) && !(result.contains("message") && result.contains("code"))) {
                        Gson gson = new Gson();
                        VaultAssetVo vo = gson.fromJson(result, VaultAssetVo.class);
                        return RetResult.data(vo);
                    } else {
                        log.error("[queryVaultAccountAsset] API调度失败: req={}, errorMsg={}", req, result);
                        return RetResult.error(result);
                    }
                } catch (Exception e) {
                    log.error("[queryVaultAccountAsset] API调度异常: req={}, errorMsg={}", req, e.getMessage());
                    return RetResult.error(e.getMessage());
                }
            }

            CompletableFuture<ApiResponse<VaultAsset>> result =
                    fireblocks.vaults().getVaultAccountAsset(vaultAccountId, assetId);
            try {
                ApiResponse<VaultAsset> response = result.get();
                VaultAssetVo vo = new VaultAssetVo();
                BeanUtil.copyProperties(response.getData(), vo);
                return RetResult.data(vo);
            } catch (Exception e) {
                log.error("[queryVaultAccountAsset] API调度异常: vaultAccountId={}, assetId={}, errorMsg={}", vaultAccountId, assetId, e.getMessage());
                return RetResult.error(e.getMessage());
            }
        } catch (Exception e) {
            log.error("[queryVaultAccountAsset] has bean error, vaultAccountId:{}, assetId:{}", vaultAccountId, assetId, e);
            return RetResult.error();
        }
    }

    @Override
//    @GetMapping("/transactions")
    @PostMapping("/queryTransactions")
    public RetResult<List<TransactionVo>> queryTransactions(@Valid @RequestBody QueryTransactionsReq req) {
//        log.info("[queryTransactions] req :{}", req);
        try {
            Fireblocks fireblocks = fireBlocksUtil.getFireBlocks();
            // 该接口没其他微服务调用, 不做特殊处理
            CompletableFuture<ApiResponse<List<TransactionResponse>>> result = fireblocks.transactions().getTransactions(
                    req.getBefore(),
                    req.getAfter(),
                    req.getStatus(),
                    req.getOrderBy(),
                    req.getSort(),
                    req.getLimit(),
                    req.getSourceType(),
                    req.getSourceId(),
                    req.getDestType(),
                    req.getDestId(),
                    req.getAssets(),
                    req.getTxHash(),
                    req.getSourceWalletId(),
                    req.getDestWalletId());
            try {
                ApiResponse<List<TransactionResponse>> response = result.get();
                List<TransactionResponse> data = response.getData();
                List<TransactionVo> list = new ArrayList<>();
                if (data == null || data.size() == 0) {
                    return RetResult.data(new ArrayList<>());
                }
                for (TransactionResponse item : data) {
                    TransactionVo temp = new TransactionVo();
                    BeanUtil.copyProperties(item, temp);
                    if(item.getAddressType() != null){
                        temp.setAddressType(item.getAddressType().getValue());
                    }
                    list.add(temp);
                }
                return RetResult.data(list);
            } catch (Exception e) {
                log.error("[queryTransactions] API调度异常: req={}, errorMsg={}", req, e.getMessage());
                return RetResult.error(e.getMessage());
            }
        } catch (Exception e) {
            log.error("[queryTransactions] has bean error, req:{}", req, e);
            return RetResult.error();
        }
    }

    @Override
//    @GetMapping("/transactions/{txId}")
    @PostMapping("/queryTransactionsById")
    public RetResult<TransactionVo> queryTransactionsById(@Valid @RequestBody QueryTransactionsByIdReq req) {
//        log.info("[queryTransactionsById] txId :{}", txId);
        String txId = req.getTxId();
        try {
            Fireblocks fireblocks = fireBlocksUtil.getFireBlocks();
            CompletableFuture<ApiResponse<TransactionResponse>> result = fireblocks.transactions().getTransaction(txId);
            try {
                ApiResponse<TransactionResponse> response = result.get();
                TransactionVo vo = new TransactionVo();
                TransactionResponse data = response.getData();
                BeanUtil.copyProperties(data, vo);
                if(data.getAddressType() != null){
                    vo.setAddressType(data.getAddressType().getValue());
                }
                return RetResult.data(vo);
            } catch (Exception e) {
                log.error("[queryTransactionsById] API调度异常: txId={}, errorMsg={}", txId, e.getMessage());
                return RetResult.error(e.getMessage());
            }
        } catch (Exception e) {
            log.error("[queryTransactionsById] has bean error, txId:{}", txId, e);
            return RetResult.error();
        }
    }

    @Override
    @PostMapping("/createTransactions")
    public RetResult<CreateTransactionVo> createTransactions(@Valid @RequestBody CreateTransactionReq req) {
        log.info("[createTransactions] req :{}", req);
        try {
            if(!checkSign(req)){
                log.error("[createTransactions] 非法调度接口, sign不正确,  req:{}", req);
                return RetResult.error("非法调度!");
            }
            Fireblocks fireblocks = fireBlocksUtil.getFireBlocks();
            TransactionRequest request = buildTransactionRequest(req);
            CompletableFuture<ApiResponse<CreateTransactionResponse>> result = fireblocks.transactions().createTransaction(
                    request, CommonUtil.getUuidFromString(req.getXEndUserWalletId()), req.getIdempotencyKey());
            try {
                ApiResponse<CreateTransactionResponse> response = result.get();
                CreateTransactionVo vo = new CreateTransactionVo();
                BeanUtil.copyProperties(response.getData(), vo);
                return RetResult.data(vo);
            } catch (Exception e) {
                log.error("[queryTransactions] API调度异常: req={}, errorMsg={}", req, e.getMessage());
                return RetResult.error(e.getMessage());
            }
        } catch (Exception e) {
            log.error("[createTransactions] has bean error, req:{}", req, e);
            return RetResult.error();
        }
    }

    /**
     * 临时防止其他接口调度, 之后版本将改成签名文件或者其他形式验证.
     * @param req
     * @return
     */
    private static boolean checkSign(CreateTransactionReq req) {
        return  "&@!MC_PAYMENT&&TRAN!425$".equals(req.getSign());
    }

    private static TransactionRequest buildTransactionRequest(CreateTransactionReq req) {
        TransactionRequest request = new TransactionRequest();
        request.setOperation(CommonUtil.getOperationFromString(req.getOperation()));
        request.setNote(req.getNote());
        request.setExternalTxId(req.getExternalTxId());
        request.setAssetId(req.getAssetId());

        TransactionPeerPathReq reqSource = req.getSource();
        SourceTransferPeerPath source = new SourceTransferPeerPath();
        source.setType(CommonUtil.getPeerPathTypFromString(reqSource.getType()));

        source.setSubType(CommonUtil.getPeerPathSubTypeFromString(reqSource.getSubType()));
        source.setId(reqSource.getId());
        source.setName(reqSource.getName());
        source.setWalletId(CommonUtil.getUuidFromString(reqSource.getWalletId()));
        request.setSource(source);

        //只有有交易的地址才能设置目标地址，不然就是多个目标地址
        TransactionDestinationPeerPathReq reqDestination = req.getDestination();
        DestinationTransferPeerPath destination = new DestinationTransferPeerPath();
        destination.setType(TransferPeerPathType.ONE_TIME_ADDRESS);
        destination.setSubType(CommonUtil.getPeerPathSubTypeFromString(reqDestination.getSubType()));
        destination.setId(reqDestination.getId());
        destination.setName(reqDestination.getName());
        destination.setWalletId(CommonUtil.getUuidFromString(reqDestination.getWalletId()));
        destination.setOneTimeAddress(CommonUtil.getOneTimeAddress(reqDestination.getOneTimeAddress()));
        request.setDestination(destination);
        request.setDestinations(null);
// }

        /*
            //批量接口需要UTXO的币种，暂时不在这里做批量操作
            List<TransactionRequestDestination> list = new ArrayList<>();
            List<TransactionDestinationReq> reqList = req.getDestinations();
            for (TransactionDestinationReq item : reqList) {
                TransactionRequestDestination desReq = new TransactionRequestDestination();
                DestinationTransferPeerPath desPath = new DestinationTransferPeerPath();
                OneTimeAddressReq address = item.getOneTimeAddress();
                desReq.setAmount(item.getAmount());
                desPath.setType(CommonUtil.getPeerPathTypFromString(item.getType()));
                desPath.setSubType(CommonUtil.getPeerPathSubTypeFromString(item.getSubType()));
                desPath.setId(item.getId());
                desPath.setName(item.getName());
                desPath.setWalletId(CommonUtil.getUuidFromString(item.getWalletId()));
                desPath.setOneTimeAddress(CommonUtil.getOneTimeAddress(address));
                desReq.setDestination(desPath);
                list.add(desReq);
            }
            request.setDestinations(list);
            request.setDestination(null);*/


        TransactionRequestAmount amount = new TransactionRequestAmount();
        amount.setActualInstance(req.getAmount());
        request.setAmount(amount);

        request.setTreatAsGrossAmount(req.getTreatAsGrossAmount());
        request.setForceSweep(req.getForceSweep());

        //调用fireblocks的交易发送时，feeLevel参数填写Medium，用来做交易发送或者gas评估
        request.feeLevel(TransactionRequest.FeeLevelEnum.MEDIUM);
        // ps: 与feeLevel互斥， 要么设置feeLevel， 要么设置fee/gasPrice/gasLimit 20240417

        return request;

    }

    @Override
    @PostMapping("/transactions/estimate_fee")
    public RetResult<EstimatedTransactionFeeVo> estimateFee(@Valid @RequestBody CreateTransactionReq req) {
//        log.info("[estimateFee] vaultAccountId :{}", req);
        try {
            Fireblocks fireblocks = fireBlocksUtil.getFireBlocks();
            TransactionRequest request = buildTransactionRequest(req);
//            TransactionRequest transactionRequest, String idempotencyKey
            CompletableFuture<ApiResponse<EstimatedTransactionFeeResponse>> result = fireblocks.transactions().estimateTransactionFee(request, req.getIdempotencyKey());
            try {
                ApiResponse<EstimatedTransactionFeeResponse> response = result.get();
                EstimatedTransactionFeeVo vo = new EstimatedTransactionFeeVo();
                BeanUtil.copyProperties(response.getData(), vo);
                return RetResult.data(vo);
            } catch (Exception e) {
                log.error("[estimateFee] API调度异常: req={}, errorMsg={}", req, e.getMessage());
                return RetResult.error(e.getMessage());
            }
        } catch (Exception e) {
            log.error("[estimateFee] has bean error, req:{}", req, e);
            return RetResult.error();
        }
    }

    @Override
//    @GetMapping("/supported_assets")
    @PostMapping("/querySupportedAssets")
    public RetResult<List<AssetTypeVo>> querySupportedAssets() {
        try {
            Fireblocks fireblocks = fireBlocksUtil.getFireBlocks();
            CompletableFuture<ApiResponse<List<AssetTypeResponse>>> result = fireblocks.blockchainsAssets().getSupportedAssets();
            try {
                ApiResponse<List<AssetTypeResponse>> response = result.get();
                List<AssetTypeVo> assetTypeVos = BeanUtil.copyToList(response.getData(), AssetTypeVo.class);
                return RetResult.data(assetTypeVos);
            } catch (Exception e) {
                log.error("[querySupportedAssets] API调度异常:", e);
                return RetResult.error(e.getMessage());
            }
        } catch (Exception e) {
            log.error("[querySupportedAssets] has bean error.", e);
            return RetResult.error();
        }
    }

    @Override
    @PostMapping("/estimate_network_fee")
    public RetResult<EstimatedNetworkFeeVo> estimateNetworkFee(@Valid @RequestBody QueryEstimateNetworkFeeReq req) {
        String assetId = req.getAssetId();
        try {
            if (StringUtils.isBlank(assetId)) {
                return RetResult.error("[assetId] is null");
            }
            //如果assetId有包含$符号, 就走特殊处理
            if (StringUtils.isNotBlank(assetId) && assetId.contains("$")) {
                try {
                    //https://api.fireblocks.io/v1/estimate_network_fee
                    String result = fireBlocksUtil.doFireBlocksApiByUrl(String.format("/estimate_network_fee?assetId=%s", assetId), "GET");
                    if (StringUtils.isNotBlank(result) && !(result.contains("message") && result.contains("code"))) {
                        Gson gson = new Gson();
                        EstimatedNetworkFeeVo vo = gson.fromJson(result, EstimatedNetworkFeeVo.class);
                        return RetResult.data(vo);
                    } else {
                        log.error("[estimateNetworkFee] API调度失败: req={}, errorMsg={}", req, result);
                        return RetResult.error(result);
                    }
                } catch (Exception e) {
                    log.error("[estimateNetworkFee] API调度异常: req={}, errorMsg={}", req, e.getMessage());
                    return RetResult.error(e.getMessage());
                }
            }
            Fireblocks fireblocks = fireBlocksUtil.getFireBlocks();
            CompletableFuture<ApiResponse<EstimatedNetworkFeeResponse>> result = fireblocks.transactions().estimateNetworkFee(assetId);
            try {
                ApiResponse<EstimatedNetworkFeeResponse> response = result.get();
                EstimatedNetworkFeeVo vo = new EstimatedNetworkFeeVo();
                BeanUtil.copyProperties(response.getData(), vo);
                return RetResult.data(vo);
            } catch (Exception e) {
                log.error("[estimateNetworkFee] API调度异常, assetId={}", assetId, e);
                return RetResult.error(e.getMessage());
            }
        } catch (Exception e) {
            log.error("[estimateNetworkFee]has bean error.assetId={}", assetId, e);
            return RetResult.error();
        }
    }

    @Override
    @PostMapping("/registerNewAsset")
    public RetResult<RegisterNewAssetVo> registerNewAsset(@Valid @RequestBody RegisterNewAssetReq req) {
        try {
            log.info("[registerNewAsset] req :{}", req);
            Fireblocks fireblocks = fireBlocksUtil.getFireBlocks();
            RegisterNewAssetRequest registerNewAssetRequest = new RegisterNewAssetRequest();
            registerNewAssetRequest.setBlockchainId(req.getBlockchainId());
            registerNewAssetRequest.setAddress(req.getAddress());
            registerNewAssetRequest.setSymbol(req.getSymbol());
            CompletableFuture<ApiResponse<AssetResponse>> result = fireblocks.blockchainsAssets().registerNewAsset(registerNewAssetRequest, req.getIdempotencyKey());
            try {
                ApiResponse<AssetResponse> response = result.get();
                RegisterNewAssetVo vo = new RegisterNewAssetVo();
                BeanUtil.copyProperties(response.getData(), vo);
                return RetResult.data(vo);
            } catch (Exception e) {
                log.error("[registerNewAsset] API调度异常:", e);
                return RetResult.error(e.getMessage());
            }
        } catch (Exception e) {
            log.error("[registerNewAsset] has bean error.", e);
            return RetResult.error();
        }
    }



    /**
     * 激活资产,
     * Initiates activation for a wallet in a vault account. Activation is required for tokens that need an on-chain transaction for creation (XLM tokens, SOL tokens etc).
     * @param req
     * @return
     */
    @Override
    @PostMapping("/activateAsset")
    public RetResult<CreateVaultAssetVo> activateAsset(@Valid @RequestBody VaultAccountAndAssetReq req ) {
        log.info("[activateAsset] req :{}", req);
        String vaultAccountId = req.getVaultAccountId();
        String assetId = req.getAssetId();
        try {
            Fireblocks fireblocks = fireBlocksUtil.getFireBlocks();
            //如果assetId有包含$符号, 就走特殊处理
            if (StringUtils.isNotBlank(assetId) && assetId.contains("$")) {
                try {
                    // /vault/accounts/{vaultAccountId}/{assetId}/activate
                    String result = fireBlocksUtil.doFireBlocksApiByUrl(String.format("/vault/accounts/%s/%s/activate", vaultAccountId, assetId), "POST");
                    if (StringUtils.isNotBlank(result) && !(result.contains("message") && result.contains("code"))) {
                        Gson gson = new Gson();
                        CreateVaultAssetVo vo = gson.fromJson(result, CreateVaultAssetVo.class);
                        return RetResult.data(vo);
                    } else {
                        log.error("[activateAsset] API调度失败: req={}, errorMsg={}", req, result);
                        return RetResult.error(result);
                    }
                } catch (Exception e) {
                    log.error("[activateAsset] API调度异常: req={}, errorMsg={}", req, e.getMessage());
                    return RetResult.error(e.getMessage());
                }
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
            String idempotencyKey = vaultAccountId + "_" + assetId +LocalDate.now().format(formatter);
            CompletableFuture<ApiResponse<CreateVaultAssetResponse>> result = fireblocks.vaults().activateAssetForVaultAccount(vaultAccountId, assetId, idempotencyKey);
            try {
                ApiResponse<CreateVaultAssetResponse> response = result.get();
                CreateVaultAssetVo vo = new CreateVaultAssetVo();
                BeanUtil.copyProperties(response.getData(), vo);
                return RetResult.data(vo);
            } catch (Exception e) {
                log.error("[activateAsset] API调度异常: vaultAccountId={}, assetId={}, errorMsg={}", vaultAccountId, assetId, e.getMessage());
                return RetResult.error(e.getMessage());
            }
        } catch (Exception e) {
            log.error("[activateAsset] has bean error, vaultAccountId:{}, assetId:{}", vaultAccountId, assetId, e);
            return RetResult.error();
        }
    }

    /**
     * Resend failed webhooks for a transaction by ID
     * @param req
     * @return
     */
    @Override
    @PostMapping("/resendWebhooks")
    public RetResult<ResendWebhooksVo> resendWebhooks(@Valid @RequestBody ResendWebhooksReq req) {
        String txId = req.getTxId();
        log.info("[resendWebhooks] txId :{}", txId);
        try {
            Fireblocks fireblocks = fireBlocksUtil.getFireBlocks();
            CompletableFuture<ApiResponse<ResendWebhooksResponse>> result = fireblocks.webhooks().resendWebhooks(txId);
            try {
                ApiResponse<ResendWebhooksResponse> response = result.get();
                ResendWebhooksVo vo = new ResendWebhooksVo();
                BeanUtil.copyProperties(response.getData(), vo);
                return RetResult.data(vo);
            } catch (Exception e) {
                log.error("[resendWebhooks] API调度异常: txId={}, role={}, errorMsg={}", txId, FireBlocksConstant.EDITOR_ROLE, e.getMessage());
                return RetResult.error(e.getMessage());
            }
        } catch (Exception e) {
            log.error("[resendWebhooks] has bean error, txId:{}", txId, e);
            return RetResult.error();
        }
    }
}
