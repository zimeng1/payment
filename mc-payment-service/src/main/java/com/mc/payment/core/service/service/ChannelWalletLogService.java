package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.ChannelWalletLogEntity;
import com.mc.payment.core.service.model.req.ChannelWalletQueryLogPageReq;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.Future;

/**
 * @author Conor
 * @description 针对表【mcp_channel_wallet_log(通道钱包日志表)】的数据库操作Service
 * @createDate 2024-08-15 10:28:03
 */
public interface ChannelWalletLogService extends IService<ChannelWalletLogEntity> {
    /**
     * 异步保存变动日志
     *
     * @param walletId
     * @param changeBalance
     * @param changeFreezeAmount
     * @param msg
     * @param date
     * @return Future<String> 异步保存变动日志的id
     */
    Future<String> asyncSaveLog(String walletId, BigDecimal changeBalance, BigDecimal changeFreezeAmount, String msg, Date date);

    /**
     * 分页查询日志
     * @param req
     * @return
     */
    BasePageRsp<ChannelWalletLogEntity> page(@RequestBody ChannelWalletQueryLogPageReq req);
}
