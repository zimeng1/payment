package com.mc.payment.core.service.facade;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.entity.AccountEntity;
import com.mc.payment.core.service.entity.ChannelAssetConfigEntity;
import com.mc.payment.core.service.entity.MerchantWalletEntity;
import com.mc.payment.core.service.entity.WithdrawalRecordEntity;
import com.mc.payment.core.service.model.enums.AssetTypeEnum;
import com.mc.payment.core.service.model.enums.ChannelSubTypeEnum;
import com.mc.payment.core.service.model.enums.StatusEnum;
import com.mc.payment.core.service.model.req.EstimateFeeReq;
import com.mc.payment.core.service.model.rsp.EstimateFeeRsp;
import com.mc.payment.core.service.service.ChannelAssetConfigService;
import com.mc.payment.core.service.service.IAccountService;
import com.mc.payment.core.service.service.IWithdrawalRecordService;
import com.mc.payment.core.service.service.MerchantWalletService;
import com.mc.payment.core.service.util.Mt5util;
import com.mc.payment.fireblocksapi.FireBlocksAPI;
import com.mc.payment.fireblocksapi.model.req.fireBlocks.*;
import com.mc.payment.fireblocksapi.model.vo.fireBlocks.EstimatedNetworkFeeVo;
import com.mc.payment.fireblocksapi.model.vo.fireBlocks.EstimatedTransactionFeeVo;
import com.mc.payment.fireblocksapi.model.vo.fireBlocks.nested.NetworkFeeVo;
import com.mc.payment.fireblocksapi.model.vo.fireBlocks.nested.TransactionFeeVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author conor
 * @since 2024/2/19 16:01:18
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class AssetConfigServiceFacade {
    //    private final IAssetConfigService assetConfigService;
//
//    private final IChannelCostService channelCostService;
//    private final IChannelAssetService channelAssetService;
    private final IWithdrawalRecordService withdrawalRecordService;
    private final Mt5util mt5util;
    private final FireBlocksAPI fireBlocksAPI;
    private final MerchantWalletService merchantWalletService;
    private final IAccountService accountService;
    private final ChannelAssetConfigService channelAssetConfigService;


    /*public RetResult<Boolean> updateById(AssetConfigUpdateReq req) {
        AssetConfigEntity entity = assetConfigService.getById(req.getId());
        if (entity == null) {
            return RetResult.error("该数据不存在");
        }

        if (!entity.getAssetName().equals(req.getAssetName()) || !entity.getNetProtocol().equals(req.getNetProtocol())) {
            // 资产名称唯一
            if (assetConfigService.count(Wrappers.lambdaQuery(AssetConfigEntity.class)
                    .eq(AssetConfigEntity::getAssetName, req.getAssetName())
                    .eq(AssetConfigEntity::getNetProtocol, req.getNetProtocol())) > 0) {
                return RetResult.error(req.getAssetName() + ",该资产已存在");
            }
        }
        //  若资产已配置在通道成本上，需先撤销配置后才能修改。
        if (channelCostService.checkConfigByAssetId(req.getId())) {
            return RetResult.error("此资产目前已被配置，请先取消配置后再进行修改。");
        }
        return RetResult.data(assetConfigService.updateById(AssetConfigEntity.valueOf(req)));
    }*/

    /**
     * 刷新资产预估费
     */
    public void refreshTheEstimatedFeeForAssets(int shardIndex, int shardTotal) {
        // 查询当前节点的资产配置
        List<ChannelAssetConfigEntity> list = channelAssetConfigService.lambdaQuery()
                .apply("mod(id, " + shardTotal + ") = " + shardIndex)
                .eq(ChannelAssetConfigEntity::getChannelSubType, ChannelSubTypeEnum.FIRE_BLOCKS.getCode())
                .eq(ChannelAssetConfigEntity::getAssetType, AssetTypeEnum.CRYPTO_CURRENCY.getCode())
                .eq(ChannelAssetConfigEntity::getStatus, StatusEnum.ACTIVE.getCode())
                .list();

        for (ChannelAssetConfigEntity channelAssetConfigEntity : list) {
            refreshTheEstimatedFeeForAsset(channelAssetConfigEntity);
        }
    }

    public void refreshTheEstimatedFeeForAsset(ChannelAssetConfigEntity channelAssetConfigEntity) {
        // 默认的保底 ,所有方案都失败就靠它了
        BigDecimal unFee = null;
        BigDecimal fee = BigDecimal.ZERO;
        // 方案一:查询FireBlocks交易预估费接口 (要传金额和账户)
        unFee = fireBlocksFeeCalculator(channelAssetConfigEntity);
        // fee 为空或者不大于0时用方案二
        if (unFee == null || unFee.compareTo(BigDecimal.ZERO) <= 0) {
            // 方案二:查询FireBlocks币种预估费接口
            unFee = fireBlocksNetworkFeeCalculator(channelAssetConfigEntity);
        }
        if (unFee == null || unFee.compareTo(BigDecimal.ZERO) <= 0) {
            // 方案三:统计实际交易产生的手续费: 取该币种最近10次出金记录产生的手续费的平均值
            unFee = actualFeeCalculator(channelAssetConfigEntity);
        }

        //
        String feeAssetName = channelAssetConfigEntity.getFeeAssetName();
        if (unFee == null || unFee.compareTo(BigDecimal.ZERO) <= 0) {
            fee = channelAssetConfigEntity.getDefaultEstimateFee();
            BigDecimal exchangeFee = mt5util.getExchangeFee(channelAssetConfigEntity.getAssetName(), feeAssetName, channelAssetConfigEntity.getDefaultEstimateFee());
            unFee = exchangeFee == null ? BigDecimal.ZERO : exchangeFee;
            log.info("{}网络:{}预估费计算失败,取默认值{}", channelAssetConfigEntity.getAssetName(), channelAssetConfigEntity.getNetProtocol(), fee);
        } else {
            BigDecimal exchangeFee = mt5util.getExchangeFee(feeAssetName, channelAssetConfigEntity.getAssetName(), unFee);
            log.info("手续费:{},计算汇率后:{}", unFee, exchangeFee);
            if (exchangeFee.compareTo(BigDecimal.ZERO) > 0) {
                fee = exchangeFee;
            } else {
                fee = channelAssetConfigEntity.getDefaultEstimateFee();
                log.info("汇率查询失败,取默认值");
            }
        }
        fee = fee.multiply(new BigDecimal("1.2"));
        unFee = unFee.multiply(new BigDecimal("1.2"));

        log.info("assetName:{},netProtocol:{}预估费1.2倍计算结果:{},unFee:{}", channelAssetConfigEntity.getAssetName(), channelAssetConfigEntity.getNetProtocol(), fee, unFee);
        // 更新预估费
        channelAssetConfigService.update(Wrappers.lambdaUpdate(ChannelAssetConfigEntity.class)
                .set(ChannelAssetConfigEntity::getEstimateFee, fee)
                .set(ChannelAssetConfigEntity::getUnEstimateFee, unFee)
                .eq(BaseNoLogicalDeleteEntity::getId, channelAssetConfigEntity.getId()));
    }

    private BigDecimal fireBlocksNetworkFeeCalculator(ChannelAssetConfigEntity channelAssetConfigEntity) {
        BigDecimal fee = null;
        try {
            String netProtocol = channelAssetConfigEntity.getNetProtocol();
            String channelAssetName = channelAssetConfigEntity.getChannelAssetName();
            QueryEstimateNetworkFeeReq queryEstimateNetworkFeeReq = new QueryEstimateNetworkFeeReq();
            queryEstimateNetworkFeeReq.setAssetId(channelAssetName);
            RetResult<EstimatedNetworkFeeVo> retResult = fireBlocksAPI.estimateNetworkFee(queryEstimateNetworkFeeReq);
            log.info("fireBlocksAPI.estimateNetworkFee req:{}, ret:{}", queryEstimateNetworkFeeReq, retResult);
            if (!retResult.isSuccess() || retResult.getData() == null) {
                log.error("fireBlocksAPI.estimateNetworkFee 没有返回预期数据,retResult:{}", retResult);
                return null;
            }
            EstimatedNetworkFeeVo data = retResult.getData();
            NetworkFeeVo temp = data.getMedium();
            TransactionFeeVo medium = new TransactionFeeVo();
            medium.setBaseFee(temp.getBaseFee());
            medium.setGasPrice(temp.getGasPrice());
            medium.setPriorityFee(temp.getPriorityFee());
            medium.setNetworkFee(temp.getNetworkFee());
            medium.setFeePerByte(temp.getFeePerByte());
            fee = calculateTransactionFee(medium, netProtocol);
        } catch (Exception e) {
            log.error("fireBlocksAPI.estimateNetworkFee error", e);
        } finally {
            log.info("fireBlocksAPI.estimateNetworkFee fee:{}", fee);
        }
        return fee;
    }

    /**
     * 计算交易费
     *
     * @param medium
     * @param netProtocol
     * @return
     */
    private static BigDecimal calculateTransactionFee(TransactionFeeVo medium, String netProtocol) {
        log.info("medium:{},assetName:{}", medium, netProtocol);
        BigDecimal fee = null;
        // 有networkfee的用networkfee，没有的通过gas price计算，否则用feeperbyte计算
        if (medium.getNetworkFee() != null) {
            fee = NumberUtil.toBigDecimal(medium.getNetworkFee());
        } else if (medium.getGasPrice() != null && "ERC20".equals(netProtocol)) {
            //诸如:ETH,ETC就只有gasPrice和priorityFee, 这里就需要计算, Gas fee=Gas Limit* Gas price;//值越高, 产生的字节空间越大
            fee = NumberUtil.toBigDecimal(medium.getGasPrice())
                    .multiply(new BigDecimal("26000"))//其中Gas Limit一般标准是21000, 但复杂计算可能会达到十几万. 所以这个也比较浮动
                    .divide(new BigDecimal("1000000000"), 20, RoundingMode.HALF_UP);// 这里的币ETH单位是gwei, 1eth=1000000000gwei
        } else if (medium.getFeePerByte() != null && ("Bitcoin".equals(netProtocol) || "Dogecoin".equals(netProtocol))) {
            fee = NumberUtil.toBigDecimal(medium.getFeePerByte())
                    .multiply(new BigDecimal("300"))
                    .divide(new BigDecimal("100000000"), 20, RoundingMode.HALF_UP);
        } else {
            log.info("查询手续费失败,medium:{}", medium);
        }
        return fee;
    }

    /**
     * 查询FireBlocks交易预估费接口
     *
     * @param channelAssetConfigEntity
     * @return
     */
    private BigDecimal fireBlocksFeeCalculator(ChannelAssetConfigEntity channelAssetConfigEntity) {
        BigDecimal fee = null;
        try {
            String assetName = channelAssetConfigEntity.getAssetName();
            String netProtocol = channelAssetConfigEntity.getNetProtocol();
            String channelAssetName = channelAssetConfigEntity.getChannelAssetName();

            MerchantWalletEntity availableWallet = merchantWalletService.lambdaQuery()
                    .eq(MerchantWalletEntity::getAssetType, AssetTypeEnum.CRYPTO_CURRENCY.getCode())
                    .eq(MerchantWalletEntity::getChannelSubType, ChannelSubTypeEnum.FIRE_BLOCKS.getCode())
                    .eq(MerchantWalletEntity::getAssetName, assetName)
                    .eq(MerchantWalletEntity::getNetProtocol, netProtocol)
                    .gt(MerchantWalletEntity::getBalance, 0)
                    .orderByDesc(MerchantWalletEntity::getBalance)
                    .last("LIMIT 1")
                    .one();
            if (availableWallet == null) {
                log.error("没有查到有余额的转出钱包");
                return null;
            }
            CreateTransactionReq createTransactionReq = new CreateTransactionReq();
            createTransactionReq.setExternalTxId(IdUtil.fastUUID());
            createTransactionReq.setIdempotencyKey(createTransactionReq.getExternalTxId());
            createTransactionReq.setAmount("1");
            createTransactionReq.setAssetId(channelAssetName);
            TransactionPeerPathReq source = new TransactionPeerPathReq();
            source.setType("VAULT_ACCOUNT");

            AccountEntity accountEntity = accountService.getById(availableWallet.getAccountId());
            source.setId(accountEntity.getExternalId());
            source.setName(availableWallet.getAccountName());
            createTransactionReq.setSource(source);
            TransactionDestinationPeerPathReq destination = new TransactionDestinationPeerPathReq();
            destination.setType("VAULT_ACCOUNT");

            OneTimeAddressReq oneTimeAddress = new OneTimeAddressReq();
            TransactionValReq addressReq = new TransactionValReq();
            addressReq.setValue(availableWallet.getWalletAddress());
            oneTimeAddress.setAddress(addressReq);
            destination.setOneTimeAddress(oneTimeAddress);
            createTransactionReq.setDestination(destination);
            createTransactionReq.setTreatAsGrossAmount(false);

            RetResult<EstimatedTransactionFeeVo> retResult = fireBlocksAPI.estimateFee(createTransactionReq);
            log.info("fireBlocksAPI.estimateFee 资产:[{}-{}], req:{}, ret:{}", assetName, netProtocol, JSONUtil.toJsonStr(createTransactionReq), retResult);
            if (!retResult.isSuccess() || retResult.getData() == null) {
                log.error("fireBlocksAPI.estimateFee 没有返回预期数据,retResult:{}", retResult);
                return null;
            }
            EstimatedTransactionFeeVo transactionFeeVo = retResult.getData();
            TransactionFeeVo medium = transactionFeeVo.getMedium();
            fee = calculateTransactionFee(medium, channelAssetName);
        } catch (Exception e) {
            log.error("fireBlocksFeeCalculator 出现异常", e);
        } finally {
            log.info("fireBlocksFeeCalculator fee:{}", fee);
        }
        return fee;
    }

    /**
     * 统计实际交易产生的手续费: 取该币种最近10次出金记录产生的手续费的平均值
     *
     * @param channelAssetConfigEntity
     * @return
     */
    private BigDecimal actualFeeCalculator(ChannelAssetConfigEntity channelAssetConfigEntity) {
        BigDecimal fee = null;
        try {
            String assetName = channelAssetConfigEntity.getAssetName();
            String netProtocol = channelAssetConfigEntity.getNetProtocol();

            List<WithdrawalRecordEntity> list = withdrawalRecordService.list(Wrappers.lambdaQuery(WithdrawalRecordEntity.class)
                    .eq(WithdrawalRecordEntity::getAssetName, assetName)
                    .eq(WithdrawalRecordEntity::getNetProtocol, netProtocol)
                    .eq(WithdrawalRecordEntity::getStatus, 4)// 出金成功
                    .orderByDesc(WithdrawalRecordEntity::getCreateTime).last("limit 10"));
            if (CollUtil.isEmpty(list)) {
                return null;
            }
            BigDecimal gasPriceSum = list.stream().map(WithdrawalRecordEntity::getGasFee).reduce(BigDecimal.ZERO, BigDecimal::add);
            fee = gasPriceSum.divide(new BigDecimal(list.size()), 20, RoundingMode.HALF_UP);
        } catch (Exception e) {
            log.error("统计实际交易产生的手续费失败", e);
        } finally {
            log.info("{},{}:实际10次交易产生的手续费:{}", channelAssetConfigEntity.getAssetName(),
                    channelAssetConfigEntity.getNetProtocol(), fee);
        }
        return fee;
    }

    public RetResult<EstimateFeeRsp> estimateFeeNew(EstimateFeeReq req) {
        ChannelAssetConfigEntity channelAssetConfigEntity = channelAssetConfigService.lambdaQuery()
                .eq(ChannelAssetConfigEntity::getChannelSubType, ChannelSubTypeEnum.FIRE_BLOCKS.getCode())
                .eq(ChannelAssetConfigEntity::getAssetType, AssetTypeEnum.CRYPTO_CURRENCY.getCode())
                .eq(ChannelAssetConfigEntity::getStatus, StatusEnum.ACTIVE.getCode())
                .eq(ChannelAssetConfigEntity::getAssetName, req.getAssetName())
                .eq(ChannelAssetConfigEntity::getNetProtocol, req.getNetProtocol())
                .one();

        if (channelAssetConfigEntity == null) {
            return RetResult.error("查询失败,不支持该资产");
        }
        if (channelAssetConfigEntity.getEstimateFee() == null || channelAssetConfigEntity.getEstimateFee().compareTo(BigDecimal.ZERO) <= 0) {
            log.info("资产配置中预估费信息未更新,重新更新");
            this.refreshTheEstimatedFeeForAsset(channelAssetConfigEntity);
            channelAssetConfigEntity = channelAssetConfigService.lambdaQuery()
                    .eq(ChannelAssetConfigEntity::getChannelSubType, ChannelSubTypeEnum.FIRE_BLOCKS.getCode())
                    .eq(ChannelAssetConfigEntity::getAssetType, AssetTypeEnum.CRYPTO_CURRENCY.getCode())
                    .eq(ChannelAssetConfigEntity::getStatus, StatusEnum.ACTIVE.getCode())
                    .eq(ChannelAssetConfigEntity::getAssetName, req.getAssetName())
                    .eq(ChannelAssetConfigEntity::getNetProtocol, req.getNetProtocol())
                    .one();
        }

        EstimateFeeRsp estimateFeeRsp = new EstimateFeeRsp();
        estimateFeeRsp.setEstimateFee(channelAssetConfigEntity.getEstimateFee());
        estimateFeeRsp.setChainTransactionFee(channelAssetConfigEntity.getUnEstimateFee());
        return RetResult.data(estimateFeeRsp);
    }
}
