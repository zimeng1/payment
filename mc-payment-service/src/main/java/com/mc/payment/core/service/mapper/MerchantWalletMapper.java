package com.mc.payment.core.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mc.payment.core.service.base.MerchantFilter;
import com.mc.payment.core.service.entity.MerchantWalletEntity;
import com.mc.payment.core.service.model.dto.CountAvailableWalletDto;
import com.mc.payment.core.service.model.dto.MerchantAvailableWalletDto;
import com.mc.payment.core.service.model.req.MerchantQueryReq;
import com.mc.payment.core.service.model.req.WalletBalanceSumReq;
import com.mc.payment.core.service.model.req.WalletPageReq;
import com.mc.payment.core.service.model.rsp.MerchantWalletExportRsp;
import com.mc.payment.core.service.model.rsp.MerchantWalletRsp;
import com.mc.payment.core.service.model.rsp.WalletBalanceSumRsp;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * @author Conor
 * @description 针对表【mcp_merchant_wallet(商户钱包)】的数据库操作Mapper
 * @createDate 2024-08-14 13:56:23
 * @Entity com.mc.payment.core.service.entity.MerchantWalletEntity
 */
public interface MerchantWalletMapper extends BaseMapper<MerchantWalletEntity> {

    List<WalletBalanceSumRsp> walletBalanceSum(@Param("merchantId") String merchantId, @Param("req") WalletBalanceSumReq req);

    @MerchantFilter
    IPage<MerchantWalletRsp> page(IPage<MerchantWalletRsp> page, @Param("req") WalletPageReq req);

    @MerchantFilter
    List<MerchantWalletExportRsp> queryExportInfo(@Param("req") WalletPageReq req);


    @MerchantFilter
    List<MerchantWalletEntity> queryBalanceSumByAssetOrAddr(@Param("accountIdSet") Set<String> accountIdSet, @Param("req") MerchantQueryReq req);

    List<CountAvailableWalletDto> countAvailableWallet(@Param("leCount") int leCount);

    /**
     * 商户钱包快照查询
     */
    List<MerchantWalletEntity> getSnapshotInfo();

    /**
     * 统计可用的入金钱包 FireBlocks
     *
     * @param leCount
     * @return
     */
    List<CountAvailableWalletDto> countAvailableDepositWalletFireBlocks(int leCount);

    /**
     * 统计可用的出金钱包 FireBlocks
     *
     * @param leCount
     * @return
     */
    List<CountAvailableWalletDto> countAvailableWithdrawalWalletFireBlocks(int leCount);


    @Select("SELECT * FROM merchant_wallet WHERE id = #{id} FOR UPDATE")
    MerchantWalletEntity selectByIdForUpdate(@Param("id") String id);


    List<MerchantAvailableWalletDto> queryAvailableWallet(@Param("merchantId") String merchantId,
                                                          @Param("channelSubType") Integer channelSubType);
}




