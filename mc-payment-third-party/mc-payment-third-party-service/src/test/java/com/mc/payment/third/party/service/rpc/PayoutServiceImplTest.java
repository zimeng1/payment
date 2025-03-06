package com.mc.payment.third.party.service.rpc;

import com.block.atm.sdk.dto.Payout;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.third.party.api.model.req.PayoutReqOld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@ExtendWith(MockitoExtension.class)
public class PayoutServiceImplTest {

    // 代付网关合约地址
    String payoutGatewayAddress = "0x8E5dF55ac224DB7424Fa8536edA9356F44474936";
    // Decimals 6
    static String USDT = "0x92eFDFa35c75B259375eBe0F84ee1d95db0489b6";
    // Decimals 6
    static String USDC = "0x2f96275bbb4a54714ef0251226c42811fb9f98aa";
    @InjectMocks
    private PaymentFeignController paymentRpcService;
    @Test
    void payout() throws InterruptedException, ExecutionException, IOException {
        PayoutReqOld payoutReq = new PayoutReqOld();
        payoutReq.setPayoutGatewayAddress(payoutGatewayAddress);

        List<Payout> payoutList = new ArrayList<>();

        //  1 USDT = 1000000
        Payout payoutUsdt = new Payout(USDT,new BigInteger("1000000"),"0xE5b04ADc994c3246AC50206926e6DAD27FcB5Bf4");
        Payout payoutUsdc = new Payout(USDC,new BigInteger("1000000"),"0xE5b04ADc994c3246AC50206926e6DAD27FcB5Bf4");

        payoutList.add(payoutUsdt);
        payoutList.add(payoutUsdc);
        payoutReq.setPayouts(payoutList);
        List<Utf8String> business = new ArrayList<>();
        business.add(new Utf8String("TX1"));
        business.add(new Utf8String("TX2"));
        payoutReq.setBusiness(business);

        // testnet 1， mainnet 5
        int chainId = 5;
        payoutReq.setChainId(chainId);
        RetResult<String> res = paymentRpcService.payoutOld(payoutReq);
        System.out.println(res.getData());
    }

    @Test
    void getBalance() {
        String coinContractAddress = "0x2f96275bbb4a54714ef0251226c42811fb9f98aa";
        String accountAddress = "0xf51b7b4fa5d8697670eb8a98982928fb448d7e53";
        RetResult<BigDecimal> res = paymentRpcService.getBalance(coinContractAddress, accountAddress);
        System.out.println(res.getData());
    }

    @Test
    void getDecimals() {
        String coinContractAddress = "0x92eFDFa35c75B259375eBe0F84ee1d95db0489b6";
        RetResult<BigInteger> res = paymentRpcService.getDecimals(coinContractAddress);
        System.out.println(res.getData());
    }

    @Test
    void getTransaction() {
        String txId = "0x62b895fda03b567abe10ac9a503309f96956104039fc8d84456825c35e138339";
        RetResult<Transaction> res = paymentRpcService.getTransaction(txId);
        System.out.println(res.getData());
    }

    @Test
    void getTransactionReceipt() {
        String txId = "0x62b895fda03b567abe10ac9a503309f96956104039fc8d84456825c35e138339";
        RetResult<TransactionReceipt> res = paymentRpcService.getTransactionReceipt(txId);
        System.out.println(res.getData());
    }

    @Test
    void txIsSuccessful() {
        String txId = "0xbeaf36df586c2ac92229ef7d5f629eeb03bad11a5dba0790daf446aacb901f05";
        RetResult<Boolean> res = paymentRpcService.txIsSuccessful(txId);
        System.out.println(res.getData());
    }

}
