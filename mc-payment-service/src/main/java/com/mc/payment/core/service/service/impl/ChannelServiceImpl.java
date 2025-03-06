package com.mc.payment.core.service.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.ChannelEntity;
import com.mc.payment.core.service.mapper.ChannelMapper;
import com.mc.payment.core.service.model.enums.StatusEnum;
import com.mc.payment.core.service.model.req.ChannelPageReq;
import com.mc.payment.core.service.model.rsp.ChannelPageRsp;
import com.mc.payment.core.service.service.IChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 通道配置服务实现类
 * </p>
 *
 * @author conor
 * @since 2024-01-31 18:46:14
 */
@Service
public class ChannelServiceImpl extends ServiceImpl<ChannelMapper, ChannelEntity> implements IChannelService {

    @Autowired
    public ChannelServiceImpl(ChannelMapper channelMapper) {
        this.baseMapper = channelMapper;
    }


    @Override
    public BasePageRsp<ChannelPageRsp> page(ChannelPageReq req) {
        Page<ChannelPageRsp> page = new Page<>(req.getCurrent(), req.getSize());
        page = (Page<ChannelPageRsp>) baseMapper.selectPage(page, req);
        return BasePageRsp.valueOf(page);
    }


    @Override
    public RetResult<Boolean> removeById(String id) {
        ChannelEntity entity = getById(id);
        if (entity == null) {
            return RetResult.error("该数据不存在");
        }
        if (entity.getStatus() != StatusEnum.DISABLE.getCode()) {
            return RetResult.error("禁用状态的才可删除");
        }
        return RetResult.data(super.removeById(id));
    }

    /**
     * 是否处于激活状态
     *
     * @param ids 通道id集合
     * @return true->全部处于激活状态
     */
    @Override
    public boolean checkActive(List<String> ids) {
        Long count = baseMapper.selectCount(Wrappers.lambdaQuery(ChannelEntity.class)
                .in(BaseNoLogicalDeleteEntity::getId, ids)
                .eq(ChannelEntity::getStatus, StatusEnum.ACTIVE.getCode()));
        return count == ids.size();
    }

    /**
     * 根据通道id集合查询资产名称集合
     *
     * @param channelIds
     * @return
     */
    @Override
    public List<String> queryChannelNameList(List<String> channelIds) {
        List<ChannelEntity> entityList = this.listByIds(channelIds);
        return entityList != null ? entityList.stream().map(ChannelEntity::getName).collect(Collectors.toList()) : new ArrayList<>();
    }
}
