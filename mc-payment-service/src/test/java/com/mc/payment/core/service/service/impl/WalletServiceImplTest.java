package com.mc.payment.core.service.service.impl;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.AccountEntity;
import com.mc.payment.core.service.entity.WalletEntity;
import com.mc.payment.core.service.mapper.WalletMapper;
import com.mc.payment.core.service.model.req.*;
import com.mc.payment.core.service.model.rsp.*;
import com.mc.payment.core.service.service.IAccountService;
import com.mc.payment.core.service.service.IAssetLastQuoteService;
import com.mc.payment.fireblocksapi.FireBlocksAPI;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Marty
 * @since 2024/6/17 17:53
 */
@ExtendWith(MockitoExtension.class)
public class WalletServiceImplTest {
//
//    @InjectMocks
//    private WalletServiceImpl walletService;
//
//    @Mock
//    private WalletMapper baseMapper;
//
//    @Mock
//    private IAccountService accountService;
//
//    @Mock
//    private IAssetLastQuoteService assetLastQuoteService;
//
//    @Mock
//    private FireBlocksAPI fireBlocksAPI;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//    }
//
//    @Test
//    public void testPageWithNotEmptyRecords() {
//        // Given
//        WalletPageReq req = new WalletPageReq();
//        req.setCurrent(1);
//        req.setSize(10);
//
//        WalletPageRsp record = new WalletPageRsp();
//        record.setAccountId("123");
//        record.setAssetName("BTC");
//
//        Page<WalletPageRsp> page = new Page<>(1, 10);
//        page.setRecords(Collections.singletonList(record));
//
//
//        when(baseMapper.page(any(Page.class), any(WalletPageReq.class))).thenReturn(page);
//        when(baseMapper.queryAccountIdAndAssetNameSumBalance(anySet(), anySet()))
//                .thenReturn(Collections.singletonList(new WalletAssetSumBalanceRsp("123", "BTC", new BigDecimal("100"))));
//
//        // When
//        BasePageRsp<WalletPageRsp> response = walletService.page(req);
//        // Then
//        assertNotNull(response);
//        assertTrue(response.getRecords().size() > 0);
//        assertEquals("123", response.getRecords().get(0).getAccountId());
//        assertEquals("BTC", response.getRecords().get(0).getAssetName());
//        assertEquals(new BigDecimal("100"), response.getRecords().get(0).getSumBalance());
//        verify(baseMapper, times(1)).page(any(Page.class), any(WalletPageReq.class));
//        verify(baseMapper, times(1)).queryAccountIdAndAssetNameSumBalance(anySet(), anySet());
//    }
//
//    @Test
//    public void testPageWithEmptyRecords() {
//        // Given
//        WalletPageReq req = new WalletPageReq();
//        req.setCurrent(1);
//        req.setSize(10);
//
//        Page<WalletPageRsp> page = new Page<>(1, 10);
//        page.setRecords(Collections.emptyList());
//
//        when(baseMapper.page(any(Page.class), any(WalletPageReq.class))).thenReturn(page);
//
//        // When
//        BasePageRsp<WalletPageRsp> response = walletService.page(req);
//
//        // Then
//        assertNotNull(response);
//        assertTrue(response.getRecords().isEmpty());
//        verify(baseMapper, times(1)).page(any(Page.class), any(WalletPageReq.class));
//        verify(baseMapper, never()).queryAccountIdAndAssetNameSumBalance(anySet(), anySet());
//    }
//
//
//    @Test
//    public void saveWalletWhenWalletDoesNotExist() {
//        // Given
//        WalletSaveReq req = new WalletSaveReq();
//        req.setAccountId("123");
//        req.setAssetName("BTC");
//        req.setWalletAddress("1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa");
//
//        when(walletService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);
//        when(accountService.getById(anyString())).thenReturn(new AccountEntity());
//
//        // When
//        RetResult<String> result = walletService.save(req);
//
//        // Then
//        assertNotNull(result);
//        assertTrue(result.isSuccess());
//        verify(baseMapper, times(1)).insert(any(WalletEntity.class));
//    }
//
//    @Test
//    public void saveWalletWhenWalletAlreadyExists() {
//        // Given
//        WalletSaveReq req = new WalletSaveReq();
//        req.setAccountId("123");
//        req.setAssetName("BTC");
//        req.setWalletAddress("1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa");
//
//        when(walletService.count(any(LambdaQueryWrapper.class))).thenReturn(1L);
//
//        // When
//        RetResult<String> result = walletService.save(req);
//
//        // Then
//        assertNotNull(result);
//        assertFalse(result.isSuccess());
//        assertEquals("该数据已存在", result.getMsg());
//        verify(baseMapper, never()).insert(any(WalletEntity.class));
//    }
//
//    @Test
//    public void removeWalletWhenWalletExists() {
//        // Given
//        String id = "123";
//        WalletServiceImpl walletServiceSpy = Mockito.spy(walletService);
//        when(walletServiceSpy.getById("id")).thenReturn(null);
//        RetResult<Boolean> result = walletServiceSpy.remove("id");
//
//        Assertions.assertEquals("该数据不存在",result.getMsg());
//    }
//
//    @Test
//    public void removeWalletWhenWalletDoesNotExist() {
//        // Given
//        String id = "123";
//
//        when(baseMapper.selectById(anyString())).thenReturn(null);
//
//        // When
//        RetResult<Boolean> result = walletService.remove(id);
//
//        // Then
//        assertNotNull(result);
//        assertFalse(result.isSuccess());
//        assertEquals("该数据不存在", result.getMsg());
//        verify(baseMapper, never()).deleteById(anyString());
//    }
}
