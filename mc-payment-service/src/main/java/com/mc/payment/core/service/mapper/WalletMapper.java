package com.mc.payment.core.service.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.mc.payment.core.service.base.MerchantFilter;
import com.mc.payment.core.service.entity.WalletEntity;
import com.mc.payment.core.service.model.dto.RefreshWalletBalanceDto;
import com.mc.payment.core.service.model.req.QueryWalletAddressListReq;
import com.mc.payment.core.service.model.req.WalletBalanceReq;
import com.mc.payment.core.service.model.req.WalletBalanceSumReq;
import com.mc.payment.core.service.model.req.WalletPageReq;
import com.mc.payment.core.service.model.rsp.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author conor
 * @since 2024-04-15 11:07:58
 */
public interface WalletMapper extends BaseMapper<WalletEntity> {

    WalletEntity getAvailableTransferIn(@Param("merchantId") String merchantId,
                                        @Param("accountType") Integer accountType,
                                        @Param("assetType") int assetType,
                                        @Param("assetName") String assetName,
                                        @Param("netProtocol") String netProtocol);

    // 根据转出金额查询可用金额最多的钱包
    WalletEntity queryByAmountMax(@Param("merchantId") String merchantId, @Param("assetName") String assetName,
                                  @Param("netProtocol") String netProtocol);

    // 根据转出金额查询可用金额最多的钱包
    WalletOfMaxBalanceAccountRsp queryAccountByAmountMax(@Param("merchantId") String merchantId, @Param("assetName") String assetName,
                                                         @Param("netProtocol") String netProtocol);


    WalletEntity getAvailableTransferOut(@Param("merchantId") String merchantId,
                                         @Param("accountType") Integer accountType, @Param("assetName") String assetName,
                                         @Param("netProtocol") String netProtocol, @Param("amount") BigDecimal amount);

    @MerchantFilter("t1.merchant_id")
    IPage<WalletPageRsp> page(IPage<WalletPageRsp> page, @Param("req") WalletPageReq req);

    @Select("select sum(balance) as balanceSum,asset_name  from mcp_wallet where create_time between #{timeStart} and #{timeEnd} group by asset_name")
    List<MerchantAssetRsp> listByTime(@Param("timeStart") Date timeStart, @Param("timeEnd") Date timeEnd);

    @Select("select sum(balance) as balanceSum,asset_name  from mcp_wallet where create_time between #{timeStart} and #{timeEnd} and account_id in (select id from mcp_account where merchant_id = #{merchantId}) group by asset_name")
    List<MerchantAssetRsp> listByMerchantIdsAntTime(@Param("merchantId") String merchantId, @Param("timeStart") Date timeStart, @Param("timeEnd") Date timeEnd);


    List<WalletEntity> getRichTransferOutList(@Param("merchantId") String merchantId, @Param("assetNames") String assetNames);

    List<WalletBalanceRsp> walletBalanceList(@Param("merchantId") String merchantId, @Param("req") WalletBalanceReq req);

    List<WalletEntity> shardList(@Param("shardIndex") int shardIndex, @Param("shardTotal") int shardTotal);

    long count(@Param("channelSubType") int channelSubType, @Param("channelAssetName") String channelAssetName);

    /**
     * 查询RefreshWalletBalanceDto列表
     *
     * @param walletIds 不传查询全部
     * @return
     */
    List<RefreshWalletBalanceDto> queryRefreshWalletBalanceDtoByIds(@Param("walletIds") List<String> walletIds);


    @MerchantFilter("merchant_id")
    @Override
    List<WalletEntity> selectList(@Param(Constants.WRAPPER) Wrapper<WalletEntity> queryWrapper);

    List<String> queryWalletAddressList(@Param("req") QueryWalletAddressListReq req);

    /**
     * 根据账号和资产名称汇总钱包余额,
     * key为账号id+'_'+资产名称 eg:1784829395305418754_ETH
     * value为账号和资产名称分组汇总的总余额
     *
     * @param accountIdSet
     * @param assetNameSet
     * @return
     */
//    @MapKey("mapKey")
    List<WalletAssetSumBalanceRsp> queryAccountIdAndAssetNameSumBalance(@Param("accountIdSet") Set<String> accountIdSet, @Param("assetNameSet") Set<String> assetNameSet);

    List<WalletBalanceSumRsp> walletBalanceSum(@Param("merchantId") String merchantId,@Param("req")  WalletBalanceSumReq req);

    void delChannelWalletAll();
    void delMerchantWalletAll();
}
