package com.mc.payment.core.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mc.payment.core.service.entity.WalletBalanceLogEntity;

import java.util.List;

/**
 * @author Conor
 * @description 针对表【mcp_wallet_balance_log(钱包余额变动表)】的数据库操作Mapper
 * @createDate 2024-05-21 13:44:46
 * @Entity com.mc.payment.core.service.entity.WalletBalanceLogEntity
 */
public interface WalletBalanceLogMapper extends BaseMapper<WalletBalanceLogEntity> {
    /**
     * 查询每个钱包最新一次更新的记录
     *
     * @return
     */
    List<WalletBalanceLogEntity> queryLatestRecordOfWallet();
}




