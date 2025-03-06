package com.mc.payment.core.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mc.payment.core.service.entity.AccountAssetRelationEntity;
import com.mc.payment.core.service.mapper.AccountAssetRelationMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountAssetRelationServiceImplTest {

    @InjectMocks
    private AccountAssetRelationServiceImpl accountAssetRelationService;
    @Mock
    private AccountAssetRelationMapper baseMapper;

    @Captor
    ArgumentCaptor<List<AccountAssetRelationEntity>> addListArgumentCaptor;
    @Captor
    ArgumentCaptor<LambdaQueryWrapper<AccountAssetRelationEntity>> removeListArgumentCaptor;

    @Test
    void updateRelation() throws IllegalAccessException {
        List<AccountAssetRelationEntity> oldList = new ArrayList<>();
        AccountAssetRelationEntity entity_1 = new AccountAssetRelationEntity();
        entity_1.setAssetId("1");
        oldList.add(entity_1);
        AccountAssetRelationEntity entity_2 = new AccountAssetRelationEntity();
        entity_2.setAssetId("2");
        oldList.add(entity_2);
        AccountAssetRelationEntity entity_3 = new AccountAssetRelationEntity();
        entity_3.setAssetId("5");
        oldList.add(entity_3);
        when(baseMapper.selectList(any())).thenReturn(oldList);

        // mock accountAssetRelationService.saveBatch
        AccountAssetRelationServiceImpl accountAssetRelationServiceSpy = Mockito.spy(accountAssetRelationService);
        Mockito.doReturn(true).when(accountAssetRelationServiceSpy).saveBatch(Mockito.any());
        Mockito.doReturn(true).when(accountAssetRelationServiceSpy).remove(Mockito.any());



        List<String> assetIds = new ArrayList<>();
        assetIds.add("1");
        assetIds.add("2");
        assetIds.add("3");
        accountAssetRelationServiceSpy.updateRelation("accountId", assetIds);


        verify(accountAssetRelationServiceSpy).saveBatch(addListArgumentCaptor.capture());
        List<AccountAssetRelationEntity> value = addListArgumentCaptor.getValue();

        List<AccountAssetRelationEntity> addList = new ArrayList<>();
        AccountAssetRelationEntity addEntity = new AccountAssetRelationEntity();
        addEntity.setAssetId("3");
        addEntity.setAccountId("accountId");
        addList.add(addEntity);

        Assertions.assertEquals(addList, value);

        verify(accountAssetRelationServiceSpy).remove(removeListArgumentCaptor.capture());
        LambdaQueryWrapper<AccountAssetRelationEntity> removeListArgumentCaptorValue = removeListArgumentCaptor.getValue();


        ISqlSegment iSqlSegment = removeListArgumentCaptorValue.getExpression().getNormal().get(6);
        Field[] declaredFields = iSqlSegment.getClass().getDeclaredFields();
        Object o = null;
        for (Field f : declaredFields) {
            f.setAccessible(true);
            if ("arg$2".equals(f.getName())) {
                 o = f.get(iSqlSegment);
            }
        }

        List<String> removeList = new ArrayList<>();
        removeList.add("5");
        Assertions.assertEquals(removeList, o);

    }

    @Test
    void queryAssetIds() {
        List<String> list = accountAssetRelationService.queryAssetIds("accountId");
        Assertions.assertEquals(new ArrayList<>(), list);
    }
}