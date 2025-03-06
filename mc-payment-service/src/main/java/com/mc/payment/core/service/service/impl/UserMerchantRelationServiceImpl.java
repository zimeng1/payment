package com.mc.payment.core.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.core.service.entity.UserMerchantRelationEntity;
import com.mc.payment.core.service.mapper.UserMerchantRelationMapper;
import com.mc.payment.core.service.service.IUserMerchantRelationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Conor
 * @description 针对表【mcp_user_merchant_relation(系统账号所属商户表)】的数据库操作Service实现
 * @createDate 2024-06-03 17:34:19
 */
@Service
public class UserMerchantRelationServiceImpl extends ServiceImpl<UserMerchantRelationMapper, UserMerchantRelationEntity>
        implements IUserMerchantRelationService {
    /**
     * 更新关系
     * 新增的关系会增加,减少的关系会删除,关系不变则不做任何处理
     *
     * @param userId
     * @param merchantIds
     */
    @Override
    @Transactional
    public void updateRelation(String userId, List<String> merchantIds) {
        if (CollUtil.isEmpty(merchantIds)) {
            this.remove(Wrappers.lambdaQuery(UserMerchantRelationEntity.class)
                    .eq(UserMerchantRelationEntity::getUserId, userId)
            );
        }
        // 查询旧关系
        List<UserMerchantRelationEntity> oldList = baseMapper.selectList(Wrappers.lambdaQuery(UserMerchantRelationEntity.class)
                .eq(UserMerchantRelationEntity::getUserId, userId));
        List<String> oldIds = oldList.stream().map(UserMerchantRelationEntity::getMerchantId).collect(Collectors.toList());

        // 计算需要新增的商户id集合
        List<String> addList = merchantIds.stream().filter(item -> !oldIds.contains(item)).collect(Collectors.toList());
        // 计算需要删除的商户id集合
        List<String> removeList = oldIds.stream().filter(item -> !merchantIds.contains(item)).collect(Collectors.toList());

        List<UserMerchantRelationEntity> list = new ArrayList<>();
        for (String id : addList) {
            UserMerchantRelationEntity entity = new UserMerchantRelationEntity();
            entity.setUserId(userId);
            entity.setMerchantId(id);
            list.add(entity);
        }
        if (CollUtil.isNotEmpty(list)) {
            this.saveBatch(list);
        }
        if (CollUtil.isNotEmpty(removeList)) {
            this.remove(Wrappers.lambdaQuery(UserMerchantRelationEntity.class)
                    .eq(UserMerchantRelationEntity::getUserId, userId)
                    .in(UserMerchantRelationEntity::getMerchantId, removeList)
            );
        }
    }

    @Override
    public List<String> queryMerchantIds(String userId) {
        return this.list(Wrappers.lambdaQuery(UserMerchantRelationEntity.class).eq(UserMerchantRelationEntity::getUserId, userId))
                .stream()
                .map(UserMerchantRelationEntity::getMerchantId)
                .collect(Collectors.toList());
    }
}




