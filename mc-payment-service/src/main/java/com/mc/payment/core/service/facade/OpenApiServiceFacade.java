package com.mc.payment.core.service.facade;

import com.mc.payment.api.model.req.QueryAssetSupportedBankReq;
import com.mc.payment.api.model.req.QueryDepositReportReq;
import com.mc.payment.api.model.req.QueryMerchantSnapshotReq;
import com.mc.payment.api.model.req.WithdrawalReq;
import com.mc.payment.api.model.rsp.*;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.entity.WithdrawalRecordEntity;

import java.util.List;

public interface OpenApiServiceFacade {

    QueryExchangeRateRsp queryExchangeRate(Integer assetType, String baseCurrency, String targetCurrency);

    RetResult<QueryDepositRsp> queryDeposit(String merchantId, String trackingId);

    RetResult<Boolean> cancelDeposit(String merchantId, String trackingId);

    /**
     * 商户出金
     *
     * @param merchantId
     * @param merchantName
     * @param req
     * @return
     */
    RetResult<WithdrawalRsp> withdrawal(String merchantId, String merchantName, WithdrawalReq req);

    WithdrawalRecordEntity handleLegalWithdrawal(String merchantId, WithdrawalReq req, WithdrawalRsp rsp);

    RetResult<QueryWithdrawalRsp> queryWithdrawal(String merchantId, String trackingId);

    List<QueryAssetSupportedBankRsp> queryAssetSupportedBank(QueryAssetSupportedBankReq req);

    List<QueryDepositReportRsp> queryReport(QueryDepositReportReq req);

    PageRsp<MerchantWalletSnapshotRsp> queryMerchantSnapshot(QueryMerchantSnapshotReq req);

}
