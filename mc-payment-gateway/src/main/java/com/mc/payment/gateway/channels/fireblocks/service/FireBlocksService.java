package com.mc.payment.gateway.channels.fireblocks.service;

import com.fireblocks.sdk.ApiResponse;
import com.fireblocks.sdk.model.CreateTransactionResponse;
import com.fireblocks.sdk.model.TransactionRequest;
import com.fireblocks.sdk.model.TransactionResponse;

public interface FireBlocksService {

    ApiResponse<TransactionResponse> queryTransaction(String txId);

    ApiResponse<CreateTransactionResponse> createTransactions(
            TransactionRequest transactionRequest, String idempotencyKey);
}
