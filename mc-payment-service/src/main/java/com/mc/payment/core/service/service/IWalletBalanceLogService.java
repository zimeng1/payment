package com.mc.payment.core.service.service;

import com.mc.payment.core.service.entity.WalletBalanceLogEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author Conor
 * @description 针对表【mcp_wallet_balance_log(钱包余额变动表)】的数据库操作Service
 * @createDate 2024-05-21 13:44:46
 */
public interface IWalletBalanceLogService extends IService<WalletBalanceLogEntity> {
    /**
     * 监控钱包余额变动
     * 然后记录日志
     *
     */
    void monitorWalletBalanceChangeJob();
}
