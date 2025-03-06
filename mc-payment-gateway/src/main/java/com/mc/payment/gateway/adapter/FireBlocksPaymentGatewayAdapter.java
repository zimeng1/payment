package com.mc.payment.gateway.adapter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.fireblocks.sdk.ApiResponse;
import com.fireblocks.sdk.model.*;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.gateway.PaymentGateway;
import com.mc.payment.gateway.channels.fireblocks.service.FireBlocksService;
import com.mc.payment.gateway.model.req.*;
import com.mc.payment.gateway.model.rsp.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class FireBlocksPaymentGatewayAdapter implements PaymentGateway {

    private final FireBlocksService service;

    @Override
    public RetResult<GatewayDepositRsp> deposit(GatewayDepositReq req) {
        return RetResult.error("not support");
    }

    @Override
    public RetResult<GatewayQueryDepositRsp> queryDeposit(GatewayQueryDepositReq req) {
        // fireblocks 这里直接调用查询出金的接口 底层接口是一样的
        GatewayQueryWithdrawalReq gatewayQueryWithdrawalReq = new GatewayQueryWithdrawalReq();
        gatewayQueryWithdrawalReq.setTransactionId(req.getTransactionId());
        RetResult<GatewayQueryWithdrawalRsp> retResult = this.queryWithdrawal(gatewayQueryWithdrawalReq);
        if (retResult.isSuccess()) {
            GatewayQueryWithdrawalRsp data = retResult.getData();
            GatewayQueryDepositRsp rsp = new GatewayQueryDepositRsp();
            rsp.setStatus(data.getStatus());
            rsp.setCompleteTime(data.getCompleteTime());
            return RetResult.data(rsp);
        } else {
            return RetResult.error(retResult.getMsg());
        }
    }

    @Override
    public RetResult<GatewayWithdrawalRsp> withdrawal(@Valid GatewayWithdrawalReq req) {
        log.info("fireblocks withdrawal req:{}", req);
        GatewayWithdrawalRsp rsp = new GatewayWithdrawalRsp();
        rsp.setTransactionId(req.getTransactionId());
        // 参数校验
        Map<String, Object> extraMap = req.getExtraMap();
        if (CollUtil.isEmpty(extraMap)) {
            return RetResult.error("extraMap cannot be empty");
        }
        String vaultAccountId = (String) extraMap.getOrDefault("vaultAccountId", "");
        if (StrUtil.isBlank(vaultAccountId)) {
            return RetResult.error("extraMap.vaultAccountId cannot be empty");
        }
        String vaultAccountName = (String) extraMap.getOrDefault("vaultAccountName", "");
        if (StrUtil.isBlank(vaultAccountName)) {
            return RetResult.error("extraMap.vaultAccountName cannot be empty");
        }
        String idempotencyKey = (String) extraMap.getOrDefault("idempotencyKey", "");
        if (StrUtil.isBlank(idempotencyKey)) {
            return RetResult.error("extraMap.idempotencyKey cannot be empty");
        }

        // 参数转换
        TransactionRequest request = new TransactionRequest();
        request.setExternalTxId(req.getTransactionId());
        TransactionRequestAmount amount = new TransactionRequestAmount();
        amount.setActualInstance(req.getAmount());
        request.setAmount(amount);
        request.setAssetId(req.getChannelId());
        request.setTreatAsGrossAmount(false);
        //调用fireblocks的交易发送时，feeLevel参数填写Medium，用来做交易发送或者gas评估
        request.feeLevel(TransactionRequest.FeeLevelEnum.MEDIUM);
        // ps: 与feeLevel互斥， 要么设置feeLevel， 要么设置fee/gasPrice/gasLimit 20240417
        SourceTransferPeerPath source = new SourceTransferPeerPath();
        source.setType(TransferPeerPathType.VAULT_ACCOUNT);
        source.setId(vaultAccountId);
        source.setName(vaultAccountName);
        request.setSource(source);
        //只有有交易的地址才能设置目标地址，不然就是多个目标地址
        DestinationTransferPeerPath destination = new DestinationTransferPeerPath();
        destination.setType(TransferPeerPathType.ONE_TIME_ADDRESS);
        OneTimeAddress oneTimeAddress = new OneTimeAddress();
        oneTimeAddress.setAddress(req.getAddress());
        destination.setOneTimeAddress(oneTimeAddress);
        request.setDestination(destination);

        ApiResponse<CreateTransactionResponse> response = null;
        try {
            log.info("fireblocks withdrawal request:{}", request);
            response = service.createTransactions(request, idempotencyKey);
            log.info("fireblocks withdrawal response:{}", response);
            if (response.getStatusCode() != 200) {
                return RetResult.error("request error");
            }
            CreateTransactionResponse data = response.getData();
            rsp.setChannelTransactionId(data.getId());
        } catch (Exception e) {
            log.error("fireblocks withdrawal error", e);
            return RetResult.error(e.getMessage());
        }
        return RetResult.data(rsp);
    }

    @Override
    public RetResult<GatewayQueryWithdrawalRsp> queryWithdrawal(GatewayQueryWithdrawalReq req) {
        GatewayQueryWithdrawalRsp rsp = new GatewayQueryWithdrawalRsp();
        try {
            log.info("fireblocks queryWithdrawal request:{}", req);
            ApiResponse<TransactionResponse> response = service.queryTransaction(req.getTransactionId());
            log.info("fireblocks queryWithdrawal response:{}", response);
            if (response.getStatusCode() != 200) {
                if (response.getStatusCode() == 404) {
                    return RetResult.error("Transaction not found");
                }
                return RetResult.error("query error");
            }
            TransactionResponse data = response.getData();
            if ("COMPLETED".equals(data.getStatus())) {
                rsp.setStatus(1);
            } else if ("BLOCKED".equals(data.getStatus()) || "CANCELLED".equals(data.getStatus()) || "REJECTED".equals(data.getStatus()) || "FAILED".equals(data.getStatus())) {
                rsp.setStatus(-1);
            } else {
                rsp.setStatus(0);
            }
            BigDecimal lastUpdated = data.getLastUpdated();
            Date completeTime = lastUpdated == null ? null : new Date(lastUpdated.longValue());
            rsp.setCompleteTime(completeTime);
        } catch (Exception e) {
            log.error("fireblocks queryWithdrawal error", e);
            return RetResult.error(e);
        }
        return RetResult.data(rsp);
    }

    @Override
    public RetResult<GatewayQueryBalanceRsp> queryBalance(GatewayQueryBalanceReq req) {
        RetResult<GatewayQueryBalanceRsp> retResult = new RetResult<>();
//        ApiResponse<VaultAsset> apiResponse = null;
//        String httpResult = null;
//        try {
//            String vaultAccountId = req.getAccountId();
//            String assetId = req.getAssetId();
//
//            Fireblocks fireblocks = fireBlocksConfig.getFireBlocks();
//            //如果assetId有包含$符号, 就走特殊处理
//            if (StringUtils.isNotBlank(assetId) && assetId.contains("$")) {
//                // /vault/accounts/{vaultAccountId}/{assetId}/balance
//                httpResult = fireBlocksConfig.doFireBlocksApiByUrl(String.format("/vault/accounts/%s/%s/balance", vaultAccountId, assetId), "POST");
//                if (StringUtils.isNotBlank(httpResult) && !(httpResult.contains("message") && httpResult.contains("code"))) {
//                    VaultAsset vo = JSONUtil.toBean(httpResult, VaultAsset.class);
//                    GatewayQueryBalanceRsp gatewayQueryBalanceRsp = new GatewayQueryBalanceRsp();
//                    gatewayQueryBalanceRsp.setBalance(vo.getTotal());
//                    retResult = RetResult.data(gatewayQueryBalanceRsp);
//                } else {
//                    retResult = RetResult.error(httpResult);
//                }
//            } else {
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
//                String idempotencyKey = vaultAccountId + "_" + assetId + LocalDate.now().format(formatter);
//                CompletableFuture<ApiResponse<VaultAsset>> future = fireblocks.vaults().updateVaultAccountAssetBalance(vaultAccountId, assetId, idempotencyKey);
//                apiResponse = future.join();
//                if (apiResponse.getStatusCode() == 200) {
//                    VaultAsset vaultAsset = apiResponse.getData();
//                    GatewayQueryBalanceRsp gatewayQueryBalanceRsp = new GatewayQueryBalanceRsp();
//                    gatewayQueryBalanceRsp.setBalance(vaultAsset.getTotal());
//                    retResult = RetResult.data(gatewayQueryBalanceRsp);
//                } else {
//                    retResult = RetResult.error();
//                }
//            }
//        } catch (CompletionException e) {
//            Throwable cause = e.getCause();
//            if (cause instanceof ApiException) {
//                ApiException apiException = (ApiException) cause;
//                String responseBody = apiException.getResponseBody();
//                retResult = RetResult.error(responseBody);
//            } else {
//                retResult = RetResult.error(e);
//            }
//            log.error("The API call failed", e);
//        } catch (Exception e) {
//            retResult = RetResult.error(e);
//            log.error("The API call failed", e);
//        } finally {
//            retResult.addExtraField("originalResponse", apiResponse);
//            retResult.addExtraField("originalResponse2", httpResult);
//            log.info("The API call req:{},result: {}", req, retResult);
//        }
        return retResult;
    }
}
