package com.mc.payment.core.service.facade;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.entity.ChannelEntity;
import com.mc.payment.core.service.model.enums.StatusEnum;
import com.mc.payment.core.service.model.req.ChannelSaveReq;
import com.mc.payment.core.service.model.req.ChannelUpdateReq;
import com.mc.payment.core.service.service.IChannelCostService;
import com.mc.payment.core.service.service.IChannelService;
import com.xxl.job.core.context.XxlJobHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author conor
 * @since 2024/2/19 15:52:16
 */
@Slf4j
@Component
public class ChannelServiceFacade {
    private final IChannelService channelService;
    private final IChannelCostService channelCostService;

    @Autowired
    public ChannelServiceFacade(IChannelService channelService, IChannelCostService channelCostService) {
        this.channelService = channelService;
        this.channelCostService = channelCostService;
    }

    public RetResult<String> save(ChannelSaveReq req) {
        //  通道名称唯一
        if (channelService.count(Wrappers.lambdaQuery(ChannelEntity.class).eq(ChannelEntity::getName, req.getName())) > 0) {
            return RetResult.error(req.getName() + ",该名称已存在");
        }
        ChannelEntity entity = ChannelEntity.valueOf(req);

        // expirationDateEnd与当前时间比较, 如果比当前时间还早, 则设置为禁用状态
        if (entity.getExpirationDateEnd() != null && entity.getExpirationDateEnd().before(new Date())) {
            entity.setStatus(StatusEnum.DISABLE.getCode());
        }
        channelService.save(entity);
        return RetResult.data(entity.getId());
    }

    public RetResult<Boolean> updateById(ChannelUpdateReq req) {
        ChannelEntity entity = channelService.getById(req.getId());
        if (entity == null) {
            return RetResult.error("该数据不存在");
        }
        //  若通道已配置在通道成本上，需先撤销配置后才能修改。
//        if (channelCostService.checkConfigByChannelId(req.getId())) {
//            return RetResult.error("此通道目前已被配置，请先取消配置后再进行修改。");
//        }
        //  名称有修改则要校验唯一
        if (!entity.getName().equals(req.getName())) {
            //  通道名称唯一
            if (channelService.count(Wrappers.lambdaQuery(ChannelEntity.class).eq(ChannelEntity::getName, req.getName())) > 0) {
                return RetResult.error(req.getName() + ",该名称已存在");
            }
        }
        ChannelEntity updateEntity = ChannelEntity.valueOf(req);
        // expirationDateEnd与当前时间比较, 如果比当前时间还早, 则设置为禁用状态
        if (updateEntity.getExpirationDateEnd() != null && updateEntity.getExpirationDateEnd().before(new Date())) {
            updateEntity.setStatus(StatusEnum.DISABLE.getCode());
        }
        return RetResult.data(channelService.updateById(updateEntity));
    }


    public void channelExpiration() {
        LambdaQueryWrapper<ChannelEntity> query = Wrappers.lambdaQuery(ChannelEntity.class)
                .le(ChannelEntity::getExpirationDateEnd, new Date())
                .eq(ChannelEntity::getStatus, StatusEnum.ACTIVE.getCode());
        try {
            List<ChannelEntity> list = channelService.list(query);
            if (CollectionUtils.isNotEmpty(list)) {
                XxlJobHelper.log("[channelExpiration] 本次处理通道过期数据数量[{}]", list.size());
                list.forEach(channelEntity -> {
                    channelEntity.setStatus(StatusEnum.DISABLE.getCode());
                    channelService.updateById(channelEntity);
                });
            }
        } catch (Exception e) {
            log.error("[channelExpiration] 通道过期处理异常", e);
        }
    }
}
