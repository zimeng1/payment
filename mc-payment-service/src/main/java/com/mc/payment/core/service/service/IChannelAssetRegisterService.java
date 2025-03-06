package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.ChannelAssetRegisterEntity;
import com.mc.payment.core.service.model.req.ChannelAssetRegisterPageReq;
import com.mc.payment.core.service.model.req.ChannelAssetRegisterSaveReq;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>
 * 通道资产注册表(用于支持新币种) 服务类
 * </p>
 *
 * @author Marty
 * @since 2024-06-20 14:59:26
 */
public interface IChannelAssetRegisterService extends IService<ChannelAssetRegisterEntity> {

    BasePageRsp<ChannelAssetRegisterEntity> page(@RequestBody ChannelAssetRegisterPageReq req);

    List<ChannelAssetRegisterEntity> getList();

    RetResult<String> registerAsset(ChannelAssetRegisterSaveReq req);


}
