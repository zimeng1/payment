package com.mc.payment.third.party.service.controller;

import com.mc.payment.common.base.RetResult;
import com.mc.payment.third.party.api.IPaymentFeignClient;
import com.mc.payment.third.party.api.model.req.BalanceQueryReq;
import com.mc.payment.third.party.api.model.req.TransactionReq;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/pay")
public class PaymentController {
    @Resource
    IPaymentFeignClient paymentFeignClient;

    @Operation(summary = "Query Balance", description = "Query Balance")
    @PostMapping("/getBalance")
    public RetResult<BigDecimal> getBalance(@Valid @RequestBody BalanceQueryReq balanceQueryReq) {
        log.info("getBalance BalanceQueryReq:{}", balanceQueryReq);
        return paymentFeignClient.getBalance(balanceQueryReq.getCoinContractAddress(), balanceQueryReq.getAccountAddress());
    }

    @Operation(summary = "Query Transaction", description = "Query Transaction")
    @PostMapping("/getTransaction")
    public RetResult<Transaction> getTransaction(@Valid @RequestBody TransactionReq transactionReq) {
        log.info("getTransaction TransactionReq:{}", transactionReq);
        return paymentFeignClient.getTransaction(transactionReq.getTxId());
    }

    @Operation(summary = "Query TransactionReceipt", description = "Query TransactionReceipt")
    @PostMapping("/getTransactionReceipt")
    public RetResult<TransactionReceipt> getTransactionReceipt(@Valid @RequestBody TransactionReq transactionReq) {
        log.info("getTransactionReceipt TransactionReq:{}", transactionReq);
        return paymentFeignClient.getTransactionReceipt(transactionReq.getTxId());
    }

    @Operation(summary = "Check txId is Successful", description = "Check txId is Successful")
    @PostMapping("/txIsSuccessful")
    public RetResult<Boolean> txIsSuccessful(@Valid @RequestBody TransactionReq transactionReq) {
        log.info("txIsSuccessful TransactionReq:{}", transactionReq);
        return paymentFeignClient.txIsSuccessful(transactionReq.getTxId());
    }
}
