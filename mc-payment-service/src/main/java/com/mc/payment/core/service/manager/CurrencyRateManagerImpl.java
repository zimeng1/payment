package com.mc.payment.core.service.manager;

import cn.hutool.core.util.StrUtil;
import com.mc.payment.core.service.service.CurrencyRateService;
import com.mc.payment.core.service.service.IAssetLastQuoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyRateManagerImpl implements CurrencyRateManager {
    private final IAssetLastQuoteService assetLastQuoteService;
    private final CurrencyRateService currencyRateService;

    /**
     * 获取货币汇率
     *
     * @param isCrypto       是否是加密货币
     * @param baseCurrency   基础货币
     * @param targetCurrency 目标货币
     * @return
     */
    @Override
    public BigDecimal getCurrencyRate(boolean isCrypto, String baseCurrency, String targetCurrency) {
        if (StrUtil.equals(baseCurrency, targetCurrency, true)) {
            // 同币种,汇率为1
            return BigDecimal.ONE;
        }
        BigDecimal exchangeRate;
        if (isCrypto) {
            exchangeRate = assetLastQuoteService.getExchangeRate(baseCurrency, targetCurrency);
        } else {
            exchangeRate = currencyRateService.getCurrencyRate(baseCurrency, targetCurrency);
        }
        return exchangeRate;
    }
}
