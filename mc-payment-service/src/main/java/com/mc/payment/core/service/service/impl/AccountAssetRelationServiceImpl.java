package com.mc.payment.core.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.core.service.entity.AccountAssetRelationEntity;
import com.mc.payment.core.service.mapper.AccountAssetRelationMapper;
import com.mc.payment.core.service.service.IAccountAssetRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 账号资产关系表 服务实现类
 * </p>
 *
 * @author conor
 * @since 2024-02-19 16:39:16
 */
@Service
public class AccountAssetRelationServiceImpl extends ServiceImpl<AccountAssetRelationMapper, AccountAssetRelationEntity> implements IAccountAssetRelationService {

    @Autowired
    public AccountAssetRelationServiceImpl(AccountAssetRelationMapper accountAssetRelationMapper) {
        this.baseMapper = accountAssetRelationMapper;
    }

    @Override
    @Transactional
    public void updateRelation(String accountId, List<String> assetIds) {
        // 查询旧关系
        List<AccountAssetRelationEntity> oldList = baseMapper.selectList(Wrappers.lambdaQuery(AccountAssetRelationEntity.class)
                .eq(AccountAssetRelationEntity::getAccountId, accountId));
        List<String> oldIds = oldList.stream().map(AccountAssetRelationEntity::getAssetId).collect(Collectors.toList());

        // 计算需要新增的资产id集合
        List<String> addList = assetIds.stream().filter(item -> !oldIds.contains(item)).collect(Collectors.toList());
        // 计算需要删除的资产id集合
        List<String> removeList = oldIds.stream().filter(item -> !assetIds.contains(item)).collect(Collectors.toList());

        List<AccountAssetRelationEntity> list = new ArrayList<>();
        for (String id : addList) {
            AccountAssetRelationEntity entity = new AccountAssetRelationEntity();
            entity.setAccountId(accountId);
            entity.setAssetId(id);
            list.add(entity);
        }
        if (CollUtil.isNotEmpty(list)) {
            this.saveBatch(list);
        }
        if (CollUtil.isNotEmpty(removeList)) {
            this.remove(Wrappers.lambdaQuery(AccountAssetRelationEntity.class)
                    .eq(AccountAssetRelationEntity::getAccountId, accountId)
                    .in(AccountAssetRelationEntity::getAssetId, removeList)
            );
        }
    }

    @Override
    public List<String> queryAssetIds(String accountId) {
        return this.list(Wrappers.lambdaQuery(AccountAssetRelationEntity.class).eq(AccountAssetRelationEntity::getAccountId, accountId))
                .stream()
                .map(AccountAssetRelationEntity::getAssetId)
                .collect(Collectors.toList());
    }
}
