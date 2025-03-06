package com.mc.payment.core.service.manager.deposit;

import cn.hutool.core.util.NumberUtil;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.exception.BusinessException;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.config.AppConfig;
import com.mc.payment.core.service.entity.DepositRecordEntity;
import com.mc.payment.core.service.entity.MerchantWalletEntity;
import com.mc.payment.core.service.entity.PaymentPageEntity;
import com.mc.payment.core.service.model.GetAvailableWalletDto;
import com.mc.payment.core.service.model.enums.PurposeTypeEnum;
import com.mc.payment.core.service.service.*;
import com.mc.payment.gateway.adapter.EzeebillPaymentGatewayAdapter;
import com.mc.payment.gateway.model.req.GatewayDepositReq;
import com.mc.payment.gateway.model.rsp.GatewayDepositRsp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("DepositStrategy_5")
public class EzeebillDepositStrategy extends DepositTemplate {

    private final EzeebillPaymentGatewayAdapter ezeebillPaymentGatewayAdapter;

    public EzeebillDepositStrategy(IWebhookEventService webhookEventService,
                                   AppConfig appConfig,
                                   PaymentPageService paymentPageService,
                                   IDepositRecordService depositRecordService,
                                   AssetBankService assetBankService,
                                   MerchantChannelAssetService merchantChannelAssetService,
                                   IAssetLastQuoteService assetLastQuoteService,
                                   CurrencyRateService currencyRateService,
                                   MerchantWalletService merchantWalletService,
                                   ChannelWalletService channelWalletService,
                                   EzeebillPaymentGatewayAdapter ezeebillPaymentGatewayAdapter) {
        super(webhookEventService, appConfig, paymentPageService, depositRecordService, assetBankService, merchantChannelAssetService, assetLastQuoteService, currencyRateService, merchantWalletService, channelWalletService);
        this.ezeebillPaymentGatewayAdapter = ezeebillPaymentGatewayAdapter;
    }

    /**
     * 法币入金-获取重定向页面地址
     *
     * @param recordEntity
     * @return 重定向页面地址
     * @throws BusinessException 说明上游通道返回错误,或者调用失败
     */
    @Override
    protected String fiatGetRedirectPageUrl(DepositRecordEntity recordEntity) throws BusinessException {
        RetResult<GatewayDepositRsp> depositRspRetResult;
        try {
            GatewayDepositReq depositReq = new GatewayDepositReq();
            depositReq.setTransactionId(recordEntity.getId());
            depositReq.setBusinessName(recordEntity.getBusinessName());
            depositReq.setAmount(NumberUtil.decimalFormat("#0.00", recordEntity.getAmount()));
            depositReq.setCurrency(recordEntity.getAssetName());
            depositReq.setPayType(recordEntity.getNetProtocol());
            // depositReq.setCallbackUrl() paypal 不需要通过接口设置回调地址
            //不传业务方的地址给上游服务   depositReq.setSuccessPageUrl(recordEntity.getSuccessPageUrl())
            depositReq.setSuccessPageUrl(appConfig.getPaymentRealend() + "/redirect/deposit/post/successPage?id=" + recordEntity.getId());//Ezeebill回调方式为post
            depositRspRetResult = ezeebillPaymentGatewayAdapter.deposit(depositReq);
        } catch (Exception e) {
            throw new BusinessException("channel call fail:" + e.getMessage());
        }
        if (depositRspRetResult.isSuccess()) {
            GatewayDepositRsp resultData = depositRspRetResult.getData();
            //  更新支付网关返回的url到paymentPage
            paymentPageService.lambdaUpdate()
                    .eq(PaymentPageEntity::getMerchantId, recordEntity.getMerchantId())
                    .eq(PaymentPageEntity::getTrackingId, recordEntity.getTrackingId())
                    .set(PaymentPageEntity::getChannelPageUrl, resultData.getRedirectUrl())
                    .update();

            String channelTransactionId = resultData.getChannelTransactionId();
            depositRecordService.lambdaUpdate()
                    .eq(BaseNoLogicalDeleteEntity::getId, recordEntity.getId())
                    .set(DepositRecordEntity::getChannelTransactionId, channelTransactionId)
                    .update();
        } else {
            throw new BusinessException("channel return fail:" + depositRspRetResult.getMsg());
        }
        return depositRspRetResult.getData().getRedirectUrl();
    }

    /**
     * 获取可用钱包
     *
     * @param recordEntity
     * @return
     */
    @Override
    protected MerchantWalletEntity getAvailableWallet(DepositRecordEntity recordEntity) {
        GetAvailableWalletDto availableWalletDto = new GetAvailableWalletDto();
        availableWalletDto.setMerchantId(recordEntity.getMerchantId());
        availableWalletDto.setAssetType(recordEntity.getAssetType());
        availableWalletDto.setChannelSubType(recordEntity.getChannelSubType());
        availableWalletDto.setAssetName(recordEntity.getAssetName());
        availableWalletDto.setNetProtocol(recordEntity.getNetProtocol());
        availableWalletDto.setPurposeType(PurposeTypeEnum.DEPOSIT_WITHDRAWAL.getCode());
        availableWalletDto.setLock(false);
        return merchantWalletService.getAvailableWallet(availableWalletDto);
    }


}
