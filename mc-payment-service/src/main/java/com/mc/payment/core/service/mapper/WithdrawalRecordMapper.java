package com.mc.payment.core.service.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.mc.payment.core.service.base.MerchantFilter;
import com.mc.payment.core.service.entity.WithdrawalRecordEntity;
import com.mc.payment.core.service.model.req.WithdrawalPageReq;
import com.mc.payment.core.service.model.rsp.WithdrawalRecordPageRsp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 出金记录表 Mapper 接口
 * </p>
 *
 * @author conor
 * @since 2024-04-18 15:54:30
 */
public interface WithdrawalRecordMapper extends BaseMapper<WithdrawalRecordEntity> {
    @MerchantFilter("wr.merchant_id")
    IPage<WithdrawalRecordPageRsp> page(IPage<WithdrawalRecordPageRsp> page, @Param("req") WithdrawalPageReq req);


    @MerchantFilter("merchant_id")
    @Override
    List<WithdrawalRecordEntity> selectList(@Param(Constants.WRAPPER) Wrapper<WithdrawalRecordEntity> queryWrapper);
}
