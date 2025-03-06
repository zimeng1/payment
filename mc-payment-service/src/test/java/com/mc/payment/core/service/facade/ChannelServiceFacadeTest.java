package com.mc.payment.core.service.facade;

import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.model.req.ChannelSaveReq;
import com.mc.payment.core.service.model.req.ChannelUpdateReq;
import com.mc.payment.core.service.service.IChannelCostService;
import com.mc.payment.core.service.service.IChannelService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ChannelServiceFacadeTest {

    @InjectMocks
    private ChannelServiceFacade channelServiceFacade;
    @Mock
    private IChannelService channelService;
    @Mock
    private IChannelCostService channelCostService;


    @Test
    void save() {
        ChannelSaveReq req = new ChannelSaveReq();
        req.setName("通道名称");
        when(channelService.count(any())).thenReturn(1L);

        RetResult<String> result = channelServiceFacade.save(req);
        Assertions.assertEquals("通道名称,该名称已存在", result.getMsg());
    }


    @Test
    void save_1() {
        ChannelSaveReq req = new ChannelSaveReq();
        req.setName("通道名称");
        when(channelService.count(any())).thenReturn(0L);

        RetResult<String> result = channelServiceFacade.save(req);
        Assertions.assertEquals(200, result.getCode());
    }

    @Test
    void updateById() {
        when(channelService.getById(any())).thenReturn(null);

        ChannelUpdateReq req = new ChannelUpdateReq();
        req.setId("不存在id");
        RetResult<Boolean> result = channelServiceFacade.updateById(req);
        Assertions.assertEquals("该数据不存在", result.getMsg());
    }

}