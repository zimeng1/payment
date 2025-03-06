package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.api.model.rsp.QueryDepositReportRsp;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.DepositRecordEntity;
import com.mc.payment.core.service.model.req.DepositPageReq;
import com.mc.payment.core.service.model.req.DepositRecordDetailReq;
import com.mc.payment.core.service.model.req.MerchantQueryReq;
import com.mc.payment.core.service.model.rsp.DepositDetailRsp;
import com.mc.payment.core.service.model.rsp.DepositRecordPageRsp;
import jakarta.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 入金记录表 服务类
 * </p>
 *
 * @author conor
 * @since 2024-04-17 18:00:15
 */
public interface IDepositRecordService extends IService<DepositRecordEntity> {

    DepositRecordEntity getOne(String merchantId, String trackingId);

    BasePageRsp<DepositRecordPageRsp> page(DepositPageReq req);

    DepositRecordEntity queryEffective(String assetName, String netProtocol, String destinationAddress);

    List<DepositRecordEntity> listByMerchantIdsAntTime(Set<String> accountIdSet, MerchantQueryReq req);

    /**
     * 入金记录导出
     *
     * @param req
     */
    void depositExport(DepositPageReq req, HttpServletResponse response) throws UnsupportedEncodingException;

    /**
     * 获取历史入金总额
     *
     * @param merchantId
     * @param assetType
     * @param assetName
     * @param netProtocol
     * @return
     */
    BigDecimal getDepositAmount(String merchantId, Integer assetType, String assetName, String netProtocol);

    /**
     * 超时入金申请
     *
     * @return
     */
    List<DepositRecordEntity> getOverdue();

    /**
     * 入金明细导出列表
     *
     * @param req
     * @return
     */
    BasePageRsp<DepositDetailRsp> getDepositDetailExport(DepositRecordDetailReq req);

    /**
     * 入金记录明细导出
     *
     * @param req
     * @param response
     */
    void depositDetailExport(DepositRecordDetailReq req, HttpServletResponse response);

    /**
     * 入金记录明细报表
     *
     * @param trackingIdList
     * @return
     */
    List<QueryDepositReportRsp> queryReport(List<String> trackingIdList);

}
