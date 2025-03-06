package com.mc.payment.core.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mc.payment.core.service.base.MerchantFilter;
import com.mc.payment.core.service.entity.DepositRecordDetailEntity;
import com.mc.payment.core.service.model.req.DepositRecordDetailReq;
import com.mc.payment.core.service.model.req.GetDepositRecordDetailReq;
import com.mc.payment.core.service.model.rsp.DepositDetailRsp;
import com.mc.payment.core.service.model.rsp.DepositRecordDetailRsp;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author conor
 * @since 2024-04-22 17:48:32
 */
public interface DepositRecordDetailMapper extends BaseMapper<DepositRecordDetailEntity> {

    @MerchantFilter("drd.merchant_id")
    List<DepositRecordDetailRsp> getDetailListByRecordId(@Param("req") GetDepositRecordDetailReq req);

    @MerchantFilter("d.merchant_id")
    IPage<DepositDetailRsp> getDetailPageList(Page<DepositDetailRsp> page,@Param("req") DepositRecordDetailReq req);
}
