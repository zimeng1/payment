package com.mc.payment.core.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mc.payment.core.service.entity.ChannelEntity;
import com.mc.payment.core.service.model.req.ChannelPageReq;
import com.mc.payment.core.service.model.rsp.ChannelPageRsp;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author conor
 * @since 2024-01-31 18:46:14
 */
public interface ChannelMapper extends BaseMapper<ChannelEntity> {

    IPage<ChannelPageRsp> selectPage(IPage<ChannelPageRsp> page, @Param("req") ChannelPageReq req);
}
