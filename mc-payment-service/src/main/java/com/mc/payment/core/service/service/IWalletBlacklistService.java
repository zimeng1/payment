package com.mc.payment.core.service.service;

import com.mc.payment.core.service.entity.WalletBlacklistEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author conor
 * @since 2024-05-16 15:40:12
 */
public interface IWalletBlacklistService extends IService<WalletBlacklistEntity> {
    /**
     * 判断是否在黑名单中
     *
     * @param walletAddress
     * @return
     */
    boolean isBlacklist(String walletAddress);
}
