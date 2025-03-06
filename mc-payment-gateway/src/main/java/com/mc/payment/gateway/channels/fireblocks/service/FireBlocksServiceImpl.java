package com.mc.payment.gateway.channels.fireblocks.service;

import com.fireblocks.sdk.ApiException;
import com.fireblocks.sdk.ApiResponse;
import com.fireblocks.sdk.Fireblocks;
import com.fireblocks.sdk.model.CreateTransactionResponse;
import com.fireblocks.sdk.model.TransactionRequest;
import com.fireblocks.sdk.model.TransactionResponse;
import com.mc.payment.gateway.channels.fireblocks.config.FireBlocksConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class FireBlocksServiceImpl implements FireBlocksService {
    private final FireBlocksConfig fireBlocksConfig;

    @Override
    public ApiResponse<TransactionResponse> queryTransaction(String txId) {
        Fireblocks fireblocks = fireBlocksConfig.getFireBlocks();
        try {
            return fireblocks.transactions().getTransaction(txId).join();
        } catch (Exception e) {
            Throwable cause = e.getCause();
            // 获取ExecutionException的根本原因
            if (cause instanceof ApiException) {
                ApiException apiException = (ApiException) cause;
                if (apiException.getCode() == 404) {
                    // 这里比较特殊 404表示找不到资源，不是真正的错误
                    //  return RetResult.ok("Transaction not found");
                    return new ApiResponse<>(404, null, null);
                }
            }
            throw new RuntimeException(cause);
        }
    }

    @Override
    public ApiResponse<CreateTransactionResponse> createTransactions(TransactionRequest transactionRequest, String idempotencyKey) {
        Fireblocks fireblocks = fireBlocksConfig.getFireBlocks();
        try {
            return fireblocks.transactions().createTransaction(
                    transactionRequest, null, idempotencyKey).join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
