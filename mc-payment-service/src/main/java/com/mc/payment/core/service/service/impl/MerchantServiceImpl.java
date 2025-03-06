package com.mc.payment.core.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.common.constant.ExceptionTypeEnum;
import com.mc.payment.common.exception.BusinessException;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.config.AppConfig;
import com.mc.payment.core.service.entity.AccountEntity;
import com.mc.payment.core.service.entity.MerchantEntity;
import com.mc.payment.core.service.facade.WalletServiceFacade;
import com.mc.payment.core.service.manager.wallet.FireBlocksWalletManager;
import com.mc.payment.core.service.mapper.MerchantMapper;
import com.mc.payment.core.service.model.enums.AccountTypeEnum;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.req.MerchantAuditReq;
import com.mc.payment.core.service.model.req.MerchantPageReq;
import com.mc.payment.core.service.model.req.MerchantSaveReq;
import com.mc.payment.core.service.model.req.MerchantUpdateReq;
import com.mc.payment.core.service.model.req.merchant.MerchantConfigPageReq;
import com.mc.payment.core.service.model.req.merchant.MerchantConfigUpdateReq;
import com.mc.payment.core.service.model.req.merchant.MerchantListReq;
import com.mc.payment.core.service.model.rsp.MerchantPageRsp;
import com.mc.payment.core.service.model.rsp.merchant.MerchantConfigGetByIdRsp;
import com.mc.payment.core.service.model.rsp.merchant.MerchantConfigPageRsp;
import com.mc.payment.core.service.model.rsp.merchant.MerchantListRsp;
import com.mc.payment.core.service.service.IAccountService;
import com.mc.payment.core.service.service.IMerchantService;
import com.mc.payment.core.service.service.MerchantChannelAssetService;
import com.mc.payment.core.service.util.ThreadTraceIdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author conor
 * @since 2024-01-31 18:46:14
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, MerchantEntity> implements IMerchantService {
    private final AppConfig appConfig;
    private final IAccountService accountService;
    private final WalletServiceFacade walletServiceFacade;
    private final MerchantChannelAssetService merchantChannelAssetService;
    @Lazy
    @Autowired
    private FireBlocksWalletManager fireBlocksWalletManager;


    @Override
    public BasePageRsp<MerchantPageRsp> page(MerchantPageReq req) {
        Page<MerchantPageRsp> page = new Page<>(req.getCurrent(), req.getSize());
        page = (Page<MerchantPageRsp>) baseMapper.page(page, req);
        return BasePageRsp.valueOf(page);
    }


    @Override
    public String save(MerchantSaveReq req) {
        req.validate();
        //  名称唯一
        if (baseMapper.exists(Wrappers.lambdaQuery(MerchantEntity.class).eq(MerchantEntity::getName, req.getName()))) {
            throw new BusinessException(ExceptionTypeEnum.ALREADY_EXIST, req.getName() + ",该名称已存在");
        }
        MerchantEntity entity = req.convert();
        String ak = IdUtil.fastSimpleUUID();
        while (this.exists(Wrappers.lambdaQuery(MerchantEntity.class).eq(MerchantEntity::getAccessKey, ak))) {
            // 生成的ak已存在 重新生成
            ak = IdUtil.fastSimpleUUID();
        }
        entity.setAccessKey(ak);
        entity.setSecretKey(IdUtil.fastSimpleUUID());
        try {
            this.save(entity);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(ExceptionTypeEnum.DEFAULT, "商户名称已存在,或生成了重复的Access Key,请重新提交");
        }
        return entity.getId();
    }

    /**
     * 商户创建完成后触发的事件 优化点:改为事件通知; 通用代码合并
     */
    private void afterMerchantCreate(MerchantEntity entity, Set<Integer> channelSubTypeSet) {
        // 集合内是否又fileblocks通道
//        if (channelSubTypeSet.contains(ChannelSubTypeEnum.FIRE_BLOCKS.getCode())) {
//            afterMerchantCreateFireBlocksHandle(entity.getId(), entity.getName());  2025年1月3日 fireblocks 走自动生成钱包规则
//        }
        if (channelSubTypeSet.contains(ChannelSubTypeEnum.OFA_PAY.getCode())) {
            afterMerchantCreateOfaPayHandle(entity.getId(), entity.getName(), AccountTypeEnum.DEPOSIT_WITHDRAWAL);
        }
        if (channelSubTypeSet.contains(ChannelSubTypeEnum.PAY_PAL.getCode())) {
            afterMerchantCreatePayPalHandle(entity.getId(), entity.getName(), AccountTypeEnum.DEPOSIT_WITHDRAWAL);
        }
        if (channelSubTypeSet.contains(ChannelSubTypeEnum.PASS_TO_PAY.getCode())) {
            afterMerchantCreatePassToPayHandle(entity.getId(), entity.getName(), AccountTypeEnum.DEPOSIT_WITHDRAWAL);
        }
        if (channelSubTypeSet.contains(ChannelSubTypeEnum.EZEEBILL.getCode())) {
            afterMerchantCreateEzeebillHandle(entity.getId(), entity.getName(), AccountTypeEnum.DEPOSIT_WITHDRAWAL);
        }
        if (channelSubTypeSet.contains(ChannelSubTypeEnum.CHEEZEE_PAY.getCode())) {
            afterMerchantCreateCheezeePayHandle(entity.getId(), entity.getName(), AccountTypeEnum.DEPOSIT_WITHDRAWAL);
        }
    }

    private void afterMerchantCreateEzeebillHandle(String merchantId, String merchantName, AccountTypeEnum accountTypeEnum) {
        boolean exists = accountService.lambdaQuery().eq(AccountEntity::getMerchantId, merchantId)
                .eq(AccountEntity::getChannelSubType, ChannelSubTypeEnum.EZEEBILL.getCode())
                .exists();
        if (exists) {
            return;
        }
        AccountEntity accountEntity = accountService.initAccount(merchantId, merchantName, ChannelSubTypeEnum.EZEEBILL.getCode(), accountTypeEnum.getCode());
        walletServiceFacade.initChannelAndMerchantWallet(accountEntity.getId());
    }

    private void afterMerchantCreatePassToPayHandle(String merchantId, String merchantName, AccountTypeEnum accountTypeEnum) {
        boolean exists = accountService.lambdaQuery().eq(AccountEntity::getMerchantId, merchantId)
                .eq(AccountEntity::getChannelSubType, ChannelSubTypeEnum.PASS_TO_PAY.getCode())
                .exists();
        if (exists) {
            return;
        }
        AccountEntity accountEntity = accountService.initAccount(merchantId, merchantName, ChannelSubTypeEnum.PASS_TO_PAY.getCode(), accountTypeEnum.getCode());
        walletServiceFacade.initChannelAndMerchantWallet(accountEntity.getId());
    }

    private void afterMerchantCreatePayPalHandle(String merchantId, String merchantName, AccountTypeEnum accountTypeEnum) {
        boolean exists = accountService.lambdaQuery().eq(AccountEntity::getMerchantId, merchantId)
                .eq(AccountEntity::getChannelSubType, ChannelSubTypeEnum.PAY_PAL.getCode())
                .exists();
        if (exists) {
            return;
        }
        AccountEntity accountEntity = accountService.initAccount(merchantId, merchantName, ChannelSubTypeEnum.PAY_PAL.getCode(), accountTypeEnum.getCode());
        walletServiceFacade.initChannelAndMerchantWallet(accountEntity.getId());
    }

    private void afterMerchantCreateOfaPayHandle(String merchantId, String merchantName, AccountTypeEnum accountTypeEnum) {
        boolean exists = accountService.lambdaQuery().eq(AccountEntity::getMerchantId, merchantId)
                .eq(AccountEntity::getChannelSubType, ChannelSubTypeEnum.OFA_PAY.getCode())
                .exists();
        if (exists) {
            return;
        }
        AccountEntity accountEntity = accountService.initAccount(merchantId, merchantName, ChannelSubTypeEnum.OFA_PAY.getCode(), accountTypeEnum.getCode());
        walletServiceFacade.initChannelAndMerchantWallet(accountEntity.getId());
    }

    public void afterMerchantCreateCheezeePayHandle(String merchantId, String merchantName, AccountTypeEnum accountTypeEnum) {
        boolean exists = accountService.lambdaQuery().eq(AccountEntity::getMerchantId, merchantId)
                .eq(AccountEntity::getChannelSubType, ChannelSubTypeEnum.CHEEZEE_PAY.getCode())
                .exists();
        if (exists) {
            return;
        }
        AccountEntity accountEntity = accountService.initAccount(merchantId, merchantName, ChannelSubTypeEnum.CHEEZEE_PAY.getCode(), accountTypeEnum.getCode());
        walletServiceFacade.initChannelAndMerchantWallet(accountEntity.getId());
    }


    /**
     * 生成fireblocks 初始化账号,还需定时任务调用远程接口才能完整创建和使用,届时还会触发钱包的生成
     *
     * @param merchantId
     * @param merchantName
     */
    private void afterMerchantCreateFireBlocksHandle(String merchantId, String merchantName) {
        boolean exists = accountService.lambdaQuery().eq(AccountEntity::getMerchantId, merchantId)
                .eq(AccountEntity::getChannelSubType, ChannelSubTypeEnum.FIRE_BLOCKS.getCode())
                .exists();
        if (exists) {
            return;
        }
        //  调用接口创建账号 10个入金 3个出金
        int depositAccountQuantity = appConfig.getDefaultDepositAccountQuantity();
        int withdrawalAccountQuantity = appConfig.getDefaultWithdrawalAccountQuantity();
        log.info("async create account depositAccountQuantity:{},withdrawalAccountQuantity:{}", depositAccountQuantity, withdrawalAccountQuantity);

        for (int i = 0; i < depositAccountQuantity; i++) {
            accountService.initAccount(merchantId, merchantName, ChannelSubTypeEnum.FIRE_BLOCKS.getCode(), AccountTypeEnum.DEPOSIT.getCode());
        }
        for (int i = 0; i < withdrawalAccountQuantity; i++) {
            accountService.initAccount(merchantId, merchantName, ChannelSubTypeEnum.FIRE_BLOCKS.getCode(), AccountTypeEnum.WITHDRAWAL.getCode());
        }
    }

    /**
     * 重置商户的SK
     *
     * @param id
     * @return
     */
    @Override
    public String resetSK(String id) {
        String sk = IdUtil.fastSimpleUUID();
        boolean update = this.update(Wrappers.lambdaUpdate(MerchantEntity.class)
                .set(MerchantEntity::getSecretKey, sk)
                .eq(MerchantEntity::getId, id));
        return update ? sk : "";
    }

    @Override
    public boolean updateById(MerchantUpdateReq req) {
        req.validate();
        MerchantEntity entity = getById(req.getId());
        if (entity == null) {
            throw new BusinessException(ExceptionTypeEnum.NOT_EXIST);
        }
        //  名称有修改则要校验唯一
        if (!entity.getName().equals(req.getName()) && baseMapper.exists(Wrappers.lambdaQuery(MerchantEntity.class).eq(MerchantEntity::getName, req.getName()))) {
            throw new BusinessException(ExceptionTypeEnum.ALREADY_EXIST, req.getName() + ",该名称已存在");
        }
        return this.updateById(req.convert());
    }

    private Set<Integer> newlyAddedChannels(String oldChannels, String newChannels) {
        if (StrUtil.equals(oldChannels, newChannels)) {
            return null;
        }
        Set<Integer> oldChannelSet = new HashSet<>();
        Set<Integer> newChannelSet = new HashSet<>();
        // 解析字符串并添加到集合中
        if (oldChannels != null && !oldChannels.isEmpty()) {
            String[] arr1 = oldChannels.split(",");
            for (String num : arr1) {
                oldChannelSet.add(Integer.valueOf(num));
            }
        }

        if (newChannels != null && !newChannels.isEmpty()) {
            String[] arr2 = newChannels.split(",");
            for (String num : arr2) {
                newChannelSet.add(Integer.valueOf(num));
            }
        }
        Set<Integer> extraInStr = new HashSet<>(newChannelSet);
        extraInStr.removeAll(oldChannelSet);

        return extraInStr;
    }

    @Override
    public MerchantEntity getByAK(String accessKey) {
        return this.getOne(Wrappers.lambdaQuery(MerchantEntity.class).eq(MerchantEntity::getAccessKey, accessKey));
    }

    /**
     * 根据渠道类型查询商户
     *
     * @param channelSubType
     * @return
     */
    @Override
    public List<MerchantEntity> listByChannel(int channelSubType) {
        return baseMapper.listByChannel(channelSubType);
    }

    @Override
    public void updateDepositAudit(MerchantAuditReq req) {
        MerchantEntity entity = new MerchantEntity();
        entity.setId(req.getId());
        entity.setDepositAudit(req.getAuditStatus());
        baseMapper.updateById(entity);
    }

    @Override
    public void updateWithdralAudit(MerchantAuditReq req) {
        MerchantEntity entity = new MerchantEntity();
        entity.setId(req.getId());
        entity.setWithdrawalAudit(req.getAuditStatus());
        baseMapper.updateById(entity);
    }

    @Override
    public BasePageRsp<MerchantConfigPageRsp> configPage(MerchantConfigPageReq req) {
        Page<MerchantConfigPageRsp> page = new Page<>(req.getCurrent(), req.getSize());
        page = (Page<MerchantConfigPageRsp>) baseMapper.configPage(page, req);
        return BasePageRsp.valueOf(page);
    }

    @Override
    public MerchantConfigGetByIdRsp getConfigById(String id) {
        MerchantEntity merchantEntity = this.getById(id);
        MerchantConfigGetByIdRsp merchantConfigGetByIdRsp = null;
        if (merchantEntity != null) {
            merchantConfigGetByIdRsp = merchantEntity.convert();
            if (!StrUtil.isBlank(merchantEntity.getChannelSubTypes())) {
                String[] split = merchantEntity.getChannelSubTypes().split(",");
                List<Integer> channelList = Arrays.stream(split).map(Integer::parseInt).toList();
                merchantConfigGetByIdRsp.setChannelList(channelList);
                merchantConfigGetByIdRsp.setChannelAssetList(merchantChannelAssetService.queryByMerchantId(id));
            }
        }
        return merchantConfigGetByIdRsp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean configUpdateById(MerchantConfigUpdateReq req) {
        req.validate();
        MerchantEntity entity = getById(req.getId());
        if (entity == null) {
            throw new BusinessException(ExceptionTypeEnum.NOT_EXIST);
        }
        String oldChannelSubTypes = entity.getChannelSubTypes();
        entity.setIpWhitelist(req.getIpWhitelist());
        entity.setAlarmEmail(req.getAlarmEmail());

        List<Integer> channelList = req.getChannelList();
        // 去重再拼接
        String subTypes = channelList.stream().map(String::valueOf).distinct().collect(Collectors.joining(","));

        entity.setChannelSubTypes(subTypes);

        boolean b = this.updateById(entity);
        merchantChannelAssetService.updateByMerchantId(req.getChannelAssetList(), entity.getId());

        if (b) {
            // 获取新增加的通道,并触发创建
            Set<Integer> newlyAddedChannels = newlyAddedChannels(oldChannelSubTypes, entity.getChannelSubTypes());
            if (CollUtil.isNotEmpty(newlyAddedChannels)) {
                ThreadTraceIdUtil.execute(() -> afterMerchantCreate(entity, newlyAddedChannels));
            }
            fireBlocksWalletManager.initGenerateWallet(entity.getId());
        }
        return b;
    }

    /**
     * 查询当前登录用户的商户列表
     *
     * @param req
     * @return
     */
    @Override
    public List<MerchantListRsp> currentLoginList(MerchantListReq req) {
        return baseMapper.currentLoginList(req);
    }
}
