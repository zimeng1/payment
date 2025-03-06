package com.mc.payment.core.service.manager;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.mc.payment.core.service.entity.ChannelCostAssetEntity;
import com.mc.payment.core.service.entity.ChannelCostEntity;
import com.mc.payment.core.service.model.enums.CostLimitEnum;
import com.mc.payment.core.service.model.enums.CostTypeEnum;
import com.mc.payment.core.service.model.enums.RoundMethodEnum;
import com.mc.payment.core.service.service.ChannelCostAssetService;
import com.mc.payment.core.service.service.IChannelCostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChannelCostManagerImplTest {

    @Mock
    private IChannelCostService channelCostService;

    @Mock
    private ChannelCostAssetService channelCostAssetService;

    @InjectMocks
    private ChannelCostManagerImpl channelCostManager;

    private ChannelCostAssetEntity channelCostAssetEntity;
    private ChannelCostEntity channelCostEntity;

    @BeforeEach
    public void setUp() {
        channelCostAssetEntity = new ChannelCostAssetEntity();
        channelCostEntity = new ChannelCostEntity();
    }

    @Test
    public void channelCostCalculator_ChannelCostAssetEntityIsNull_ReturnsZero() {

        LambdaQueryChainWrapper<ChannelCostAssetEntity> lambdaQueryWrapper = mock(LambdaQueryChainWrapper.class);
        when(channelCostAssetService.lambdaQuery()).thenReturn(lambdaQueryWrapper);
        when(lambdaQueryWrapper.eq(any(), any())).thenReturn(lambdaQueryWrapper);
        when(lambdaQueryWrapper.one()).thenReturn(null);

        BigDecimal result = channelCostManager.channelCostCalculator(0, 0, "asset", "protocol", BigDecimal.TEN, BigDecimal.ONE);

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    public void channelCostCalculator_ChannelCostEntityIsNull_ReturnsZero() {

        LambdaQueryChainWrapper<ChannelCostAssetEntity> lambdaQueryWrapper = mock(LambdaQueryChainWrapper.class);
        when(channelCostAssetService.lambdaQuery()).thenReturn(lambdaQueryWrapper);
        when(lambdaQueryWrapper.eq(any(), any())).thenReturn(lambdaQueryWrapper);
        when(lambdaQueryWrapper.one()).thenReturn(channelCostAssetEntity);
        channelCostAssetEntity.setCostId("costId");

        when(channelCostService.getById("costId")).thenReturn(null);

        BigDecimal result = channelCostManager.channelCostCalculator(0, 0, "asset", "protocol", BigDecimal.TEN, BigDecimal.ONE);

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    public void channelCostCalculator_CostTypeIsPerTransaction_ReturnsCost() {

        LambdaQueryChainWrapper<ChannelCostAssetEntity> lambdaQueryWrapper = mock(LambdaQueryChainWrapper.class);
        when(channelCostAssetService.lambdaQuery()).thenReturn(lambdaQueryWrapper);
        when(lambdaQueryWrapper.eq(any(), any())).thenReturn(lambdaQueryWrapper);
        when(lambdaQueryWrapper.one()).thenReturn(channelCostAssetEntity);

        channelCostAssetEntity.setCostId("costId");
        channelCostEntity.setCost(BigDecimal.valueOf(5));
        channelCostEntity.setCostType(CostTypeEnum.ITEM_0.getCode());

        when(channelCostService.getById("costId")).thenReturn(channelCostEntity);

        BigDecimal result = channelCostManager.channelCostCalculator(0, 0, "asset", "protocol", BigDecimal.TEN, BigDecimal.ONE);

        assertEquals(BigDecimal.valueOf(5), result);
    }

    @Test
    public void channelCostCalculator_CostTypeIsPerRate_CalculatesCost() {

        LambdaQueryChainWrapper<ChannelCostAssetEntity> lambdaQueryWrapper = mock(LambdaQueryChainWrapper.class);
        when(channelCostAssetService.lambdaQuery()).thenReturn(lambdaQueryWrapper);
        when(lambdaQueryWrapper.eq(any(), any())).thenReturn(lambdaQueryWrapper);
        when(lambdaQueryWrapper.one()).thenReturn(channelCostAssetEntity);

        channelCostAssetEntity.setCostId("costId");
        channelCostEntity.setCost(BigDecimal.valueOf(2)); // 费率为2%
        channelCostEntity.setCostType(CostTypeEnum.ITEM_1.getCode());
        channelCostEntity.setRoundMethod(RoundMethodEnum.ITEM_2.getCode());
        channelCostEntity.setCostLimitOption(CostLimitEnum.ITEM_0.getCode() + "," + CostLimitEnum.ITEM_1.getCode());
        channelCostEntity.setMinCostLimit(BigDecimal.valueOf(1));
        channelCostEntity.setMaxCostLimit(BigDecimal.valueOf(10));
        channelCostEntity.setCostPrecision(5);

        when(channelCostService.getById("costId")).thenReturn(channelCostEntity);

        BigDecimal result = channelCostManager.channelCostCalculator(0, 0, "asset", "protocol", BigDecimal.valueOf(100), BigDecimal.ONE);

        assertEquals(BigDecimal.valueOf(2), result); // 100 * 1 * 2% = 2
    }

    @Test
    public void channelCostCalculator_RoundMethodUp_CalculatesCost() {

        LambdaQueryChainWrapper<ChannelCostAssetEntity> lambdaQueryWrapper = mock(LambdaQueryChainWrapper.class);
        when(channelCostAssetService.lambdaQuery()).thenReturn(lambdaQueryWrapper);
        when(lambdaQueryWrapper.eq(any(), any())).thenReturn(lambdaQueryWrapper);
        when(lambdaQueryWrapper.one()).thenReturn(channelCostAssetEntity);

        channelCostAssetEntity.setCostId("costId");
        channelCostEntity.setRate(BigDecimal.valueOf(2.5)); // 费率为2.5%
        channelCostEntity.setCostType(CostTypeEnum.ITEM_1.getCode());
        channelCostEntity.setRoundMethod(RoundMethodEnum.ITEM_0.getCode());
        channelCostEntity.setCostLimitOption(CostLimitEnum.ITEM_0.getCode());
        channelCostEntity.setCostPrecision(5);

        when(channelCostService.getById("costId")).thenReturn(channelCostEntity);

        BigDecimal result = channelCostManager.channelCostCalculator(0, 0, "asset", "protocol", BigDecimal.valueOf(100), BigDecimal.ONE);

        assertEquals(new BigDecimal("2.50000"), result); // 向上取整
    }

    @Test
    public void channelCostCalculator_RoundMethodDown_CalculatesCost() {

        LambdaQueryChainWrapper<ChannelCostAssetEntity> lambdaQueryWrapper = mock(LambdaQueryChainWrapper.class);
        when(channelCostAssetService.lambdaQuery()).thenReturn(lambdaQueryWrapper);
        when(lambdaQueryWrapper.eq(any(), any())).thenReturn(lambdaQueryWrapper);
        when(lambdaQueryWrapper.one()).thenReturn(channelCostAssetEntity);

        channelCostAssetEntity.setCostId("costId");
        channelCostEntity.setRate(BigDecimal.valueOf(2.5)); // 费率为2.5%
        channelCostEntity.setCostType(CostTypeEnum.ITEM_1.getCode());
        channelCostEntity.setRoundMethod(RoundMethodEnum.ITEM_1.getCode());
        channelCostEntity.setCostLimitOption(CostLimitEnum.ITEM_0.getCode());
        channelCostEntity.setCostPrecision(2);

        when(channelCostService.getById("costId")).thenReturn(channelCostEntity);

        BigDecimal result = channelCostManager.channelCostCalculator(0, 0, "asset", "protocol",
                BigDecimal.valueOf(101), BigDecimal.ONE);

        assertEquals(new BigDecimal("2.52"), result); // 向下取整
    }

    @Test
    public void channelCostCalculator_ApplyMinCostLimit_CalculatesCost() {

        LambdaQueryChainWrapper<ChannelCostAssetEntity> lambdaQueryWrapper = mock(LambdaQueryChainWrapper.class);
        when(channelCostAssetService.lambdaQuery()).thenReturn(lambdaQueryWrapper);
        when(lambdaQueryWrapper.eq(any(), any())).thenReturn(lambdaQueryWrapper);
        when(lambdaQueryWrapper.one()).thenReturn(channelCostAssetEntity);

        channelCostAssetEntity.setCostId("costId");
        channelCostEntity.setCost(BigDecimal.valueOf(0.5)); // 费率为0.5%
        channelCostEntity.setCostType(CostTypeEnum.ITEM_1.getCode());
        channelCostEntity.setRoundMethod(RoundMethodEnum.ITEM_2.getCode());
        channelCostEntity.setCostLimitOption(CostLimitEnum.ITEM_0.getCode());
        channelCostEntity.setMinCostLimit(BigDecimal.valueOf(1));
        channelCostEntity.setCostPrecision(5);

        when(channelCostService.getById("costId")).thenReturn(channelCostEntity);

        BigDecimal result = channelCostManager.channelCostCalculator(0, 0, "asset", "protocol", BigDecimal.valueOf(100), BigDecimal.ONE);

        assertEquals(BigDecimal.valueOf(1), result); // 应用最低成本
    }

    @Test
    public void channelCostCalculator_ApplyMaxCostLimit_CalculatesCost() {

        LambdaQueryChainWrapper<ChannelCostAssetEntity> lambdaQueryWrapper = mock(LambdaQueryChainWrapper.class);
        when(channelCostAssetService.lambdaQuery()).thenReturn(lambdaQueryWrapper);
        when(lambdaQueryWrapper.eq(any(), any())).thenReturn(lambdaQueryWrapper);
        when(lambdaQueryWrapper.one()).thenReturn(channelCostAssetEntity);

        channelCostAssetEntity.setCostId("costId");
        channelCostEntity.setRate(BigDecimal.valueOf(15)); // 费率为15%
        channelCostEntity.setCostType(CostTypeEnum.ITEM_1.getCode());
        channelCostEntity.setRoundMethod(RoundMethodEnum.ITEM_2.getCode());
        channelCostEntity.setCostLimitOption(CostLimitEnum.ITEM_1.getCode());
        channelCostEntity.setMaxCostLimit(BigDecimal.valueOf(10));
        channelCostEntity.setCostPrecision(5);

        when(channelCostService.getById("costId")).thenReturn(channelCostEntity);

        BigDecimal result = channelCostManager.channelCostCalculator(0, 0, "asset", "protocol", BigDecimal.valueOf(100), BigDecimal.ONE);

        assertEquals(BigDecimal.valueOf(10), result); // 应用最高成本
    }
}
