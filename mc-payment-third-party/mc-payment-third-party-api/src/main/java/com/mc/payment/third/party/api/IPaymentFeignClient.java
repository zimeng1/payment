package com.mc.payment.third.party.api;

import com.mc.payment.common.base.RetResult;
import com.mc.payment.third.party.api.model.req.PayoutReq;
import com.mc.payment.third.party.api.model.req.PayoutReqOld;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author conor
 * @since 2024/01/29 11:07
 */
@FeignClient(value = "mc-payment-third-party-service", path = "/api/v1/third/party/payment",contextId = "third-party-Feign-BlockATM")
public interface IPaymentFeignClient {

    @PostMapping("/payoutOld")
    RetResult<String> payoutOld(@RequestBody PayoutReqOld payoutReq);

    @PostMapping("/payout")
    RetResult<String> payout(@RequestBody PayoutReq payoutReq);

    @GetMapping("/getBalance/{coinContractAddress}/{accountAddress}")
    RetResult<BigDecimal> getBalance(@PathVariable("coinContractAddress") String coinContractAddress, @PathVariable("accountAddress") String accountAddress);

    @GetMapping("/getDecimals/{contractAddress}")
    RetResult<BigInteger> getDecimals(@PathVariable("contractAddress") String contractAddress);

    @GetMapping("/getTransaction/{txId}")
    RetResult<Transaction> getTransaction(@PathVariable("txId") String txId);

    @GetMapping("/getTransactionReceipt/{txId}")
    RetResult<TransactionReceipt> getTransactionReceipt(@PathVariable("txId") String txId);

    @GetMapping("/txIsSuccessful/{txId}")
    RetResult<Boolean> txIsSuccessful(@PathVariable("txId") String txId);
}
