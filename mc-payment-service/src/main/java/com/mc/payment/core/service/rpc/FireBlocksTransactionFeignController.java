package com.mc.payment.core.service.rpc;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.common.rpc.model.fireBlocks.TransactionVo;
import com.mc.payment.common.rpc.model.fireBlocks.nested.FeeInfoVo;
import com.mc.payment.common.rpc.model.fireBlocks.nested.TraPeerPathVo;
import com.mc.payment.core.api.IFireBlocksTransactionFeignClient;
import com.mc.payment.core.service.config.AppConfig;
import com.mc.payment.core.service.entity.*;
import com.mc.payment.core.service.facade.ExternalServiceFacade;
import com.mc.payment.core.service.facade.WebhookEventServiceFacade;
import com.mc.payment.core.service.model.enums.*;
import com.mc.payment.core.service.service.*;
import com.mc.payment.core.service.util.CommonUtil;
import com.mc.payment.core.service.util.MonitorLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author conor
 * @since 2024/2/21 11:18:26
 */
@Slf4j
@RestController
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/fireBlocks/transaction")
public class FireBlocksTransactionFeignController implements IFireBlocksTransactionFeignClient {

    @Autowired
    private IDepositRecordService depositRecordService;
    @Autowired
    private IWithdrawalRecordService withdrawalRecordService;
    @Autowired
    private WebhookEventServiceFacade webhookEventServiceFacade;
    @Autowired
    private IDepositRecordDetailService depositRecordDetailService;
    @Autowired
    private IWalletBlacklistService walletBlacklistService;
    @Autowired
    private IAssetLastQuoteService assetLastQuoteService;
    @Autowired
    private ExternalServiceFacade externalServiceFacade;
    @Autowired
    private ChannelAssetConfigService channelAssetConfigService;
    @Autowired
    private MerchantWalletService merchantWalletService;

    @Autowired
    private IWithdrawalRecordDetailService withdrawalRecordDetailService;

    @Autowired
    private IMerchantService merchantService;

    @Qualifier("appConfig")
    @Autowired
    private AppConfig appConfig;

    @Autowired
    private ChannelWalletService channelWalletService;

    @Override
    @PostMapping("/handle")
    public void handle(@RequestBody TransactionVo vo, @RequestParam("eventType") String eventType) {
        log.info("FireBlocksTransactionFeignController handle vo:{},eventType:{}", vo, eventType);
        //判断是否是入金操作 由下游操作决定
        TraPeerPathVo source = vo.getSource();
        if ("UNKNOWN".equals(source.getType()) && "External".equals(source.getName()) && "VAULT_ACCOUNT".equals(vo.getDestination().getType())) {
            if ("COMPLETED".equals(vo.getStatus())
                    || "BLOCKED".equals(vo.getStatus())
                    || "REJECTED".equals(vo.getStatus())
                    || "FAILED".equals(vo.getStatus())) {
                deposit(vo);
            }
        }

        //出金操作判断
        if ("ONE_TIME_ADDRESS".equals(vo.getDestination().getType()) && "VAULT_ACCOUNT".equals(vo.getSource().getType())) {

            // COMPLETED 成功
            // BLOCKED交易被阻止, 一般是TAP 规则而被阻止*(低于10U, 高于1000U, 或者新账号无权限).
            //
            //REJECTED: 被拒绝的交易状态, 一般得由管理员级别的去解冻
            //
            //FAILED: 交易失败, 这是最常见的失败状态, 一般是资金不够.
            if ("COMPLETED".equals(vo.getStatus())
                    || "BLOCKED".equals(vo.getStatus())
                    || "REJECTED".equals(vo.getStatus())
                    || "FAILED".equals(vo.getStatus())) {
                // 失败了 要解冻钱包
                withdrawal(vo);
            }

        }
    }

