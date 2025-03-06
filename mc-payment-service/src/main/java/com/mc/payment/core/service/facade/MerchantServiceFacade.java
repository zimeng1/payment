package com.mc.payment.core.service.facade;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Validator;
import com.mc.payment.core.service.config.AppConfig;
import com.mc.payment.core.service.entity.*;
import com.mc.payment.core.service.model.dto.CountAvailableWalletDto;
import com.mc.payment.core.service.model.dto.EmailJobParamByReserveDto;
import com.mc.payment.core.service.model.enums.*;
import com.mc.payment.core.service.model.req.channel.ChannelAssetConfigListReq;
import com.mc.payment.core.service.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author conor
 * @since 2024/2/19 15:52:16
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class MerchantServiceFacade {
    private final IAccountService accountService;
    private final IMerchantService merchantService;
    private final MerchantWalletService merchantWalletService;
    private final IJobPlanService jobPlanService;
    private final ChannelAssetConfigService channelAssetConfigService;
    //    private final IMerchantChannelRelationService merchantChannelRelationService;
    private final AppConfig appConfig;
    private final WalletServiceFacade walletServiceFacade;
    private final MerchantChannelAssetService merchantChannelAssetService;
    private final IEmailTemplateService emailTemplateService;

    /**
     * 补充钱包数量任务
     * 主要使用场景:
     * 1.商户钱包是payment直接生成的,不需要通过支付通道创建
     * 2.在使用过程中,新增了一个平台资产和通道资产,需要为商户生成对应资产的钱包
     */
    public void replenishWalletQuantityJob(ChannelSubTypeEnum channelSubTypeEnum) {
        if (ChannelSubTypeEnum.FIRE_BLOCKS.equals(channelSubTypeEnum)) {
            throw new IllegalArgumentException("不支持的通道类型,这个通道特殊,请使用专属方法replenishFireBlocksWalletQuantityJob");
        }
        List<AccountEntity> accountEntityList = accountService.lambdaQuery()
                .eq(AccountEntity::getChannelSubType, channelSubTypeEnum.getCode())
                .eq(AccountEntity::getStatus, AccountStatusEnum.GENERATE_SUCCESS.getCode())
                .list();
        for (AccountEntity accountEntity : accountEntityList) {
            // 查询该账号下的钱包没有的通道资产
            List<ChannelAssetConfigEntity> supportAssetList =
                    channelAssetConfigService.queryAccountNotExistWallet(accountEntity.getId(), channelSubTypeEnum.getCode());

            if (CollUtil.isEmpty(supportAssetList)) {
                log.debug("没有需要生成的钱包,accountId:{}", accountEntity.getId());
                continue;
            }
            // 生成钱包
            walletServiceFacade.initChannelAndMerchantWallet(accountEntity.getId(), supportAssetList);
        }
    }

    /**
     * 补充FireBlocks钱包数量任务
     * 1. 针对已有的账号,为其补齐配置了商户资产的钱包
     * 2. 针对数量不够的情况,生成账号,并为其生成钱包
     */
    public void replenishFireBlocksWalletQuantityJob() {
        // 钱包最低可用数
        int leCount = appConfig.getMinWalletAvailableNum();
        // 生成钱包数量
        int createCount = appConfig.getCreateWalletCount();

        List<CountAvailableWalletDto> depositWalletList = merchantWalletService.countAvailableWalletFireBlocks(leCount, true);
        List<CountAvailableWalletDto> withdrawalWalletList = merchantWalletService.countAvailableWalletFireBlocks(leCount, true);

        if (CollUtil.isEmpty(depositWalletList) && CollUtil.isEmpty(withdrawalWalletList)) {
            log.debug("没有需要生成的钱包");
            return;
        }

        // 查询通道资产配置数据
        List<ChannelAssetConfigEntity> supportAssetList = channelAssetConfigService.list(
                new ChannelAssetConfigListReq(ChannelSubTypeEnum.FIRE_BLOCKS.getCode(), null, StatusEnum.ACTIVE.getCode()));
        Map<String, ChannelAssetConfigEntity> channelAssetMap = supportAssetList.stream().collect(Collectors.toMap(o -> o.getAssetName() + o.getNetProtocol(), Function.identity()));

        this.initChannelAndMerchantWalletFromFireBlocks(depositWalletList, channelAssetMap, createCount, AccountTypeEnum.DEPOSIT);
        // 出金钱包默认生成3个
        this.initChannelAndMerchantWalletFromFireBlocks(withdrawalWalletList, channelAssetMap, 3, AccountTypeEnum.WITHDRAWAL);
    }

    /**
     * 补充FireBlocks钱包数量任务-新
     */
    public void replenishFireBlocksWalletQuantityJobNew() {
        // 查询查询配置了自动生成钱包的商户资产配置
        List<MerchantChannelAssetEntity> merchantChannelAssetList = merchantChannelAssetService.lambdaQuery()
                .eq(MerchantChannelAssetEntity::getChannelSubType, ChannelSubTypeEnum.FIRE_BLOCKS.getCode())
                .eq(MerchantChannelAssetEntity::getGenerateWalletStatus, BooleanStatusEnum.ITEM_1.getCode())
                .list();
        // 按商户id分组
        Map<String, List<MerchantChannelAssetEntity>> merchantChannelAssetMap = merchantChannelAssetList.stream()
                .collect(Collectors.groupingBy(MerchantChannelAssetEntity::getMerchantId));
        for (Map.Entry<String, List<MerchantChannelAssetEntity>> entry : merchantChannelAssetMap.entrySet()) {
            String merchantId = entry.getKey();
            MerchantEntity merchantEntity = merchantService.getById(merchantId);
            for (MerchantChannelAssetEntity merchantChannelAssetEntity : entry.getValue()) {
//                // 查询未建该资产的账号,让其生成钱包
//                List<String> accountIds = accountService.queryAccountIdNotExistWallet(merchantId, merchantChannelAssetEntity.getAssetName(), merchantChannelAssetEntity.getNetProtocol(),
//                        ChannelSubTypeEnum.FIRE_BLOCKS.getCode(), AccountTypeEnum.DEPOSIT.getCode());
//                if (CollUtil.isEmpty(accountIds)) {
//                    log.debug("未建资产的账号数不足,直接创新账号,merchantId:{},assetName:{},netProtocol:{}", merchantId, merchantChannelAssetEntity.getAssetName(), merchantChannelAssetEntity.getNetProtocol());
//                    accountService.initAccount(merchantId, merchantEntity.getName(),
//                            ChannelSubTypeEnum.FIRE_BLOCKS.getCode(), AccountTypeEnum.DEPOSIT.getCode());
//                } else {
//                    for (String accountId : accountIds) {
//                        log.debug("生成钱包,accountId:{},merchantChannelAssetEntity:{}", accountId, merchantChannelAssetEntity);
//                        walletServiceFacade.initChannelAndMerchantWallet(accountId, Collections.singletonList(merchantChannelAssetEntity));
//                    }
//                }
            }
        }
    }

    private void initChannelAndMerchantWalletFromFireBlocks(List<CountAvailableWalletDto> list,
                                                            Map<String, ChannelAssetConfigEntity> channelAssetMap,
                                                            int createCount,
                                                            AccountTypeEnum accountTypeEnum) {
        if (CollUtil.isEmpty(list)) {
            log.debug("没有需要生成的钱包 accountTypeEnum:{}", accountTypeEnum.getCode());
            return;
        }
        Map<String, List<CountAvailableWalletDto>> map = list.stream()
                .collect(Collectors.groupingBy(CountAvailableWalletDto::getMerchantId));

        for (Map.Entry<String, List<CountAvailableWalletDto>> entry : map.entrySet()) {
            String merchantId = entry.getKey();
            List<CountAvailableWalletDto> values = entry.getValue();
            boolean needCreateAccount = false;
            for (CountAvailableWalletDto value : values) {
                ChannelAssetConfigEntity channelAssetEntity = channelAssetMap.get(value.getAssetName() + value.getNetProtocol());
                if (channelAssetEntity == null) {
                    log.error("出现通道资产配置不存在的情况,跳过,merchantId:{},assetName:{},netProtocol:{}", merchantId, value.getAssetName(), value.getNetProtocol());
                    continue;
                }
                // 查询未建该资产的账号,让其生成钱包
                List<String> accountIds = accountService.queryAccountIdNotExistWallet(merchantId, value.getAssetName(), value.getNetProtocol(),
                        ChannelSubTypeEnum.FIRE_BLOCKS.getCode(), accountTypeEnum.getCode());
                if (CollUtil.isEmpty(accountIds) || accountIds.size() < createCount) {
                    log.debug("未建资产的账号数不足,直接创新账号,merchantId:{},assetName:{},netProtocol:{}", merchantId, value.getAssetName(), value.getNetProtocol());
                    needCreateAccount = true;
                } else {
                    for (int i = 0; i < createCount; i++) {
                        String accountId = accountIds.get(i);
                        log.debug("生成钱包,accountId:{},channelAssetEntity:{}", accountId, channelAssetEntity);
                        walletServiceFacade.initChannelAndMerchantWallet(accountId, Collections.singletonList(channelAssetEntity));
                    }
                }
            }
            // 出现没有未建资产的账号,则无脑生成createCount个拥有全量币种的账号  todo 这里可优化为对新生成账号生成指定币种钱包,目前改动太大不这样子做
            if (needCreateAccount) {
                for (int i = 0; i < createCount; i++) {
                    log.debug("生成账号,merchantId:{}", merchantId);
                    accountService.initAccount(merchantId, values.get(0).getMerchantName(),
                            ChannelSubTypeEnum.FIRE_BLOCKS.getCode(), accountTypeEnum.getCode());
                }
            }
        }
    }

    /**
     * 扫描商户的出金地址是否低于设置的备付金,如果是则生成告警邮箱任务计划
     */
    public void scanMerchantReserveRatioJob() {
        List<MerchantEntity> list = merchantService.lambdaQuery()
                .eq(MerchantEntity::getStatus, StatusEnum.ACTIVE.getCode())
                .list();
        for (MerchantEntity merchantEntity : list) {
            try {
                // 查询开启了备付金告警功能的商户可用资产配置
                List<MerchantChannelAssetEntity> alarmMerchantChannelAssetList =
                        merchantChannelAssetService.lambdaQuery()
                                .eq(MerchantChannelAssetEntity::getMerchantId, merchantEntity.getId())
                                .eq(MerchantChannelAssetEntity::getAlarmStatus, StatusEnum.ACTIVE.getCode())
                                .list();
                if (CollUtil.isEmpty(alarmMerchantChannelAssetList)) {
                    continue;
                }

                // 查询商户的出金钱包数据
                List<MerchantWalletEntity> merchantWalletEntityList = merchantWalletService.lambdaQuery()
                        .eq(MerchantWalletEntity::getMerchantId, merchantEntity.getId())
                        .in(MerchantWalletEntity::getPurposeType, PurposeTypeEnum.WITHDRAWAL.getCode(), PurposeTypeEnum.DEPOSIT_WITHDRAWAL.getCode())
                        .ge(MerchantWalletEntity::getStatus, MerchantWalletStatusEnum.USED_WAIT.getCode())
                        .list();
                // key = channelSubType + assetName + netProtocol
                Map<String, List<MerchantWalletEntity>> walletMap = merchantWalletEntityList.stream()
                        .collect(Collectors.groupingBy(o -> o.getChannelSubType() + o.getAssetName() + o.getNetProtocol()));
                for (MerchantChannelAssetEntity merchantChannelAssetEntity : alarmMerchantChannelAssetList) {
                    String key = merchantChannelAssetEntity.getChannelSubType() + merchantChannelAssetEntity.getAssetName() + merchantChannelAssetEntity.getNetProtocol();
                    List<MerchantWalletEntity> walletList = walletMap.get(key);
                    if (CollUtil.isEmpty(walletList)) {
                        continue;
                    }
                    BigDecimal sumUsdtAmount = walletList.stream().map(MerchantWalletEntity::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
                    if (sumUsdtAmount.compareTo(merchantChannelAssetEntity.getReserveAlarmValue()) < 0) {
                        addSendEmailJob(merchantEntity, merchantChannelAssetEntity.getAssetName(),
                                merchantChannelAssetEntity.getNetProtocol());
                    }
                }
            } catch (Exception e) {
                log.error("扫描生成商户备付金告警 异常", e);
            }
        }

    }

    /**
     * 生成告警邮箱任务计划
     *
     * @param merchantEntity 商户信息
     * @param assetName      资产名- 需要通知的资产名
     */
    private void addSendEmailJob(MerchantEntity merchantEntity, String assetName, String netProtocol) {
        log.info("商户[{}]的出金地址低于设置的备付金,生成告警邮箱任务计划", merchantEntity.getName());
        if (!Validator.isEmail(merchantEntity.getAlarmEmail())) {
            log.error("商户[{}]的告警邮箱不合法,不生成告警任务", merchantEntity.getName());
            return;
        }

        //这里需要发送邮件通知用户
        EmailTemplateEntity emailTemplateEntity = emailTemplateService.getEmailTemplateCacheMap().get(EmailContentEnum.INSUFFICIENT_BALANCE.getCode());

        String assetMsg = StringUtils.isBlank(assetName) ? "" : "，您的资产：" + assetName + "，网络协议：" + netProtocol;
        EmailJobParamByReserveDto emailJobParamDto = new EmailJobParamByReserveDto(merchantEntity.getAlarmEmail(),
                EmailContentEnum.INSUFFICIENT_BALANCE.getSubject(),
                emailTemplateEntity.getContent().replaceFirst("%s", merchantEntity.getName() + assetMsg),
                merchantEntity.getId(),
                assetName);
        jobPlanService.addJobPlan(JobPlanHandlerEnum.SEND_EMAIL, emailJobParamDto);
    }


}
