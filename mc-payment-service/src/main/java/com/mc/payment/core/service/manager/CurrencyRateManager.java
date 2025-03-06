package com.mc.payment.core.service.manager;

import java.math.BigDecimal;

/**
 * 货币汇率管理
 */
public interface CurrencyRateManager {
    /**
     * 获取货币汇率
     *
     * @param isCrypto       是否是加密货币
     * @param baseCurrency   基础货币
     * @param targetCurrency 目标货币
     * @return
     */
    BigDecimal getCurrencyRate(boolean isCrypto, String baseCurrency, String targetCurrency);
}
