package com.mc.payment.core.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mc.payment.core.service.base.MerchantFilter;
import com.mc.payment.core.service.entity.AccountEntity;
import com.mc.payment.core.service.model.req.MerchantQueryReq;
import com.mc.payment.core.service.model.rsp.AccountPageRsp;
import com.mc.payment.core.service.model.req.AccountPageReq;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 账号管理 Mapper 接口
 * </p>
 *
 * @author conor
 * @since 2024-02-02 15:30:01
 */
public interface AccountMapper extends BaseMapper<AccountEntity> {

    @MerchantFilter("t1.merchant_id")
    IPage<AccountPageRsp> page(IPage<AccountPageRsp> page, @Param("req") AccountPageReq req);

    @MerchantFilter("a.merchant_id")
    List<AccountEntity> findListByAccInfo(@Param("req") MerchantQueryReq req);

    @MerchantFilter("a.merchant_id")
    List<AccountEntity> findListByAccInfoAndWalletInfo(@Param("req") MerchantQueryReq req);

    List<String> queryAccountIdNotExistWallet(@Param("merchantId") String merchantId,
                                              @Param("assetName") String assetName, @Param("netProtocol") String netProtocol,
                                              @Param("channelSubType") int channelSubType, @Param("accountType") int accountType);
}
