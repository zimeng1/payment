package com.mc.payment.core.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mc.payment.core.service.entity.AssetLastQuoteEntity;
import com.mc.payment.core.service.mapper.AssetLastQuoteMapper;
import com.mc.payment.core.service.model.enums.LastQuoteDataSourceEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AssetLastQuoteServiceImplTest {

    @InjectMocks
    private AssetLastQuoteServiceImpl assetLastQuoteService;

    @Mock
    private AssetLastQuoteMapper assetLastQuoteMapper;

    @Mock
    private LastQuoteDataSourceEnum lastQuoteDataSourceEnum;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testGetExchangeRateWhenMaxIsTrue() {
        // Setup
       /* AssetLastQuoteEntity mt5Quote = new AssetLastQuoteEntity();
        mt5Quote.setBid(BigDecimal.ONE);
        mt5Quote.setAsk(BigDecimal.TEN);
        mt5Quote.setDataSource(LastQuoteDataSourceEnum.MT5.getCode());

        AssetLastQuoteEntity binanceQuote = new AssetLastQuoteEntity();
        binanceQuote.setBid(BigDecimal.ZERO);
        binanceQuote.setAsk(BigDecimal.ONE);
        binanceQuote.setDataSource(LastQuoteDataSourceEnum.BINANCE.getCode());

//        when(lastQuoteDataSourceEnum.getCode()).thenReturn( 0);
        // Mock behavior
//        when(assetLastQuoteService.getOne(any(LambdaQueryWrapper.class))).thenReturn( binanceQuote);
        when(assetLastQuoteService.getOne(anyString(),  LastQuoteDataSourceEnum.MT5)).thenReturn(mt5Quote);
//        when(assetLastQuoteService.getOne(anyString(),  LastQuoteDataSourceEnum.BINANCE)).thenReturn(binanceQuote);

        // Execute
        BigDecimal rate = assetLastQuoteService.getExchangeRate("BTC", true);

        // Verify
        assertEquals(BigDecimal.ZERO, rate);
        Mockito.verify(assetLastQuoteMapper, Mockito.times(3)).selectOne(any());*/
    }

    @Test
    public void testGetExchangeRateWhenMaxIsFalse() {
        // Setup
        /*AssetLastQuoteEntity mt5Quote = new AssetLastQuoteEntity();
        mt5Quote.setBid(BigDecimal.TEN);
        mt5Quote.setAsk(BigDecimal.ONE);
        mt5Quote.setDataSource(LastQuoteDataSourceEnum.MT5.getCode());

        AssetLastQuoteEntity binanceQuote = new AssetLastQuoteEntity();
        binanceQuote.setBid(BigDecimal.ZERO);
        binanceQuote.setAsk(BigDecimal.ONE);
        binanceQuote.setDataSource(LastQuoteDataSourceEnum.BINANCE.getCode());

        // Mock behavior
        when(assetLastQuoteMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn( binanceQuote);
        when(assetLastQuoteService.getOne(anyString(),  LastQuoteDataSourceEnum.MT5)).thenReturn(mt5Quote);
        when(assetLastQuoteService.getOne(anyString(),  LastQuoteDataSourceEnum.BINANCE)).thenReturn(binanceQuote);
        // Execute
        BigDecimal rate = assetLastQuoteService.getExchangeRate("BTC", false);

        // Verify
        assertEquals(BigDecimal.ONE, rate);
        Mockito.verify(assetLastQuoteMapper, Mockito.times(3)).selectOne(any(LambdaQueryWrapper.class));*/
    }

    @Test
    public void testGetExchangeRateWhenBothSourcesReturnNull() {
        // Setup and Mock behavior
//        when(assetLastQuoteMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null, null);
//
//        // Execute
//        BigDecimal rate = assetLastQuoteService.getExchangeRate("UNKNOWN", true);
//
//        // Verify
//        assertEquals(BigDecimal.ZERO, rate);
//        Mockito.verify(assetLastQuoteMapper, Mockito.times(2)).selectOne(any(LambdaQueryWrapper.class));
    }
}
