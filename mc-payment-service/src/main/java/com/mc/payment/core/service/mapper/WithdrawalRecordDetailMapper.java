package com.mc.payment.core.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mc.payment.core.service.base.MerchantFilter;
import com.mc.payment.core.service.entity.WithdrawalRecordDetailEntity;
import com.mc.payment.core.service.model.req.WithdrawalRecordDetailReq;
import com.mc.payment.core.service.model.rsp.WithdrawalDetailRsp;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 * @author conor
 * @since 2024-04-22 17:48:32
 */
public interface WithdrawalRecordDetailMapper extends BaseMapper<WithdrawalRecordDetailEntity> {

    @MerchantFilter("d.merchant_id")
    IPage<WithdrawalDetailRsp> getDetailPageList(Page<WithdrawalDetailRsp> page,@Param("req") WithdrawalRecordDetailReq req);
}
