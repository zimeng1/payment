package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.AccountEntity;
import com.mc.payment.core.service.entity.WithdrawalRecordEntity;
import com.mc.payment.core.service.model.enums.ChangeEventTypeEnum;
import com.mc.payment.core.service.model.req.MerchantQueryReq;
import com.mc.payment.core.service.model.req.WithdrawalPageReq;
import com.mc.payment.core.service.model.req.WithdrawalRecordDetailReq;
import com.mc.payment.core.service.model.req.WithdrawalStopReq;
import com.mc.payment.core.service.model.rsp.WithdrawalRecordPageRsp;
import com.mc.payment.core.service.model.rsp.WithdrawalStopRsp;
import com.mc.payment.gateway.model.rsp.GatewayWithdrawalRsp;
import jakarta.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 出金记录表 服务类
 * </p>
 *
 * @author conor
 * @since 2024-04-17 18:00:16
 */
public interface IWithdrawalRecordService extends IService<WithdrawalRecordEntity> {

    WithdrawalRecordEntity getOne(String merchantId, String trackingId);

    BasePageRsp<WithdrawalRecordPageRsp> page(WithdrawalPageReq req);

    List<WithdrawalRecordEntity> listByMerchantIdsAntTime(Set<String> accountIdSet, MerchantQueryReq req);


    /**
     * 查询过去的hour小时内,提币金额超过了amount金额的出金目标地址
     * 比如:
     * 在过去的24小时内，提币金额超过了1500 U
     * 在过去的7天内，提币金额超过了5000 U
     *
     * @param hour
     * @param amount
     * @return
     */
    List<String> listOverAmountAddress(int hour, int amount);

    /**
     * 查询过去的hour小时内,提币次数操作了size次数的出金目标地址
     * 在过去的24小时内，提币次数超过了5次
     */
    List<String> listOverTimesAddress(int hour, int times);

    /**
     * 出金记录导出
     *
     * @param req
     * @param response
     */
    void withdrawalExport(WithdrawalPageReq req, HttpServletResponse response) throws UnsupportedEncodingException;

    WithdrawalStopRsp stopWithdrawal(WithdrawalStopReq req);

    void unfreezeWallet(WithdrawalRecordEntity entity);

    void legalTenderUnfreezeAndChangeBalance(WithdrawalRecordEntity entity, ChangeEventTypeEnum changeEventTypeEnum);

    /**
     * 获取历史出金总额
     *
     * @param merchantId
     * @param assetType
     * @param assetName
     * @param netProtocol
     * @return
     */
    BigDecimal getWithdrawalAmount(String merchantId, Integer assetType, String assetName, String netProtocol);

    /**
     * 出金记录明细导出
     *
     * @param req
     * @param response
     */
    void withdrawalDetailExport(WithdrawalRecordDetailReq req, HttpServletResponse response);

    RetResult<GatewayWithdrawalRsp> fireblocksWithdrawal(WithdrawalRecordEntity withdrawalRecord, String assetId, AccountEntity accountEntity);

    /**
     * 余额不足触发告警
     *
     * @param recordEntity
     */
    public void balanceAlert(WithdrawalRecordEntity recordEntity);

    void payoutTimeoutLimit();
}
