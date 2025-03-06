package com.mc.payment.core.service.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mc.payment.api.model.rsp.QueryDepositReportRsp;
import com.mc.payment.core.service.base.MerchantFilter;
import com.mc.payment.core.service.entity.DepositRecordEntity;
import com.mc.payment.core.service.model.req.DepositPageReq;
import com.mc.payment.core.service.model.req.DepositRecordDetailReq;
import com.mc.payment.core.service.model.rsp.DepositDetailExportRsp;
import com.mc.payment.core.service.model.rsp.DepositDetailRsp;
import com.mc.payment.core.service.model.rsp.DepositRecordPageRsp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 入金记录表 Mapper 接口
 * </p>
 *
 * @author conor
 * @since 2024-04-17 18:00:15
 */
public interface DepositRecordMapper extends BaseMapper<DepositRecordEntity> {

    @MerchantFilter("dr.merchant_id")
    IPage<DepositRecordPageRsp> page(IPage<DepositRecordPageRsp> page, @Param("req") DepositPageReq req);


    @MerchantFilter("merchant_id")
    @Override
    List<DepositRecordEntity> selectList(@Param(Constants.WRAPPER) Wrapper<DepositRecordEntity> queryWrapper);

    @MerchantFilter
    List<DepositDetailExportRsp> getDetail(@Param("depositIds") List<String> depositIds);

    /**
     * 超时入金申请
     *
     * @return
     */
    List<DepositRecordEntity> getOverdue();

    /**
     * 入金明细导出列表
     *
     * @param page
     * @param req
     * @return
     */
    @MerchantFilter("d.merchant_id")
    IPage<DepositDetailRsp> getDepositDetailExport(Page<DepositDetailRsp> page, @Param("req") DepositRecordDetailReq req);

    /**
     * 入金记录明细报表
     *
     * @param trackingIdList
     * @return
     */
    @MerchantFilter("d.merchant_id")
    List<QueryDepositReportRsp> queryReport(@Param("trackingIdList") List<String> trackingIdList);
}
