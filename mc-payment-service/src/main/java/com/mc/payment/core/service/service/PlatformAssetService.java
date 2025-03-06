package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.PlatformAssetEntity;
import com.mc.payment.core.service.model.req.platform.PlatformAssetListReq;
import com.mc.payment.core.service.model.req.platform.PlatformAssetPageReq;
import com.mc.payment.core.service.model.req.platform.PlatformAssetSaveReq;

import java.util.List;

/**
 * @author Conor
 * @description 针对表【mcp_platform_asset(平台资产)】的数据库操作Service
 * @createDate 2024-11-04 14:21:48
 */
public interface PlatformAssetService extends IService<PlatformAssetEntity> {

    BasePageRsp<PlatformAssetEntity> selectPage(PlatformAssetPageReq req);

    String save(PlatformAssetSaveReq req);

    List<PlatformAssetEntity> list(PlatformAssetListReq req);

    /**
     * 查询是否启用
     *
     * @param assetType
     * @param assetName
     * @return
     */
    boolean isActivated(Integer assetType, String assetName);
}
