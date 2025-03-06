package com.mc.payment.core.service.manager.deposit;

import com.mc.payment.common.exception.BusinessException;
import com.mc.payment.core.service.config.AppConfig;
import com.mc.payment.core.service.entity.DepositRecordEntity;
import com.mc.payment.core.service.entity.MerchantWalletEntity;
import com.mc.payment.core.service.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 未确定支付通道时的入金策略
 *
 * @author Conor
 * @since 2024-12-18 14:36:31.018
 */
@Slf4j
@Component("DepositStrategy_-1")
public class UndecidedDepositStrategy extends DepositTemplate {

    public UndecidedDepositStrategy(IWebhookEventService webhookEventService,
                                    AppConfig appConfig,
                                    PaymentPageService paymentPageService,
                                    IDepositRecordService depositRecordService,
                                    AssetBankService assetBankService,
                                    MerchantChannelAssetService merchantChannelAssetService,
                                    IAssetLastQuoteService assetLastQuoteService,
                                    CurrencyRateService currencyRateService,
                                    MerchantWalletService merchantWalletService,
                                    ChannelWalletService channelWalletService) {
        super(webhookEventService, appConfig, paymentPageService, depositRecordService, assetBankService, merchantChannelAssetService, assetLastQuoteService, currencyRateService, merchantWalletService, channelWalletService);
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
        throw new BusinessException("The channel is undecided");
    }

    /**
     * 获取可用钱包
     *
     * @param recordEntity
     * @return
     */
    @Override
    protected MerchantWalletEntity getAvailableWallet(DepositRecordEntity recordEntity) {
        throw new BusinessException("The channel is undecided");
    }

}
