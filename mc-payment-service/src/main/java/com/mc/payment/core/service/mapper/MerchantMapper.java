package com.mc.payment.core.service.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.mc.payment.core.service.base.MerchantFilter;
import com.mc.payment.core.service.entity.MerchantEntity;
import com.mc.payment.core.service.model.req.MerchantChannelSaveReq;
import com.mc.payment.core.service.model.req.MerchantPageReq;
import com.mc.payment.core.service.model.req.merchant.MerchantConfigPageReq;
import com.mc.payment.core.service.model.req.merchant.MerchantListReq;
import com.mc.payment.core.service.model.rsp.MerchantPageRsp;
import com.mc.payment.core.service.model.rsp.merchant.MerchantConfigPageRsp;
import com.mc.payment.core.service.model.rsp.merchant.MerchantListRsp;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author conor
 * @since 2024-01-31 18:46:14
 */
public interface MerchantMapper extends BaseMapper<MerchantEntity> {
    @MerchantFilter("t1.id")
    IPage<MerchantPageRsp> page(IPage<MerchantPageRsp> page, @Param("req") MerchantPageReq req);

    @MerchantFilter("t1.id")
    MerchantPageRsp getById(@Param("id") String id);

    @MerchantFilter("t1.id")
    List<MerchantChannelSaveReq> listById(@Param("id") String id);

    @MerchantFilter("id")
    @Override
    List<MerchantEntity> selectList(@Param(Constants.WRAPPER) Wrapper<MerchantEntity> queryWrapper);

    @MerchantFilter("id")
    @Override
    List<MerchantEntity> selectBatchIds(@Param(Constants.COLL) Collection<? extends Serializable> idList);

    @MerchantFilter("id")
    @Override
    Long selectCount(@Param(Constants.WRAPPER) Wrapper<MerchantEntity> queryWrapper);

    List<MerchantEntity> listByChannel(@Param("channelSubType") int channelSubType);

    @MerchantFilter("t1.id")
    IPage<MerchantConfigPageRsp> configPage(IPage<MerchantConfigPageRsp> page, @Param("req") MerchantConfigPageReq req);

    @MerchantFilter("id")
    List<MerchantListRsp> currentLoginList(@Param("req") MerchantListReq req);
}