    private void withdrawal(TransactionVo vo) {
        //出金操作
        try {
            String externalTxId = vo.getExternalTxId(); //交易唯一id  规则 "MC_"+merchantId + "_" + trackingId;
            String[] externalTxIdArrays = externalTxId.split("_");
            WithdrawalRecordEntity recordEntity = withdrawalRecordService.getOne(externalTxIdArrays[1], externalTxIdArrays[2]);
            //生成出金明细
            WithdrawalRecordDetailEntity withdrawalRecordDetail = new WithdrawalRecordDetailEntity();
            withdrawalRecordDetail.setChannelSubType(ChannelSubTypeEnum.FIRE_BLOCKS.getCode());
            withdrawalRecordDetail.setMerchantId(externalTxIdArrays[1]);
            withdrawalRecordDetail.setTxHash(vo.getTxHash());
            withdrawalRecordDetail.setSourceAddress(vo.getSourceAddress());
            withdrawalRecordDetail.setDestinationAddress(vo.getDestinationAddress());
            withdrawalRecordDetail.setStatus(StrUtil.equals("COMPLETED", vo.getStatus())
                    ? WithdrawalDetailStausEnum.ITEM_3.getCode() : WithdrawalDetailStausEnum.ITEM_6.getCode());
            withdrawalRecordDetail.setAmount(new BigDecimal(vo.getAmountInfo().getAmount() == null ? "0" : vo.getAmountInfo().getAmount()));
            withdrawalRecordDetail.setNetworkFee(new BigDecimal(vo.getFeeInfo().getNetworkFee() == null ? "0" : vo.getFeeInfo().getNetworkFee()));
            withdrawalRecordDetail.setServiceFee(new BigDecimal(vo.getFeeInfo().getServiceFee() == null ? "0" : vo.getFeeInfo().getServiceFee()));
            withdrawalRecordDetail.setAddrBalance(BigDecimal.ZERO);
            withdrawalRecordDetailService.saveOrUpdateByTxHash(withdrawalRecordDetail);
            if (recordEntity != null) {
                //  刷新钱包余额
                // walletService.refreshWalletBalanceBatch(Collections.singletonList(recordEntity.getFreezeWalletId()));

                recordEntity.setTxHash(vo.getTxHash());
                MerchantWalletEntity walletEntity = merchantWalletService.getById(recordEntity.getWalletId());
                withdrawalRecordDetailService.lambdaUpdate().set(WithdrawalRecordDetailEntity::getRecordId, recordEntity.getId())
                        .set(WithdrawalRecordDetailEntity::getAssetName, recordEntity.getAssetName())
                        .set(WithdrawalRecordDetailEntity::getNetProtocol, recordEntity.getNetProtocol())
                        .set(WithdrawalRecordDetailEntity::getMerchantName, recordEntity.getMerchantName())
                        .set(WithdrawalRecordDetailEntity::getRate, recordEntity.getRate())
                        .set(WithdrawalRecordDetailEntity::getFeeRate, recordEntity.getFeeRate())
                        .set(WithdrawalRecordDetailEntity::getAddrBalance, walletEntity == null ? BigDecimal.ZERO : walletEntity.getBalance())
                        .eq(WithdrawalRecordDetailEntity::getId, withdrawalRecordDetail.getId())
                        .update();
                if (!"COMPLETED".equals(vo.getStatus())) {
                    recordEntity.setStatus(6);
                    withdrawalRecordDetailService.lambdaUpdate()
                            .set(WithdrawalRecordDetailEntity::getStatus, WithdrawalDetailStausEnum.ITEM_6.getCode())
                            .eq(WithdrawalRecordDetailEntity::getId, withdrawalRecordDetail.getId())
                            .update();
                    //出金错误状态订单增多指标监控
                    JSONObject depositFailMonitor = new JSONObject().put("Service", "payment").put("MonitorKey", "depositFailMonitor")
                            .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
                    MonitorLogUtil.log(depositFailMonitor);
                    log.error("出金异常 vo:{}", vo);
                } else {
                    // //状态,[0:已提交,1:待审核,2:余额不足,3:出金中,4:出金完成,5:已拒绝,6:出金错误,7,终止出金]
                    recordEntity.setStatus(4);
                    //出金指标监控
                    JSONObject withdrawalMonitor = new JSONObject().put("Service", "payment").put("MonitorKey", "withdrawalMonitor")
                            .put("address", recordEntity.getDestinationAddress()).put("amount", recordEntity.getAmount().multiply(recordEntity.getRate()))
                            .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
                    MonitorLogUtil.log(withdrawalMonitor);
                    //资产出金金额超7天均值指标监控
                    JSONObject withdrawal7AvgAmount = new JSONObject().put("Service", "payment").put("MonitorKey", "withdrawalAmount")
                            .put("amount", recordEntity.getAmount().multiply(recordEntity.getRate()))
                            .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
                    MonitorLogUtil.log(withdrawal7AvgAmount);
                    //出金成功状态订单时间相连指标监控
                    JSONObject depositCompleteMonitor = new JSONObject().put("Service", "payment")
                            .put("MonitorKey", "depositCompleteMonitor").put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
                    MonitorLogUtil.log(depositCompleteMonitor);
                }
                // 出金成功或失败 解冻对应金额 如果冻结金额被扣为0 则解冻
                // 处理金额解冻问题, 先解冻异种币手续费, 再解冻出金账号
                // 如果是本币手续费的话, 需要加上手续费, 如果是异种币手续费,就不需要加上手续费
                externalServiceFacade.unfreezeWallet(recordEntity);

                // 维护汇率字段
                List<String> symbolList = CommonUtil.getSymbolListByNames(recordEntity.getAssetName(), recordEntity.getFeeAssetName());
                Map<String, BigDecimal> symbolPriceMap = assetLastQuoteService.getSymbolAndMaxPriceBySymbol(symbolList);
                recordEntity.setRate(CommonUtil.getRateByNameAndMap(recordEntity.getAssetName(), symbolPriceMap));
                recordEntity.setFeeRate(CommonUtil.getRateByNameAndMap(recordEntity.getFeeAssetName(), symbolPriceMap));

                // 将回调的feeInfo->networkFee(The fee paid to the network)
                BigDecimal gasFee = getGasFeeByVo(vo.getFeeInfo());
                recordEntity.setGasFee(gasFee);
                withdrawalRecordService.updateById(recordEntity);

                webhookEventServiceFacade.asyncSaveAndTriggerWebhook(recordEntity);


            } else {
                log.error("未匹配到出金记录 externalTxId:{}", externalTxId);
            }
        } catch (Exception e) {
            log.error("出金回调异常 vo:{}", vo, e);
        }
    }

//    private void unfreezeHandle(WalletEntity wallet, BigDecimal amount) {
//        // 检查冻结金额是否大于等于 (金额)
//        if (wallet.getFreezeAmount().compareTo(amount) >= 0) {
//            // 扣除冻结金额
//            BigDecimal newFreezeAmount = wallet.getFreezeAmount().subtract(amount);
//            wallet.setFreezeAmount(newFreezeAmount);
//            // 如果冻结金额减至0，则解冻
//            if (newFreezeAmount.compareTo(BigDecimal.ZERO) == 0) {
//                wallet.setStatus(0); // 假设使用枚举表示钱包状态
//            }
//            // 更新钱包信息
//            walletService.updateById(wallet);
//        } else {
//            // 冻结金额不足，处理异常情况
//            log.error("冻结金额不足, 需要解冻金额:{}, 钱包:{}", amount, wallet);
//        }
//    }

