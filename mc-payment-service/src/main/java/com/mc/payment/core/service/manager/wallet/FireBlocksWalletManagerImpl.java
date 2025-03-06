package com.mc.payment.core.service.manager.wallet;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.config.AppConfig;
import com.mc.payment.core.service.entity.AccountEntity;
import com.mc.payment.core.service.entity.ChannelWalletEntity;
import com.mc.payment.core.service.entity.MerchantEntity;
import com.mc.payment.core.service.entity.MerchantWalletEntity;
import com.mc.payment.core.service.model.dto.MerchantAssetDetailDto;
import com.mc.payment.core.service.model.dto.MerchantAssetDto;
import com.mc.payment.core.service.model.dto.MerchantAvailableWalletDto;
import com.mc.payment.core.service.model.dto.MerchantGenerateWalletAssetDto;
import com.mc.payment.core.service.model.enums.*;
import com.mc.payment.core.service.model.req.merchant.GenerateWalletAsset;
import com.mc.payment.core.service.model.req.merchant.GenerateWalletReq;
import com.mc.payment.core.service.service.*;
import com.mc.payment.fireblocksapi.FireBlocksAPI;
import com.mc.payment.fireblocksapi.model.req.fireBlocks.CreateAccountReq;
import com.mc.payment.fireblocksapi.model.req.fireBlocks.CreateWalletReq;
import com.mc.payment.fireblocksapi.model.req.fireBlocks.QueryAssetAddressesReq;
import com.mc.payment.fireblocksapi.model.vo.fireBlocks.CreateVaultAssetVo;
import com.mc.payment.fireblocksapi.model.vo.fireBlocks.PaginatedAddressVo;
import com.mc.payment.fireblocksapi.model.vo.fireBlocks.VaultAccountVo;
import com.mc.payment.fireblocksapi.model.vo.fireBlocks.nested.VaultWalletAddressVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FireBlocksWalletManagerImpl implements FireBlocksWalletManager {
    private final IAccountService accountService;
    private final IMerchantService merchantService;
    private final MerchantChannelAssetService merchantChannelAssetService;
    private final ChannelWalletService channelWalletService;
    private final MerchantWalletService merchantWalletService;
    private final FireBlocksAPI fireBlocksAPI;
    private final AppConfig appConfig;

    /**
     * 基于商户配置中的自动生成钱包配置,自动生成钱包(待使用状态)
     */
    @Override
    public void autoGenerateWalletJob() {
        log.info("自动生成钱包任务开始");
        // 1.查询启用状态的商户配置的自动生成钱包的资产列表
        List<MerchantGenerateWalletAssetDto> merchantGenerateWalletAssetDtos = merchantChannelAssetService.queryMerchantGenerateWalletAsset(AssetTypeEnum.CRYPTO_CURRENCY.getCode(),
                ChannelSubTypeEnum.FIRE_BLOCKS.getCode(), BooleanStatusEnum.ITEM_1.getCode());

        // 商户资产配置 key:商户id value:商户资产配置列表
        Map<String, List<MerchantGenerateWalletAssetDto>> merchantChannelAssetDtoMap = merchantGenerateWalletAssetDtos.stream()
                .collect(Collectors.groupingBy(MerchantGenerateWalletAssetDto::getMerchantId));


        for (Map.Entry<String, List<MerchantGenerateWalletAssetDto>> entry : merchantChannelAssetDtoMap.entrySet()) {
            String merchantId = entry.getKey();
            List<MerchantGenerateWalletAssetDto> merchantChannelAssetDtos = entry.getValue();
            String merchantName = merchantChannelAssetDtos.get(0).getMerchantName();

            // 3. 检索每个商户可用钱包的数量
            List<MerchantAvailableWalletDto> merchantAvailableWalletDtos = merchantWalletService.queryAvailableWallet(merchantId, ChannelSubTypeEnum.FIRE_BLOCKS.getCode());
            // 商户可用钱包 key:商户id+资产名称+网络协议+用途类型 value:可用钱包
            Map<String, MerchantAvailableWalletDto> merchantAvailableWalletDtoMap = merchantAvailableWalletDtos.stream()
                    .collect(Collectors.toMap(o -> merchantId + o.getAssetName() + o.getNetProtocol() + o.getPurposeType(),
                            Function.identity()));
            int depositWalletGenerateQuantity = 0;
            int withdrawalWalletGenerateQuantity = 0;

            for (MerchantGenerateWalletAssetDto merchantChannelAssetDto : merchantChannelAssetDtos) {
                log.info("商户:{},资产:{}-{}生成钱包", merchantId, merchantChannelAssetDto.getAssetName(), merchantChannelAssetDto.getNetProtocol());
                // 入金钱包
                String key = merchantId + merchantChannelAssetDto.getAssetName() + merchantChannelAssetDto.getNetProtocol() + PurposeTypeEnum.DEPOSIT.getCode();
                MerchantAvailableWalletDto merchantAvailableWalletDto = merchantAvailableWalletDtoMap.get(key);

                log.info("入金钱包当前可用情况:{},自动生成钱包规则-阈值:{},生成数量:{}", merchantChannelAssetDto,
                        merchantChannelAssetDto.getGenerateWalletLeQuantity(), merchantChannelAssetDto.getGenerateWalletQuantity());
                if (merchantAvailableWalletDto == null || merchantAvailableWalletDto.getWalletCount() <= merchantChannelAssetDto.getGenerateWalletLeQuantity()) {
                    // 说明需要生成钱包
                    depositWalletGenerateQuantity = merchantChannelAssetDto.getGenerateWalletQuantity();
                    this.generateWallet(merchantId, merchantChannelAssetDto.getMerchantName(), merchantChannelAssetDto,
                            depositWalletGenerateQuantity, PurposeTypeEnum.DEPOSIT);
                }

                // 出金钱包 2025年2月5日  自动逻辑只处理入金钱包
//                key = merchantId + merchantChannelAssetDto.getAssetName() + merchantChannelAssetDto.getNetProtocol() + PurposeTypeEnum.WITHDRAWAL.getCode();
//                merchantAvailableWalletDto = merchantAvailableWalletDtoMap.get(key);
//                log.info("出金钱包可用情况:{}", merchantAvailableWalletDto);
//                if (merchantAvailableWalletDto == null) {
//                    // 说明是第一次生成钱包,要生成三个出金钱包
//                    withdrawalWalletGenerateQuantity = 3;
//                    this.generateWallet(merchantId, merchantChannelAssetDto.getMerchantName(), merchantChannelAssetDto, withdrawalWalletGenerateQuantity, PurposeTypeEnum.WITHDRAWAL);
//                }
            }
            log.info("商户:{}-{},本次自动生成钱包结束,入金钱包生成数量:{},出金钱包生成数量:{}", merchantId, merchantName,
                    depositWalletGenerateQuantity,
                    withdrawalWalletGenerateQuantity);
        }
    }

    /**
     * 生成账号和钱包(账号可能会沿用之前的,前提是这个账号没有这个资产的钱包)
     *
     * @param merchantId
     * @param merchantName
     * @param merchantChannelAssetDto
     * @param generateWalletQuantity
     * @param purposeTypeEnum
     */
    private void generateWallet(String merchantId, String merchantName, MerchantGenerateWalletAssetDto merchantChannelAssetDto,
                                Integer generateWalletQuantity, PurposeTypeEnum purposeTypeEnum) {
        log.info("generateWallet:商户:{},资产:{}-{}生成钱包数量:{},purposeTypeEnum:{}", merchantId,
                merchantChannelAssetDto.getAssetName(),
                merchantChannelAssetDto.getNetProtocol(), generateWalletQuantity, purposeTypeEnum);
        List<String> accountIds = accountService.queryAccountIdNotExistWallet(merchantId,
                merchantChannelAssetDto.getAssetName(),
                merchantChannelAssetDto.getNetProtocol(),
                ChannelSubTypeEnum.FIRE_BLOCKS.getCode(),
                purposeTypeEnum.getCode());


        for (int i = 0; i < generateWalletQuantity; i++) {
            AccountEntity accountEntity;
            if (i < accountIds.size()) {
                log.info("商户:{},资产:{}-{}生成钱包,使用已有账号:{},第{}个", merchantId, merchantChannelAssetDto.getAssetName(),
                        merchantChannelAssetDto.getNetProtocol(), accountIds.get(i), i);
                // 说明有空闲的账号,直接用
                accountEntity = accountService.getById(accountIds.get(i));
            } else {
                log.info("商户:{},资产:{}-{}生成钱包,需要新建账号,第{}个", merchantId, merchantChannelAssetDto.getAssetName(),
                        merchantChannelAssetDto.getNetProtocol(), i);
                // 生成可用的账号0
                accountEntity = accountService.generateAccount(merchantId, merchantName, ChannelSubTypeEnum.FIRE_BLOCKS, purposeTypeEnum.getCode());
            }

            // 生成钱包
            MerchantAssetDto assetDto = new MerchantAssetDto();
            assetDto.setAssetName(merchantChannelAssetDto.getAssetName());
            assetDto.setNetProtocol(merchantChannelAssetDto.getNetProtocol());
            ImmutablePair<ChannelWalletEntity, MerchantWalletEntity> immutablePair = this.saveWalletEntity(assetDto, accountEntity);
            ChannelWalletEntity channelWalletEntity = immutablePair.left;
            MerchantWalletEntity merchantWalletEntity = immutablePair.right;

            // 保存钱包
            ImmutableTriple<Integer, String, String> immutableTriple = createWalletByApi(accountEntity.getExternalId(), merchantChannelAssetDto.getChannelAssetName());

            Integer status = immutableTriple.left;
            String address = immutableTriple.middle;
            String statusMsg = immutableTriple.right;

            channelWalletEntity.setStatus(status);
            channelWalletEntity.setStatusMsg(statusMsg);
            if (StrUtil.isNotBlank(address)) {
                channelWalletEntity.setWalletAddress(address);
                merchantWalletEntity.setWalletAddress(address);
            }
            channelWalletService.save(channelWalletEntity);
            merchantWalletEntity.setChannelWalletId(channelWalletEntity.getId());
            merchantWalletEntity.setStatus(status);
            merchantWalletEntity.setStatusMsg(statusMsg);
            merchantWalletService.save(merchantWalletEntity);
        }
    }

    /**
     * 基于前端提交的请求,生成钱包,(待创建的),完整的生成钱包需要定时任务generateWalletJob去处理
     *
     * @param req
     * @param purposeTypeEnum
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateWallet(GenerateWalletReq req, PurposeTypeEnum purposeTypeEnum) {
        String merchantId = req.getMerchantId();
        List<GenerateWalletAsset> generateWalletAssets = req.getGenerateWalletAssets();
        log.info("商户:{},生成钱包资产列表: {}", merchantId, generateWalletAssets);
        // 校验商户是否存在
        MerchantEntity merchantEntity = merchantService.getById(merchantId);
        if (merchantEntity == null) {
            throw new ValidateException("商户不存在");
        }
        // 校验所选资产是否属于商户配置的资产列表中
        List<MerchantAssetDetailDto> merchantAssetDtos = merchantChannelAssetService.queryAssetDetail(merchantId,
                AssetTypeEnum.CRYPTO_CURRENCY.getCode(), ChannelSubTypeEnum.FIRE_BLOCKS.getCode(), null);
        Map<String, MerchantAssetDto> merchantAssetDtoMap =
                merchantAssetDtos.stream().collect(Collectors.toMap(o -> o.getAssetName() + o.getNetProtocol(), Function.identity()));
        for (GenerateWalletAsset generateWalletAsset : generateWalletAssets) {
            if (!merchantAssetDtoMap.containsKey(generateWalletAsset.getAssetName() + generateWalletAsset.getNetProtocol())) {
                throw new ValidateException("资产:" + generateWalletAsset.getAssetName() + "-" + generateWalletAsset.getNetProtocol() +
                        "不属于商户配置的资产列表中");
            }
        }

        // 本次生成钱包资产的最大数量, 用于判断是否需要多少个账号
        int maxQuantity = generateWalletAssets.stream().mapToInt(GenerateWalletAsset::getQuantity).max().orElse(0);
        List<AccountEntity> list = new ArrayList<>();
        for (int i = 0; i < maxQuantity; i++) {
            // 生成待建账号记录
            AccountEntity accountEntity = accountService.initAccount(merchantId, merchantEntity.getName(),
                    ChannelSubTypeEnum.FIRE_BLOCKS.getCode(), purposeTypeEnum.getCode());
            list.add(accountEntity);
        }
        // 生成初待建钱包记录
        for (GenerateWalletAsset generateWalletAsset : generateWalletAssets) {
            MerchantAssetDto assetDto = merchantAssetDtoMap.get(generateWalletAsset.getAssetName() + generateWalletAsset.getNetProtocol());
            for (int i = 0; i < generateWalletAsset.getQuantity(); i++) {
                AccountEntity accountEntity = list.get(i);
                // 生成钱包
                ImmutablePair<ChannelWalletEntity, MerchantWalletEntity> immutablePair = this.saveWalletEntity(assetDto, accountEntity);
                ChannelWalletEntity channelWalletEntity = immutablePair.left;
                MerchantWalletEntity merchantWalletEntity = immutablePair.right;
                channelWalletService.save(channelWalletEntity);
                merchantWalletEntity.setChannelWalletId(channelWalletEntity.getId());
                merchantWalletService.save(merchantWalletEntity);
            }
        }

    }

    private ImmutablePair<ChannelWalletEntity, MerchantWalletEntity> saveWalletEntity(MerchantAssetDto assetDto,
                                                                                      AccountEntity accountEntity) {
        ChannelWalletEntity channelWalletEntity = new ChannelWalletEntity();
        channelWalletEntity.setAssetType(AssetTypeEnum.CRYPTO_CURRENCY.getCode());
        channelWalletEntity.setChannelSubType(ChannelSubTypeEnum.FIRE_BLOCKS.getCode());
        channelWalletEntity.setAssetName(assetDto.getAssetName());
        channelWalletEntity.setNetProtocol(assetDto.getNetProtocol());
        channelWalletEntity.setWalletAddress("temp#" + accountEntity.getId() + "_" + System.currentTimeMillis());
        channelWalletEntity.setBalance(BigDecimal.ZERO);
        channelWalletEntity.setFreezeAmount(BigDecimal.ZERO);
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("assetId", assetDto.getChannelAssetName());
        jsonObject.set("accountId", accountEntity.getId());
        // jsonObject.set("vaultAccountId", accountEntity.getExternalId()); 账号创建完成后才有
        channelWalletEntity.setApiCredential(jsonObject.toString());
        channelWalletEntity.setRemark("");
        channelWalletEntity.setStatus(ChannelWalletStatusEnum.GENERATE_WAIT.getCode());
        channelWalletEntity.setStatusMsg("");

        MerchantWalletEntity merchantWalletEntity = new MerchantWalletEntity();
        merchantWalletEntity.setMerchantId(accountEntity.getMerchantId());
        merchantWalletEntity.setAccountId(accountEntity.getId());
        merchantWalletEntity.setAccountName(accountEntity.getName());
        merchantWalletEntity.setAssetType(AssetTypeEnum.CRYPTO_CURRENCY.getCode());
        merchantWalletEntity.setChannelSubType(ChannelSubTypeEnum.FIRE_BLOCKS.getCode());
        merchantWalletEntity.setAssetName(assetDto.getAssetName());
        merchantWalletEntity.setNetProtocol(assetDto.getNetProtocol());
        merchantWalletEntity.setPurposeType(accountEntity.getAccountType());
        merchantWalletEntity.setWalletAddress("temp#" + accountEntity.getId() + "_" + System.currentTimeMillis());
        merchantWalletEntity.setBalance(BigDecimal.ZERO);
        merchantWalletEntity.setFreezeAmount(BigDecimal.ZERO);
        merchantWalletEntity.setRemark("");
        merchantWalletEntity.setStatus(MerchantWalletStatusEnum.GENERATE_WAIT.getCode());
        merchantWalletEntity.setStatusMsg("");

        return ImmutablePair.of(channelWalletEntity, merchantWalletEntity);
    }

    /**
     * 扫描待创建的钱包,生成钱包
     */
    @Override
    public void generateWalletJob() {
        // 1.查询待建通道钱包数据
        List<ChannelWalletEntity> list = channelWalletService.lambdaQuery()
                .eq(ChannelWalletEntity::getAssetType, AssetTypeEnum.CRYPTO_CURRENCY.getCode())
                .eq(ChannelWalletEntity::getChannelSubType, ChannelSubTypeEnum.FIRE_BLOCKS.getCode())
                .eq(ChannelWalletEntity::getStatus, ChannelWalletStatusEnum.GENERATE_WAIT.getCode())
                .orderByAsc(BaseNoLogicalDeleteEntity::getId)
                .last("LIMIT 50").list();
        log.info("待建钱包数量:{}", list.size());

        for (ChannelWalletEntity channelWalletEntity : list) {
            log.info("对待建钱包进行生成,channelWalletEntity:{}", channelWalletEntity);
            JSONObject jsonObject = JSONUtil.parseObj(channelWalletEntity.getApiCredential());
            String assetId = jsonObject.getStr("assetId");
            String accountId = jsonObject.getStr("accountId");
            if (StrUtil.isBlank(assetId) || StrUtil.isBlank(accountId)) {
                log.error("assetId or accountId is null,channelWalletId:{},assetId:{},accountId:{}", channelWalletEntity.getId(), assetId, accountId);
                continue;
            }
            // 判断是否需要新建账号
            AccountEntity accountEntity = accountService.getById(accountId);
            if (accountEntity.getStatus() != AccountStatusEnum.GENERATE_SUCCESS.getCode()) {
                generateForeBlocksAccount(accountEntity);
            }
            if (accountEntity.getStatus() != AccountStatusEnum.GENERATE_SUCCESS.getCode()) {
                log.error("accountEntity status is not success,channelWalletId:{},accountId:{},status:{}", channelWalletEntity.getId(), accountId, accountEntity.getStatus());
                continue;
            }
            // 调用远程接口创建远程钱包
            createFireBlocksWallet(channelWalletEntity, accountEntity.getExternalId());
        }
        log.info("待建钱包生成结束");
    }

    /**
     * 取商户配置的可用资产,生成钱包
     * 用于第一次商户配置资产时,初始化钱包的,默认是10个入金,3个出金
     * todo 优化
     *
     * @param merchantId
     */
    @Override
    public void initGenerateWallet(String merchantId) {
        MerchantEntity merchantEntity = merchantService.getById(merchantId);
        if (merchantEntity == null) {
            throw new ValidateException("商户不存在");
        }
        List<MerchantAssetDetailDto> merchantAssetDtos = merchantChannelAssetService.queryAssetDetail(merchantId,
                AssetTypeEnum.CRYPTO_CURRENCY.getCode(), ChannelSubTypeEnum.FIRE_BLOCKS.getCode(), null);
        // 需要生成的入金的资产
        List<MerchantAssetDetailDto> depositMerchantAssetDtos = new ArrayList<>();
        // 需要生成的出金的资产
        List<MerchantAssetDetailDto> withdrawalMerchantAssetDtos = new ArrayList<>();
        for (MerchantAssetDetailDto merchantAssetDto : merchantAssetDtos) {
            boolean exists = merchantWalletService.lambdaQuery().eq(MerchantWalletEntity::getMerchantId, merchantId)
                    .eq(MerchantWalletEntity::getAssetName, merchantAssetDto.getAssetName())
                    .eq(MerchantWalletEntity::getNetProtocol, merchantAssetDto.getNetProtocol())
                    .eq(MerchantWalletEntity::getChannelSubType, ChannelSubTypeEnum.FIRE_BLOCKS.getCode())
                    .ne(MerchantWalletEntity::getStatus, MerchantWalletStatusEnum.GENERATE_FAIL.getCode())
                    .eq(MerchantWalletEntity::getPurposeType, PurposeTypeEnum.DEPOSIT.getCode())
                    .exists();
            if (!exists) {
                depositMerchantAssetDtos.add(merchantAssetDto);
            }
            exists = merchantWalletService.lambdaQuery().eq(MerchantWalletEntity::getMerchantId, merchantId)
                    .eq(MerchantWalletEntity::getAssetName, merchantAssetDto.getAssetName())
                    .eq(MerchantWalletEntity::getNetProtocol, merchantAssetDto.getNetProtocol())
                    .eq(MerchantWalletEntity::getChannelSubType, ChannelSubTypeEnum.FIRE_BLOCKS.getCode())
                    .ne(MerchantWalletEntity::getStatus, MerchantWalletStatusEnum.GENERATE_FAIL.getCode())
                    .eq(MerchantWalletEntity::getPurposeType, PurposeTypeEnum.WITHDRAWAL.getCode())
                    .exists();
            if (!exists) {
                withdrawalMerchantAssetDtos.add(merchantAssetDto);
            }
        }

        //  调用接口创建账号 10个入金 3个出金
        int depositAccountQuantity = appConfig.getDefaultDepositAccountQuantity();
        int withdrawalAccountQuantity = appConfig.getDefaultWithdrawalAccountQuantity();
        if (!depositMerchantAssetDtos.isEmpty()) {
            for (int i = 0; i < depositAccountQuantity; i++) {
                AccountEntity accountEntity = accountService.initAccount(merchantId, merchantEntity.getName(),
                        ChannelSubTypeEnum.FIRE_BLOCKS.getCode(), PurposeTypeEnum.DEPOSIT.getCode());
                for (MerchantAssetDetailDto merchantAssetDto : depositMerchantAssetDtos) {
                    log.info("商户:{},资产:{}-{}初始化生成入金钱包", merchantId, merchantAssetDto.getAssetName(),
                            merchantAssetDto.getNetProtocol());
                    ImmutablePair<ChannelWalletEntity, MerchantWalletEntity> immutablePair = this.saveWalletEntity(merchantAssetDto, accountEntity);
                    ChannelWalletEntity channelWalletEntity = immutablePair.left;
                    MerchantWalletEntity merchantWalletEntity = immutablePair.right;
                    channelWalletService.save(channelWalletEntity);
                    merchantWalletEntity.setChannelWalletId(channelWalletEntity.getId());
                    merchantWalletService.save(merchantWalletEntity);
                }
            }
        }
        if (!withdrawalMerchantAssetDtos.isEmpty()) {
            List<AccountEntity> list = accountService.lambdaQuery().eq(AccountEntity::getMerchantId, merchantId)
                    .eq(AccountEntity::getChannelSubType, ChannelSubTypeEnum.FIRE_BLOCKS.getCode())
                    .eq(AccountEntity::getAccountType, PurposeTypeEnum.WITHDRAWAL.getCode())
                    .eq(AccountEntity::getStatus, AccountStatusEnum.GENERATE_SUCCESS.getCode())
                    .list();
            for (int i = 0; i < withdrawalAccountQuantity; i++) {
                AccountEntity accountEntity = list.isEmpty() ? null : list.get(i);
                if (accountEntity == null) {
                    accountEntity = accountService.initAccount(merchantId, merchantEntity.getName(),
                            ChannelSubTypeEnum.FIRE_BLOCKS.getCode(), PurposeTypeEnum.WITHDRAWAL.getCode());
                }
                for (MerchantAssetDetailDto merchantAssetDto : withdrawalMerchantAssetDtos) {
                    log.info("商户:{},资产:{}-{}初始化生成出金钱包", merchantId, merchantAssetDto.getAssetName(),
                            merchantAssetDto.getNetProtocol());
                    ImmutablePair<ChannelWalletEntity, MerchantWalletEntity> immutablePair = this.saveWalletEntity(merchantAssetDto, accountEntity);
                    ChannelWalletEntity channelWalletEntity = immutablePair.left;
                    MerchantWalletEntity merchantWalletEntity = immutablePair.right;
                    channelWalletService.save(channelWalletEntity);
                    merchantWalletEntity.setChannelWalletId(channelWalletEntity.getId());
                    merchantWalletService.save(merchantWalletEntity);
                }
            }
        }
    }

    private void generateForeBlocksAccount(AccountEntity accountEntity) {
        accountService.lambdaUpdate()
                .set(AccountEntity::getStatus, AccountStatusEnum.GENERATE_ING.getCode())
                .eq(AccountEntity::getId, accountEntity.getId()).update();
        // 调用远程接口创建远程账号
        CreateAccountReq createAccountReq = new CreateAccountReq();
        createAccountReq.setName(accountEntity.getName());
        RetResult<VaultAccountVo> retResult = fireBlocksAPI.createAccount(createAccountReq);
        log.debug("fireBlocksAPI.createAccount createAccountReq:{},ret:{}", createAccountReq, retResult);
        if (retResult.isSuccess()) {
            accountEntity.setStatus(AccountStatusEnum.GENERATE_SUCCESS.getCode());
            accountEntity.setExternalId(retResult.getData().getId());
        } else {
            accountEntity.setStatus(AccountStatusEnum.GENERATE_FAIL.getCode());
            accountEntity.setStatusMsg(retResult.getMsg());
        }
        accountService.updateById(accountEntity);
    }


    /**
     * 暴露一个手动接口 方便对失败的进行重试
     *
     * @param channelWalletEntity
     */
    private void createFireBlocksWallet(ChannelWalletEntity channelWalletEntity, String vaultAccountId) {
        String channelWalletId = channelWalletEntity.getId();
        String apiCredential = channelWalletEntity.getApiCredential();
        String assetId;
        try {
            JSONObject jsonObject = JSONUtil.parseObj(apiCredential);
            assetId = jsonObject.getStr("assetId");
            if (StrUtil.isBlank(vaultAccountId) || StrUtil.isBlank(assetId)) {
                log.error("vaultAccountId or assetId is null,channelWalletId:{},vaultAccountId:{},assetId:{}", channelWalletId, vaultAccountId, assetId);
                return;
            }
        } catch (Exception e) {
            log.error("apiCredential is error,channelWalletId:{},apiCredential:{}", channelWalletId, apiCredential);
            return;
        }

        Integer status = ChannelWalletStatusEnum.GENERATE_ING.getCode();
        channelWalletService.update(Wrappers.lambdaUpdate(ChannelWalletEntity.class)
                .eq(BaseNoLogicalDeleteEntity::getId, channelWalletId)
                .eq(ChannelWalletEntity::getStatus, ChannelWalletStatusEnum.GENERATE_WAIT.getCode())
                .set(ChannelWalletEntity::getStatus, status));

        ImmutableTriple<Integer, String, String> immutableTriple = createWalletByApi(vaultAccountId, assetId);
        status = immutableTriple.left;
        String address = immutableTriple.middle;
        String statusMsg = immutableTriple.right;

        channelWalletService.update(Wrappers.lambdaUpdate(ChannelWalletEntity.class)
                .eq(BaseNoLogicalDeleteEntity::getId, channelWalletId)
                .eq(ChannelWalletEntity::getStatus, ChannelWalletStatusEnum.GENERATE_ING.getCode())
                .set(ChannelWalletEntity::getStatus, status)
                .set(StrUtil.isNotBlank(statusMsg), ChannelWalletEntity::getStatusMsg, statusMsg)
                .set(StrUtil.isNotBlank(address), ChannelWalletEntity::getWalletAddress, address));

        merchantWalletService.update(Wrappers.lambdaUpdate(MerchantWalletEntity.class)
                .eq(MerchantWalletEntity::getAssetType, AssetTypeEnum.CRYPTO_CURRENCY.getCode())
                .eq(MerchantWalletEntity::getChannelSubType, ChannelSubTypeEnum.FIRE_BLOCKS.getCode())
                .eq(MerchantWalletEntity::getStatus, MerchantWalletStatusEnum.GENERATE_WAIT.getCode())
                .eq(MerchantWalletEntity::getChannelWalletId, channelWalletId)
                .set(MerchantWalletEntity::getStatus, status)
                .set(StrUtil.isNotBlank(statusMsg), MerchantWalletEntity::getStatusMsg, statusMsg)
                .set(StrUtil.isNotBlank(address), MerchantWalletEntity::getWalletAddress, address));
    }


    private ImmutableTriple<Integer, String, String> createWalletByApi(String vaultAccountId, String assetId) {
        int status = 0;
        String address = "";
        String msg = "";
        if (StrUtil.isBlank(vaultAccountId)) {
            status = ChannelWalletStatusEnum.GENERATE_FAIL.getCode();
            return ImmutableTriple.of(status, address, "vaultAccountId is null");
        }

        CreateWalletReq req = new CreateWalletReq();
        req.setVaultAccountId(vaultAccountId);
        req.setAssetId(assetId);
        RetResult<CreateVaultAssetVo> createVaultAssetVoRetResult = null;
        try {
            log.info("fireBlocksAPI.createWallet req:{}", req);
            createVaultAssetVoRetResult = fireBlocksAPI.createWallet(req);
            log.info("fireBlocksAPI.createWallet req:{},ret:{}", req, createVaultAssetVoRetResult);
            if (createVaultAssetVoRetResult.isSuccess()) {
                CreateVaultAssetVo vaultAssetVo = createVaultAssetVoRetResult.getData();
                address = vaultAssetVo.getAddress();
                status = ChannelWalletStatusEnum.GENERATE_SUCCESS.getCode();
            } else {
                if (createVaultAssetVoRetResult.getMsg().contains("Asset wallet already exists in the vault account")) {
                    // 创建fireblocks提示已存在,则查一下把本地的更新上即可
                    QueryAssetAddressesReq addressesReq = new QueryAssetAddressesReq();
                    addressesReq.setVaultAccountId(vaultAccountId);
                    addressesReq.setAssetId(assetId);
                    log.info("fireBlocksAPI.queryAssetAddresses addressesReq:{}", addressesReq);
                    RetResult<PaginatedAddressVo> addressVoRetResult = fireBlocksAPI.queryAssetAddresses(addressesReq);
                    log.info("fireBlocksAPI.queryAssetAddresses addressesReq:{},ret:{}", addressesReq, addressVoRetResult);
                    status = ChannelWalletStatusEnum.GENERATE_FAIL.getCode();
                    msg = "远程提示已创建,通过查询接口获取到地址,也失败:" + addressVoRetResult.getMsg();
                    if (addressVoRetResult.isSuccess()) {
                        PaginatedAddressVo addressVo = addressVoRetResult.getData();
                        if (addressVo != null) {
                            List<VaultWalletAddressVo> addressList = addressVo.getAddresses();
                            if (CollUtil.isNotEmpty(addressList)) {
                                VaultWalletAddressVo vaultAddressVo = addressList.get(0);
                                address = vaultAddressVo.getAddress();
                                status = ChannelWalletStatusEnum.GENERATE_SUCCESS.getCode();
                                msg = "远程提示已创建,通过查询接口获取到地址";
                            }
                        }
                    }
                } else {
                    status = ChannelWalletStatusEnum.GENERATE_FAIL.getCode();
                    msg = "创建失败:" + createVaultAssetVoRetResult.getMsg();
                }
            }
        } catch (Exception e) {
            status = ChannelWalletStatusEnum.GENERATE_FAIL.getCode();
            msg = "远程创建报错:" + e.getMessage();
        }

        return ImmutableTriple.of(status, address, msg);
    }
}
