package com.mc.payment.core.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.AccountEntity;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.req.AccountPageReq;
import com.mc.payment.core.service.model.req.MerchantQueryReq;
import com.mc.payment.core.service.model.rsp.AccountPageRsp;

import java.util.List;

/**
 * <p>
 * 账号管理 服务类
 * </p>
 *
 * @author conor
 * @since 2024-02-02 15:30:01
 */
public interface IAccountService extends IService<AccountEntity> {

    BasePageRsp<AccountPageRsp> page(AccountPageReq req);

    List<AccountEntity> listByMerchantIds(MerchantQueryReq req);

    AccountEntity initAccount(String merchantId, String merchantName, int channelSubType, int accountType);

    List<String> queryAccountIdNotExistWallet(String merchantId, String assetName, String netProtocol, int channelSubType, int accountType);

    /**
     * 为商户生成一个可用的账号
     * <p>
     * 目前只有fireblocks通道会有多个账号的场景,其余的通道都是一个商户一个账号,所以除了fireblocks外,其他通道不要多次调用这个方法
     * fireblocks通道会根据账号类型生成不同的账号,生成过程中会向fireblocks发起创建账号的请求,然后更新账号信息
     *
     * @param merchantId
     * @param merchantName
     * @param channelSubTypeEnum
     * @param accountType
     * @return
     */
    AccountEntity generateAccount(String merchantId,
                                  String merchantName,
                                  ChannelSubTypeEnum channelSubTypeEnum,
                                  Integer accountType);
}
