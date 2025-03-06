package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.core.service.entity.AccountAssetRelationEntity;

import java.util.List;

/**
 * <p>
 * 账号资产关系表 服务类
 * </p>
 *
 * @author conor
 * @since 2024-02-19 16:39:16
 */
public interface IAccountAssetRelationService extends IService<AccountAssetRelationEntity> {
    /**
     * 更新关系
     * 新增的关系会增加,减少的关系会删除,关系不变则不做任何处理
     *
     * @param accountId
     * @param assetIds
     */
    void updateRelation(String accountId, List<String> assetIds);

    List<String> queryAssetIds(String accountId);
}
