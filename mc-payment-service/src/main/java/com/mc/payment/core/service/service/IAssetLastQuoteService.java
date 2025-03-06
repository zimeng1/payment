package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.core.service.entity.AssetLastQuoteEntity;
import com.mc.payment.core.service.model.enums.LastQuoteDataSourceEnum;

import java.util.List;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

/**
 * <p>
 * 资产最新报价表 服务类
 * </p>
 *
 * @author conor
 * @since 2024-05-14 10:23:47
 */
public interface IAssetLastQuoteService extends IService<AssetLastQuoteEntity> {

    AssetLastQuoteEntity getOne(String symbol, LastQuoteDataSourceEnum lastQuoteDataSourceEnum);

    List<AssetLastQuoteEntity> list(LastQuoteDataSourceEnum lastQuoteDataSourceEnum);

    /**
     * 刷新最新报价
     */
    void refreshTheEstimatedFeeForAsset();

    Map<String, BigDecimal> getSymbolAndMaxPriceBySymbol(Collection<String> symbols);

    BigDecimal getExchangeRate(String assetName, boolean isMax);
    BigDecimal getExchangeRate(String baseCurrency, String targetCurrency);
}
