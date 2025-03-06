package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.WalletEntity;
import com.mc.payment.core.service.entity.WithdrawalRecordEntity;
import com.mc.payment.core.service.model.dto.ChannelAssetDto;
import com.mc.payment.core.service.model.req.*;
import com.mc.payment.core.service.model.rsp.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author conor
 * @since 2024-04-15 11:07:58
 */
public interface IWalletService extends IService<WalletEntity> {
    BasePageRsp<WalletPageRsp> page(WalletPageReq req);

    RetResult<String> save(WalletSaveReq req);

    RetResult<Boolean> updateById(WalletUpdateReq req);

    RetResult<Boolean> remove(String id);

    /**
     * 查询钱包余额列表
     *
     * @param merchantId
     * @param req
     * @return
     */
    List<WalletBalanceRsp> walletBalanceList(String merchantId, WalletBalanceReq req);

    /**
     * 批量将钱包恢复为可用 解锁或者解冻
     *
     * @param ids
     * @return
     */
    boolean recoverByIds(List<String> ids);

    /**
     * 修改钱包状态
     *
     * @param ids
     * @param status
     * @return
     */
    boolean updateStatusByIds(List<String> ids, int status);


    /**
     * 获取该商户下对应资产的金额最多的可用钱包
     *
     * @param merchantId  商户id
     * @param accountType 账户类型
     * @param assetType   资产类型
     * @param assetName   资产名称
     * @param netProtocol 网络协议/支付类型
     * @param lock        是否锁定
     * @return
     */
    WalletEntity getAvailableTransferIn(String merchantId, Integer accountType, int assetType, String assetName, String netProtocol, boolean lock);

    /**
     * 获取多个可用的转出钱包,并且冻结相应的金额
     *
     * @param merchantId
     * @param assetName
     * @param netProtocol
     * @param amount
     * @return
     */
    WalletEntity getAvailableTransferOut(String merchantId, Integer accountType, String assetName, String netProtocol, BigDecimal amount);

    /**
     * 查询可用余额最多的转出钱包
     *
     * @param merchantId
     * @param assetName
     * @param netProtocol
     * @return
     */
    WalletEntity queryByAmountMax(String merchantId, String assetName, String netProtocol);

    /**
     * 查询可用余额最多的转出账户钱包-ps:包含账户信息
     *
     * @param merchantId
     * @param assetName
     * @param netProtocol
     * @return
     */
    WalletOfMaxBalanceAccountRsp queryAccountByAmountMax(String merchantId, String assetName, String netProtocol);

    List<MerchantAssetRsp> listByMerchantIdAntTime(String merchantName, Date timeStart, Date timeEnd);

    WalletEntity getAvailableTransferOut2Asset(String merchantId, String assetName, String netProtocol, String feeAssetName, BigDecimal amount, BigDecimal feeAmount, WithdrawalRecordEntity entity);

    /**
     * 分片查询钱包集合
     *
     * @param shardIndex 当前分片索引
     * @param shardTotal 分片总数
     * @return
     */
    List<WalletEntity> shardList(int shardIndex, int shardTotal);

    /**
     * 查询账户的所有资产余额
     *
     * @param accountIds
     * @return 余额 单位:U
     */
    BigDecimal queryBalanceSum(List<String> accountIds);

    long count(int channelSubType, String channelAssetName);

    /**
     * 查询钱包余额, 根据条件查询
     *
     * @param accountIdSet
     * @param req
     * @return 余额 单位:U
     */
    List<WalletEntity> queryBalanceSumByAssetOrAddr(Set<String> accountIdSet, MerchantQueryReq req);

    /**
     * 按钱包id批量刷新钱包余额
     *
     * @param walletIds
     */
    void refreshWalletBalanceBatch(List<String> walletIds);

    /**
     * 查询钱包地址列表
     *
     * @param req
     * @return
     */
    List<String> queryWalletAddressList(QueryWalletAddressListReq req);

    /**
     * 生成钱包地址二维码
     *
     * @param req
     * @return
     */
    String generateWalletQRCode(GenerateWalletQRCodeReq req);

    /**
     * 刷新钱包余额
     *
     * @param shardIndex 当前节点的索引
     * @param shardTotal 总节点数
     */
    void refreshWalletBalanceJob(int shardIndex, int shardTotal);

    void saveByChannelAssetDto(ChannelAssetDto assetDto, String merchantId, String accountId);

    List<WalletBalanceSumRsp> walletBalanceSum(String merchantId, WalletBalanceSumReq req);

    void dataMigrate();
}