    /**
     * 入金操作
     *
     * @param vo
     */
    private void deposit(TransactionVo vo) {
        try {
            String assetId = vo.getAssetId();
            String destinationAddress = vo.getDestinationAddress();

            ChannelAssetConfigEntity channelAssetEntity = channelAssetConfigService.lambdaQuery()
                    .eq(ChannelAssetConfigEntity::getChannelSubType, ChannelSubTypeEnum.FIRE_BLOCKS.getCode())
                    .eq(ChannelAssetConfigEntity::getChannelAssetName, assetId)
                    .eq(ChannelAssetConfigEntity::getStatus, StatusEnum.ACTIVE.getCode())
                    .one();
            if (channelAssetEntity == null) {
                log.info("未匹配到资产 vo:{}", vo);
                return;
            }
            // 根据入金地址查找入金申请记录
            DepositRecordEntity recordEntity = depositRecordService.queryEffective(channelAssetEntity.getAssetName(), channelAssetEntity.getNetProtocol(), destinationAddress);
            if (recordEntity == null) {
                //生成入金记录明细
                DepositRecordDetailEntity recordDetailEntity = new DepositRecordDetailEntity();
                recordDetailEntity.setSourceAddress(vo.getSourceAddress());
                recordDetailEntity.setDestinationAddress(vo.getDestinationAddress());
                recordDetailEntity.setMerchantId("0");
                recordDetailEntity.setTxHash(vo.getTxHash());
                recordDetailEntity.setChannelSubType(ChannelSubTypeEnum.FIRE_BLOCKS.getCode());
                recordDetailEntity.setAmount(new BigDecimal(vo.getAmountInfo().getAmount() == null ? "0" : vo.getAmountInfo().getAmount()));
                recordDetailEntity.setNetworkFee(new BigDecimal(vo.getFeeInfo().getNetworkFee() == null ? "0" : vo.getFeeInfo().getNetworkFee()));
                recordDetailEntity.setAddrBalance(BigDecimal.ZERO);
                recordDetailEntity.setServiceFee(new BigDecimal(vo.getFeeInfo().getServiceFee() == null ? "0" : vo.getFeeInfo().getServiceFee()));
                recordDetailEntity.setLastUpdated(vo.getLastUpdated() == null ? System.currentTimeMillis() : vo.getLastUpdated().longValue());
                recordDetailEntity.setStatus(StrUtil.equals("COMPLETED", vo.getStatus())
                        ? DepositDetailStausEnum.ITEM_3.getCode() : DepositDetailStausEnum.ITEM_7.getCode());
                recordDetailEntity.setAssetName(channelAssetEntity.getAssetName());
                recordDetailEntity.setNetProtocol(channelAssetEntity.getNetProtocol());
                ChannelWalletEntity channelWallet = channelWalletService.getOne(channelAssetEntity.getAssetName(),
                        channelAssetEntity.getNetProtocol(), vo.getDestinationAddress());
                recordDetailEntity.setAddrBalance(channelWallet == null ? BigDecimal.ZERO : channelWallet.getBalance());
                depositRecordDetailService.saveOrUpdateByTxHash(recordDetailEntity);
                log.info("未匹配到入金申记录 vo:{}", vo);
                return;
            }
            BigDecimal amount = NumberUtil.toBigDecimal(vo.getAmountInfo().getAmount());
            //  刷新钱包余额
            // walletService.refreshWalletBalanceBatch(Collections.singletonList(recordEntity.getWalletId()));
            // 反洗钱检测 如果在黑名单中则冻结金额
            boolean black = walletBlacklistService.isBlacklist(vo.getSourceAddress());
            if (black) {
                // 如果在黑名单中则冻结金额
//                WalletEntity wallet = walletService.getById(recordEntity.getWalletId());
//                BigDecimal freezeAmount = wallet.getFreezeAmount();
//                walletService.update(Wrappers.lambdaUpdate(WalletEntity.class)
//                        .eq(BaseNoLogicalDeleteEntity::getId, recordEntity.getWalletId())
//                        .set(WalletEntity::getStatus, 2)
//                        .set(WalletEntity::getFreezeAmount, freezeAmount.add(NumberUtil.toBigDecimal(vo.getAmountInfo().getAmount()))));


                merchantWalletService.changeBalanceAndAmount(ChangeEventTypeEnum.DEPOSIT_RISK, recordEntity.getId(), recordEntity.getWalletId(), amount, amount, "入金地址被风控，入金资金已冻结,txhash:" + vo.getTxHash());
                log.info(vo.getSourceAddress() + ",入金地址被风控，入金资金已冻结，请联系客服解冻资金。");
                return;
            }

            // 维护汇率字段
            List<String> symbolList = CommonUtil.getSymbolListByNames(recordEntity.getAssetName(), recordEntity.getFeeAssetName());
            Map<String, BigDecimal> symbolPriceMap = assetLastQuoteService.getSymbolAndMaxPriceBySymbol(symbolList);
            BigDecimal rate = CommonUtil.getRateByNameAndMap(recordEntity.getAssetName(), symbolPriceMap);
            BigDecimal feeRate = CommonUtil.getRateByNameAndMap(recordEntity.getFeeAssetName(), symbolPriceMap);
            recordEntity.setRate(rate);
            recordEntity.setFeeRate(feeRate);
            MerchantWalletEntity walletEntity = merchantWalletService.getById(recordEntity.getWalletId());
            DepositRecordDetailEntity recordDetailEntity = getDepositRecordDetailEntity(vo, recordEntity);
            recordDetailEntity.setAddrBalance(walletEntity == null ? BigDecimal.ZERO : walletEntity.getBalance());
            recordDetailEntity.setRate(rate);
            recordDetailEntity.setFeeRate(feeRate);
            recordEntity.setAddrBalance(recordDetailEntity.getAddrBalance());
            recordDetailEntity.setAuditStatus(DepositAuditStatusEnum.ITEM_1.getCode());
            depositRecordDetailService.saveOrUpdateByTxHash(recordDetailEntity);
            // 查询时,加上过期时间的限制, 防止:入金充值地址已失效且状态显示请求失效，当继续往这个地址充值时候，能正常充值且状态会改变
            Long expireTimestamp = recordEntity.getExpireTimestamp();
            // 过期时间加上冷却时间 当前认为冷却期间的进账也属于这笔订单
            Date expireDate = new Date(expireTimestamp + appConfig.getWalletCooldownTime());
//                BigDecimal accumulatedAmount = depositRecordDetailService.sumAccumulatedAmount(recordEntity.getId());
            List<DepositRecordDetailEntity> list = depositRecordDetailService.listByRecordIdAndExpireTime(recordEntity.getId(), expireDate);
            BigDecimal accumulatedAmount = list.stream().map(DepositRecordDetailEntity::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            // 累计手续费, 将list集合里的networkFee和serviceFee相加累计
            BigDecimal sumDetailGasFee = list.stream().map(depositRecordDetailEntity -> {
                return depositRecordDetailEntity.getNetworkFee().add(depositRecordDetailEntity.getServiceFee());
            }).reduce(BigDecimal.ZERO, BigDecimal::add);
            MerchantEntity merchant = merchantService.getById(recordEntity.getMerchantId());
            BigDecimal targetAmount = recordEntity.getAmount();
            if (!"COMPLETED".equals(vo.getStatus())) {
                recordEntity.setStatus(DepositRecordStatusEnum.ITEM_4.getCode());
                depositRecordDetailService.lambdaUpdate().set(DepositRecordDetailEntity::getStatus, DepositDetailStausEnum.ITEM_7.getCode())
                        .eq(DepositRecordDetailEntity::getId, recordDetailEntity.getId()).update();
                //请求失效状态订单增多指标监控
                JSONObject failDeposit = new JSONObject().put("Service", "payment").put("MonitorKey", "failDeposit")
                        .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
                MonitorLogUtil.log(failDeposit);
                log.error("入金异常 vo:{}", vo);
            } else {
                if (merchant.getDepositAudit() == BooleanStatusEnum.ITEM_1.getCode()) {
                    //审核过为最终状态不再更新
                    if (recordEntity.getAuditStatus() == DepositAuditStatusEnum.ITEM_1.getCode()
                            || recordEntity.getAuditStatus() == DepositAuditStatusEnum.ITEM_2.getCode()) {
                    } else {
                        recordEntity.setStatus(DepositRecordStatusEnum.ITEM_5.getCode());
                    }
                } else {
                    // 比较累计金额是否达到目标金额 判断是否部分入金
                    if (accumulatedAmount.compareTo(targetAmount) >= 0) {
                        recordEntity.setStatus(DepositRecordStatusEnum.ITEM_2.getCode());
                        //完全入金状态订单时间相连指标监控
                        JSONObject completeDeposit = new JSONObject().put("Service", "payment").put("MonitorKey", "completeDeposit")
                                .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
                        MonitorLogUtil.log(completeDeposit);
                    } else {
                        recordEntity.setStatus(DepositRecordStatusEnum.ITEM_1.getCode());
                        //部分入金状态订单增多指标监控
                        JSONObject partialDeposit = new JSONObject().put("Service", "payment").put("MonitorKey", "partialDeposit")
                                .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
                        MonitorLogUtil.log(partialDeposit);
                    }
                }
                recordEntity.setAccumulatedAmount(accumulatedAmount);
                recordEntity.setSourceAddress(vo.getSourceAddress());
                recordEntity.setGasFee(sumDetailGasFee);
                //同一地址短时间内频繁入金指标监控
                JSONObject depositFreMonitor = new JSONObject().put("Service", "payment").put("MonitorKey", "depositFreMonitor")
                        .put("address", vo.getSourceAddress()).put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
                MonitorLogUtil.log(depositFreMonitor);
            }
            depositRecordService.updateById(recordEntity);
            if (recordEntity.getStatus() == DepositRecordStatusEnum.ITEM_1.getCode()
                    || recordEntity.getStatus() == DepositRecordStatusEnum.ITEM_2.getCode()) {
                merchantWalletService.changeBalance(ChangeEventTypeEnum.DEPOSIT, recordEntity.getId(), recordEntity.getWalletId(), amount, "入金成功");
                //资产入金金额超7天均值指标监控
                JSONObject deposit7AvgAmount = new JSONObject().put("Service", "payment").put("MonitorKey", "depositAmount")
                        .put("amount", amount.multiply(rate)).put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
                MonitorLogUtil.log(deposit7AvgAmount);
            }
            if (recordEntity.getStatus() == DepositRecordStatusEnum.ITEM_2.getCode()
                    || recordEntity.getStatus() == DepositRecordStatusEnum.ITEM_4.getCode()) {
                //如果是完全入金或者入金异常,则要解锁对应的钱包
                merchantWalletService.unlockAndCollWallet(Collections.singletonList(recordEntity.getWalletId()), appConfig.getWalletCooldownTime());
            }
            webhookEventServiceFacade.asyncSaveAndTriggerWebhook(recordEntity);
        } catch (Exception e) {
            log.error("入金回调异常 vo:{}", vo, e);
        }
    }

    private static @NotNull DepositRecordDetailEntity getDepositRecordDetailEntity(TransactionVo vo, DepositRecordEntity recordEntity) {
        DepositRecordDetailEntity recordDetailEntity = new DepositRecordDetailEntity();
        recordDetailEntity.setRecordId(recordEntity.getId());
        recordDetailEntity.setAssetName(recordEntity.getAssetName());
        recordDetailEntity.setNetProtocol(recordEntity.getNetProtocol());
        recordDetailEntity.setSourceAddress(vo.getSourceAddress());
        recordDetailEntity.setDestinationAddress(vo.getDestinationAddress());
        recordDetailEntity.setMerchantId(recordEntity.getMerchantId());
        recordDetailEntity.setMerchantName(recordEntity.getMerchantName());
        recordDetailEntity.setTxHash(vo.getTxHash());
        recordDetailEntity.setChannelSubType(ChannelSubTypeEnum.FIRE_BLOCKS.getCode());
        recordDetailEntity.setStatus(DepositDetailStausEnum.ITEM_3.getCode());
        recordDetailEntity.setAmount(new BigDecimal(vo.getAmountInfo().getAmount() == null ? "0" : vo.getAmountInfo().getAmount()));
        recordDetailEntity.setNetworkFee(new BigDecimal(vo.getFeeInfo().getNetworkFee() == null ? "0" : vo.getFeeInfo().getNetworkFee()));
        recordDetailEntity.setServiceFee(new BigDecimal(vo.getFeeInfo().getServiceFee() == null ? "0" : vo.getFeeInfo().getServiceFee()));
        recordDetailEntity.setLastUpdated(vo.getLastUpdated() == null ? System.currentTimeMillis() : vo.getLastUpdated().longValue());
        return recordDetailEntity;
    }


    private static BigDecimal getGasFeeByVo(FeeInfoVo feeInfo) {
        BigDecimal networkFee = new BigDecimal(feeInfo.getNetworkFee() == null ? "0" : feeInfo.getNetworkFee());
        BigDecimal serviceFee = new BigDecimal(feeInfo.getServiceFee() == null ? "0" : feeInfo.getServiceFee());
        return networkFee.add(serviceFee);
    }
}
