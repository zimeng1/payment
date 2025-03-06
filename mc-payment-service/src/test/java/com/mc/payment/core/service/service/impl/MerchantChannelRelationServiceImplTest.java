package com.mc.payment.core.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mc.payment.core.service.entity.MerchantChannelRelationEntity;
import com.mc.payment.core.service.mapper.MerchantChannelRelationMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class MerchantChannelRelationServiceImplTest {
    @InjectMocks
    private MerchantChannelRelationServiceImpl merchantChannelRelationService;
    @Mock
    private MerchantChannelRelationMapper merchantChannelRelationMapper;


    @Captor
    ArgumentCaptor<List<MerchantChannelRelationEntity>> addListArgumentCaptor;
    @Captor
    ArgumentCaptor<LambdaQueryWrapper<MerchantChannelRelationEntity>> removeListArgumentCaptor;

//    @Test
//    void updateRelation() throws IllegalAccessException {
//        List<MerchantChannelRelationEntity> oldList = new ArrayList<>();
//        MerchantChannelRelationEntity entity_1 = new MerchantChannelRelationEntity();
//        entity_1.setChannelId("1");
//        oldList.add(entity_1);
//        MerchantChannelRelationEntity entity_2 = new MerchantChannelRelationEntity();
//        entity_2.setChannelId("2");
//        oldList.add(entity_2);
//        MerchantChannelRelationEntity entity_3 = new MerchantChannelRelationEntity();
//        entity_3.setChannelId("5");
//        oldList.add(entity_3);
//        when(merchantChannelRelationMapper.selectList(any())).thenReturn(oldList);
//
//
//        MerchantChannelRelationServiceImpl merchantChannelRelationServiceSpy = Mockito.spy(merchantChannelRelationService);
//        Mockito.doReturn(true).when(merchantChannelRelationServiceSpy).saveBatch(Mockito.any());
//        Mockito.doReturn(true).when(merchantChannelRelationServiceSpy).remove(Mockito.any());
//
//
//
//        List<String> assetIds = new ArrayList<>();
//        assetIds.add("1");
//        assetIds.add("2");
//        assetIds.add("3");
//        merchantChannelRelationServiceSpy.updateRelation("id", assetIds);
//
//
//        verify(merchantChannelRelationServiceSpy).saveBatch(addListArgumentCaptor.capture());
//        List<MerchantChannelRelationEntity> value = addListArgumentCaptor.getValue();
//
//        List<MerchantChannelRelationEntity> addList = new ArrayList<>();
//        MerchantChannelRelationEntity addEntity = new MerchantChannelRelationEntity();
//        addEntity.setChannelId("3");
//        addEntity.setMerchantId("id");
//        addList.add(addEntity);
//
//        Assertions.assertEquals(addList, value);
//
//        verify(merchantChannelRelationServiceSpy).remove(removeListArgumentCaptor.capture());
//        LambdaQueryWrapper<MerchantChannelRelationEntity> removeListArgumentCaptorValue = removeListArgumentCaptor.getValue();
//
//
//        ISqlSegment iSqlSegment = removeListArgumentCaptorValue.getExpression().getNormal().get(6);
//        Field[] declaredFields = iSqlSegment.getClass().getDeclaredFields();
//        Object o = null;
//        for (Field f : declaredFields) {
//            f.setAccessible(true);
//            if ("arg$2".equals(f.getName())) {
//                o = f.get(iSqlSegment);
//            }
//        }
//
//        List<String> removeList = new ArrayList<>();
//        removeList.add("5");
//        Assertions.assertEquals(removeList, o);
//    }
//
//    @Test
//    void queryChannelIds() {
//        List<String> list = merchantChannelRelationService.queryChannelIds("accountId");
//        Assertions.assertEquals(new ArrayList<>(), list);
//    }
}