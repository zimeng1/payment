package com.mc.payment.core.service.manager.deposit;

import com.mc.payment.common.exception.BusinessException;
import com.mc.payment.core.service.config.AppConfig;
import com.mc.payment.core.service.entity.DepositRecordEntity;
import com.mc.payment.core.service.entity.MerchantWalletEntity;
import com.mc.payment.core.service.model.GetAvailableWalletDto;
import com.mc.payment.core.service.model.enums.PurposeTypeEnum;
import com.mc.payment.core.service.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("DepositStrategy_1")
public class FireBlocksDepositStrategy extends DepositTemplate {


    public FireBlocksDepositStrategy(IWebhookEventService webhookEventService,
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
     * @param depositRecordEntity
     * @return 重定向页面地址
     * @throws BusinessException 说明上游通道返回错误,或者调用失败
     */
    @Override
    protected String fiatGetRedirectPageUrl(DepositRecordEntity depositRecordEntity) throws BusinessException {
        return "";
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
        availableWalletDto.setPurposeType(PurposeTypeEnum.DEPOSIT.getCode());
        availableWalletDto.setLock(true);
        return merchantWalletService.getAvailableWallet(availableWalletDto);
    }


}
