package com.mc.payment.core.service.service.impl;

import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.ChannelEntity;
import com.mc.payment.core.service.mapper.ChannelMapper;
import com.mc.payment.core.service.model.rsp.ChannelPageRsp;
import com.mc.payment.core.service.model.enums.StatusEnum;
import com.mc.payment.core.service.model.req.ChannelPageReq;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChannelServiceImplTest {
    @InjectMocks
    private ChannelServiceImpl channelService;
    @Mock
    private ChannelMapper channelMapper;

    @Test
    void page() {
        ChannelPageReq req = new ChannelPageReq();
        req.setCurrent(1L);
        BasePageRsp<ChannelPageRsp> page = channelService.page(req);
        Assertions.assertEquals(1L, page.getCurrent());
    }

    @Test
    void removeById() {
        when(channelMapper.selectById("1")).thenReturn(null);
        RetResult<Boolean> result = channelService.removeById("1");

        Assertions.assertEquals("该数据不存在", result.getMsg());


        ChannelEntity channelEntity = new ChannelEntity();
        channelEntity.setStatus(StatusEnum.ACTIVE.getCode());
        when(channelMapper.selectById("2")).thenReturn(channelEntity);
        RetResult<Boolean> result2 = channelService.removeById("2");

        Assertions.assertEquals("禁用状态的才可删除", result2.getMsg());
    }

//    @Test
//    void checkActive() {
//        ChannelEntity channelEntity = new ChannelEntity();
//        channelEntity.setStatus(StatusEnum.ACTIVE.getCode());
//        when(channelMapper.selectById("1")).thenReturn(channelEntity);
//        boolean b = channelService.checkActive("1");
//
//        Assertions.assertEquals(true, b);
//    }

    @Test
    void testCheckActive() {
        when(channelMapper.selectCount(any())).thenReturn(2L);
        List<String> ids = new ArrayList<>();
        ids.add("1");
        ids.add("2");
        boolean b = channelService.checkActive(ids);

        Assertions.assertEquals(true, b);
    }

    @Test
    void queryChannelNameList() {
        List<String> ids = new ArrayList<>();
        when(channelMapper.selectBatchIds(ids)).thenReturn(null);

        List<String> list = channelService.queryChannelNameList(ids);

        Assertions.assertEquals(new ArrayList<>(), list);
    }
}