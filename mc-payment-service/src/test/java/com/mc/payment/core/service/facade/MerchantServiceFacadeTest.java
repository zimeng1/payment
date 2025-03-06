package com.mc.payment.core.service.facade;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mc.payment.core.service.entity.AccountEntity;
import com.mc.payment.core.service.entity.MerchantChannelRelationEntity;
import com.mc.payment.core.service.entity.MerchantEntity;
import com.mc.payment.core.service.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class MerchantServiceFacadeTest {
//
//    @InjectMocks
//    private MerchantServiceFacade merchantServiceFacade;
//
//    @Mock
//    private IMerchantService merchantService;
//
//    @Mock
//    private IAccountService accountService;
//
//    @Mock
//    private IWalletService walletService;
//
//    @Mock
//    private IJobPlanService jobPlanService;
//
//    @Mock
//    private IMerchantChannelRelationService merchantChannelRelationService;
//
////    @BeforeEach
////    public void setUp() {
////        MockitoAnnotations.openMocks(this);
////    }
//
//
//    @Test
//    void shouldNotGenerateEmailJobWhenReserveIsSufficient() {
//        MerchantEntity merchantEntity = new MerchantEntity();
//        merchantEntity.setId("merchantId");
//        Mockito.lenient().when(merchantService.list()).thenReturn(Arrays.asList(merchantEntity));
//
//        AccountEntity accountEntity = new AccountEntity();
//        accountEntity.setId("accountId");
//        accountEntity.setMerchantId("merchantId");
//        Mockito.lenient().when(accountService.list(anyString(), anyInt())).thenReturn(Arrays.asList(accountEntity));
//
//        MerchantChannelRelationEntity merchantChannelRelationEntity = new MerchantChannelRelationEntity();
//        merchantChannelRelationEntity.setReserveRatio(new BigDecimal("0.5"));
//        merchantChannelRelationEntity.setReserveFundType(0);
//        Mockito.lenient().when(merchantChannelRelationService.list(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList(merchantChannelRelationEntity));
//
//        Mockito.lenient().when(walletService.queryBalanceSum(any())).thenReturn(new BigDecimal("1"));
//
//        merchantServiceFacade.scanMerchantReserveRatioJob();
//
//        verify(jobPlanService, never()).addJobPlan(any(), any());
//    }
//
//    @Test
//    void shouldGenerateEmailJobWhenReserveIsInsufficient() {
//        MerchantEntity merchantEntity = new MerchantEntity();
//        merchantEntity.setId("merchantId");
//        Mockito.lenient().when(merchantService.list()).thenReturn(Arrays.asList(merchantEntity));
//
//        AccountEntity accountEntity = new AccountEntity();
//        accountEntity.setId("accountId");
//        accountEntity.setMerchantId("merchantId");
//        Mockito.lenient().when(accountService.list(anyString(), anyInt())).thenReturn(Arrays.asList(accountEntity));
//
//        MerchantChannelRelationEntity merchantChannelRelationEntity = new MerchantChannelRelationEntity();
//        merchantChannelRelationEntity.setReserveRatio(new BigDecimal("0.5"));
//        merchantChannelRelationEntity.setReserveFundType(0);
//        Mockito.lenient().when(merchantChannelRelationService.list(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList(merchantChannelRelationEntity));
//
//        Mockito.lenient().when(walletService.queryBalanceSum(any())).thenReturn(new BigDecimal("0.4"));
//
//        merchantServiceFacade.scanMerchantReserveRatioJob();
//
//        // 检查 jobPlanService.addJobPlan(<any>, <any>) 是否被调用
//        verify(jobPlanService, times(1)).addJobPlan(any(), any());
//    }
//
//    @Test
//    void shouldNotGenerateEmailJobWhenNoMerchants() {
//        Mockito.lenient().when(merchantService.list()).thenReturn(Collections.emptyList());
//
//        merchantServiceFacade.scanMerchantReserveRatioJob();
//
//        verify(jobPlanService, never()).addJobPlan(any(), any());
//    }
}