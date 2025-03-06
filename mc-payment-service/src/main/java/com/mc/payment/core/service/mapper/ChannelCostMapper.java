package com.mc.payment.core.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mc.payment.core.service.entity.ChannelCostEntity;
import com.mc.payment.core.service.model.rsp.ChannelCostPageRsp;
import com.mc.payment.core.service.model.req.ChannelCostPageReq;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author conor
 * @since 2024-01-31 18:46:14
 */
public interface ChannelCostMapper extends BaseMapper<ChannelCostEntity> {

    IPage<ChannelCostPageRsp> selectPage(IPage<ChannelCostPageRsp> page, @Param("req") ChannelCostPageReq req);

    /**
     * @param merchantId     商户id
     * @param assetId        资产id
     * @param businessAction 业务动作,[0:入金,1:出金]
     * @return
     */
    List<ChannelCostEntity> list(@Param("merchantId") String merchantId, @Param("assetId") String assetId, @Param("businessAction") Integer businessAction);
}
