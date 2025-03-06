package com.mc.payment.core.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mc.payment.core.service.entity.UserEntity;
import com.mc.payment.core.service.model.req.UserPageReq;
import com.mc.payment.core.service.model.rsp.UserPageRsp;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author conor
 * @since 2024-01-25 10:12:50
 */
public interface UserMapper extends BaseMapper<UserEntity> {

    IPage<UserPageRsp> page(IPage<UserPageRsp> page, @Param("req") UserPageReq req);
}
