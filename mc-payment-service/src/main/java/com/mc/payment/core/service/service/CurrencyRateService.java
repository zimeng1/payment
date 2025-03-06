package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.core.service.entity.CurrencyRateEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Conor
 * @description 针对表【mcp_currency_rate(货币汇率表)】的数据库操作Service
 * @createDate 2024-07-26 10:39:05
 */
public interface CurrencyRateService extends IService<CurrencyRateEntity> {
// todo 加密货币使用的是mcp_asset_last_quote，
//  不是mcp_currency_rate 最终都要使用mcp_currency_rate，需要mt5整合所有的加密货币汇率后，再使用mcp_currency_rate

    /**
     * 刷新汇率
     * 从exchangerate-api.com获取最新汇率
     */
    void refreshCurrencyRate();

    /**
     * 根据基础货币和目标货币获取汇率
     *
     * @param baseCurrency   基础货币
     * @param targetCurrency 目标货币
     * @return 汇率
     */
    CurrencyRateEntity getCurrencyRateOne(String baseCurrency, String targetCurrency);

    BigDecimal getCurrencyRate(String baseCurrency, String targetCurrency);

    /**
     * 根据基础货币获取所有汇率
     *
     * @param baseCurrency 基础货币
     * @return 汇率
     */
    List<CurrencyRateEntity> getCurrencyRateList(String baseCurrency);
}
