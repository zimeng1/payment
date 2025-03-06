package com.mc.payment.api;


import com.mc.payment.api.config.FeignDoc;
import com.mc.payment.api.config.PaymentOpenApiAuthInterceptor;
import com.mc.payment.api.model.req.*;
import com.mc.payment.api.model.rsp.*;
import com.mc.payment.common.base.RetResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignDoc(name = "支付接口", description = "Payment API")
@FeignClient(name = "mc-payment-service", contextId = "mc-payment-service-feign", configuration = PaymentOpenApiAuthInterceptor.class)
public interface PaymentFeignClient {

    @FeignDoc(name = "1.入金申请", description = "Deposit request")
    @PostMapping("/openapi/v1/deposit/request")
    RetResult<DepositRsp> deposit(@Validated @RequestBody DepositReq req);

    @FeignDoc(name = "2.入金查询", description = "Query deposit")
    @PostMapping("/openapi/v1/deposit/query")
    RetResult<QueryDepositRsp> queryDeposit(@Validated @RequestBody QueryDepositReq req);

    @FeignDoc(name = "3.取消入金", description = "Cancel deposit")
    @PostMapping("/openapi/v1/deposit/cancel")
    RetResult<Boolean> cancelDeposit(@RequestBody @Validated CancelDepositReq req);

    @FeignDoc(name = "4.出金申请", description = "Withdrawal request")
    @PostMapping("/openapi/v1/withdrawal/request")
    RetResult<WithdrawalRsp> withdrawal(@Validated @RequestBody WithdrawalReq req);

    @FeignDoc(name = "5.出金查询", description = "Query withdrawal")
    @PostMapping("/openapi/v1/withdrawal/query")
    RetResult<QueryWithdrawalRsp> queryWithdrawal(@Validated @RequestBody QueryWithdrawalReq req);

    @FeignDoc(name = "汇率查询", description = "Query Exchange Rate")
    @PostMapping("/openapi/v1/queryExchangeRate")
    RetResult<QueryExchangeRateRsp> queryExchangeRate(@Validated @RequestBody QueryExchangeRateReq req);

    @FeignDoc(name = "资产列表查询", description = "Query Asset")
    @PostMapping("/openapi/v1/queryAsset")
    RetResult<List<QueryAssetRsp>> queryAsset(@Validated @RequestBody QueryAssetReq req);

    @FeignDoc(name = "资产支持银行查询", description = "Query Asset Supported Bank")
    @PostMapping("/openapi/v1/queryAssetSupportedBank")
    RetResult<List<QueryAssetSupportedBankRsp>> queryAssetSupportedBank(@Validated @RequestBody QueryAssetSupportedBankReq req);

    @FeignDoc(name = "入金报表查询", description = "Query deposit report")
    @PostMapping("/openapi/v1/deposit/queryReport")
    RetResult<List<QueryDepositReportRsp>> queryReport(@Validated @RequestBody QueryDepositReportReq req);

    @FeignDoc(name = "商户钱包快照查询", description = "Query MerchantWalletSnapshot")
    @PostMapping("/openapi/v1/queryMerchantSnapshot")
    RetResult<PageRsp<MerchantWalletSnapshotRsp>> queryMerchantSnapshot(@RequestBody QueryMerchantSnapshotReq req);
}
