package com.mc.payment.core.service.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.common.constant.CommonConstant;
import com.mc.payment.core.service.config.AppConfig;
import com.mc.payment.core.service.constant.AssetConstants;
import com.mc.payment.core.service.entity.AssetLastQuoteEntity;
import com.mc.payment.core.service.mapper.AssetLastQuoteMapper;
import com.mc.payment.core.service.model.enums.LastQuoteDataSourceEnum;
import com.mc.payment.core.service.service.IAssetLastQuoteService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 资产最新报价表 服务实现类
 * </p>
 *
 * @author conor
 * @since 2024-05-14 10:23:47
 */
@Slf4j
@Service
public class AssetLastQuoteServiceImpl extends ServiceImpl<AssetLastQuoteMapper, AssetLastQuoteEntity> implements IAssetLastQuoteService {
    // 用于拼接symbol和dataSource的间隔符
    private static final String SEPARATOR = "_";
    private final AppConfig appConfig;
    // 获取 spring.profiles.active 的值
    @Value("${spring.profiles.active}")
    private String active;


    public AssetLastQuoteServiceImpl(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public AssetLastQuoteEntity getOne(String symbol, LastQuoteDataSourceEnum lastQuoteDataSourceEnum) {
        return this.getOne(Wrappers.lambdaQuery(AssetLastQuoteEntity.class)
                .eq(AssetLastQuoteEntity::getSymbol, symbol)
                .eq(AssetLastQuoteEntity::getDataSource, lastQuoteDataSourceEnum.getCode()));
    }

    @Override
    public List<AssetLastQuoteEntity> list(LastQuoteDataSourceEnum lastQuoteDataSourceEnum) {
        return this.list(Wrappers.lambdaQuery(AssetLastQuoteEntity.class)
                .eq(AssetLastQuoteEntity::getDataSource, lastQuoteDataSourceEnum.getCode()));
    }

    /**
     * 刷新最新报价
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshTheEstimatedFeeForAsset() {
        Map<String, AssetLastQuoteEntity> currentMap = this.list()
                .stream().collect(HashMap::new, (m, v) -> m.put(v.getSymbol() + SEPARATOR + v.getDataSource(), v), HashMap::putAll);

        ImmutablePair<List<AssetLastQuoteEntity>, List<AssetLastQuoteEntity>> mt5Pair = this.querySymbolLastQuotes(currentMap);
        List<AssetLastQuoteEntity> addList = new ArrayList<>(mt5Pair.getLeft());
        List<AssetLastQuoteEntity> updateList = new ArrayList<>(mt5Pair.getRight());

        ImmutablePair<List<AssetLastQuoteEntity>, List<AssetLastQuoteEntity>> binancePair = this.querySymbolLastQuotesByBinance(currentMap);

        addList.addAll(binancePair.getLeft());
        updateList.addAll(binancePair.getRight());
        if (!addList.isEmpty()) {
            this.saveBatch(addList);
        }
        if (!updateList.isEmpty()) {
            this.updateBatchById(updateList);
        }
        log.info("refreshTheEstimatedFeeForAsset success addList size:{},updateList size:{}", addList.size(), updateList.size());
    }

    /**
     * 查询MT5的最新报价
     *
     * @param currentMap 当前的最新报价
     * @return addList, updateList
     */
    private ImmutablePair<List<AssetLastQuoteEntity>, List<AssetLastQuoteEntity>> querySymbolLastQuotes(Map<String, AssetLastQuoteEntity> currentMap) {
        List<AssetLastQuoteEntity> addList = new ArrayList<>();
        List<AssetLastQuoteEntity> updateList = new ArrayList<>();
        String post = null;
        try {
            post = HttpUtil.createPost(appConfig.getMt5ApiUrl() + "/Symbol/GetLastQuotes").execute().body();
            if (JSONUtil.isTypeJSONObject(post)) {
                JSONObject jsonObject = JSONUtil.parseObj(post);
                Boolean result = jsonObject.getBool("Success");
                if (result != null && result) {
                    JSONArray jsonArray = jsonObject.getJSONArray("Data");

                    for (Object o : jsonArray) {
                        JSONObject parseObj = JSONUtil.parseObj(o);
                        AssetLastQuoteEntity assetLastQuoteEntity = currentMap.getOrDefault(parseObj.getStr("s") + SEPARATOR + LastQuoteDataSourceEnum.MT5.getCode(),
                                new AssetLastQuoteEntity());
                        assetLastQuoteEntity.setSymbol(parseObj.getStr("s"));
                        assetLastQuoteEntity.setAsk(parseObj.getBigDecimal("a"));
                        assetLastQuoteEntity.setBid(parseObj.getBigDecimal("b"));
                        assetLastQuoteEntity.setTickTime(DateUtil.parse(parseObj.getStr("t")));
                        assetLastQuoteEntity.setDataSource(LastQuoteDataSourceEnum.MT5.getCode());
                        if (assetLastQuoteEntity.getId() == null) {
                            addList.add(assetLastQuoteEntity);
                        } else {
                            updateList.add(assetLastQuoteEntity);
                        }
                    }
                }
            }
        } catch (HttpException e) {
            log.error("querySymbolLastQuotes error", e);
        } finally {
            log.info("/Symbol/GetLastQuotes,post:{} addList size:{},updateList size:{}", post, addList.size(), updateList.size());
        }
        return ImmutablePair.of(addList, updateList);
    }

    /**
     * 查询币安的最新报价
     *
     * @param currentMap 当前的最新报价
     * @return addList, updateList
     */
    private ImmutablePair<List<AssetLastQuoteEntity>, List<AssetLastQuoteEntity>> querySymbolLastQuotesByBinance(Map<String, AssetLastQuoteEntity> currentMap) {
        List<AssetLastQuoteEntity> addList = new ArrayList<>();
        List<AssetLastQuoteEntity> updateList = new ArrayList<>();
        String post = null;
        try {
            // 如果是本地启动则加上代理
            if (CommonConstant.DEV.equals(active)) {
                post = HttpUtil.createGet(appConfig.getBinanceSpotUrl() + "/api/v3/ticker/price")
                        .setHttpProxy("localhost", 7890).execute().body();
            } else {
                post = HttpUtil.createGet(appConfig.getBinanceSpotUrl() + "/api/v3/ticker/price").execute().body();
            }
            if (JSONUtil.isTypeJSONArray(post)) {
                JSONArray jsonArray = JSONUtil.parseArray(post);
                for (Object o : jsonArray) {
                    JSONObject jsonObject = JSONUtil.parseObj(o);
                    AssetLastQuoteEntity assetLastQuoteEntity = currentMap.getOrDefault(jsonObject.getStr("symbol") + SEPARATOR + LastQuoteDataSourceEnum.BINANCE.getCode(),
                            new AssetLastQuoteEntity());
                    assetLastQuoteEntity.setSymbol(jsonObject.getStr("symbol"));
                    assetLastQuoteEntity.setAsk(new BigDecimal(jsonObject.getStr("price")));
                    assetLastQuoteEntity.setBid(assetLastQuoteEntity.getAsk());
                    assetLastQuoteEntity.setTickTime(new Date());
                    assetLastQuoteEntity.setDataSource(LastQuoteDataSourceEnum.BINANCE.getCode());
                    if (assetLastQuoteEntity.getId() == null) {
                        addList.add(assetLastQuoteEntity);
                    } else {
                        updateList.add(assetLastQuoteEntity);
                    }
                }
            }
        } catch (Exception e) {
            log.error("querySymbolLastQuotesByBinance error", e);
        } finally {
            log.info("/api/v3/ticker/price,post:{} addList size:{},updateList size:{}", post, addList.size(), updateList.size());
        }
        return ImmutablePair.of(addList, updateList);
    }


    /**
     * 获取币种和最大价格, (二期需求: 获取的报价优先按最新报价获取, 而不是优先按照mt5)
     * ps: 如果是获取usdt转换usdt的汇率, 则返回1 (20240612)
     *
     * @param symbols
     * @return
     */
    @Override
    public Map<String, BigDecimal> getSymbolAndMaxPriceBySymbol(Collection<String> symbols) {
        if (CollectionUtils.isNotEmpty(symbols)) {
            // 按'最新报价时间'asc排序查询, 然后如果有同个币种有多个汇率时, 取最新报价时间的最大值
            List<AssetLastQuoteEntity> assetlist = this.list(Wrappers.lambdaQuery(AssetLastQuoteEntity.class)
                    .in(AssetLastQuoteEntity::getSymbol, symbols)
                    .orderByAsc(AssetLastQuoteEntity::getTickTime));
            Map<String, BigDecimal> resultMap = assetlist.stream().collect(Collectors.toMap(AssetLastQuoteEntity::getSymbol,
                    asset -> {
                        return asset.getAsk().compareTo(asset.getBid()) >= 0 ? asset.getAsk() : asset.getBid();
                    }, (value1, value2) -> value2 // 取最新的
            ));
            resultMap.put(AssetConstants.AN_USDT + AssetConstants.AN_USDT, BigDecimal.ONE);
            return resultMap;
        }
        Map<String, BigDecimal> resultMap = new HashMap<>();
        resultMap.put(AssetConstants.AN_USDT + AssetConstants.AN_USDT, BigDecimal.ONE);
        return resultMap;
    }

    /**
     * 查询本地的汇率, 从币种转换为USDT的汇率
     * 取大值/取小值
     * <p>
     * 从mt5查询不到时, 从binance查询
     *
     * @param assetName 币种
     * @param isMax     是否取大值
     * @return
     */
    public BigDecimal getExchangeRate(String assetName, boolean isMax) {
        BigDecimal exchangeRate = BigDecimal.ZERO;
        AssetLastQuoteEntity assetLastQuoteEntity = null;
        try {
            assetLastQuoteEntity = this.getOne(assetName + AssetConstants.AN_USDT, LastQuoteDataSourceEnum.MT5);
            if (assetLastQuoteEntity == null) {
                log.info("MT5 assetLastQuoteEntity is null, change to binance assetName:{}", assetName);
                assetLastQuoteEntity = this.getOne(assetName + AssetConstants.AN_USDT, LastQuoteDataSourceEnum.BINANCE);
            }
            if (assetLastQuoteEntity != null) {
                BigDecimal ask = assetLastQuoteEntity.getAsk();
                BigDecimal bid = assetLastQuoteEntity.getBid();
                if (isMax) {
                    exchangeRate = ask.compareTo(bid) >= 0 ? ask : bid;
                } else {
                    exchangeRate = ask.compareTo(bid) >= 0 ? bid : ask;
                }
            }
        } catch (Exception e) {
            log.error("[getExchangeRate] 执行异常 rsp:{}", assetName, e);
        } finally {
            log.info("assetName {},汇率为:{},assetLastQuoteEntity:{}", assetName, exchangeRate, assetLastQuoteEntity);
        }
        return exchangeRate;
    }

    @Override
    public BigDecimal getExchangeRate(String baseCurrency, String targetCurrency) {
        if (StrUtil.equals(baseCurrency, targetCurrency)) {
            return BigDecimal.ONE;
        }
        BigDecimal exchangeRate = null;
        ImmutablePair<BigDecimal, BigDecimal> immutablePair = queryLastQuote(baseCurrency, targetCurrency);
        if (immutablePair != null) {
            // 取卖价
            exchangeRate = immutablePair.getRight();
        } else {
            // 如果查询不到, 则反转查询
            immutablePair = queryLastQuote(targetCurrency, baseCurrency);
            if (immutablePair != null) {
                // 取买价
                exchangeRate = immutablePair.getLeft();
                if (exchangeRate != null) {
                    exchangeRate = BigDecimal.ONE.divide(exchangeRate, 8, RoundingMode.HALF_UP);
                }
            }
        }
        return exchangeRate;
    }

    /**
     * 查询最新报价
     *
     * @param baseCurrency
     * @param targetCurrency
     * @return ask, bid
     */
    private ImmutablePair<BigDecimal, BigDecimal> queryLastQuote(String baseCurrency, String targetCurrency) {
        AssetLastQuoteEntity assetLastQuoteEntity = this.getOne(baseCurrency + targetCurrency, LastQuoteDataSourceEnum.MT5);
        if (assetLastQuoteEntity == null) {
            log.info("MT5 assetLastQuoteEntity is null, change to binance baseCurrency:{},targetCurrency:{}", baseCurrency, targetCurrency);
            assetLastQuoteEntity = this.getOne(baseCurrency + targetCurrency, LastQuoteDataSourceEnum.BINANCE);
        }
        return assetLastQuoteEntity == null ? null : ImmutablePair.of(assetLastQuoteEntity.getAsk(), assetLastQuoteEntity.getBid());
    }
}
