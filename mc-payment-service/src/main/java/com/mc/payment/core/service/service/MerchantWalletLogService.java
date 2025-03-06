package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.MerchantWalletLogEntity;
import com.mc.payment.core.service.model.req.MerchantWalletLogReq;
import com.mc.payment.core.service.model.req.MerchantWalletQueryLogReq;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.Future;

/**
 * @author Conor
 * @description 针对表【mcp_merchant_wallet_log(商户钱包日志表)】的数据库操作Service
 * @createDate 2024-08-14 21:08:06
 */
public interface MerchantWalletLogService extends IService<MerchantWalletLogEntity> {
    /**
     * 异步保存变动日志
     *
     * @param walletId
     * @param changeBalance
     * @param changeFreezeAmount
     * @param msg
     * @param date
     */
    void asyncSaveLog(String walletId, BigDecimal changeBalance, BigDecimal changeFreezeAmount, String msg, Date date);

    /**
     * 异步保存变动日志
     *
     * @param req
     * @return 日志id
     */
    Future<String> asyncSaveLog(MerchantWalletLogReq req);

    /**
     * 分页查询商户钱包日志
     * @param req
     * @return
     */
    BasePageRsp<MerchantWalletLogEntity> page(MerchantWalletQueryLogReq req);

}
