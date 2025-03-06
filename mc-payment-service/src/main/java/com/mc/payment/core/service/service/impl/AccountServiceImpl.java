package com.mc.payment.core.service.service.impl;

import cn.hutool.extra.pinyin.PinyinUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.AccountEntity;
import com.mc.payment.core.service.mapper.AccountMapper;
import com.mc.payment.core.service.model.enums.AccountStatusEnum;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.req.AccountPageReq;
import com.mc.payment.core.service.model.req.MerchantQueryReq;
import com.mc.payment.core.service.model.rsp.AccountPageRsp;
import com.mc.payment.core.service.service.IAccountService;
import com.mc.payment.core.service.util.RetryUtil;
import com.mc.payment.fireblocksapi.FireBlocksAPI;
import com.mc.payment.fireblocksapi.model.req.fireBlocks.CreateAccountReq;
import com.mc.payment.fireblocksapi.model.vo.fireBlocks.VaultAccountVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 账号管理 服务实现类
 * </p>
 *
 * @author conor
 * @since 2024-02-02 15:30:01
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, AccountEntity> implements IAccountService {

    private final FireBlocksAPI fireBlocksAPI;

    @Override
    public BasePageRsp<AccountPageRsp> page(AccountPageReq req) {
        Page<AccountPageRsp> page = new Page<>(req.getCurrent(), req.getSize());
        page = (Page<AccountPageRsp>) baseMapper.page(page, req);
        return BasePageRsp.valueOf(page);
    }


    @Override
    public List<AccountEntity> listByMerchantIds(MerchantQueryReq req) {
        //1.先查出账号信息(这里分为有无(资产和钱包地址)两种)来选择哪种方式查询, 然后根据账号信息查询相关数据. 然后再查商户数量.
        if (CollectionUtils.isEmpty(req.getAssetNameList()) && CollectionUtils.isEmpty(req.getAddrList())) {
            return baseMapper.findListByAccInfo(req);
        } else {
            return baseMapper.findListByAccInfoAndWalletInfo(req);
        }
    }


    /**
     * 初始化账号
     * <p>
     * 会根据通道的不通,进行不同的处理 有的通道会直接生成,有的这是生成一个待生成的账号,然后创建远程账号后再更新
     *
     * @param merchantId
     * @param merchantName
     * @param channelSubType
     * @param accountType
     * @return
     */
    @Override
    public AccountEntity initAccount(String merchantId, String merchantName, int channelSubType, int accountType) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setMerchantId(merchantId);
        accountEntity.setAccountType(accountType);
        accountEntity.setChannelSubType(channelSubType);
        accountEntity.setName(this.generateAccountName(merchantId, merchantName, accountType));
        if (ChannelSubTypeEnum.FIRE_BLOCKS.getCode() == channelSubType) {
            //  生成fireblocks账号,要进行远程创建,所以这里先生成一个待生成账号
            accountEntity.setStatus(AccountStatusEnum.GENERATE_WAIT.getCode());
        } else {
            accountEntity.setStatus(AccountStatusEnum.GENERATE_SUCCESS.getCode());
        }
        this.save(accountEntity);
        return accountEntity;
    }

    /**
     * 查询未建该资产的账号
     *
     * @param merchantId
     * @param assetName
     * @param netProtocol
     * @param channelSubType
     * @param accountType
     * @return
     */
    @Override
    public List<String> queryAccountIdNotExistWallet(String merchantId, String assetName, String netProtocol, int channelSubType, int accountType) {
        return baseMapper.queryAccountIdNotExistWallet(merchantId, assetName, netProtocol, channelSubType, accountType);
    }

    /**
     * 为商户生成一个可用的账号
     * <p>
     * 目前只有fireblocks通道会有多个账号的场景,其余的通道都是一个商户一个账号,所以除了fireblocks外,其他通道不要多次调用这个方法
     *
     * @param merchantId
     * @param merchantName
     * @param channelSubTypeEnum
     * @param accountType
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AccountEntity generateAccount(String merchantId, String merchantName,
                                         ChannelSubTypeEnum channelSubTypeEnum, Integer accountType) {
        boolean isFireBlocks = channelSubTypeEnum == ChannelSubTypeEnum.FIRE_BLOCKS;
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setMerchantId(merchantId);
        accountEntity.setAccountType(accountType);
        accountEntity.setChannelSubType(channelSubTypeEnum.getCode());
        accountEntity.setName(this.generateAccountName(merchantId, merchantName, accountType));
        // 生成fireblocks账号,要进行远程创建,所以这里先生成一个待生成账号
        accountEntity.setStatus(isFireBlocks ? AccountStatusEnum.GENERATE_WAIT.getCode() : AccountStatusEnum.GENERATE_SUCCESS.getCode());
        this.save(accountEntity);
        if (isFireBlocks) {
            // 重试3次
            RetResult<VaultAccountVo> retResult = RetryUtil.retry(() -> {
                RetResult<VaultAccountVo> result = fireBlocksAPI.createAccount(new CreateAccountReq(accountEntity.getName()));
                log.debug("fireBlocksAPI.createAccount ,ret:{}", result);
                if (result.isSuccess()) {
                    accountEntity.setStatus(AccountStatusEnum.GENERATE_SUCCESS.getCode());
                    accountEntity.setExternalId(result.getData().getId());
                } else {
                    accountEntity.setStatusMsg(result.getMsg());
                }
                return result;
            }, result -> !result.isSuccess(), 3, 1000);

            if (retResult == null || !retResult.isSuccess()) {
                accountEntity.setStatus(AccountStatusEnum.GENERATE_FAIL.getCode());
            }
            this.updateById(accountEntity);
        }

        return accountEntity;
    }

    //==========

    /**
     * 生成账号名称
     *
     * @param merchantId
     * @param merchantName
     * @param accountType
     * @return
     */
    private String generateAccountName(String merchantId, String merchantName, Integer accountType) {
        // 0:入金账户,1:出金账户
        // 账号命名规则: MC_商户名称拼音缩写前15个字符_入金/出金_当前时间戳
        String merchantNamePinyinFirst = PinyinUtil.getFirstLetter(merchantName, "").toUpperCase();
        //  取前15个字符
        merchantNamePinyinFirst = merchantNamePinyinFirst.length() > 15 ? merchantNamePinyinFirst.substring(0, 15) : merchantNamePinyinFirst;
        String accountName = "MC_" + merchantNamePinyinFirst + "_" + accountType + "_" + System.currentTimeMillis();
        log.info("生成账户,商户ID:{},商户名称:{},商户名称拼音缩写:{},账户类型:{},accountName:{}",
                merchantId, merchantName, merchantNamePinyinFirst, accountType, accountName);
        return accountName;
    }
}
