package com.mc.payment.core.service.facade;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.config.AppConfig;
import com.mc.payment.core.service.entity.AccountEntity;
import com.mc.payment.core.service.entity.ChannelAssetConfigEntity;
import com.mc.payment.core.service.entity.ChannelWalletEntity;
import com.mc.payment.core.service.entity.MerchantWalletEntity;
import com.mc.payment.core.service.model.enums.*;
import com.mc.payment.core.service.model.req.channel.ChannelAssetConfigListReq;
import com.mc.payment.core.service.service.ChannelAssetConfigService;
import com.mc.payment.core.service.service.ChannelWalletService;
import com.mc.payment.core.service.service.IAccountService;
import com.mc.payment.core.service.service.MerchantWalletService;
import com.mc.payment.fireblocksapi.FireBlocksAPI;
import com.mc.payment.fireblocksapi.model.req.fireBlocks.CreateWalletReq;
import com.mc.payment.fireblocksapi.model.req.fireBlocks.QueryAssetAddressesReq;
import com.mc.payment.fireblocksapi.model.vo.fireBlocks.CreateVaultAssetVo;
import com.mc.payment.fireblocksapi.model.vo.fireBlocks.PaginatedAddressVo;
import com.mc.payment.fireblocksapi.model.vo.fireBlocks.nested.VaultWalletAddressVo;
import com.mc.payment.gateway.channels.cheezeepay.config.CheezeePayConfig;
import com.mc.payment.gateway.channels.ezeebill.config.EzeebillConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class WalletServiceFacade {
    private final FireBlocksAPI fireBlocksAPI;
    private final ChannelWalletService channelWalletService;
    private final MerchantWalletService merchantWalletService;
    private final IAccountService accountService;
    private final AppConfig appConfig;
    private final EzeebillConfig ezeebillConfig;
    private final CheezeePayConfig cheezeePayConfig;
    private final ChannelAssetConfigService channelAssetConfigService;


    /**
     * 钱包初始化
     * <p>
     */
    public void initChannelAndMerchantWallet(String accountId) {
        initChannelAndMerchantWallet(accountId, null);
    }

    public void initChannelAndMerchantWallet(String accountId, List<ChannelAssetConfigEntity> supportAssetList) {
        AccountEntity accountEntity = accountService.getById(accountId);
        if (accountEntity == null) {
            log.error("account not found, accountId:{}", accountId);
            return;
        }
        if (accountEntity.getStatus() != AccountStatusEnum.GENERATE_SUCCESS.getCode()) {
            log.error("account Not generated successfully, accountId:{}", accountId);
            return;
        }
        log.info("initChannelAndMerchantWallet,accountEntity:{},supportAssetList:{}", accountEntity, supportAssetList);
        Integer channelSubType = accountEntity.getChannelSubType();
        supportAssetList = CollUtil.isNotEmpty(supportAssetList) ? supportAssetList :
                channelAssetConfigService.list(new ChannelAssetConfigListReq(channelSubType, null, StatusEnum.ACTIVE.getCode()));

        batchGenerateInitFireBlocks(accountEntity, supportAssetList);
        batchGenerateInitOFA(accountEntity, supportAssetList);
        batchGenerateInitPayPal(accountEntity, supportAssetList);
        batchGenerateInitPassToPay(accountEntity, supportAssetList);
        batchGenerateInitEzeebill(accountEntity, supportAssetList);
        batchGenerateInitCheezeePay(accountEntity, supportAssetList);

    }

    private void batchGenerateInitEzeebill(AccountEntity accountEntity, List<ChannelAssetConfigEntity> list) {
        if (accountEntity.getChannelSubType() != ChannelSubTypeEnum.EZEEBILL.getCode()) {
            return;
        }
        for (ChannelAssetConfigEntity channelAssetConfigEntity : list) {
            generateInitEzeebill(accountEntity, channelAssetConfigEntity);
        }
    }

    private void generateInitEzeebill(AccountEntity accountEntity, ChannelAssetConfigEntity channelAssetEntity) {
        try {
            String accountName = ezeebillConfig.getMerch_id(channelAssetEntity.getAssetName()) == null ? "" : ezeebillConfig.getMerch_id(channelAssetEntity.getAssetName());

            // 通道钱包地址是accountName,且通道钱包生成一次即可
            ChannelWalletEntity channelWalletEntity = channelWalletService.getOne(Wrappers.lambdaQuery(ChannelWalletEntity.class)
                    .eq(ChannelWalletEntity::getAssetType, AssetTypeEnum.FIAT_CURRENCY.getCode())
                    .eq(ChannelWalletEntity::getChannelSubType, ChannelSubTypeEnum.EZEEBILL.getCode())
                    .eq(ChannelWalletEntity::getAssetName, channelAssetEntity.getAssetName())
                    .eq(ChannelWalletEntity::getNetProtocol, channelAssetEntity.getNetProtocol())
                    .eq(ChannelWalletEntity::getWalletAddress, accountName));
            if (channelWalletEntity == null) {
                channelWalletEntity = new ChannelWalletEntity();
                channelWalletEntity.setAssetType(AssetTypeEnum.FIAT_CURRENCY.getCode());
                channelWalletEntity.setChannelSubType(ChannelSubTypeEnum.EZEEBILL.getCode());
                channelWalletEntity.setAssetName(channelAssetEntity.getAssetName());
                channelWalletEntity.setNetProtocol(channelAssetEntity.getNetProtocol());
                channelWalletEntity.setWalletAddress(accountName);
                channelWalletEntity.setBalance(BigDecimal.ZERO);
                channelWalletEntity.setFreezeAmount(BigDecimal.ZERO);
                channelWalletEntity.setApiCredential("{}");
                channelWalletEntity.setRemark("");
                channelWalletEntity.setStatus(ChannelWalletStatusEnum.GENERATE_SUCCESS.getCode());
                channelWalletEntity.setStatusMsg("");
                channelWalletService.save(channelWalletEntity);
            }
            // 生成商户钱包
            MerchantWalletEntity merchantWalletEntity = new MerchantWalletEntity();
            merchantWalletEntity.setMerchantId(accountEntity.getMerchantId());
            merchantWalletEntity.setAccountId(accountEntity.getId());
            merchantWalletEntity.setAccountName(accountEntity.getName());
            merchantWalletEntity.setAssetType(AssetTypeEnum.FIAT_CURRENCY.getCode());
            merchantWalletEntity.setChannelSubType(ChannelSubTypeEnum.EZEEBILL.getCode());
            merchantWalletEntity.setAssetName(channelAssetEntity.getAssetName());
            merchantWalletEntity.setNetProtocol(channelAssetEntity.getNetProtocol());
            merchantWalletEntity.setPurposeType(accountEntity.getAccountType());
            merchantWalletEntity.setWalletAddress(accountEntity.getId() + "_" + accountName);
            merchantWalletEntity.setBalance(BigDecimal.ZERO);
            merchantWalletEntity.setFreezeAmount(BigDecimal.ZERO);
            merchantWalletEntity.setChannelWalletId(channelWalletEntity.getId());
            merchantWalletEntity.setStatus(MerchantWalletStatusEnum.USED_WAIT.getCode());
            merchantWalletService.save(merchantWalletEntity);
        } catch (Exception e) {
            log.error("generateInitPayPal,accountEntity:{},channelAssetEntity:{}", accountEntity, channelAssetEntity, e);
        }
    }

    private void batchGenerateInitPassToPay(AccountEntity accountEntity, List<ChannelAssetConfigEntity> list) {
        if (accountEntity.getChannelSubType() != ChannelSubTypeEnum.PASS_TO_PAY.getCode()) {
            return;
        }
        for (ChannelAssetConfigEntity channelAssetConfigEntity : list) {
            generateInitPassToPay(accountEntity, channelAssetConfigEntity);
        }
    }

    /**
     * 生成PassToPay钱包
     * <p>
     * 使用通道提供的商户号+appid来当作通道的钱包地址
     *
     * @param accountEntity
     * @param channelAssetEntity
     */
    public void generateInitPassToPay(AccountEntity accountEntity, ChannelAssetConfigEntity channelAssetEntity) {
        try {
            String channelWalletAddress = appConfig.getPassToPayMchNo() + "_" + appConfig.getPassToPayAppId();
            // 通道钱包地址是accountName,且通道钱包生成一次即可
            ChannelWalletEntity channelWalletEntity = channelWalletService.getOne(Wrappers.lambdaQuery(ChannelWalletEntity.class)
                    .eq(ChannelWalletEntity::getAssetType, AssetTypeEnum.FIAT_CURRENCY.getCode())
                    .eq(ChannelWalletEntity::getChannelSubType, accountEntity.getChannelSubType())
                    .eq(ChannelWalletEntity::getAssetName, channelAssetEntity.getAssetName())
                    .eq(ChannelWalletEntity::getNetProtocol, channelAssetEntity.getNetProtocol())
                    .eq(ChannelWalletEntity::getWalletAddress, channelWalletAddress));
            if (channelWalletEntity == null) {
                channelWalletEntity = new ChannelWalletEntity();
                channelWalletEntity.setAssetType(AssetTypeEnum.FIAT_CURRENCY.getCode());
                channelWalletEntity.setChannelSubType(accountEntity.getChannelSubType());
                channelWalletEntity.setAssetName(channelAssetEntity.getAssetName());
                channelWalletEntity.setNetProtocol(channelAssetEntity.getNetProtocol());
                channelWalletEntity.setWalletAddress(channelWalletAddress);
                channelWalletEntity.setBalance(BigDecimal.ZERO);
                channelWalletEntity.setFreezeAmount(BigDecimal.ZERO);
                channelWalletEntity.setApiCredential("{}");
                channelWalletEntity.setRemark("");
                channelWalletEntity.setStatus(ChannelWalletStatusEnum.GENERATE_SUCCESS.getCode());
                channelWalletEntity.setStatusMsg("");
                channelWalletService.save(channelWalletEntity);
            }
            // 生成商户钱包
            MerchantWalletEntity merchantWalletEntity = new MerchantWalletEntity();
            merchantWalletEntity.setMerchantId(accountEntity.getMerchantId());
            merchantWalletEntity.setAccountId(accountEntity.getId());
            merchantWalletEntity.setAccountName(accountEntity.getName());
            merchantWalletEntity.setAssetType(AssetTypeEnum.FIAT_CURRENCY.getCode());
            merchantWalletEntity.setChannelSubType(accountEntity.getChannelSubType());
            merchantWalletEntity.setAssetName(channelAssetEntity.getAssetName());
            merchantWalletEntity.setNetProtocol(channelAssetEntity.getNetProtocol());
            merchantWalletEntity.setPurposeType(accountEntity.getAccountType());
            merchantWalletEntity.setWalletAddress(accountEntity.getId() + "_" + channelWalletAddress);
            merchantWalletEntity.setBalance(BigDecimal.ZERO);
            merchantWalletEntity.setFreezeAmount(BigDecimal.ZERO);
            merchantWalletEntity.setChannelWalletId(channelWalletEntity.getId());
            merchantWalletEntity.setStatus(MerchantWalletStatusEnum.USED_WAIT.getCode());
            merchantWalletService.save(merchantWalletEntity);
        } catch (Exception e) {
            log.error("generateInitPassToPay,accountEntity:{},channelAssetEntity:{}", accountEntity, channelAssetEntity, e);
        }
    }

    private void batchGenerateInitPayPal(AccountEntity accountEntity, List<ChannelAssetConfigEntity> list) {
        if (accountEntity.getChannelSubType() != ChannelSubTypeEnum.PAY_PAL.getCode()) {
            return;
        }
        for (ChannelAssetConfigEntity channelAssetConfigEntity : list) {
            generateInitPayPal(accountEntity, channelAssetConfigEntity);
        }
    }

    private void generateInitPayPal(AccountEntity accountEntity, ChannelAssetConfigEntity channelAssetEntity) {
        try {
            String channelCredential = channelAssetEntity.getChannelCredential();
            JSONObject jsonObject = JSONUtil.parseObj(channelCredential);
            String accountName = jsonObject.getStr("accountName");
            // 通道钱包地址是accountName,且通道钱包生成一次即可
            ChannelWalletEntity channelWalletEntity = channelWalletService.getOne(Wrappers.lambdaQuery(ChannelWalletEntity.class)
                    .eq(ChannelWalletEntity::getAssetType, AssetTypeEnum.FIAT_CURRENCY.getCode())
                    .eq(ChannelWalletEntity::getChannelSubType, accountEntity.getChannelSubType())
                    .eq(ChannelWalletEntity::getAssetName, channelAssetEntity.getAssetName())
                    .eq(ChannelWalletEntity::getNetProtocol, channelAssetEntity.getNetProtocol())
                    .eq(ChannelWalletEntity::getWalletAddress, accountName));
            if (channelWalletEntity == null) {
                channelWalletEntity = new ChannelWalletEntity();
                channelWalletEntity.setAssetType(AssetTypeEnum.FIAT_CURRENCY.getCode());
                channelWalletEntity.setChannelSubType(accountEntity.getChannelSubType());
                channelWalletEntity.setAssetName(channelAssetEntity.getAssetName());
                channelWalletEntity.setNetProtocol(channelAssetEntity.getNetProtocol());
                channelWalletEntity.setWalletAddress(accountName);
                channelWalletEntity.setBalance(BigDecimal.ZERO);
                channelWalletEntity.setFreezeAmount(BigDecimal.ZERO);
                channelWalletEntity.setApiCredential("{}");
                channelWalletEntity.setRemark("");
                channelWalletEntity.setStatus(ChannelWalletStatusEnum.GENERATE_SUCCESS.getCode());
                channelWalletEntity.setStatusMsg("");
                channelWalletService.save(channelWalletEntity);
            }
            // 生成商户钱包
            MerchantWalletEntity merchantWalletEntity = new MerchantWalletEntity();
            merchantWalletEntity.setMerchantId(accountEntity.getMerchantId());
            merchantWalletEntity.setAccountId(accountEntity.getId());
            merchantWalletEntity.setAccountName(accountEntity.getName());
            merchantWalletEntity.setAssetType(AssetTypeEnum.FIAT_CURRENCY.getCode());
            merchantWalletEntity.setChannelSubType(accountEntity.getChannelSubType());
            merchantWalletEntity.setAssetName(channelAssetEntity.getAssetName());
            merchantWalletEntity.setNetProtocol(channelAssetEntity.getNetProtocol());
            merchantWalletEntity.setPurposeType(accountEntity.getAccountType());
            merchantWalletEntity.setWalletAddress(accountEntity.getId() + "_" + accountName);
            merchantWalletEntity.setBalance(BigDecimal.ZERO);
            merchantWalletEntity.setFreezeAmount(BigDecimal.ZERO);
            merchantWalletEntity.setChannelWalletId(channelWalletEntity.getId());
            merchantWalletEntity.setStatus(MerchantWalletStatusEnum.USED_WAIT.getCode());
            merchantWalletService.save(merchantWalletEntity);
        } catch (Exception e) {
            log.error("generateInitPayPal,accountEntity:{},channelAssetEntity:{}", accountEntity, channelAssetEntity, e);
        }
    }

    private void batchGenerateInitCheezeePay(AccountEntity accountEntity, List<ChannelAssetConfigEntity> list) {
        if (accountEntity.getChannelSubType() != ChannelSubTypeEnum.CHEEZEE_PAY.getCode()) {
            return;
        }
        for (ChannelAssetConfigEntity channelAssetConfigEntity : list) {
            generateInitCheezeePay(accountEntity, channelAssetConfigEntity);
        }
    }

    private void generateInitCheezeePay(AccountEntity accountEntity, ChannelAssetConfigEntity channelAssetEntity) {
        try {
            String channelWalletAddress = cheezeePayConfig.getCheezeepayMchId() + "_" + cheezeePayConfig.getCheezeepayAppId();
            // 通道钱包地址是accountName,且通道钱包生成一次即可
            ChannelWalletEntity channelWalletEntity = channelWalletService.getOne(Wrappers.lambdaQuery(ChannelWalletEntity.class)
                    .eq(ChannelWalletEntity::getAssetType, AssetTypeEnum.FIAT_CURRENCY.getCode())
                    .eq(ChannelWalletEntity::getChannelSubType, accountEntity.getChannelSubType())
                    .eq(ChannelWalletEntity::getAssetName, channelAssetEntity.getAssetName())
                    .eq(ChannelWalletEntity::getNetProtocol, channelAssetEntity.getNetProtocol())
                    .eq(ChannelWalletEntity::getWalletAddress, channelWalletAddress));
            if (channelWalletEntity == null) {
                channelWalletEntity = new ChannelWalletEntity();
                channelWalletEntity.setAssetType(AssetTypeEnum.FIAT_CURRENCY.getCode());
                channelWalletEntity.setChannelSubType(accountEntity.getChannelSubType());
                channelWalletEntity.setAssetName(channelAssetEntity.getAssetName());
                channelWalletEntity.setNetProtocol(channelAssetEntity.getNetProtocol());
                channelWalletEntity.setWalletAddress(channelWalletAddress);
                channelWalletEntity.setBalance(BigDecimal.ZERO);
                channelWalletEntity.setFreezeAmount(BigDecimal.ZERO);
                channelWalletEntity.setApiCredential("{}");
                channelWalletEntity.setRemark("");
                channelWalletEntity.setStatus(ChannelWalletStatusEnum.GENERATE_SUCCESS.getCode());
                channelWalletEntity.setStatusMsg("");
                channelWalletService.save(channelWalletEntity);
            }
            // 生成商户钱包
            MerchantWalletEntity merchantWalletEntity = new MerchantWalletEntity();
            merchantWalletEntity.setMerchantId(accountEntity.getMerchantId());
            merchantWalletEntity.setAccountId(accountEntity.getId());
            merchantWalletEntity.setAccountName(accountEntity.getName());
            merchantWalletEntity.setAssetType(AssetTypeEnum.FIAT_CURRENCY.getCode());
            merchantWalletEntity.setChannelSubType(accountEntity.getChannelSubType());
            merchantWalletEntity.setAssetName(channelAssetEntity.getAssetName());
            merchantWalletEntity.setNetProtocol(channelAssetEntity.getNetProtocol());
            merchantWalletEntity.setPurposeType(accountEntity.getAccountType());
            merchantWalletEntity.setWalletAddress(accountEntity.getId() + "_" + channelWalletAddress);
            merchantWalletEntity.setBalance(BigDecimal.ZERO);
            merchantWalletEntity.setFreezeAmount(BigDecimal.ZERO);
            merchantWalletEntity.setChannelWalletId(channelWalletEntity.getId());
            merchantWalletEntity.setStatus(MerchantWalletStatusEnum.USED_WAIT.getCode());
            merchantWalletService.save(merchantWalletEntity);
        } catch (Exception e) {
            log.error("generateInitPassToPay,accountEntity:{},channelAssetEntity:{}", accountEntity, channelAssetEntity, e);
        }
    }

    private void batchGenerateInitOFA(AccountEntity accountEntity, List<ChannelAssetConfigEntity> list) {
        if (accountEntity.getChannelSubType() != ChannelSubTypeEnum.OFA_PAY.getCode()) {
            return;
        }
        // 查询已经有的通道钱包
        List<ChannelWalletEntity> channelWalletEntities = channelWalletService.list(Wrappers.lambdaQuery(ChannelWalletEntity.class)
                .eq(ChannelWalletEntity::getAssetType, AssetTypeEnum.FIAT_CURRENCY.getCode())
                .eq(ChannelWalletEntity::getChannelSubType, ChannelSubTypeEnum.OFA_PAY.getCode())
                .orderByAsc(BaseNoLogicalDeleteEntity::getCreateTime));
        Map<String, ChannelWalletEntity> channelWalletEntityMap = channelWalletEntities.stream().collect(Collectors.toMap(ChannelWalletEntity::getWalletAddress, Function.identity()));
        for (ChannelAssetConfigEntity channelAssetConfigEntity : list) {
            generateInitOFA(accountEntity, channelAssetConfigEntity, channelWalletEntityMap);
        }
    }

    /**
     * 生成OFA钱包
     * <p>
     * 通道钱包地址是scode,且通道钱包生成一次即可
     * 商户钱包地址是accountId_scode
     *
     * @param accountEntity
     * @param channelAssetEntity
     * @param channelWalletEntityMap key is scode 也是通道钱包地址
     */
    private void generateInitOFA(AccountEntity accountEntity, ChannelAssetConfigEntity channelAssetEntity,
                                 Map<String, ChannelWalletEntity> channelWalletEntityMap) {
        try {
            String channelCredential = channelAssetEntity.getChannelCredential();
            JSONObject jsonObject = JSONUtil.parseObj(channelCredential);
            String scode = jsonObject.getStr("scode");
            ChannelWalletEntity channelWalletEntity = channelWalletEntityMap.get(scode);

            if (channelWalletEntity == null) {
                channelWalletEntity = new ChannelWalletEntity();
                channelWalletEntity.setAssetType(AssetTypeEnum.FIAT_CURRENCY.getCode());
                channelWalletEntity.setChannelSubType(ChannelSubTypeEnum.OFA_PAY.getCode());
                channelWalletEntity.setAssetName(channelAssetEntity.getAssetName());
                channelWalletEntity.setNetProtocol(channelAssetEntity.getNetProtocol());
                channelWalletEntity.setWalletAddress(scode);
                channelWalletEntity.setBalance(BigDecimal.ZERO);
                channelWalletEntity.setFreezeAmount(BigDecimal.ZERO);
                channelWalletEntity.setApiCredential("{}");
                channelWalletEntity.setRemark("");
                channelWalletEntity.setStatus(ChannelWalletStatusEnum.GENERATE_SUCCESS.getCode());
                channelWalletEntity.setStatusMsg("");
                channelWalletService.save(channelWalletEntity);
            }
            MerchantWalletEntity merchantWalletEntity = new MerchantWalletEntity();
            merchantWalletEntity.setMerchantId(accountEntity.getMerchantId());
            merchantWalletEntity.setAccountId(accountEntity.getId());
            merchantWalletEntity.setAccountName(accountEntity.getName());
            merchantWalletEntity.setAssetType(AssetTypeEnum.FIAT_CURRENCY.getCode());
            merchantWalletEntity.setChannelSubType(ChannelSubTypeEnum.OFA_PAY.getCode());
            merchantWalletEntity.setPurposeType(accountEntity.getAccountType());
            merchantWalletEntity.setWalletAddress(accountEntity.getId() + "_" + scode);
            merchantWalletEntity.setChannelWalletId(channelWalletEntity.getId());
            merchantWalletEntity.setAssetName(channelAssetEntity.getAssetName());
            merchantWalletEntity.setNetProtocol(channelAssetEntity.getNetProtocol());
            merchantWalletEntity.setBalance(BigDecimal.ZERO);
            merchantWalletEntity.setFreezeAmount(BigDecimal.ZERO);
            merchantWalletEntity.setRemark("");
            merchantWalletEntity.setStatus(MerchantWalletStatusEnum.USED_WAIT.getCode());
            merchantWalletService.save(merchantWalletEntity);
        } catch (Exception e) {
            log.error("generateInitOFA,accountEntity:{},channelAssetEntity:{}", accountEntity, channelAssetEntity, e);
        }
    }

    private void batchGenerateInitFireBlocks(AccountEntity accountEntity, List<ChannelAssetConfigEntity> list) {
        if (accountEntity.getChannelSubType() != ChannelSubTypeEnum.FIRE_BLOCKS.getCode()) {
            return;
        }
        for (ChannelAssetConfigEntity channelAssetConfigEntity : list) {
            generateInitFireBlocks(accountEntity, channelAssetConfigEntity);
        }
    }

    private void generateInitFireBlocks(AccountEntity accountEntity, ChannelAssetConfigEntity channelAssetEntity) {
        try {
            ChannelWalletEntity channelWalletEntity = new ChannelWalletEntity();
            channelWalletEntity.setAssetType(AssetTypeEnum.CRYPTO_CURRENCY.getCode());
            channelWalletEntity.setChannelSubType(ChannelSubTypeEnum.FIRE_BLOCKS.getCode());
            channelWalletEntity.setAssetName(channelAssetEntity.getAssetName());
            channelWalletEntity.setNetProtocol(channelAssetEntity.getNetProtocol());
            channelWalletEntity.setWalletAddress("temp#" + accountEntity.getId() + "_" + System.currentTimeMillis());
            channelWalletEntity.setBalance(BigDecimal.ZERO);
            channelWalletEntity.setFreezeAmount(BigDecimal.ZERO);
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("assetId", channelAssetEntity.getChannelAssetName());
            jsonObject.set("vaultAccountId", accountEntity.getExternalId());
            channelWalletEntity.setApiCredential(jsonObject.toString());
            channelWalletEntity.setRemark("");
            channelWalletEntity.setStatus(ChannelWalletStatusEnum.GENERATE_WAIT.getCode());
            channelWalletEntity.setStatusMsg("");
            channelWalletService.save(channelWalletEntity);

            MerchantWalletEntity merchantWalletEntity = new MerchantWalletEntity();
            merchantWalletEntity.setMerchantId(accountEntity.getMerchantId());
            merchantWalletEntity.setAccountId(accountEntity.getId());
            merchantWalletEntity.setAccountName(accountEntity.getName());
            merchantWalletEntity.setAssetType(AssetTypeEnum.CRYPTO_CURRENCY.getCode());
            merchantWalletEntity.setChannelSubType(ChannelSubTypeEnum.FIRE_BLOCKS.getCode());
            merchantWalletEntity.setAssetName(channelAssetEntity.getAssetName());
            merchantWalletEntity.setNetProtocol(channelAssetEntity.getNetProtocol());
            merchantWalletEntity.setPurposeType(accountEntity.getAccountType());
            merchantWalletEntity.setWalletAddress("temp#" + accountEntity.getId() + "_" + System.currentTimeMillis());
            merchantWalletEntity.setBalance(BigDecimal.ZERO);
            merchantWalletEntity.setFreezeAmount(BigDecimal.ZERO);
            merchantWalletEntity.setRemark("");
            merchantWalletEntity.setChannelWalletId(channelWalletEntity.getId());
            merchantWalletEntity.setStatus(MerchantWalletStatusEnum.GENERATE_WAIT.getCode());
            merchantWalletEntity.setStatusMsg("");
            merchantWalletService.save(merchantWalletEntity);
        } catch (Exception e) {
            log.error("generateInitFireBlocks,accountEntity:{},channelAssetEntity:{}", accountEntity, channelAssetEntity, e);
        }
    }

    /**
     * 扫描待生成的fireblock钱包,调用远程接口进行创建
     */
    public void generateFireBlocksWalletJob() {
        for (int i = 0; i < 50; i++) {
            ChannelWalletEntity channelWalletEntity = null;
            try {
                channelWalletEntity = channelWalletService.getOne(Wrappers.lambdaQuery(ChannelWalletEntity.class)
                        .eq(ChannelWalletEntity::getAssetType, 0)
                        .eq(ChannelWalletEntity::getChannelSubType, ChannelSubTypeEnum.FIRE_BLOCKS.getCode())
                        .eq(ChannelWalletEntity::getStatus, ChannelWalletStatusEnum.GENERATE_WAIT.getCode())
                        .orderByAsc(BaseNoLogicalDeleteEntity::getId)
                        .last("LIMIT 1"));
                if (channelWalletEntity == null) {
                    break;
                }
                createFireBlocksWallet(channelWalletEntity);
            } catch (Exception e) {
                log.error("generateFireBlocksWallet error,{}", channelWalletEntity, e);
            }
        }
    }

    /**
     * 暴露一个手动接口 方便对失败的进行重试
     *
     * @param channelWalletEntity
     */
    public void createFireBlocksWallet(ChannelWalletEntity channelWalletEntity) {
        String channelWalletId = channelWalletEntity.getId();
        String apiCredential = channelWalletEntity.getApiCredential();
        String vaultAccountId = null;
        String assetId = null;
        try {
            JSONObject jsonObject = JSONUtil.parseObj(apiCredential);
            vaultAccountId = jsonObject.getStr("vaultAccountId");
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
        Integer merchantWalletStatus = MerchantWalletStatusEnum.GENERATE_ING.getCode();
        String statusMsg = null;
        String address = null;
        channelWalletService.update(Wrappers.lambdaUpdate(ChannelWalletEntity.class)
                .eq(BaseNoLogicalDeleteEntity::getId, channelWalletId)
                .eq(ChannelWalletEntity::getStatus, ChannelWalletStatusEnum.GENERATE_WAIT.getCode())
                .set(ChannelWalletEntity::getStatus, status));
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
                merchantWalletStatus = MerchantWalletStatusEnum.USED_WAIT.getCode();
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
                    merchantWalletStatus = MerchantWalletStatusEnum.GENERATE_FAIL.getCode();
                    statusMsg = "远程提示已创建,通过查询接口获取到地址,也失败:" + addressVoRetResult.getMsg();
                    if (addressVoRetResult.isSuccess()) {
                        PaginatedAddressVo addressVo = addressVoRetResult.getData();
                        if (addressVo != null) {
                            List<VaultWalletAddressVo> addressList = addressVo.getAddresses();
                            if (CollUtil.isNotEmpty(addressList)) {
                                VaultWalletAddressVo vaultAddressVo = addressList.get(0);
                                address = vaultAddressVo.getAddress();
                                status = ChannelWalletStatusEnum.GENERATE_SUCCESS.getCode();
                                merchantWalletStatus = MerchantWalletStatusEnum.USED_WAIT.getCode();
                                statusMsg = "远程提示已创建,通过查询接口获取到地址";
                            }
                        }
                    }
                } else {
                    status = ChannelWalletStatusEnum.GENERATE_FAIL.getCode();
                    merchantWalletStatus = MerchantWalletStatusEnum.GENERATE_FAIL.getCode();
                    statusMsg = "创建失败:" + createVaultAssetVoRetResult.getMsg();
                }
            }
        } catch (Exception e) {
            status = ChannelWalletStatusEnum.GENERATE_FAIL.getCode();
            merchantWalletStatus = MerchantWalletStatusEnum.USED_WAIT.getCode();
            statusMsg = "远程创建报错:" + e.getMessage();
        }

        channelWalletService.update(Wrappers.lambdaUpdate(ChannelWalletEntity.class)
                .eq(BaseNoLogicalDeleteEntity::getId, channelWalletId)
                .eq(ChannelWalletEntity::getStatus, ChannelWalletStatusEnum.GENERATE_ING.getCode())
                .set(ChannelWalletEntity::getStatus, status)
                .set(StrUtil.isNotBlank(statusMsg), ChannelWalletEntity::getStatusMsg, statusMsg)
                .set(StrUtil.isNotBlank(address), ChannelWalletEntity::getWalletAddress, address));

        merchantWalletService.update(Wrappers.lambdaUpdate(MerchantWalletEntity.class)
                .eq(MerchantWalletEntity::getAssetType, 0)
                .eq(MerchantWalletEntity::getChannelSubType, ChannelSubTypeEnum.FIRE_BLOCKS.getCode())
                .eq(MerchantWalletEntity::getStatus, MerchantWalletStatusEnum.GENERATE_WAIT.getCode())
                .eq(MerchantWalletEntity::getChannelWalletId, channelWalletId)
                .set(MerchantWalletEntity::getStatus, merchantWalletStatus)
                .set(StrUtil.isNotBlank(statusMsg), MerchantWalletEntity::getStatusMsg, statusMsg)
                .set(StrUtil.isNotBlank(address), MerchantWalletEntity::getWalletAddress, address));
    }
}
