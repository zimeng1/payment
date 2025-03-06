package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.ChannelAssetEntity;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.req.ChannelAssetPageReq;
import com.mc.payment.core.service.model.req.ChannelAssetSaveReq;

import java.util.List;

/**
 * <p>
 * 通道支持的资产服务类
 * </p>
 *
 * @author conor
 * @since 2024-04-24 14:43:44
 */
@Deprecated
public interface IChannelAssetService extends IService<ChannelAssetEntity> {


    ChannelAssetEntity getOne(ChannelSubTypeEnum channelSubTypeEnum, String assetName, String netProtocol);

    ChannelAssetEntity getOne(Integer channelSubType, String assetName, String netProtocol);

    ChannelAssetEntity getOne(Integer channelSubType, String channelAssetName);


    List<ChannelAssetEntity> getList(Integer channelSubType);

    BasePageRsp<ChannelAssetEntity> page(ChannelAssetPageReq req);

    RetResult<Boolean> removeById(String id);

    RetResult<String> save(ChannelAssetSaveReq req);


    List<ChannelAssetEntity> getListByTypeList(List<Integer> channelSubTypeList);

    List<ChannelAssetEntity> getChannelAssetNameList(ChannelAssetPageReq req);

    List<ChannelAssetEntity> getFireBlocksSupportedAssetList();


    String getAssetNameByChannelAssetName(Integer channelSubType, String assetName, String channelAssetName, String netProtocol);


    List<String> getAssetNameList(List<Integer> typeList);

}
