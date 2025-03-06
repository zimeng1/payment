package com.mc.payment.core.service.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.core.service.config.AppConfig;
import com.mc.payment.core.service.entity.CurrencyRateEntity;
import com.mc.payment.core.service.mapper.CurrencyRateMapper;
import com.mc.payment.core.service.service.CurrencyRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Conor
 * @description 针对表【mcp_currency_rate(货币汇率表)】的数据库操作Service实现
 * @createDate 2024-07-26 10:39:05
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class CurrencyRateServiceImpl extends ServiceImpl<CurrencyRateMapper, CurrencyRateEntity>
        implements CurrencyRateService {
    private final AppConfig appConfig;

    /**
     * 刷新汇率
     * 从exchangerate-api.com获取最新汇率
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshCurrencyRate() {
        List<CurrencyRateEntity> list = this.list();
        Map<String, CurrencyRateEntity> map = list.stream().collect(Collectors.toMap(o -> o.getBaseCurrency() + "/" + o.getTargetCurrency(), Function.identity()));
        List<CurrencyRateEntity> updateList = new ArrayList<>();
        List<CurrencyRateEntity> addList = new ArrayList<>();
        String result = HttpUtil.get("https://v6.exchangerate-api.com/v6/" + appConfig.getExchangeRateApiKey() + "/latest/USD");
        JSONObject jsonObject = JSONUtil.parseObj(result);
        JSONObject conversionRates = jsonObject.getJSONObject("conversion_rates");
        for (Map.Entry<String, Object> entry : conversionRates.entrySet()) {
            String currencyKey = "USD/" + entry.getKey();
            CurrencyRateEntity currencyRateEntity = map.get(currencyKey);
            if (currencyRateEntity == null) {
                currencyRateEntity = new CurrencyRateEntity();
                currencyRateEntity.setBaseCurrency("USD");
                currencyRateEntity.setTargetCurrency(entry.getKey());
                currencyRateEntity.setExchangeRate(new BigDecimal(entry.getValue().toString()));
                addList.add(currencyRateEntity);
            } else {
                currencyRateEntity.setExchangeRate(new BigDecimal(entry.getValue().toString()));
                updateList.add(currencyRateEntity);
            }
        }
        if (!addList.isEmpty()) {
            this.saveBatch(addList);
        }
        if (!updateList.isEmpty()) {
            this.updateBatchById(updateList);
        }
    }


    /**
     * 根据基础货币和目标货币获取汇率
     *
     * @param baseCurrency   基础货币
     * @param targetCurrency 目标货币
     * @return 汇率
     */
    @Override
    public CurrencyRateEntity getCurrencyRateOne(String baseCurrency, String targetCurrency) {
        return this.getOne(Wrappers.lambdaQuery(CurrencyRateEntity.class)
                .eq(CurrencyRateEntity::getBaseCurrency, baseCurrency)
                .eq(CurrencyRateEntity::getTargetCurrency, targetCurrency));
    }

    /**
     * 根据基础货币和目标货币获取汇率 优先获取正向汇率，如果没有则获取反向汇率
     *
     * @param baseCurrency
     * @param targetCurrency
     * @return
     */
    @Override
    public BigDecimal getCurrencyRate(String baseCurrency, String targetCurrency) {
        BigDecimal result = null;
        CurrencyRateEntity currencyRate = getCurrencyRateOne(baseCurrency, targetCurrency);
        if (currencyRate != null) {
            result = currencyRate.getExchangeRate();
        } else {
            // 尝试反转
            currencyRate = getCurrencyRateOne(targetCurrency, baseCurrency);
            if (currencyRate != null) {
                result = BigDecimal.ONE.divide(currencyRate.getExchangeRate(), 5, RoundingMode.HALF_UP);
            }
        }
        return result;
    }

    /**
     * 根据基础货币获取所有汇率
     *
     * @param baseCurrency 基础货币
     * @return 汇率
     */
    @Override
    public List<CurrencyRateEntity> getCurrencyRateList(String baseCurrency) {
        return this.list(Wrappers.lambdaQuery(CurrencyRateEntity.class).eq(CurrencyRateEntity::getBaseCurrency, baseCurrency));
    }
}




