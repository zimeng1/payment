package com.mc.payment.core.service.service;

import com.mc.payment.core.service.entity.UserMerchantRelationEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Conor
* @description 针对表【mcp_user_merchant_relation(系统账号所属商户表)】的数据库操作Service
* @createDate 2024-06-03 17:34:19
*/
public interface IUserMerchantRelationService extends IService<UserMerchantRelationEntity> {
    /**
     * 更新关系
     * 新增的关系会增加,减少的关系会删除,关系不变则不做任何处理
     *
     * @param userId
     * @param merchantIds
     */
    void updateRelation(String userId, List<String> merchantIds);

    List<String> queryMerchantIds(String userId);
}
