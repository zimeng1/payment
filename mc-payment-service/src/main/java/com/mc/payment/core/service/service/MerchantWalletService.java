package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.MerchantWalletEntity;
import com.mc.payment.core.service.entity.WithdrawalRecordEntity;
import com.mc.payment.core.service.model.GetAvailableWalletDto;
import com.mc.payment.core.service.model.dto.CountAvailableWalletDto;
import com.mc.payment.core.service.model.dto.MerchantAvailableWalletDto;
import com.mc.payment.core.service.model.enums.ChangeEventTypeEnum;
import com.mc.payment.core.service.model.req.*;
import com.mc.payment.core.service.model.rsp.CryptoWithdrawWalletRsp;
import com.mc.payment.core.service.model.rsp.MerchantWalletRsp;
import com.mc.payment.core.service.model.rsp.WalletBalanceRsp;
import com.mc.payment.core.service.model.rsp.WalletBalanceSumRsp;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * @author Conor
 * @description 针对表【mcp_merchant_wallet(商户钱包)】的数据库操作Service
 * @createDate 2024-08-14 13:56:23
 */
public interface MerchantWalletService extends IService<MerchantWalletEntity> {
    BasePageRsp<MerchantWalletRsp> page(WalletPageReq req);


    /**
     * 获取一个可用的钱包
     * <p>
     * 优先可用余额最多,冻结金额最小的钱包
     *
     * @param getAvailableWalletDto
     * @return
     */
    MerchantWalletEntity getAvailableWallet(GetAvailableWalletDto getAvailableWalletDto);

    /**
     * 获取加密货币出金钱包,并且冻结相应金额
     * <p/>
     * 要获取两个,一个用于出金,一个用于手续费币种扣费
     *
     * @return left:出金钱包, right:手续费币种扣费钱包
     */
    CryptoWithdrawWalletRsp getCryptoWithdrawWalletAndFreeze(String merchantId, Integer channelSubType,
                                                             String assetName, String netProtocol,
                                                             String feeAssetName, BigDecimal amount, BigDecimal feeAmount);

    /**
     * 获取一个可用的出金钱包,并且冻结相应金额
     *
     * @param merchantId
     * @param channelSubType
     * @param assetName
     * @param netProtocol
     * @param amount
     * @return
     */
    MerchantWalletEntity getWithdrawWalletAndFreeze(String merchantId, Integer channelSubType, Integer assetType, String assetName, String netProtocol, BigDecimal amount);
//    /**
//     * 入金变更余额
//     *
//     * @param depositId
//     * @param walletId
//     * @param amount
//     * @param msg
//     * @return
//     */
//    boolean depositAddBalance(String depositId, String walletId, BigDecimal amount, String msg);
//
//    /**
//     * 出金成功减少余额
//     *
//     * @param withdrawId
//     * @param walletId
//     * @param amount
//     * @param msg
//     * @return
//     */
//    boolean withdrawSubBalanceAndUnFreezeAmount(String withdrawId, String walletId, BigDecimal amount, String msg);
//
//    /**
//     * 出金开始冻结金额
//     *
//     * @param withdrawId
//     * @param walletId
//     * @param freezeAmount
//     * @param msg
//     * @return
//     */
//    boolean withdrawFreezeAmount(String withdrawId, String walletId, BigDecimal freezeAmount, String msg);
//
//    /**
//     * 出金失败解冻金额
//     *
//     * @param withdrawId
//     * @param walletId
//     * @param freezeAmount
//     * @param msg
//     * @return
//     */
//    boolean withdrawUnFreezeAmount(String withdrawId, String walletId, BigDecimal freezeAmount, String msg);


    MerchantWalletEntity getWithdrawWallet(WithdrawalRecordEntity withdrawalRecord);

    /**
     * 变更余额
     *
     * @param changeEventTypeEnum 事件类型
     * @param correlationId       关联id
     * @param walletId            钱包id
     * @param amount              变更金额,正数为增加,负数为减少
     * @param msg                 变更原因
     * @return
     */
    boolean changeBalance(ChangeEventTypeEnum changeEventTypeEnum, String correlationId, String walletId, BigDecimal amount, String msg);

    /**
     * 变更余额和冻结金额
     *
     * @param changeEventTypeEnum 事件类型
     * @param correlationId       关联id
     * @param walletId            钱包id
     * @param balance             变更余额,正数为增加,负数为减少
     * @param freezeAmount        变更冻结金额,正数为增加,负数为减少
     * @param msg                 变更原因
     * @return
     */
    boolean changeBalanceAndAmount(ChangeEventTypeEnum changeEventTypeEnum, String correlationId, String walletId, BigDecimal balance, BigDecimal freezeAmount, String msg);

    /**
     * 锁定钱包
     *
     * @param walletId
     * @return
     */
    boolean lockWallet(String walletId);

    /**
     * 解锁钱包并且进入冷却中
     *
     * @param walletIds
     * @return
     */
    boolean unlockAndCollWallet(List<String> walletIds);

    /**
     * 解锁钱包并且进入冷却中
     *
     * @param walletIds   钱包id集合
     * @param coolingTime 冷却时间,单位:毫秒
     * @return
     */
    boolean unlockAndCollWallet(List<String> walletIds, long coolingTime);

    /**
     * 恢复冷却中钱包
     *
     * @param walletIds
     * @return
     */
    boolean recoverCoolWallet(List<String> walletIds);

    String generateWalletQRCode(GenerateWalletQRCodeReq req);

    List<WalletBalanceRsp> walletBalanceList(String merchantId, WalletBalanceReq req);

    /**
     * 查询账户的所有资产余额,并且换算成U
     *
     * @param accountIds
     * @return 余额 单位:U
     */
    BigDecimal queryBalanceSum(List<String> accountIds);

    /**
     * 查询钱包余额, 根据条件查询
     *
     * @param accountIdSet
     * @param req
     * @return 余额 单位:U
     */
    List<MerchantWalletEntity> queryBalanceSumByAssetOrAddr(Set<String> accountIdSet, MerchantQueryReq req);

    List<WalletBalanceSumRsp> walletBalanceSum(String merchantId, WalletBalanceSumReq req);

    /**
     * fireblocks通道的商户出金钱包,需要依托于通道钱包的余额进行更新余额
     *
     * @param channelWalletId 通道钱包id
     * @param logId           通道钱包变更日志id
     * @param balance         余额
     */
    void syncChannelFireBlocksWithdrawWalletBalance(String channelWalletId, String logId, BigDecimal balance);

    /**
     * 商户钱包导出
     *
     * @param req
     * @param response
     */
    void export(WalletPageReq req, HttpServletResponse response);

    /**
     * 统计可用钱包数量信息,加密货币,且是入金钱包,通道为fireblocks
     *
     * @param leCount 低于等于该数量的钱包数量信息
     * @return
     */
    List<CountAvailableWalletDto> countAvailableWallet(int leCount);

    /**
     * 统计可用钱包数量信息,加密货币,通道为fireblocks
     *
     * @param leCount         低于等于该数量的钱包数量信息
     * @param depositWithdraw true 入金钱包, false 出金钱包
     * @return
     */
    List<CountAvailableWalletDto> countAvailableWalletFireBlocks(int leCount, boolean depositWithdraw);

    /**
     * 商户钱包快照
     */
    void scanMerchantWallet();

    MerchantWalletEntity selectByIdForUpdate(String id);

    List<MerchantAvailableWalletDto> queryAvailableWallet(String merchantId, Integer channelSubType);
}
