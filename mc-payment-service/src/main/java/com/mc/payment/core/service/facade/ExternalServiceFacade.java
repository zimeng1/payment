package com.mc.payment.core.service.facade;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mc.payment.api.model.dto.DepositEventDto;
import com.mc.payment.api.model.dto.WithdrawalEventDto;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.exception.BusinessException;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.config.AppConfig;
import com.mc.payment.core.service.constant.AssetConstants;
import com.mc.payment.core.service.constant.WebhookEventConstants;
import com.mc.payment.core.service.entity.*;
import com.mc.payment.core.service.model.GetAvailableWalletDto;
import com.mc.payment.core.service.model.enums.*;
import com.mc.payment.core.service.model.req.*;
import com.mc.payment.core.service.model.rsp.*;
import com.mc.payment.core.service.service.*;
import com.mc.payment.core.service.util.CommonUtil;
import com.mc.payment.core.service.util.MonitorLogUtil;
import com.mc.payment.fireblocksapi.FireBlocksAPI;
import com.mc.payment.fireblocksapi.model.req.fireBlocks.*;
import com.mc.payment.fireblocksapi.model.vo.fireBlocks.CreateTransactionVo;
import com.mc.payment.gateway.model.rsp.GatewayWithdrawalRsp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 对外接口Service
 *
 * @author conor
 * @since 2024/2/19 15:52:16
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ExternalServiceFacade {
    private final IDepositRecordService depositRecordService;
    private final IDepositRecordDetailService depositRecordDetailService;
    private final IWithdrawalRecordService withdrawalRecordService;
    private final AppConfig appConfig;
    private final IAccountService accountService;
    private final FireBlocksAPI fireBlocksAPI;
    private final IWebhookEventService webhookEventService;
    private final WebhookEventServiceFacade webhookEventServiceFacade;

    private final AssetConfigServiceFacade assetConfigServiceFacade;
    private final IWalletBlacklistService walletBlacklistService;
    private final IAssetLastQuoteService assetLastQuoteService;

    private final IChannelCostService channelCostService;
    private final IMerchantService merchantService;
    private final ProtocolConfigService protocolConfigService;
    private final MerchantWalletService merchantWalletService;
    private final ChannelWalletService channelWalletService;
    private final IPaymentPageServiceFacade paymentPageServiceFacade;
    private final ChannelAssetConfigService channelAssetConfigService;

    /**
     * 入金申请接口
     *
     * @param merchantId 商户ID
     * @param req
     * @return
     * @author Conor
     * @since 2024-04-18 13:35:06.229
     */
    @Transactional(rollbackFor = Exception.class)
    public RetResult<DepositRequestRsp> depositRequest(String merchantId, String merchantName, DepositRequestReq req) {
        log.info("入金申请接口,入参:{},{},{}", merchantId, merchantName, req);
        //1 校验
        //2生产入金记录
        //3组装返回结果
        //  trackingId 同一个商户不可重复
        long count = depositRecordService.count(Wrappers.lambdaQuery(DepositRecordEntity.class)
                .eq(DepositRecordEntity::getMerchantId, merchantId)
                .eq(DepositRecordEntity::getTrackingId, req.getTrackingId()));
        if (count > 0) {
            return RetResult.error("trackingId重复,请检查:" + req.getTrackingId());
        }
        // 扫描入金申请,解锁超时的钱包
//        this.scanDepositRequest();

        //效验, 防止乱传没有的配置的资产信息
//        AssetConfigEntity configEntity = assetConfigService.getOne(AssetTypeEnum.CRYPTO_CURRENCY.getCode(),
//                req.getAssetName(), req.getNetProtocol());
        ChannelAssetConfigEntity channelAssetConfigEntity = channelAssetConfigService.lambdaQuery()
                .eq(ChannelAssetConfigEntity::getChannelSubType, req.getChannelSubType())
                .eq(ChannelAssetConfigEntity::getAssetType, AssetTypeEnum.CRYPTO_CURRENCY.getCode())
                .eq(ChannelAssetConfigEntity::getAssetName, req.getAssetName())
                .eq(ChannelAssetConfigEntity::getNetProtocol, req.getNetProtocol())
                .eq(ChannelAssetConfigEntity::getStatus, StatusEnum.ACTIVE.getCode())
                .one();
        if (channelAssetConfigEntity == null) {
            return RetResult.error("您输入的资产信息不存在, 请检查.");
        }
        //赋值
        String assetName = req.getAssetName();
        String feeAssetName = channelAssetConfigEntity.getFeeAssetName();

        BigDecimal amount = req.getAmount();
        // 先查汇率, 再检查最小充值金额
        List<String> symbolList = CommonUtil.getSymbolListByNames(assetName, feeAssetName);
        Map<String, BigDecimal> symbolPriceMap = assetLastQuoteService.getSymbolAndMaxPriceBySymbol(symbolList);
        BigDecimal minDepositAmount = channelAssetConfigEntity.getMinDepositAmount();
        if (CommonUtil.checkMinAmount(assetName, amount, minDepositAmount, symbolPriceMap)) {
            return RetResult.error("您申请的入金的金额低于最小充值金额, 请检查.");
        }

        // 获取可用的钱包并且锁定
        GetAvailableWalletDto availableWalletDto = new GetAvailableWalletDto();
        availableWalletDto.setMerchantId(merchantId);
        availableWalletDto.setAssetType(0);
        availableWalletDto.setChannelSubType(ChannelSubTypeEnum.FIRE_BLOCKS.getCode());
        availableWalletDto.setAssetName(req.getAssetName());
        availableWalletDto.setNetProtocol(req.getNetProtocol());
        availableWalletDto.setPurposeType(PurposeTypeEnum.DEPOSIT.getCode());
        availableWalletDto.setLock(true);
        MerchantWalletEntity walletEntity = merchantWalletService.getAvailableWallet(availableWalletDto);
        if (walletEntity == null) {
            // 没有可用的钱包,提醒五分钟后重试,定时任务会解锁超时的钱包/冷却的/以及创建新的钱包
            return RetResult.error("There is no wallet available, please try again in 5 minutes");
        }
        String destinationAddress = walletEntity.getWalletAddress();

        // 获取手续费
        //  BigDecimal gasFee = this.queryGasFee();
        // 计算通道费
        BigDecimal channelFee = this.computeChannelFee(merchantId, req.getAssetName(), req.getNetProtocol(),
                req.getAmount());
        // 计算锁定超时时间
        long expireTimestamp = DateUtil.offsetMillisecond(new Date(), appConfig.getDepositExpire()).getTime();

        BigDecimal rate = CommonUtil.getRateByNameAndMap(assetName, symbolPriceMap);
        BigDecimal feeRate = CommonUtil.getRateByNameAndMap(feeAssetName, symbolPriceMap);
        // 生成入金申请单
        DepositRecordEntity entity = DepositRecordEntity.valueOf(req);
        entity.setAssetNet(channelAssetConfigEntity.getAssetNet());
        entity.setMerchantId(merchantId);
        entity.setMerchantName(merchantName);
        entity.setGasFee(BigDecimal.ZERO); // 入金不需要了,考虑删除
        entity.setAccumulatedAmount(BigDecimal.ZERO);
        entity.setChannelFee(channelFee);
        entity.setDestinationAddress(destinationAddress);
        entity.setExpireTimestamp(expireTimestamp);
        entity.setAccountId(walletEntity.getAccountId());
        entity.setWalletId(walletEntity.getId());
        entity.setAddrBalance(walletEntity.getBalance());
        entity.setStatus(DepositRecordStatusEnum.ITEM_0.getCode());
        entity.setFeeAssetName(feeAssetName);
        entity.setRate(rate);
        entity.setFeeRate(feeRate);
        entity.setAssetType(AssetTypeEnum.CRYPTO_CURRENCY.getCode());
        log.info("生成入金申请单 {}", entity);
        // 保存入金申请
        depositRecordService.save(entity);
        //入金指标监控
        JSONObject depositMonitor = new JSONObject().put("Service", "payment").put("MonitorKey", "depositMonitor")
                .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
        MonitorLogUtil.log(depositMonitor);
        //资金快进快出指标监控
        JSONObject depositDiffMonitor = new JSONObject().put("Service", "payment").put("MonitorKey",
                        "depositDiffMonitor")
                .put("userId", entity.getUserId()).put("amount", entity.getAmount().multiply(entity.getRate()))
                .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
        MonitorLogUtil.log(depositDiffMonitor);
        webhookEventServiceFacade.asyncSaveAndTriggerWebhook(entity);
        DepositRequestRsp rsp = new DepositRequestRsp();
        rsp.setTrackingId(req.getTrackingId());
        rsp.setExpireTimestamp(expireTimestamp);
        rsp.setAddress(destinationAddress);
        rsp.setChannelFee(entity.getChannelFee());

        return RetResult.data(rsp);
    }

    /**
     * 扫描入金申请,解锁超时的钱包
     * <p>
     * todo 商户钱包表 改造
     * 商户钱包表中 截止时间 从冷却时间调整为:状态为锁定时是锁定时间,状态为冷却时是冷却时间
     * 定时任务改造,做以下几件事
     * 1.目前只处理fireblocks通道的入金申请数据
     * 2.将超时的未入金的申请单更新为请求失效,原因更新为超时未入金
     * 3.对超时的部分入金的申请单,不做状态更新,原因更新未超时未完全入金
     * 4.对超时的钱包进行解锁,从锁定变更为冻结状态
     * 5.对冷却期的钱包进行解冻,从冻结状态变更为待使用
     */
    public void scanDepositRequest() {
        try {
            log.info("扫描入金申请,解锁超时的钱包");
            // 获取超时的入金申请
            List<DepositRecordEntity> list = depositRecordService.getOverdue();
            if (CollUtil.isEmpty(list)) {
                log.info("未扫描到超时入金申请");
                return;
            }
            List<String> walletIds = new ArrayList<>();
            Map<String, DepositRecordEntity> depositRecordEntityMap = new HashMap<>();
            for (DepositRecordEntity recordEntity : list) {
                walletIds.add(recordEntity.getWalletId());
                depositRecordEntityMap.put(recordEntity.getWalletId(), recordEntity);
            }
//            List<String> walletIds = list.stream().map(DepositRecordEntity::getWalletId).toList();
            List<MerchantWalletEntity> walletEntityList = merchantWalletService.listByIds(walletIds);
            // 需解锁的钱包
            List<String> lockWalletIds = new ArrayList<>();
            // 需解冻的钱包
            List<String> coolWalletIds = new ArrayList<>();
            // 需要设置为失败的入金申请
            List<DepositRecordEntity> failDepositRecord = new ArrayList<>();
            for (MerchantWalletEntity merchantWalletEntity : walletEntityList) {
                if (merchantWalletEntity.getStatus() == MerchantWalletStatusEnum.LOCK_ING.getCode()) {
                    lockWalletIds.add(merchantWalletEntity.getId());
                    //请求失效状态订单增多指标监控
                    JSONObject failDeposit = new JSONObject().put("Service", "payment").put("MonitorKey", "failDeposit")
                            .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
                    MonitorLogUtil.log(failDeposit);
                } else if (merchantWalletEntity.getStatus() == MerchantWalletStatusEnum.COOL_ING.getCode()
                        && merchantWalletEntity.getDeadline().getTime() <= System.currentTimeMillis()) {
                    coolWalletIds.add(merchantWalletEntity.getId());
                    DepositRecordEntity recordEntity = depositRecordEntityMap.get(merchantWalletEntity.getId());
                    failDepositRecord.add(recordEntity);
                }
            }

            log.info("解锁超时的钱包:{}", lockWalletIds);
            if (!lockWalletIds.isEmpty()) {
                merchantWalletService.unlockAndCollWallet(lockWalletIds, appConfig.getWalletCooldownTime());
            }
            if (!coolWalletIds.isEmpty()) {
                merchantWalletService.recoverCoolWallet(coolWalletIds);
                if (CollUtil.isNotEmpty(failDepositRecord)) {
                    Map<Integer, List<String>> map = failDepositRecord.stream()
                            .collect(Collectors.groupingBy(DepositRecordEntity::getStatus
                                    , Collectors.mapping(DepositRecordEntity::getId, Collectors.toList())));
                    List<String> failList = map.get(DepositRecordStatusEnum.ITEM_0.getCode());
                    List<String> depositList = map.get(DepositRecordStatusEnum.ITEM_1.getCode());
                    if (CollUtil.isNotEmpty(failList)) {
                        // 申请单也要标记为失败
                        depositRecordService.update(Wrappers.lambdaUpdate(DepositRecordEntity.class)
                                .set(DepositRecordEntity::getStatus, DepositRecordStatusEnum.ITEM_4.getCode())
                                .set(DepositRecordEntity::getStayReason, "超时未入金")
                                .in(DepositRecordEntity::getId, failList));
                        List<DepositRecordEntity> listByIds = depositRecordService.listByIds(failList);
                        for (DepositRecordEntity entity : listByIds) {
                            entity.setStatus(DepositRecordStatusEnum.ITEM_4.getCode());
                            webhookEventServiceFacade.asyncSaveAndTriggerWebhook(entity);
                        }
                    }
                    if (CollUtil.isNotEmpty(depositList)) {
                        depositRecordService.update(Wrappers.lambdaUpdate(DepositRecordEntity.class)
                                .set(DepositRecordEntity::getStayReason, "超时未入金")
                                .in(DepositRecordEntity::getId, depositList));
                    }
                }
            }
            // 针对只有冷却的没有关联进行中申请单的钱包,解冻
            List<MerchantWalletEntity> collWalletList =
                    merchantWalletService.list(Wrappers.lambdaQuery(MerchantWalletEntity.class)
                            .eq(MerchantWalletEntity::getStatus, MerchantWalletStatusEnum.COOL_ING.getCode())
                            .lt(MerchantWalletEntity::getDeadline, new Date()));
            if (CollUtil.isNotEmpty(collWalletList)) {
                coolWalletIds = collWalletList.stream().map(BaseNoLogicalDeleteEntity::getId).toList();
                merchantWalletService.recoverCoolWallet(coolWalletIds);
            }

            // 处理没有选择网络协议的超时入金申请
            depositRecordService.lambdaQuery().eq(DepositRecordEntity::getNetProtocol, "")
                    .eq(DepositRecordEntity::getStatus, DepositRecordStatusEnum.ITEM_0.getCode())
                    .lt(DepositRecordEntity::getExpireTimestamp, System.currentTimeMillis())
                    .list()
                    .forEach(entity -> {
                        entity.setStatus(DepositRecordStatusEnum.ITEM_4.getCode());
                        depositRecordService.lambdaUpdate()
                                .eq(DepositRecordEntity::getId, entity.getId())
                                .set(DepositRecordEntity::getStatus, DepositRecordStatusEnum.ITEM_4.getCode())
                                .update();
                        webhookEventServiceFacade.asyncSaveAndTriggerWebhook(entity);
                    });
        } catch (Exception e) {
            log.error("扫描入金申请,解锁超时的钱包 异常", e);
        }
    }

    /**
     * 包含MCPayment的成本费用和通道的费用
     *
     * @param merchantId
     * @param assetName
     * @param netProtocol
     * @param amount
     * @return
     */
    private BigDecimal computeChannelFee(String merchantId, String assetName, String netProtocol, BigDecimal amount) {
        // todo
        // 计算成本
        // 获取通道成本
        return BigDecimal.ZERO;
    }


    /**
     * 取消入金申请
     *
     * @param req
     * @return
     * @author Conor
     * @since 2024-04-18 13:37:50.052
     */
    public RetResult<Boolean> depositRequest(String merchantId, DepositCancelReq req) {
        DepositRecordEntity entity = depositRecordService.getOne(merchantId, req.getTrackingId());
        if (entity == null || entity.getStatus() != 0) {
            return RetResult.error("该单不处于待入金状态,无法进行撤销");
        }
        //撤销入金状态订单增多指标监控
        JSONObject cancelDeposit = new JSONObject().put("Service", "payment").put("MonitorKey", "cancelDeposit")
                .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
        MonitorLogUtil.log(cancelDeposit);
        entity.setStatus(3);
        depositRecordService.updateById(entity);
        webhookEventServiceFacade.asyncSaveAndTriggerWebhook(entity);
//        walletService.recoverByIds(Collections.singletonList(entity.getWalletId()));
        merchantWalletService.unlockAndCollWallet(Collections.singletonList(entity.getWalletId()), 0L);
        return RetResult.data(true);
    }

    public RetResult<List<WalletBalanceRsp>> walletBalance(String merchantId, WalletBalanceReq req) {

//        List<WalletBalanceRsp> walletBalanceList = walletService.walletBalanceList(merchantId, req);
        List<WalletBalanceRsp> walletBalanceList = merchantWalletService.walletBalanceList(merchantId, req);


//        List<WalletBalanceRsp> rspList = walletBalanceList.stream().peek(rsp -> {
//            // 冻结状态的要减掉冻结金额
//            BigDecimal subtract = rsp.getBalance().subtract(rsp.getFreezeAmount());
//            // 小于零则为零
//            rsp.setAvailableBalance(subtract.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : subtract);
//        }).toList();
        return RetResult.data(walletBalanceList);
    }

    /**
     * 校验出金地址是否符合规则
     *
     * @param req
     * @return
     */
    public boolean checkWithdrawalAddress(WithdrawalRequestReq req) {
        if (appConfig.getWithdrawalAddressEnabled() == 1) {
            Map<String, String> protocolConfigMap = protocolConfigService.list().stream()
                    .collect(Collectors.toMap(ProtocolConfigEntity::getNetProtocol,
                            ProtocolConfigEntity::getRegularExpression));
            String regularExpression = protocolConfigMap.get(req.getNetProtocol());
            if (StrUtil.isNotEmpty(regularExpression)) {
                if (!req.getAddress().matches(regularExpression)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 出金申请
     *
     * @param merchantId
     * @param reqs
     * @return
     * @author Conor
     * @since 2024-04-18 14:33:10.468
     */
    @Transactional(rollbackFor = Exception.class)
    public RetResult<List<WithdrawalRequestRsp>> withdrawalRequest(String merchantId, String merchantName,
                                                                   List<WithdrawalRequestReq> reqs) {
        log.info("出金申请 merchantId:{},merchantName:{},reqs:{}", merchantId, merchantName, reqs);
        if (reqs.size() > 10) {
            return RetResult.error("出金申请单不能超过10个");
        }
        for (WithdrawalRequestReq req : reqs) {
            if (!checkWithdrawalAddress(req)) {
                return RetResult.error("出金地址格式不正确");
            }
        }
        List<WithdrawalRecordEntity> recordEntityList = new ArrayList<>();
        List<WithdrawalRequestRsp> rspList = new ArrayList<>();
        MerchantEntity merchantEntity = merchantService.getById(merchantId);
        Integer status = 1;
        for (WithdrawalRequestReq req : reqs) {

            // 反洗钱功能
            boolean black = walletBlacklistService.isBlacklist(req.getAddress());
            if (black) {
                return RetResult.error(req.getAddress() + ",该出金地址被风控，无法执行出金请求，请更换地址后重试。");
            }

            //  trackingId 同一个商户不可重复
            long count = withdrawalRecordService.count(Wrappers.lambdaQuery(WithdrawalRecordEntity.class)
                    .eq(WithdrawalRecordEntity::getMerchantId, merchantId)
                    .eq(WithdrawalRecordEntity::getTrackingId, req.getTrackingId()));
            if (count > 0) {
                return RetResult.error("trackingId重复,请检查:" + req.getTrackingId());
            }

            String assetName = req.getAssetName();
            String netProtocol = req.getNetProtocol();
//            AssetConfigEntity configEntity = assetConfigService.getOne(AssetTypeEnum.CRYPTO_CURRENCY.getCode(),
//                    assetName, netProtocol);
            ChannelAssetConfigEntity channelAssetConfigEntity = channelAssetConfigService.lambdaQuery()
                    .eq(ChannelAssetConfigEntity::getChannelSubType, req.getChannelSubType())
                    .eq(ChannelAssetConfigEntity::getAssetType, AssetTypeEnum.CRYPTO_CURRENCY.getCode())
                    .eq(ChannelAssetConfigEntity::getAssetName, req.getAssetName())
                    .eq(ChannelAssetConfigEntity::getNetProtocol, req.getNetProtocol())
                    .eq(ChannelAssetConfigEntity::getStatus, StatusEnum.ACTIVE.getCode())
                    .one();
            if (channelAssetConfigEntity == null) {
                return RetResult.error("查询失败,不支持该资产");
            }

            // 计算通道费 作废 不在这计算
            BigDecimal channelFee = BigDecimal.ZERO;
            // = this.computeChannelFee(merchantId, req.getAssetName(), req.getNetProtocol(), req.getAmount());
            // 生成出金申请单
            WithdrawalRecordEntity entity = WithdrawalRecordEntity.valueOf(req);
            entity.setAssetNet(channelAssetConfigEntity.getAssetNet());
            entity.setMerchantId(merchantId);
            entity.setMerchantName(merchantName);
            // 获取手续费
            BigDecimal gasFee = BigDecimal.ZERO;

            // 维护通道资产名称
            entity.setChannelAssetName(channelAssetConfigEntity.getChannelAssetName());

            // 从feeAssetName字段是手续费的通道资产名称,
            String feeAssetName = channelAssetConfigEntity.getFeeAssetName();

            // true的时候为异种币手续费
            boolean difFeeFlag = !assetName.equals(feeAssetName);
            List<String> symbolList = new ArrayList<>();
            symbolList.add(assetName + AssetConstants.AN_USDT);
            if (difFeeFlag) {
                symbolList.add(feeAssetName + AssetConstants.AN_USDT);
            }
            // 维护汇率字段
            Map<String, BigDecimal> symbolPriceMap = assetLastQuoteService.getSymbolAndMaxPriceBySymbol(symbolList);
            BigDecimal minWithdrawalAmount =
                    (channelAssetConfigEntity.getMinWithdrawalAmount() == null || channelAssetConfigEntity.getMinWithdrawalAmount().compareTo(BigDecimal.ZERO) <= 0) ? BigDecimal.TEN : channelAssetConfigEntity.getMinWithdrawalAmount();
            if (CommonUtil.checkMinAmount(assetName, req.getAmount(), minWithdrawalAmount, symbolPriceMap)) {
                return RetResult.error("您申请的出金的金额低于最小出金金额, 请检查.");
            }
            entity.setRate(CommonUtil.getRateByNameAndMap(assetName, symbolPriceMap));
            entity.setFeeRate(CommonUtil.getRateByNameAndMap(feeAssetName, symbolPriceMap));
            entity.setFeeAssetName(feeAssetName);
            // 获取通道费, 目前通道只有fireblocks(二期), 等之后再从上游传递
            BigDecimal channelCost = channelCostService.getCostByParam(assetName, "1",
                    BusinessActionEnum.WITHDRAWALS.getCode(), req.getAmount(), entity.getFeeRate());
            entity.setChannelFee(channelCost);

            Integer handlerStatus = frozenAndHandlerRecord(merchantId, assetName, netProtocol, req.getAmount(),
                    entity, difFeeFlag, feeAssetName, channelCost);
            entity.setGasFee(gasFee);
            entity.setStatus(handlerStatus == null ? status : handlerStatus);
            entity.setAssetType(AssetTypeEnum.CRYPTO_CURRENCY.getCode());
            //频繁出金校验
            if (StrUtil.isNotBlank(req.getUserId())) {
                Integer withdrawalNum = withdrawalRecordService.lambdaQuery().eq(WithdrawalRecordEntity::getUserId,
                                req.getUserId())
                        .apply("create_time >= DATE_SUB(NOW(),INTERVAL 1 HOUR)").count().intValue();
                if (withdrawalNum > 3) {
                    entity.setStatus(WithdrawalRecordStatusEnum.ITEM_1.getCode());
                    entity.setAutoAudit(BooleanStatusEnum.ITEM_0.getCode());
                    entity.setStayReason("出金一小时内大于3次");
                }
            }
            //出金指标监控
            JSONObject withdrawalMonitor = new JSONObject().put("Service", "payment").put("MonitorKey",
                            "withdrawalMonitor")
                    .put("address", entity.getDestinationAddress()).put("amount",
                            entity.getAmount().multiply(entity.getRate()))
                    .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
            MonitorLogUtil.log(withdrawalMonitor);
            //资金快进快出指标监控
            JSONObject withdrawalDiffMonitor = new JSONObject().put("Service", "payment").put("MonitorKey",
                            "withdrawalDiffMonitor")
                    .put("userId", entity.getUserId()).put("amount", entity.getAmount().multiply(entity.getRate()))
                    .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
            MonitorLogUtil.log(withdrawalDiffMonitor);
            recordEntityList.add(entity);

            WithdrawalRequestRsp rsp = new WithdrawalRequestRsp();
            rsp.setChannelFee(channelFee);
            rsp.setGasFee(gasFee);
            rsp.setTrackingId(req.getTrackingId());
            rsp.setWalletAddress(entity.getSourceAddress());
            rspList.add(rsp);

        }
        withdrawalRecordService.saveBatch(recordEntityList);
        // webhook
        webhookEventServiceFacade.asyncSaveAndTriggerWebhook(recordEntityList);

        for (WithdrawalRecordEntity recordEntity : recordEntityList) {
            //商户配置中出金审核为否触发自动审核
            if (merchantEntity.getWithdrawalAudit() == BooleanStatusEnum.ITEM_0.getCode()
                    && recordEntity.getAutoAudit() == BooleanStatusEnum.ITEM_1.getCode()) {
                log.info("触发自动审核 {}", recordEntity);
                this.withdrawalAudit(merchantId, new WithdrawalAuditReq(recordEntity.getTrackingId(), 1,
                        recordEntity.getRemark()));
            }
            if (recordEntity.getStatus() == WithdrawalRecordStatusEnum.ITEM_2.getCode()) {
                // 余额不足 触发告警
                withdrawalRecordService.balanceAlert(recordEntity);
            }
        }
        return RetResult.data(rspList);
    }

    /**
     * 检查出金余额是否充足, 充足则冻结金额, 并返回null, 如果不足则返回2(WithdrawalRecordStatusEnum.ITEM_2.getCode())
     *
     * @param merchantId
     * @param assetName
     * @param netProtocol
     * @param amount
     * @param entity
     * @param difFeeFlag
     * @param feeAssetName
     * @param channelCost
     * @return
     */
    private Integer frozenAndHandlerRecord(String merchantId, String assetName, String netProtocol, BigDecimal amount
            , WithdrawalRecordEntity entity, boolean difFeeFlag, String feeAssetName, BigDecimal channelCost) {
        //只有手续费币种不一致的时候才走多个
//        WalletEntity walletEntity;
        EstimateFeeReq estimateFeeReq = new EstimateFeeReq();
        estimateFeeReq.setAssetName(assetName);
        estimateFeeReq.setNetProtocol(netProtocol);
        estimateFeeReq.setChannelSubType(1);
        RetResult<EstimateFeeRsp> estimateFeeRsp = assetConfigServiceFacade.estimateFeeNew(estimateFeeReq);
        // 获取手续费, 因为手续费是根据币种计算的, 所以直接取没转换的币种手续费都没问题, 因为处理时, 是按不同钱包维度处理得
        BigDecimal estimateFee = estimateFeeRsp.isSuccess() && estimateFeeRsp.getData() != null ?
                estimateFeeRsp.getData().getChainTransactionFee() : BigDecimal.ZERO;
        entity.setFreezeEsFee(estimateFee.add(channelCost));
//        if (difFeeFlag) {
//            walletEntity = walletService.getAvailableTransferOut2Asset(merchantId, assetName, netProtocol,
//            feeAssetName, amount, estimateFee.add(channelCost), entity);
//        } else {
//            walletEntity = walletService.getAvailableTransferOut(merchantId, assetName, netProtocol, amount.add
//            (estimateFee).add(channelCost));
//            if (walletEntity != null) {
//                entity.setFreezeWalletId(walletEntity.getId());
//            }
//        }
        // 获取可用的钱包并且冻结
        CryptoWithdrawWalletRsp cryptoWithdrawWallet =
                merchantWalletService.getCryptoWithdrawWalletAndFreeze(merchantId,
                        ChannelSubTypeEnum.FIRE_BLOCKS.getCode(), assetName, netProtocol, feeAssetName, amount,
                        estimateFee.add(channelCost));
        if (cryptoWithdrawWallet == null) {
            // 没有可用的钱包 2:余额不足
            entity.setAddrBalance(BigDecimal.ZERO);
            //余额不足状态订单增多指标监控
            JSONObject InsBalanceMonitor = new JSONObject().put("Service", "payment").put("MonitorKey",
                            "InsBalanceMonitor")
                    .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
            MonitorLogUtil.log(InsBalanceMonitor);
            return WithdrawalRecordStatusEnum.ITEM_2.getCode();
        } else {
            entity.setSourceAddress(cryptoWithdrawWallet.getWalletAddress());
            // 获取手续费 作废 不在这计算
            //  gasFee = this.queryGasFee(req.getTrackingId(), req.getAmount(), req.getAssetName(), req.getAddress(),
            //  walletEntity);
            entity.setAccountId(cryptoWithdrawWallet.getAccountId());
            entity.setWalletId(cryptoWithdrawWallet.getWalletId());
            entity.setFreezeWalletId(cryptoWithdrawWallet.getFreezeWalletId());
            entity.setAddrBalance(cryptoWithdrawWallet.getBalance());
        }
        return null;
    }

    public RetResult<WithdrawalAuditRsp> withdrawalAudit(FundWithdrawalAuditReq req) {
        WithdrawalRecordEntity entity = withdrawalRecordService.getById(req.getId());
        WithdrawalAuditRsp rsp = new WithdrawalAuditRsp();
        rsp.setTrackingId(entity.getTrackingId());
        //加密货币
        if (AssetTypeEnum.CRYPTO_CURRENCY.getCode() == entity.getAssetType()) {
            // 老接口方法暂时不动 统一重构
            return this.withdrawalAudit(entity.getMerchantId(), new WithdrawalAuditReq(entity.getTrackingId(),
                    req.getAuditStatus(), ""));
        }
        //法币
        else if (AssetTypeEnum.FIAT_CURRENCY.getCode() == entity.getAssetType()) {
            if (WithdrawalRecordStatusEnum.ITEM_1.getCode() != entity.getStatus()) {
                throw new IllegalArgumentException("Not in pending review status, unable to operate!");
            }
            //审核通过
            if (WithdrawalAuditStatusEnum.ITEM_1.getCode() == req.getAuditStatus()) {
                try {
                    String redirectPageUrl = paymentPageServiceFacade.fundWithDrawal(entity);
                    rsp.setRedirectPageUrl(redirectPageUrl);
                } catch (BusinessException e) {
                    //业务异常属于业务问题阻碍的异常，需要人为处理
                    if (e.getMessage() != null && e.getMessage().equals(WithdrawalRecordStatusEnum.ITEM_2.getDesc())) {
                        throw new BusinessException(e.getMessage());
                    }
                } catch (IllegalArgumentException e) {
                    //记录状态为出金错误(非法异常属于校验类异常，需要终止此单，留作记录)
                    withdrawalRecordService.lambdaUpdate()
                            .set(WithdrawalRecordEntity::getStatus, WithdrawalRecordStatusEnum.ITEM_6.getCode())
                            .set(WithdrawalRecordEntity::getStayReason, e.getMessage().length() > 120 ? e.getMessage().substring(0, 120) : e.getMessage())
                            .eq(WithdrawalRecordEntity::getId, entity.getId())
                            .update();
                    throw e;
                }

                withdrawalRecordService.lambdaUpdate()
                        .set(WithdrawalRecordEntity::getAuditStatus, WithdrawalAuditStatusEnum.ITEM_1.getCode())
                        .eq(WithdrawalRecordEntity::getId, entity.getId())
                        .update();

            }
            //审核不通过
            else if (WithdrawalAuditStatusEnum.ITEM_2.getCode() == req.getAuditStatus()) {
                withdrawalRecordService.lambdaUpdate()
                        .set(WithdrawalRecordEntity::getStatus, WithdrawalRecordStatusEnum.ITEM_5.getCode())
                        .set(WithdrawalRecordEntity::getAuditStatus, WithdrawalAuditStatusEnum.ITEM_2.getCode())
                        .eq(WithdrawalRecordEntity::getId, entity.getId())
                        .update();
                //解锁钱包
                this.unfreezeWallet(entity);
                //触发webhook
                WebhookEventEntity webhookEventEntity = new WebhookEventEntity();
                webhookEventEntity.setEvent(WebhookEventConstants.WITHDRAWAL_EVENT);
                webhookEventEntity.setData(JSONUtil.toJsonStr(new WithdrawalEventDto(entity.getTrackingId()
                        , WithdrawalRecordStatusEnum.ITEM_5.getCode(), entity.getAmount(), entity.getStayReason())));
                webhookEventEntity.setTrackingId(entity.getTrackingId());
                webhookEventEntity.setWebhookUrl(entity.getWebhookUrl());
                webhookEventEntity.setMerchantId(entity.getMerchantId());
                webhookEventService.asyncSendWebhookEvent(webhookEventEntity);
            }
        }
        return RetResult.data(rsp);
    }

    /**
     * 出金审核
     *
     * @param merchantId
     * @param req
     * @return
     */
    public RetResult<WithdrawalAuditRsp> withdrawalAudit(String merchantId, WithdrawalAuditReq req) {
        log.info("出金审核 merchantId:{},req:{}", merchantId, req);
        WithdrawalAuditRsp rsp = new WithdrawalAuditRsp();
        rsp.setTrackingId(req.getTrackingId());

        WithdrawalRecordEntity entity = withdrawalRecordService.getOne(merchantId, req.getTrackingId());
        if (entity.getStatus() != WithdrawalRecordStatusEnum.ITEM_1.getCode()) {
            log.info("该单不处于待审核状态,无法进行审核");
            return RetResult.error("该单不处于待审核状态,无法进行审核");
        }

        if (req.getAuditStatus() == WithdrawalAuditStatusEnum.ITEM_1.getCode()) {
            // 审核通过 改为出金中
            entity.setStatus(WithdrawalRecordStatusEnum.ITEM_3.getCode());
            entity.setAuditStatus(WithdrawalAuditStatusEnum.ITEM_1.getCode());
            //  执行出金流程
            AccountEntity accountEntity = accountService.getById(entity.getAccountId());
            if (accountEntity == null) {
                withdrawalRecordService.lambdaUpdate().set(WithdrawalRecordEntity::getStatus,
                                WithdrawalRecordStatusEnum.ITEM_2.getCode())
                        .eq(WithdrawalRecordEntity::getId, entity.getId()).update();
                // 余额不足 触发告警
                withdrawalRecordService.balanceAlert(entity);
                throw new IllegalArgumentException("Insufficient Balance!");
            }
            ChannelAssetConfigEntity channelAssetEntity = channelAssetConfigService.lambdaQuery()
                    .eq(ChannelAssetConfigEntity::getChannelSubType, entity.getChannelSubType())
                    .eq(ChannelAssetConfigEntity::getAssetType, AssetTypeEnum.CRYPTO_CURRENCY.getCode())
                    .eq(ChannelAssetConfigEntity::getAssetName, entity.getAssetName())
                    .eq(ChannelAssetConfigEntity::getNetProtocol, entity.getNetProtocol())
                    .eq(ChannelAssetConfigEntity::getStatus, StatusEnum.ACTIVE.getCode())
                    .one();
            if (channelAssetEntity == null) {
                //出金错误, 查不到mchannel_asset信息, 直接返回, 不在查询
                entity.setStatus(WithdrawalRecordStatusEnum.ITEM_6.getCode());
                log.info("[withdrawalAudit] channelAssetEntity is not find, ChannelSubType={}, AssetName={}, " +
                        "NetProtocol={}", entity.getChannelSubType(), entity.getAssetName(), entity.getNetProtocol());
                return handlerResult(req, entity, rsp);
            }

            RetResult<GatewayWithdrawalRsp> retResult = withdrawalRecordService.fireblocksWithdrawal(entity,
                    channelAssetEntity.getChannelAssetName(), accountEntity);
            if (retResult.isSuccess()) {
                GatewayWithdrawalRsp data = retResult.getData();
                if (data != null) {
                    entity.setTransactionId(data.getChannelTransactionId());
                }
                entity.setStatus(WithdrawalRecordStatusEnum.ITEM_3.getCode());
            } else {
                entity.setStatus(WithdrawalRecordStatusEnum.ITEM_6.getCode());
            }
        } else {
            // 审核不通过
            entity.setStatus(WithdrawalRecordStatusEnum.ITEM_5.getCode());
            entity.setAuditStatus(WithdrawalAuditStatusEnum.ITEM_2.getCode());
        }
        return handlerResult(req, entity, rsp);
    }

    private RetResult<WithdrawalAuditRsp> handlerResult(WithdrawalAuditReq req, WithdrawalRecordEntity
            entity, WithdrawalAuditRsp rsp) {
        entity.setRemark(req.getRemark());
        withdrawalRecordService.updateById(entity);
        if (entity.getStatus() == WithdrawalRecordStatusEnum.ITEM_5.getCode()
                || entity.getStatus() == WithdrawalRecordStatusEnum.ITEM_6.getCode()) {
            this.unfreezeWallet(entity);
        }
        webhookEventServiceFacade.asyncSaveAndTriggerWebhook(entity);
        return RetResult.data(rsp);
    }

    public RetResult<List<Object>> webhookQuery(String merchantId, WebhookEventQueryReq req) {
        List<Object> rspList = new ArrayList<>();
        List<WebhookEventEntity> list = webhookEventService.list(Wrappers.lambdaQuery(WebhookEventEntity.class)
                .eq(WebhookEventEntity::getMerchantId, merchantId)
                .eq(WebhookEventEntity::getEvent, req.getEvent())
                .eq(WebhookEventEntity::getTrackingId, req.getTrackingId())
                .orderByAsc(WebhookEventEntity::getCreateTime));
        for (WebhookEventEntity entity : list) {
            JSONObject entries = JSONUtil.parseObj(entity.getData());
            rspList.add(entries);
        }
        return RetResult.data(rspList);
    }

    /**
     * 资产列表查询
     *
     * @return
     */
    public RetResult<List<AssetListQueryRsp>> assetListQuery(AssetListQueryReq req) {
        LambdaQueryWrapper<ChannelAssetConfigEntity> queryWrapper = Wrappers.lambdaQuery(ChannelAssetConfigEntity.class)
                .eq(ChannelAssetConfigEntity::getStatus, StatusEnum.ACTIVE.getCode())
                .orderByAsc(BaseNoLogicalDeleteEntity::getCreateTime);
        if (StringUtils.isNotBlank(req.getAssetName())) {
            queryWrapper.eq(ChannelAssetConfigEntity::getAssetName, req.getAssetName());
        }
        if (StringUtils.isNotBlank(req.getNetProtocol())) {
            queryWrapper.eq(ChannelAssetConfigEntity::getNetProtocol, req.getNetProtocol());
        }
        List<ChannelAssetConfigEntity> list = channelAssetConfigService.list(queryWrapper);

        List<AssetListQueryRsp> resultList = list.stream().map(AssetListQueryRsp::valueOf).toList();
        if (CollectionUtils.isNotEmpty(resultList)) {
            Set<String> symbols =
                    list.stream().map(entry -> entry.getAssetName() + AssetConstants.AN_USDT).collect(Collectors.toSet());
            Map<String, BigDecimal> symbolPriceMap = assetLastQuoteService.getSymbolAndMaxPriceBySymbol(symbols);
            BigDecimal magnification = new BigDecimal("1.3");
            resultList.forEach(entry -> {
                if (symbolPriceMap.get(entry.getAssetName() + AssetConstants.AN_USDT) == null) {
                    //没找到汇率则返回0.
                    entry.setReferMinDepositAmountSelf(BigDecimal.ZERO);
                    entry.setReferMinWithdrawalAmountSelf(BigDecimal.ZERO);
                } else {
                    // (最小出入金金额 * 1.3) / 汇率
                    BigDecimal ratio = symbolPriceMap.get(entry.getAssetName() + AssetConstants.AN_USDT);
                    entry.setReferMinDepositAmountSelf(entry.getMinDepositAmount().multiply(magnification).divide(ratio, 16, RoundingMode.CEILING));
                    entry.setReferMinWithdrawalAmountSelf(entry.getMinWithdrawalAmount().multiply(magnification).divide(ratio, 16, RoundingMode.CEILING));
                }
            });
        }
        return RetResult.data(resultList);
    }

    /**
     * 刷新出金记录状态
     *
     * @param withdrawalRecordId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public RetResult<WithdrawalRecordEntity> withdrawalRecordRefresh(String withdrawalRecordId) {
        // 改为同步通道钱包, 然后给商户的出金钱包新增余额
        WithdrawalRecordEntity entity = withdrawalRecordService.getById(withdrawalRecordId);
        if (entity == null) {
            return RetResult.error("未查到该出金记录");
        }
        // 2:余额不足 才进行刷新处理
        Integer status = entity.getStatus();
        if (status != WithdrawalRecordStatusEnum.ITEM_2.getCode()) {
            return RetResult.data(entity);
        }

        String assetName = entity.getAssetName();
        String netProtocol = entity.getNetProtocol();

        ChannelAssetConfigEntity channelAssetConfigEntity = channelAssetConfigService.lambdaQuery()
                .eq(ChannelAssetConfigEntity::getChannelSubType, entity.getChannelSubType())
                .eq(ChannelAssetConfigEntity::getAssetName, assetName)
                .eq(ChannelAssetConfigEntity::getNetProtocol, netProtocol)
                .eq(ChannelAssetConfigEntity::getStatus, StatusEnum.ACTIVE.getCode())
                .one();
        if (channelAssetConfigEntity == null) {
            return RetResult.error("查询失败,不支持该资产");
        }
        String feeAssetName = channelAssetConfigEntity.getFeeAssetName();

        // true的时候为异种币手续费
        boolean difFeeFlag = !assetName.equals(feeAssetName);

        // 刷新该商户的所有钱包余额
        channelWalletService.syncBalanceByFireBlocks(entity.getAccountId());

        // 获取可用的钱包并且冻结
        BigDecimal gasFee = BigDecimal.ZERO;
        Integer handlerStatus = frozenAndHandlerRecord(entity.getMerchantId(), entity.getAssetName(),
                entity.getNetProtocol(), entity.getAmount(), entity, difFeeFlag, feeAssetName, entity.getChannelFee());
        entity.setGasFee(gasFee);
        // 如果handlerStatus = null, 则余额充足, 只有待审核才能去审核
        entity.setStatus(handlerStatus == null ? WithdrawalRecordStatusEnum.ITEM_1.getCode() : handlerStatus);

        // 三期: 余额充足, 再走自动审核
        if (entity.getStatus().equals(WithdrawalRecordStatusEnum.ITEM_1.getCode()) && entity.getAutoAudit() == 1) {
            // 更新和执行webhook
            withdrawalRecordService.updateById(entity);
            webhookEventServiceFacade.asyncSaveAndTriggerWebhook(entity);
            log.info("[withdrawalRecordRefresh] 触发自动审核 {}", entity);
            this.withdrawalAudit(entity.getMerchantId(), new WithdrawalAuditReq(entity.getTrackingId(), 1,
                    entity.getRemark()));
        }

        return RetResult.data(entity);
    }


    public RetResult<WithdrawalCheckRsp> withdrawalCheck(String merchantId, WithdrawalCheckReq req) {
        MerchantWalletEntity walletEntity =
                merchantWalletService.getOne(Wrappers.lambdaQuery(MerchantWalletEntity.class)
                        .eq(MerchantWalletEntity::getMerchantId, merchantId).eq(MerchantWalletEntity::getAssetName,
                                req.getAssetName())
                        .eq(MerchantWalletEntity::getNetProtocol, req.getNetProtocol()));
        if (walletEntity == null) {
            return RetResult.data(new WithdrawalCheckRsp(0));
        }
        // 钱包余额减去冻结金额是否大于提现金额
        if (walletEntity.getBalance().subtract(walletEntity.getFreezeAmount()).compareTo(req.getAmount()) < 0) {
            return RetResult.data(new WithdrawalCheckRsp(0));
        }
        return RetResult.data(new WithdrawalCheckRsp(1));
    }

    public void scanWithdrawRequestByTimeout() {
        List<WithdrawalRecordEntity> list =
                withdrawalRecordService.list(Wrappers.lambdaQuery(WithdrawalRecordEntity.class).eq(WithdrawalRecordEntity::getStatus, 3)
                        .lt(WithdrawalRecordEntity::getCreateTime, DateUtil.offsetMinute(new Date(), -30)));
        for (WithdrawalRecordEntity recordEntity : list) {
            if (StrUtil.isEmpty(recordEntity.getTransactionId())) {
                // 没有交易id 直接标记为失败
                recordEntity.setStatus(6);
                unfreezeWallet(recordEntity);
                withdrawalRecordService.updateById(recordEntity);
                log.info("出金超时,没有txId直接标记为失败,跟踪id:{}", recordEntity.getTrackingId());
            } else {
                //    重新触发webhook
                ResendWebhooksReq resendWebhooksReq = new ResendWebhooksReq();
                resendWebhooksReq.setTxId(recordEntity.getTransactionId());
                fireBlocksAPI.resendWebhooks(resendWebhooksReq);
                log.info("重新触发webhook,跟踪id:{},txId:{}", recordEntity.getTrackingId(), recordEntity.getTransactionId());
            }
        }
    }


    /**
     * 解冻钱包
     */
    public void unfreezeWallet(WithdrawalRecordEntity recordEntity) {
        // 出金成功或失败 解冻对应金额 如果冻结金额被扣为0 则解冻
        // 处理金额解冻问题, 先解冻异种币手续费, 再解冻出金账号
        // 如果是本币手续费的话, 需要加上手续费, 如果是异种币手续费,就不需要加上手续费
        BigDecimal amount = recordEntity.getAmount();
        BigDecimal freezeDifEsFee = recordEntity.getFreezeEsFee();//ps:这里冻结的金额=手续费+平台费
        BigDecimal channelFee = recordEntity.getChannelFee();
        String freezeWalletId = recordEntity.getFreezeWalletId();
        // freezeWalletId="0" 为历史数据 这里做特殊处理
        if (StringUtils.isNotBlank(freezeWalletId) && !freezeWalletId.equals(recordEntity.getWalletId()) && !"0".equals(freezeWalletId)) {
            //walletService.refreshWalletBalanceBatch(Collections.singletonList(freezeWalletId));
//            WalletEntity freezeDifWallet = walletService.getById(freezeWalletId);
//            unfreezeHandle(freezeDifWallet, freezeDifEsFee);
            merchantWalletService.changeBalanceAndAmount(ChangeEventTypeEnum.WITHDRAWAL_FAIL, recordEntity.getId(),
                    recordEntity.getWalletId(), BigDecimal.ZERO, amount.negate(), "解冻异种币余额");
            merchantWalletService.changeBalanceAndAmount(ChangeEventTypeEnum.WITHDRAWAL_FAIL, recordEntity.getId(),
                    freezeWalletId, freezeDifEsFee, freezeDifEsFee.negate(), "解冻异种币手续费");
        } else {
            // 如果是本币手续费的话, 需要加上手续费
            amount = amount.add(freezeDifEsFee);
            merchantWalletService.changeBalanceAndAmount(ChangeEventTypeEnum.WITHDRAWAL_FAIL, recordEntity.getId(),
                    recordEntity.getWalletId(), BigDecimal.ZERO, amount.negate(), "解冻本币余额及手续费");
        }
        // 解冻本币金额操作

//        WalletEntity wallet = walletService.getById(recordEntity.getWalletId());
//        if (wallet != null) {
//            unfreezeHandle(wallet, amount);
//            recordEntity.setAddrBalance(wallet.getBalance());
//        }
    }


    public RetResult<List<WithdrawalQueryRsp>> withdrawalQuery(String merchantId, WithdrawalQueryReq req) {
        log.info("出金查询 merchantId:{},req:{}", merchantId, req);
        if (CollUtil.isEmpty(req.getTrackingIds()) || req.getTrackingIds().size() > 20) {
            return RetResult.error("出金查询单不能为空或超过20个");
        }
        List<WithdrawalRecordEntity> list =
                withdrawalRecordService.list(Wrappers.lambdaQuery(WithdrawalRecordEntity.class)
                        .eq(WithdrawalRecordEntity::getMerchantId, merchantId)
                        .in(WithdrawalRecordEntity::getTrackingId, req.getTrackingIds()));
        if (CollUtil.isEmpty(list)) {
            return RetResult.error("未查到该出金记录");
        }
        return RetResult.data(list.stream().map(WithdrawalQueryRsp::valueOf).toList());
    }

    public RetResult<List<DepositQueryRsp>> depositQuery(String merchantId, DepositQueryReq req) {
        log.info("入金查询 merchantId:{},req:{}", merchantId, req);
        if (CollUtil.isEmpty(req.getTrackingIds()) || req.getTrackingIds().size() > 20) {
            return RetResult.error("入金查询单不能为空或超过20个");
        }
        List<DepositRecordEntity> list = depositRecordService.list(Wrappers.lambdaQuery(DepositRecordEntity.class)
                .eq(DepositRecordEntity::getMerchantId, merchantId)
                .in(DepositRecordEntity::getTrackingId, req.getTrackingIds()));
        if (CollUtil.isEmpty(list)) {
            return RetResult.error("未查到该入金记录");
        }
        List<DepositQueryRsp> rspList = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        for (DepositRecordEntity entity : list) {
            rspList.add(DepositQueryRsp.valueOf(entity));
            ids.add(entity.getId());
        }
        List<DepositRecordDetailEntity> details = depositRecordDetailService.listByRecordIds(ids);
        Map<String, List<DepositRecordDetailEntity>> detailMap =
                details.stream().collect(Collectors.groupingBy(DepositRecordDetailEntity::getRecordId));
        for (DepositQueryRsp rsp : rspList) {
            List<DepositRecordDetailEntity> detailList = detailMap.get(rsp.getId());
            if (CollUtil.isNotEmpty(detailList)) {
                rsp.setDetails(detailList.stream().map(DepositDetailQueryRsp::valueOf).toList());
            }
        }
        return RetResult.data(rspList);
    }

    public RetResult<List<WalletBalanceSumRsp>> walletBalanceSum(String merchantId, WalletBalanceSumReq req) {
        log.info("钱包余额汇总查询 merchantId:{},req:{}", merchantId, req);
        return RetResult.data(merchantWalletService.walletBalanceSum(merchantId, req));
    }

    public WithdrawalReExecuteRsp reExecute(WithdrawalReexecuteReq req) {
        WithdrawalRecordEntity withdrawalRecord = withdrawalRecordService.getById(req.getId());
        Integer assetType = withdrawalRecord.getAssetType();
        WithdrawalReExecuteRsp rsp = new WithdrawalReExecuteRsp();
        rsp.setTrackingId(withdrawalRecord.getTrackingId());
        //加密货币
        if (AssetTypeEnum.CRYPTO_CURRENCY.getCode() == assetType) {
            //  执行出金流程
            Integer status = frozenAndHandlerRecord(withdrawalRecord.getMerchantId(),
                    withdrawalRecord.getAssetName(), withdrawalRecord.getNetProtocol(), withdrawalRecord.getAmount(),
                    withdrawalRecord, !StrUtil.equals(withdrawalRecord.getAssetName(),
                            withdrawalRecord.getFeeAssetName())
                    , withdrawalRecord.getFeeAssetName(), withdrawalRecord.getChannelFee());
            // 获取可用的钱包并且冻结
            if (ObjectUtil.isNotNull(status)) {
                throw new IllegalArgumentException("Insufficient Balance!");
            }
            AccountEntity accountEntity = accountService.getById(withdrawalRecord.getAccountId());
            CreateTransactionReq createTransactionReq = new CreateTransactionReq();
            String externalTxId = "MC_" + withdrawalRecord.getMerchantId() + "_" + withdrawalRecord.getTrackingId();
            createTransactionReq.setExternalTxId(externalTxId);
            createTransactionReq.setIdempotencyKey(IdUtil.fastUUID());
            createTransactionReq.setAmount(withdrawalRecord.getAmount().toString());
            ChannelAssetConfigEntity channelAssetEntity = channelAssetConfigService.lambdaQuery()
                    .eq(ChannelAssetConfigEntity::getChannelSubType, withdrawalRecord.getChannelSubType())
                    .eq(ChannelAssetConfigEntity::getAssetType, AssetTypeEnum.CRYPTO_CURRENCY.getCode())
                    .eq(ChannelAssetConfigEntity::getAssetName, withdrawalRecord.getAssetName())
                    .eq(ChannelAssetConfigEntity::getNetProtocol, withdrawalRecord.getNetProtocol())
                    .eq(ChannelAssetConfigEntity::getStatus, StatusEnum.ACTIVE.getCode())
                    .one();
            if (channelAssetEntity == null) {
                //出金错误, 查不到mchannel_asset信息, 直接返回, 不在查询
                withdrawalRecord.setStatus(WithdrawalRecordStatusEnum.ITEM_6.getCode());
                log.info("[withdrawalAudit] channelAssetEntity is not find, ChannelSubType={}, AssetName={}, " +
                                "NetProtocol={}",
                        withdrawalRecord.getChannelSubType(), withdrawalRecord.getAssetName(),
                        withdrawalRecord.getNetProtocol());
                withdrawalRecordService.updateById(withdrawalRecord);
                //解冻钱包
                this.unfreezeWallet(withdrawalRecord);
                return rsp;
            } else {
                // todo 优化 通道资产名称存到记录中
                createTransactionReq.setAssetId(channelAssetEntity.getChannelAssetName());
            }
            TransactionPeerPathReq source = new TransactionPeerPathReq();
            source.setType("VAULT_ACCOUNT");
            source.setId(accountEntity.getExternalId());
            source.setName(accountEntity.getName());
            createTransactionReq.setSource(source);
            TransactionDestinationPeerPathReq destination = new TransactionDestinationPeerPathReq();
            destination.setType("VAULT_ACCOUNT");
            OneTimeAddressReq oneTimeAddress = new OneTimeAddressReq();
            TransactionValReq address = new TransactionValReq();
            address.setValue(withdrawalRecord.getDestinationAddress());
            oneTimeAddress.setAddress(address);
            destination.setOneTimeAddress(oneTimeAddress);
            createTransactionReq.setDestination(destination);
            createTransactionReq.setTreatAsGrossAmount(false);
            createTransactionReq.setSign("&@!MC_PAYMENT&&TRAN!425$"); //临时处理, 防止其他服务调度.
            log.info("createTransactionReq:{}", createTransactionReq);
            //记录一下时间, 在执行方法后后打印出来
            long now = System.currentTimeMillis();
            RetResult<CreateTransactionVo> createTransactionVoRetResult =
                    fireBlocksAPI.createTransactions(createTransactionReq);
            log.info("[withdrawalAudit], 调用出金申请耗时:{}, createTransactionVoRetResult:{}",
                    System.currentTimeMillis() - now, createTransactionVoRetResult);
            if (createTransactionVoRetResult.isSuccess()) {
                // //状态,[0:已提交,1:待审核,2:余额不足,3:出金中,4:出金完成,5:已拒绝,6:出金错误,7,终止出金]
                CreateTransactionVo data = createTransactionVoRetResult.getData();
                if (data != null) {
                    withdrawalRecord.setTransactionId(data.getId());
                }
                withdrawalRecord.setStatus(WithdrawalRecordStatusEnum.ITEM_3.getCode());
            } else {
                withdrawalRecord.setStatus(WithdrawalRecordStatusEnum.ITEM_6.getCode());
                this.unfreezeWallet(withdrawalRecord);
            }
            withdrawalRecordService.updateById(withdrawalRecord);
            webhookEventServiceFacade.asyncSaveAndTriggerWebhook(withdrawalRecord);
        }
        //法币
        else if (AssetTypeEnum.FIAT_CURRENCY.getCode() == assetType) {
            String redirectUrl = paymentPageServiceFacade.fundWithDrawal(withdrawalRecord);
            rsp.setRedirectUrl(redirectUrl);
        }
        return rsp;
    }

    public DepositAuditRsp depositAudit(FundDepositAuditReq req) {
        DepositRecordEntity depositRecord = depositRecordService.getById(req.getId());
        if (DepositRecordStatusEnum.ITEM_5.getCode() != depositRecord.getStatus()) {
            throw new IllegalArgumentException("Not in pending review status,unable to operate!");
        }
        Integer assetType = depositRecord.getAssetType();
        Integer auditStatus = req.getAuditStatus();
        DepositAuditRsp rsp = new DepositAuditRsp();
        //加密货币
        if (assetType == AssetTypeEnum.CRYPTO_CURRENCY.getCode()) {
            Integer eventStatus = DepositRecordStatusEnum.ITEM_2.getCode();
            List<DepositRecordDetailEntity> list =
                    depositRecordDetailService.lambdaQuery().eq(DepositRecordDetailEntity::getRecordId,
                                    depositRecord.getId())
                            .list();
            BigDecimal accumulatedAmount =
                    list.stream().map(DepositRecordDetailEntity::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (accumulatedAmount.compareTo(depositRecord.getAmount()) < 0) {
                //部分入金
                eventStatus = DepositRecordStatusEnum.ITEM_1.getCode();
            }
            //审核通过
            if (auditStatus == DepositAuditStatusEnum.ITEM_1.getCode()) {
                depositRecordService.lambdaUpdate().set(DepositRecordEntity::getStatus, eventStatus)
                        .set(DepositRecordEntity::getAuditStatus, DepositAuditStatusEnum.ITEM_1.getCode())
                        .eq(DepositRecordEntity::getId, depositRecord.getId()).update();
            }
            //审核不通过
            else if (auditStatus == DepositAuditStatusEnum.ITEM_2.getCode()) {
                depositRecordService.lambdaUpdate().set(DepositRecordEntity::getStatus, eventStatus)
                        .set(DepositRecordEntity::getAuditStatus, DepositAuditStatusEnum.ITEM_2.getCode())
                        .eq(DepositRecordEntity::getId, depositRecord.getId()).update();
            }
            // 触发webhook
            webhookEventServiceFacade.asyncSaveAndTriggerWebhook(depositRecord);
        }
        //法币
        else if (assetType == AssetTypeEnum.FIAT_CURRENCY.getCode()) {
            //审核通过
            if (auditStatus == DepositAuditStatusEnum.ITEM_1.getCode()) {
                depositRecordService.lambdaUpdate().set(DepositRecordEntity::getStatus,
                                DepositRecordStatusEnum.ITEM_2.getCode())
                        .set(DepositRecordEntity::getAuditStatus, DepositAuditStatusEnum.ITEM_1.getCode())
                        .eq(DepositRecordEntity::getId, depositRecord.getId()).update();
                merchantWalletService.changeBalance(ChangeEventTypeEnum.DEPOSIT, depositRecord.getId(),
                        depositRecord.getWalletId(),
                        depositRecord.getAccumulatedAmount(), "入金成功");
            }
            //审核不通过
            else if (auditStatus == DepositAuditStatusEnum.ITEM_2.getCode()) {
                depositRecordService.lambdaUpdate().set(DepositRecordEntity::getStatus,
                                DepositRecordStatusEnum.ITEM_2.getCode())
                        .set(DepositRecordEntity::getAuditStatus, DepositAuditStatusEnum.ITEM_2.getCode())
                        .eq(DepositRecordEntity::getId, depositRecord.getId()).update();
            }
            // 触发webhook
            WebhookEventEntity webhookEventEntity = new WebhookEventEntity();
            webhookEventEntity.setEvent(WebhookEventConstants.DEPOSIT_EVENT);
            webhookEventEntity.setData(JSONUtil.toJsonStr(new DepositEventDto(depositRecord.getTrackingId()
                    , DepositRecordStatusEnum.ITEM_2.getCode(), depositRecord.getAmount())));
            webhookEventEntity.setTrackingId(depositRecord.getTrackingId());
            webhookEventEntity.setWebhookUrl(depositRecord.getWebhookUrl());
            webhookEventEntity.setMerchantId(depositRecord.getMerchantId());
            webhookEventService.asyncSendWebhookEvent(webhookEventEntity);
        }
        rsp.setTrackingId(depositRecord.getTrackingId());
        return rsp;
    }
}
