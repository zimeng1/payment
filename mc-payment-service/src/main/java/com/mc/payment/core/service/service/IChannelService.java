package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.ChannelEntity;
import com.mc.payment.core.service.model.req.ChannelPageReq;
import com.mc.payment.core.service.model.rsp.ChannelPageRsp;

import java.util.List;

/**
 * <p>
 * 通道配置服务类
 * </p>
 *
 * @author conor
 * @since 2024-01-31 18:46:14
 */
public interface IChannelService extends IService<ChannelEntity> {
    BasePageRsp<ChannelPageRsp> page(ChannelPageReq req);


    RetResult<Boolean> removeById(String id);


    /**
     * 是否处于激活状态
     *
     * @param ids 通道id集合
     * @return true->全部处于激活状态
     */
    boolean checkActive(List<String> ids);

    /**
     * 根据通道id集合查询资产名称集合
     *
     * @param channelIds
     * @return
     */
    List<String> queryChannelNameList(List<String> channelIds);


}
