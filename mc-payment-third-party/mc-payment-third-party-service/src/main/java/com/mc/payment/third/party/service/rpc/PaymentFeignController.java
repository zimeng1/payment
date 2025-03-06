package com.mc.payment.third.party.service.rpc;

import com.alibaba.fastjson.JSON;
import com.block.atm.sdk.dto.Payout;
import com.block.atm.sdk.eth.Erc20Helper;
import com.block.atm.sdk.eth.PayoutHelper;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.third.party.api.IPaymentFeignClient;
import com.mc.payment.third.party.api.model.constant.BlockATMPayout;
import com.mc.payment.third.party.api.model.req.PayOutDetail;
import com.mc.payment.third.party.api.model.req.PayoutReq;
import com.mc.payment.third.party.api.model.req.PayoutReqOld;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author conor
 * @since 2024/01/29 11:10
 */
@Slf4j
@RestController
@RequestMapping("/payment")
public class PaymentFeignController implements IPaymentFeignClient {
    @Override
    @PostMapping("/payoutOld")
    public RetResult<String> payoutOld(@RequestBody PayoutReqOld payoutReq) {
        log.info("send payout :{}", payoutReq);
        try {
            PayoutHelper payout = new PayoutHelper(BlockATMPayout.JSON_RPC_URL);
            String txId = payout.payout(BlockATMPayout.PRIVATE_KEY, payoutReq.getPayoutGatewayAddress(), payoutReq.getPayouts(), payoutReq.getBusiness(), payoutReq.getChainId());
            log.info("payout return txId:{}, {}", txId, BlockATMPayout.PRIVATE_KEY);
            return RetResult.data(txId);
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
            log.error("payout error:{}", payoutReq, e);
            return RetResult.error();
        }
    }

    @Override
    @PostMapping("/payout")
    public RetResult<String> payout(@RequestBody PayoutReq payoutReq) {
        log.info("send payout :{}", payoutReq);
        try {
            List<PayOutDetail> payoutDetails = payoutReq.getPayouts();
            List<Payout> payouts = new ArrayList<>();
            List<Utf8String> business = new ArrayList<>();
            for (PayOutDetail payOutDetail : payoutDetails) {
                // todo getDecimals缓存起来
                RetResult<BigInteger> decimals = getDecimals(payOutDetail.getTokenAddress());
                BigInteger decimalsBigInt = (BigInteger) decimals.getData();
                BigDecimal calculatedAmount = payOutDetail.getAmount().multiply(BigDecimal.TEN.pow(decimalsBigInt.intValue()));
                Payout payout = new Payout(payOutDetail.getTokenAddress(), calculatedAmount.toBigInteger(), payOutDetail.getToAddress());
                payouts.add(payout);
                business.add(new Utf8String(payOutDetail.getOrderNo()));
            }

            log.debug("payout req:{}", payoutReq);
            PayoutHelper payout = new PayoutHelper(BlockATMPayout.JSON_RPC_URL);
            String txId = payout.payout(BlockATMPayout.PRIVATE_KEY, payoutReq.getPayoutGatewayAddress(), payouts, business, payoutReq.getChainId());
            log.info("payout return txId:{}, {}", txId, BlockATMPayout.PRIVATE_KEY);
            return RetResult.data(txId);
        } catch (InterruptedException | ExecutionException | IOException e) {
            log.error("payout error:{}", payoutReq, e);
            return RetResult.error();
        }
    }

    @Override
    @GetMapping("/getBalance/{coinContractAddress}/{accountAddress}")
    public RetResult<BigDecimal> getBalance(@PathVariable("coinContractAddress") String coinContractAddress, @PathVariable("accountAddress") String accountAddress) {
        try {
            Erc20Helper erc20Helper = new Erc20Helper(BlockATMPayout.JSON_RPC_URL);
            BigInteger balance = erc20Helper.getBalance(coinContractAddress, accountAddress);
            BigInteger decimals = erc20Helper.getDecimals(coinContractAddress);
            if (decimals.compareTo(BigInteger.ZERO) < 0) {
                log.warn("accountAddress:{} getDecimals:{}", accountAddress, decimals);
                return RetResult.error();
            }
            BigDecimal result = new BigDecimal(balance).divide(BigDecimal.TEN.pow(decimals.intValue()), decimals.intValue(), RoundingMode.HALF_UP);
            log.info("getTransaction result:{}", result);
            return RetResult.data(result);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("getBalance error:{},{}", coinContractAddress, accountAddress, e);
            return RetResult.error();
        }
    }

    @Cacheable(value = "caffeine", key = "#coinContractAddress")
    @Override
    @GetMapping("/getDecimals/{contractAddress}")
    public RetResult<BigInteger> getDecimals(@PathVariable("contractAddress") String coinContractAddress) {
        try {
            log.info("getDecimals coinContractAddress:{}", coinContractAddress);
            Erc20Helper erc20Helper = new Erc20Helper(BlockATMPayout.JSON_RPC_URL);
            BigInteger decimals = erc20Helper.getDecimals(coinContractAddress);
            log.info("getTransaction result:{}", decimals);
            return RetResult.data(decimals);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("getDecimals error:{}", coinContractAddress, e);
            return RetResult.error();
        }
    }

    @Override
    @GetMapping("/getTransaction/{txId}")
    public RetResult<Transaction> getTransaction(@PathVariable("txId")String txId) {
        try {
            PayoutHelper payout = new PayoutHelper(BlockATMPayout.JSON_RPC_URL);
            EthTransaction eth = payout.getTransaction(txId);
            log.info("getTransaction result:{}", JSON.toJSONString(eth.getResult()));
            return RetResult.data(eth.getResult());
        } catch (IOException e) {
            e.printStackTrace();
            log.error("getTransaction error:{}", txId, e);
            return RetResult.error();
        }
    }

    @Override    @GetMapping("/getTransactionReceipt/{txId}")
    public RetResult<TransactionReceipt> getTransactionReceipt(@PathVariable("txId") String txId) {
        try {
            PayoutHelper payout = new PayoutHelper(BlockATMPayout.JSON_RPC_URL);
            EthGetTransactionReceipt eth = payout.getTransactionReceipt(txId);
            log.info("getTransactionReceipt result:{}", JSON.toJSONString(eth.getResult()));
            return RetResult.data(eth.getResult());
        } catch (IOException e) {
            e.printStackTrace();
            log.error("getTransactionReceipt error:{}", txId, e);
            return RetResult.error();
        }
    }

    @GetMapping("/txIsSuccessful/{txId}")
    public RetResult<Boolean> txIsSuccessful(@PathVariable("txId") String txId) {
        try {
            PayoutHelper payout = new PayoutHelper(BlockATMPayout.JSON_RPC_URL);
            Boolean result = payout.txIsSuccessful(txId);
            log.info("txIsSuccessful result:{}", result);
            return RetResult.data(result);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("txIsSuccessful error:{}", txId, e);
            return RetResult.error();
        }
    }
}
