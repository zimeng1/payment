package com.mc.payment.core.service.manager;

import com.mc.payment.core.service.entity.AccountEntity;

/**
 * 支付账号管理
 *
 * @author Conor
 * @since 2025-01-02 10:49:17.939
 */
public interface AccountManager {
    /**
     * 生成ForeBlocks账号
     *
     * @return
     */
    AccountEntity generateForeBlocksAccount();
}
