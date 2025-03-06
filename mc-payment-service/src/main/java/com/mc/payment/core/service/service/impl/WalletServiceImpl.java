package com.mc.payment.core.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.*;
import com.mc.payment.core.service.mapper.WalletMapper;
import com.mc.payment.core.service.model.dto.ChannelAssetDto;
import com.mc.payment.core.service.model.dto.RefreshWalletBalanceDto;
import com.mc.payment.core.service.model.enums.AssetTypeEnum;
import com.mc.payment.core.service.model.req.*;
import com.mc.payment.core.service.model.rsp.*;
import com.mc.payment.core.service.service.*;
import com.mc.payment.core.service.util.CommonUtil;
import com.mc.payment.fireblocksapi.FireBlocksAPI;
import com.mc.payment.fireblocksapi.model.req.fireBlocks.QueryVaultAccountAssetReq;
import com.mc.payment.fireblocksapi.model.vo.fireBlocks.VaultAssetVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author conor
 * @since 2024-04-15 11:07:58
 */
@Slf4j
@Service
public class WalletServiceImpl extends ServiceImpl<WalletMapper, WalletEntity> implements IWalletService {
    @Lazy
    @Autowired
    private IAccountService accountService;

    @Autowired
    private IAssetLastQuoteService assetLastQuoteService;
    @Autowired
    private FireBlocksAPI fireBlocksAPI;
    @Lazy
    @Autowired
    private ChannelWalletService channelWalletService;
    @Lazy
    @Autowired
    private MerchantWalletService merchantWalletService;
    @Lazy
    @Autowired
    private IMerchantService merchantService;

