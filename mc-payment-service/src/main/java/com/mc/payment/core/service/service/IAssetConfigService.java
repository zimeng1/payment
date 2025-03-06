package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.AssetConfigEntity;
import com.mc.payment.core.service.model.req.AssetConfigPageReq;
import com.mc.payment.core.service.model.req.AssetConfigSaveReq;
import com.mc.payment.core.service.model.req.AssetListByParamReq;
import com.mc.payment.core.service.model.rsp.AssetConfigListRsp;
import com.mc.payment.core.service.model.rsp.AssetConfigPageRsp;

import java.util.List;

/**
 * <p>
 * 资产配置表 服务类
 * </p>
 *
 * @author conor
 * @since 2024-01-30 17:17:25
 */
@Deprecated
public interface IAssetConfigService extends IService<AssetConfigEntity> {
    BasePageRsp<AssetConfigPageRsp> page(AssetConfigPageReq req);

    RetResult<String> save(AssetConfigSaveReq req);

    AssetConfigEntity getOne(Integer assetType, String assetName, String netProtocol);

    List<AssetConfigListRsp> assetListByParam(AssetListByParamReq req);


}