    @Override
    public BasePageRsp<WalletPageRsp> page(WalletPageReq req) {
        Page<WalletPageRsp> page = new Page<>(req.getCurrent(), req.getSize());
        page = (Page<WalletPageRsp>) baseMapper.page(page, req);

        List<WalletPageRsp> records = page.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            // 查询账户id和资产名称对应的汇总余额.
            Set<String> accountIdSet = records.stream().map(WalletPageRsp::getAccountId).collect(Collectors.toSet());
            Set<String> assetNameSet = records.stream().map(WalletPageRsp::getAssetName).collect(Collectors.toSet());
            List<WalletAssetSumBalanceRsp> walletAssetSumBalanceList = baseMapper.queryAccountIdAndAssetNameSumBalance(accountIdSet, assetNameSet);
            if (CollectionUtils.isEmpty(walletAssetSumBalanceList)) {
                return BasePageRsp.valueOf(page);
            }
            Map<String, BigDecimal> resultMap = walletAssetSumBalanceList.stream().collect(Collectors.toMap(walletAssetSumBalanceRsp -> walletAssetSumBalanceRsp.getAccountId() + "_" + walletAssetSumBalanceRsp.getAssetName(), WalletAssetSumBalanceRsp::getSumBalance));
            records.forEach(walletPageRsp -> {
                BigDecimal sumBalance = resultMap.get(walletPageRsp.getAccountId() + "_" + walletPageRsp.getAssetName());
                walletPageRsp.setSumBalance(sumBalance);
            });
        }
        return BasePageRsp.valueOf(page);
    }

    @Override
    public RetResult<String> save(WalletSaveReq req) {
        long count = this.count(Wrappers.lambdaQuery(WalletEntity.class)
                .eq(WalletEntity::getAccountId, req.getAccountId())
                .eq(WalletEntity::getAssetName, req.getAssetName())
                .eq(WalletEntity::getWalletAddress, req.getWalletAddress()));
        if (count > 0) {
            return RetResult.error("该数据已存在");
        }

        //刚新建的钱包, 余额相关的都为0
        WalletEntity entity = WalletEntity.valueOf(req);
        AccountEntity accountEntity = accountService.getById(req.getAccountId());
        entity.setMerchantId(accountEntity.getMerchantId());
        entity.setFreezeAmount(BigDecimal.ZERO);
        entity.setBalance(BigDecimal.ZERO);
        this.save(entity);
        return RetResult.data(entity.getId());
    }

    @Override
    public RetResult<Boolean> updateById(WalletUpdateReq req) {
        WalletEntity entity = WalletEntity.valueOf(req);
        AccountEntity accountEntity = accountService.getById(req.getAccountId());
        entity.setMerchantId(accountEntity.getMerchantId());
        return RetResult.data(this.updateById(entity));
    }

    @Override
    public RetResult<Boolean> remove(String id) {
        WalletEntity entity = getById(id);
        if (entity == null) {
            return RetResult.error("该数据不存在");
        }
        return RetResult.data(super.removeById(id));
    }

    @Override
    public List<WalletBalanceRsp> walletBalanceList(String merchantId, WalletBalanceReq req) {
        return baseMapper.walletBalanceList(merchantId, req);
    }

    @Override
    public boolean recoverByIds(List<String> ids) {
        return this.updateStatusByIds(ids, 0);
    }

    /**
     * 修改钱包状态
     *
     * @param ids
     * @param status
     * @return
     */
    @Override
    public boolean updateStatusByIds(List<String> ids, int status) {
        return this.update(Wrappers.lambdaUpdate(WalletEntity.class)
                .set(WalletEntity::getStatus, status)
                .in(WalletEntity::getId, ids));
    }

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
    @Override
    public WalletEntity getAvailableTransferIn(String merchantId, Integer accountType, int assetType, String assetName, String netProtocol, boolean lock) {
        WalletEntity walletEntity = baseMapper.getAvailableTransferIn(merchantId, accountType, assetType, assetName, netProtocol);
        if (lock && walletEntity != null) {
            // 锁定钱包
            walletEntity.setStatus(1);
            this.update(Wrappers.lambdaUpdate(WalletEntity.class)
                    .set(WalletEntity::getStatus, 1)
                    .eq(WalletEntity::getId, walletEntity.getId()));
        }
        return walletEntity;
    }

    /**
     * 获取多个可用的转出钱包,并且冻结相应的金额
     *
     * @param merchantId
     * @param assetName
     * @param netProtocol
     * @param amount
     * @return
     */
    public WalletEntity getAvailableTransferOut(String merchantId, Integer accountType, String assetName, String netProtocol, BigDecimal amount) {
        WalletEntity walletEntity = baseMapper.getAvailableTransferOut(merchantId, accountType, assetName, netProtocol, amount);
        if (walletEntity == null) {
            return null;
        }
        // 冻结钱包
        walletEntity.setStatus(2);
        walletEntity.setFreezeAmount(walletEntity.getFreezeAmount().add(amount));

        this.update(Wrappers.lambdaUpdate(WalletEntity.class)
                .set(WalletEntity::getStatus, 2)
                .set(WalletEntity::getFreezeAmount, walletEntity.getFreezeAmount().add(amount))
                .eq(WalletEntity::getId, walletEntity.getId()));
        return walletEntity;
    }

    /**
     * 查询可用余额最多的转出钱包
     *
     * @param merchantId
     * @param assetName
     * @param netProtocol
     * @return
     */
    @Override
    public WalletEntity queryByAmountMax(String merchantId, String assetName, String netProtocol) {
        return baseMapper.queryByAmountMax(merchantId, assetName, netProtocol);
    }

    /**
     * 查询可用余额最多的转出账户钱包-ps:包含账户信息
     *
     * @param merchantId
     * @param assetName
     * @param netProtocol
     * @return
     */
    @Override
    public WalletOfMaxBalanceAccountRsp queryAccountByAmountMax(String merchantId, String assetName, String netProtocol) {
        return baseMapper.queryAccountByAmountMax(merchantId, assetName, netProtocol);
    }

    @Override
    public List<MerchantAssetRsp> listByMerchantIdAntTime(String merchantId, Date timeStart, Date timeEnd) {
        if (StringUtils.isBlank(merchantId)) {
            return baseMapper.listByTime(timeStart, timeEnd);
        } else {
            return baseMapper.listByMerchantIdsAntTime(merchantId, timeStart, timeEnd);
        }
    }

    /**
     * 出金时, 有异种资产手续费,额外处理
     *
     * @param merchantId
     * @param assetName
     * @param netProtocol
     * @param feeAssetName
     * @param amount
     * @param feeAmount
     * @return
     */
    public WalletEntity getAvailableTransferOut2Asset(String merchantId, String assetName, String netProtocol, String feeAssetName, BigDecimal amount, BigDecimal feeAmount, WithdrawalRecordEntity entity) {
        String assetNames = String.format(" ('%s', '%s') ", assetName, feeAssetName);
        List<WalletEntity> richTransferOutList = baseMapper.getRichTransferOutList(merchantId, assetNames);
        if (CollUtil.isEmpty(richTransferOutList) || richTransferOutList.size() < 2) {
            return null;
        } else {
            WalletEntity outWallet = null;
            //然后循环检查一个账户下是否有足够的2个钱包
            Map<String, List<WalletEntity>> accountMap = richTransferOutList.stream().collect(Collectors.groupingBy(WalletEntity::getAccountId));
            ArrayList<WalletEntity> availableTransferOutList = new ArrayList<>();
            for (Map.Entry<String, List<WalletEntity>> entry : accountMap.entrySet()) {
                List<WalletEntity> walletEntities = entry.getValue();
                if (walletEntities.size() >= 2) {
                    //循环walletEntities,根据币种的不同检查是否有足够的钱包
                    for (WalletEntity walletEntity : walletEntities) {
                        if (walletEntity.getAssetName().equals(assetName) && walletEntity.getNetProtocol().equals(netProtocol)) {
                            //(余额-冻结金额-本次出金额)>= 0
                            BigDecimal overBalance = walletEntity.getBalance().subtract(walletEntity.getFreezeAmount()).subtract(amount);
                            if (overBalance.compareTo(BigDecimal.ZERO) >= 0) {
                                availableTransferOutList.add(walletEntity);
                            }
                        } else if (walletEntity.getAssetName().equals(feeAssetName)) {
                            //(余额-冻结金额-手续费) > 0, ps:手续费是浮动的, 所以必须大于0
                            BigDecimal overBalance = walletEntity.getBalance().subtract(walletEntity.getFreezeAmount()).subtract(feeAmount);
                            if (overBalance.compareTo(BigDecimal.ZERO) > 0) {
                                availableTransferOutList.add(walletEntity);
                            }
                        }
                    }
                    // 只有两个钱包满足条件才成功
                    if (availableTransferOutList.size() == 2) {
                        break;
                    } else {
                        availableTransferOutList.clear();
                    }
                }
            }
            if (availableTransferOutList.size() == 2) {
                //循环2个钱包,一个是资产币种,一个是手续费币种
                for (WalletEntity temp : availableTransferOutList) {
                    temp.setStatus(2);
                    if (temp.getAssetName().equals(feeAssetName)) {
                        //异种币手续费
                        temp.setFreezeAmount(temp.getFreezeAmount().add(feeAmount));
                        entity.setFreezeWalletId(temp.getId());
                    } else {
                        //本币需要出金的金额
                        temp.setFreezeAmount(temp.getFreezeAmount().add(amount));
                        outWallet = temp;
                    }
                    this.updateById(temp);
                }
            }
            return outWallet;
        }
    }

    /**
     * 分片查询钱包集合
     *
     * @param shardIndex 当前分片索引
     * @param shardTotal 分片总数
     * @return
     */
    @Override
    public List<WalletEntity> shardList(int shardIndex, int shardTotal) {
        return baseMapper.shardList(shardIndex, shardTotal);
    }

    /**
     * 查询账户的所有资产余额
     * ps: 优化:查钱包表时, 只查有钱的数据, 再去汇总(余额*汇率), 查汇率也是一次性查询, 不多次查询
     *
     * @param accountIds
     * @return 余额 单位:U
     */
    @Override
    public BigDecimal queryBalanceSum(List<String> accountIds) {
        List<WalletEntity> walletEntityList = this.list(Wrappers.lambdaQuery(WalletEntity.class).in(WalletEntity::getAccountId, accountIds).gt(WalletEntity::getBalance, BigDecimal.ZERO));
        // key : assetName, value: balance
        Map<String, BigDecimal> assetBalanceMap = walletEntityList.stream().collect(Collectors.toMap(WalletEntity::getAssetName, WalletEntity::getBalance, BigDecimal::add));
        List<String> assetNameList = assetBalanceMap.keySet().stream().toList();
        Map<String, BigDecimal> symbolAndMaxPriceMap = assetLastQuoteService.getSymbolAndMaxPriceBySymbol(assetNameList);
        if (CollUtil.isNotEmpty(assetBalanceMap)) {
            assetBalanceMap.forEach((assetName, balance) -> {
//                BigDecimal usdtRate = assetLastQuoteService.getExchangeRate(assetName, true);
                BigDecimal usdtRate = CommonUtil.getRateByNameAndMap(assetName, symbolAndMaxPriceMap);
                assetBalanceMap.put(assetName, balance.multiply(usdtRate));
            });
            return assetBalanceMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public long count(int channelSubType, String channelAssetName) {
        return baseMapper.count(channelSubType, channelAssetName);
    }

    @Override
    public List<WalletEntity> queryBalanceSumByAssetOrAddr(Set<String> accountIdSet, MerchantQueryReq req) {
        //1.先查出钱包信息
        LambdaQueryWrapper<WalletEntity> query = Wrappers.lambdaQuery(WalletEntity.class);
        if (CollectionUtils.isNotEmpty(req.getMerchantIdList())) {
            query.in(WalletEntity::getMerchantId, req.getMerchantIdList());
        }
        if (CollectionUtils.isNotEmpty(accountIdSet)) {
            // 如果赛选账户的条件都为空, 就不调用in查询账户
            if (CollectionUtils.isNotEmpty(req.getAssetNameList()) || CollectionUtils.isNotEmpty(req.getAddrList()) || CollectionUtils.isNotEmpty(req.getAccountTypeList()) || CollectionUtils.isNotEmpty(req.getAccountIdList())) {
                query.in(WalletEntity::getAccountId, accountIdSet);
            }
        }
        if (CollectionUtils.isNotEmpty(req.getAssetNameList())) {
            query.in(WalletEntity::getAssetName, req.getAssetNameList());
        }
        if (CollectionUtils.isNotEmpty(req.getAddrList())) {
            query.in(WalletEntity::getId, req.getAddrList());
        }
        return this.getBaseMapper().selectList(query);
    }

    /**
     * 按钱包id批量刷新钱包余额
     *
     * @param walletIds
     */
    @Override
    public void refreshWalletBalanceBatch(List<String> walletIds) {
        List<RefreshWalletBalanceDto> list = this.baseMapper.queryRefreshWalletBalanceDtoByIds(walletIds);
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(this::refreshWalletBalanceByWallet);
        }
    }


    private void refreshWalletBalanceByWallet(RefreshWalletBalanceDto dto) {
        log.info("refreshWalletBalanceByWallet dto:{}", dto);
        try {
            QueryVaultAccountAssetReq req = new QueryVaultAccountAssetReq();
            req.setVaultAccountId(dto.getAccountExternalId());
            req.setAssetId(dto.getChannelAssetName());
            log.info("fireBlocksAPI.queryVaultAccountAsset req:{}", req);
            RetResult<VaultAssetVo> retResult = fireBlocksAPI.queryVaultAccountAsset(req);
            log.info("fireBlocksAPI.queryVaultAccountAsset retResult:{}", retResult);
            if (retResult.isSuccess()) {
                VaultAssetVo vaultAssetVo = retResult.getData();
                String total = vaultAssetVo.getTotal();
                this.update(Wrappers.lambdaUpdate(WalletEntity.class)
                        .set(WalletEntity::getBalance, new BigDecimal(total))
                        .eq(WalletEntity::getId, dto.getWalletId()));
            }
        } catch (Exception e) {
            log.error("refreshWalletBalanceByWallet error", e);
        }
    }

    /**
     * 查询可用钱包地址列表
     *
     * @param req
     * @return
     */
    @Override
    public List<String> queryWalletAddressList(QueryWalletAddressListReq req) {
        return this.baseMapper.queryWalletAddressList(req);
    }

    /**
     * 生成钱包地址二维码
     *
     * @param req
     * @return
     */
    @Override
    public String generateWalletQRCode(GenerateWalletQRCodeReq req) {
        return QrCodeUtil.generateAsBase64(req.getWalletAddress(), new QrConfig(260, 260), "png");
    }

    @Override
    public void refreshWalletBalanceJob(int shardIndex, int shardTotal) {
        List<WalletEntity> walletEntities = this.shardList(shardIndex, shardTotal);
        this.refreshWalletBalanceBatch(walletEntities.stream().map(WalletEntity::getId).collect(Collectors.toList()));
    }

    @Override
    public void saveByChannelAssetDto(ChannelAssetDto assetDto, String merchantId, String accountId) {
        WalletEntity walletEntity = new WalletEntity();
        walletEntity.setMerchantId(merchantId);
        walletEntity.setAccountId(accountId);
        // todo 待优化
        walletEntity.setAssetType(AssetTypeEnum.FIAT_CURRENCY.getCode());
        walletEntity.setAssetName(assetDto.getAssetName());
        walletEntity.setNetProtocol(assetDto.getNetProtocol());
        walletEntity.setChannelAssetName(assetDto.getChannelAssetName());
        // ofaPay拿这个当地址,也就是对方给接口上的scode
        walletEntity.setWalletAddress(assetDto.getTokenAddress());
        walletEntity.setBalance(BigDecimal.ZERO);
        walletEntity.setFreezeAmount(BigDecimal.ZERO);
        //walletEntity.setPrivateKey();
        //walletEntity.setRemark();
        //walletEntity.setExternalId();
        //walletEntity.setStatus();
        //walletEntity.setDeleted();
        //walletEntity.setId();
        //walletEntity.setCreateBy();
        //walletEntity.setCreateTime();
        //walletEntity.setUpdateBy();
        //walletEntity.setUpdateTime();
        // ofapay不需要创建远程钱包,直接保存
        this.save(walletEntity);
    }


    @Override
    public List<WalletBalanceSumRsp> walletBalanceSum(String merchantId, WalletBalanceSumReq req) {
        return baseMapper.walletBalanceSum(merchantId, req);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dataMigrate() {
        baseMapper.delChannelWalletAll();
        baseMapper.delMerchantWalletAll();
        List<WalletEntity> list = this.list();
        Map<String, AccountEntity> accountEntityMap = new HashMap<>();
        Map<String, MerchantEntity> merchantEntityMap = new HashMap<>();
        Map<String, ChannelWalletEntity> channelWalletEntityMap = new HashMap<>();

        for (WalletEntity walletEntity : list) {
            AccountEntity accountEntity = accountEntityMap.get(walletEntity.getAccountId());
            if (accountEntity == null) {
                accountEntity = accountService.getById(walletEntity.getAccountId());
                accountEntityMap.put(walletEntity.getAccountId(), accountEntity);
            }
            if (accountEntity == null) {
                log.info("账户不存在, 跳过,walletEntity:{}", walletEntity);
                continue;
            }
            if (accountEntity.getChannelSubType() != 1 && accountEntity.getChannelSubType() != 2) {
                log.info("不是fireblocks和ofapay通道的钱包不处理,walletEntity:{}", walletEntity);
                continue;
            }
            MerchantEntity merchantEntity = merchantEntityMap.get(accountEntity.getMerchantId());
            if (merchantEntity == null) {
                merchantEntity = merchantService.getById(accountEntity.getMerchantId());
                merchantEntityMap.put(accountEntity.getMerchantId(), merchantEntity);
            }


            if (accountEntity.getChannelSubType() == 2) {
                ChannelWalletEntity channelWalletEntity = channelWalletEntityMap.get(walletEntity.getWalletAddress());
                if (channelWalletEntity != null) {
                    MerchantWalletEntity merchantWalletEntity = convertMerchantWalletEntity(walletEntity, channelWalletEntity.getId(), 1,
                            accountEntity.getAccountType(), walletEntity.getAccountId() + "_" + walletEntity.getWalletAddress(),
                            accountEntity.getChannelSubType(), accountEntity.getName());
                    merchantWalletService.save(merchantWalletEntity);
                    continue;
                }
                channelWalletEntity = convertChannelWalletEntity(walletEntity, 1, accountEntity.getChannelSubType(), null, null);
                channelWalletService.save(channelWalletEntity);
                channelWalletEntityMap.put(walletEntity.getWalletAddress(), channelWalletEntity);
                MerchantWalletEntity merchantWalletEntity = convertMerchantWalletEntity(walletEntity, channelWalletEntity.getId(), 1,
                        accountEntity.getAccountType(), walletEntity.getAccountId() + "_" + walletEntity.getWalletAddress(),
                        accountEntity.getChannelSubType(), accountEntity.getName());
                merchantWalletService.save(merchantWalletEntity);
            }
            if (accountEntity.getChannelSubType() == 1) {
                ChannelWalletEntity channelWalletEntity = convertChannelWalletEntity(walletEntity, 0, accountEntity.getChannelSubType(), walletEntity.getChannelAssetName(), accountEntity.getExternalId());
                channelWalletService.save(channelWalletEntity);
                channelWalletEntityMap.put(walletEntity.getWalletAddress(), channelWalletEntity);
                MerchantWalletEntity merchantWalletEntity = convertMerchantWalletEntity(walletEntity, channelWalletEntity.getId(), 0,
                        accountEntity.getAccountType(), walletEntity.getWalletAddress(), accountEntity.getChannelSubType(), accountEntity.getName());
                merchantWalletService.save(merchantWalletEntity);
            }

        }
    }

    private static ChannelWalletEntity convertChannelWalletEntity(WalletEntity walletEntity, Integer assetType, Integer channelSubType, String assetId, String vaultAccountId) {
        ChannelWalletEntity channelWalletEntity = new ChannelWalletEntity();
        channelWalletEntity.setAssetType(assetType);
        channelWalletEntity.setChannelSubType(channelSubType);
        channelWalletEntity.setAssetName(walletEntity.getAssetName());
        channelWalletEntity.setNetProtocol(walletEntity.getNetProtocol());
        channelWalletEntity.setWalletAddress(walletEntity.getWalletAddress());
        channelWalletEntity.setBalance(walletEntity.getBalance());
        channelWalletEntity.setFreezeAmount(walletEntity.getFreezeAmount());
        if (StrUtil.isNotEmpty(assetId) || StrUtil.isNotEmpty(vaultAccountId)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("assetId", assetId);
            jsonObject.set("vaultAccountId", vaultAccountId);
            channelWalletEntity.setApiCredential(jsonObject.toString());
        } else {
            channelWalletEntity.setApiCredential("{}");
        }
        channelWalletEntity.setRemark("");
        channelWalletEntity.setStatus(3);
        channelWalletEntity.setStatusMsg("");
        channelWalletEntity.setCreateBy(walletEntity.getCreateBy());
        channelWalletEntity.setCreateTime(walletEntity.getCreateTime());
        channelWalletEntity.setUpdateBy(walletEntity.getUpdateBy());
        channelWalletEntity.setUpdateTime(walletEntity.getUpdateTime());
        return channelWalletEntity;
    }

    private static MerchantWalletEntity convertMerchantWalletEntity(WalletEntity walletEntity, String channelWalletId,
                                                                    Integer assetType, Integer purposeType, String walletAddress, Integer channelSubType, String accountName) {
        MerchantWalletEntity merchantWalletEntity = new MerchantWalletEntity();
        merchantWalletEntity.setMerchantId(walletEntity.getMerchantId());
        merchantWalletEntity.setAccountId(walletEntity.getAccountId());
        merchantWalletEntity.setAccountName(accountName);
        merchantWalletEntity.setAssetType(assetType);
        merchantWalletEntity.setChannelSubType(channelSubType);
        merchantWalletEntity.setAssetName(walletEntity.getAssetName());
        merchantWalletEntity.setNetProtocol(walletEntity.getNetProtocol());
        merchantWalletEntity.setPurposeType(purposeType);
        merchantWalletEntity.setWalletAddress(walletAddress);
        merchantWalletEntity.setBalance(walletEntity.getBalance());
        merchantWalletEntity.setFreezeAmount(walletEntity.getFreezeAmount());
        merchantWalletEntity.setRemark(walletEntity.getRemark());
        merchantWalletEntity.setChannelWalletId(channelWalletId);
        // 状态,[0:可用,1:锁定,2:冻结]
        // 状态,[0:待生成,1:生成中,2:生成失败,3:待使用,4:锁定中,5:冷却中]
        if (walletEntity.getStatus() == 1) {
            merchantWalletEntity.setStatus(4);
        } else {
            merchantWalletEntity.setStatus(3);
        }
        merchantWalletEntity.setStatusMsg("");
        merchantWalletEntity.setCreateBy(walletEntity.getCreateBy());
        merchantWalletEntity.setCreateTime(walletEntity.getCreateTime());
        merchantWalletEntity.setUpdateBy(walletEntity.getUpdateBy());
        merchantWalletEntity.setUpdateTime(walletEntity.getUpdateTime());
        return merchantWalletEntity;
    }
}
