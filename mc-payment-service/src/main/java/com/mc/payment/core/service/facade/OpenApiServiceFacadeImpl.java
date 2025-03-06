package com.mc.payment.core.service.facade;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mc.payment.api.model.dto.DepositDetailDto;
import com.mc.payment.api.model.dto.DepositEventDto;
import com.mc.payment.api.model.dto.WithdrawalDetailDto;
import com.mc.payment.api.model.dto.WithdrawalEventDto;
import com.mc.payment.api.model.req.QueryAssetSupportedBankReq;
import com.mc.payment.api.model.req.QueryDepositReportReq;
import com.mc.payment.api.model.req.QueryMerchantSnapshotReq;
import com.mc.payment.api.model.req.WithdrawalReq;
import com.mc.payment.api.model.rsp.*;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.exception.BusinessException;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.config.AppConfig;
import com.mc.payment.core.service.constant.AssetConstants;
import com.mc.payment.core.service.constant.WebhookEventConstants;
import com.mc.payment.core.service.entity.*;
import com.mc.payment.core.service.manager.CurrencyRateManager;
import com.mc.payment.core.service.manager.withdrawal.WithdrawalManager;
import com.mc.payment.core.service.model.dto.MerchantAssetDto;
import com.mc.payment.core.service.model.enums.*;
import com.mc.payment.core.service.model.req.EstimateFeeReq;
import com.mc.payment.core.service.model.req.WithdrawalAuditReq;
import com.mc.payment.core.service.model.rsp.CryptoWithdrawWalletRsp;
import com.mc.payment.core.service.model.rsp.EstimateFeeRsp;
import com.mc.payment.core.service.model.rsp.WithdrawalAuditRsp;
import com.mc.payment.core.service.service.*;
import com.mc.payment.core.service.util.CommonUtil;
import com.mc.payment.core.service.util.MonitorLogUtil;
import com.mc.payment.gateway.model.rsp.GatewayWithdrawalRsp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class OpenApiServiceFacadeImpl implements OpenApiServiceFacade {
    private final IDepositRecordService depositRecordService;
    private final IDepositRecordDetailService depositRecordDetailService;
    private final IWithdrawalRecordService withdrawalRecordService;
    private final IWithdrawalRecordDetailService withdrawalRecordDetailService;
    private final MerchantWalletService merchantWalletService;
    private final IWebhookEventService webhookEventService;
    private final AssetBankService assetBankService;
    private final IPaymentPageServiceFacade paymentPageServiceFacade;
    private final IMerchantService merchantService;
    private final IAssetLastQuoteService assetLastQuoteService;
    private final IMerchantWalletSnapshotService merchantWalletSnapshotService;
    private final MerchantChannelAssetService merchantChannelAssetService;
    private final ProtocolConfigService protocolConfigService;
    private final IWalletBlacklistService walletBlacklistService;
    private final IChannelCostService channelCostService;
    private final WebhookEventServiceFacade webhookEventServiceFacade;
    private final IAccountService accountService;
    private final AssetConfigServiceFacade assetConfigServiceFacade;
    private final ChannelAssetConfigService channelAssetConfigService;
    private final AppConfig appConfig;
    private final WithdrawalManager withdrawalManager;
    private final CurrencyRateManager currencyRateManager;
    @Autowired
    @Lazy
    private OpenApiServiceFacadeImpl self;


    @Override
    public RetResult<QueryDepositRsp> queryDeposit(String merchantId, String trackingId) {
        DepositRecordEntity depositRecordEntity = depositRecordService.getOne(merchantId, trackingId);
        if (depositRecordEntity == null) {
            return RetResult.error("该单不存在");
        }
        List<DepositRecordDetailEntity> depositRecordDetailEntityList = depositRecordDetailService.list(depositRecordEntity.getId());
        QueryDepositRsp queryDepositRsp = new QueryDepositRsp();

        //组装queryDepositRsp
        BeanUtils.copyProperties(depositRecordEntity, queryDepositRsp);
        if (depositRecordDetailEntityList != null) {
            List<DepositDetailDto> depositDetailDtos = depositRecordDetailEntityList.stream()
                    .map(entity -> {
                        DepositDetailDto depositDetailDto = new DepositDetailDto();
                        BeanUtils.copyProperties(entity, depositDetailDto);
                        return depositDetailDto;
                    })
                    .collect(Collectors.toList());
            queryDepositRsp.setDepositDetailDtoList(depositDetailDtos);
        }
        return RetResult.data(queryDepositRsp);
    }

    @Override
    public RetResult<Boolean> cancelDeposit(String merchantId, String trackingId) {
        DepositRecordEntity entity = depositRecordService.getOne(merchantId, trackingId);
        if (entity == null || entity.getStatus() != 0) {
            return RetResult.error("该单不处于待入金状态,无法进行撤销");
        }
        entity.setStatus(DepositRecordStatusEnum.ITEM_3.getCode());
        depositRecordService.updateById(entity);
        //撤销入金状态订单增多指标监控
        JSONObject cancelDeposit = new JSONObject().put("Service", "payment").put("MonitorKey", "cancelDeposit")
                .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
        MonitorLogUtil.log(cancelDeposit);
        // 触发webhook
        WebhookEventEntity webhookEventEntity = new WebhookEventEntity();
        webhookEventEntity.setEvent(WebhookEventConstants.DEPOSIT_EVENT);
        webhookEventEntity.setData(JSONUtil.toJsonStr(new DepositEventDto(entity.getTrackingId(), entity.getStatus(), entity.getAmount())));
        webhookEventEntity.setTrackingId(entity.getTrackingId());
        webhookEventEntity.setWebhookUrl(entity.getWebhookUrl());
        webhookEventEntity.setMerchantId(merchantId);
        webhookEventService.asyncSendWebhookEvent(webhookEventEntity);

        merchantWalletService.unlockAndCollWallet(Collections.singletonList(entity.getWalletId()), 0L);
        return RetResult.data(true);
    }

    @Override
    public QueryExchangeRateRsp queryExchangeRate(Integer assetType, String baseCurrency, String targetCurrency) {
        log.debug("查询汇率接口,assetType:{},baseCurrency:{},targetCurrency:{}", assetType, baseCurrency, targetCurrency);
        if (StrUtil.equals(baseCurrency, targetCurrency, true)) {
            // 同币种,汇率为1
            return new QueryExchangeRateRsp(BigDecimal.ONE);
        }
        assetType = assetType == null ? AssetTypeEnum.FIAT_CURRENCY.getCode() : assetType;
        BigDecimal exchangeRate = currencyRateManager.getCurrencyRate(assetType == AssetTypeEnum.CRYPTO_CURRENCY.getCode(),
                baseCurrency, targetCurrency);
        return exchangeRate != null ? new QueryExchangeRateRsp(exchangeRate) : null;
    }


    @Override
    public RetResult<WithdrawalRsp> withdrawal(String merchantId, String merchantName, WithdrawalReq req) {
        return RetResult.data(withdrawalManager.requestProcess(merchantId, merchantName, req));
/*        MonitorUtil.withdrawRequestCounter(merchantId, req);
        Integer assetType = req.getAssetType();

        if (assetType.equals(AssetTypeEnum.CRYPTO_CURRENCY.getCode())) {
            //虚拟货币
            return virtualCurrencyWithdrawal(merchantId, merchantName, req);
        } else {
            //法币
            return legalTenderWithdrawal(merchantId, merchantName, req);
        }*/
    }

    @Override
    public RetResult<QueryWithdrawalRsp> queryWithdrawal(String merchantId, String trackingId) {
        WithdrawalRecordEntity withdrawalRecordEntity = withdrawalRecordService.getOne(merchantId, trackingId);
        if (withdrawalRecordEntity == null) {
            return RetResult.error("该单不存在");
        }
        List<WithdrawalRecordDetailEntity> withdrawalRecordDetailEntityList = withdrawalRecordDetailService.list(withdrawalRecordEntity.getId());

        QueryWithdrawalRsp queryWithdrawalRsp = new QueryWithdrawalRsp();

        //组装queryWithdrawalRsp
        BeanUtils.copyProperties(withdrawalRecordEntity, queryWithdrawalRsp);
        if (withdrawalRecordDetailEntityList != null) {
            List<WithdrawalDetailDto> withdrawalDetailDtos = withdrawalRecordDetailEntityList.stream()
                    .map(entity -> {
                        WithdrawalDetailDto withdrawalDetailDto = new WithdrawalDetailDto();
                        BeanUtils.copyProperties(entity, withdrawalDetailDto);
                        return withdrawalDetailDto;
                    })
                    .collect(Collectors.toList());
            queryWithdrawalRsp.setWithdrawalDetailDtoList(withdrawalDetailDtos);
        }
        return RetResult.data(queryWithdrawalRsp);
    }


    @Override
    public List<QueryAssetSupportedBankRsp> queryAssetSupportedBank(QueryAssetSupportedBankReq req) {
        List<AssetBankEntity> list = assetBankService.list(Wrappers.lambdaQuery(AssetBankEntity.class)
                .eq(AssetBankEntity::getAssetName, req.getAssetName())
                .eq(AssetBankEntity::getNetProtocol, req.getNetProtocol())
                .apply("FIND_IN_SET(" + req.getPaymentType() + ",payment_type) > 0")
                .orderByDesc(BaseNoLogicalDeleteEntity::getCreateTime));
        return list.stream().map(AssetBankEntity::convert).toList();
    }

    /**
     * 出金校验
     *
     * @param merchantId
     * @param req
     */
    private void withdrawalValidate(String merchantId, WithdrawalReq req) {

        //拦截非法币出金
        if (AssetTypeEnum.FIAT_CURRENCY.getCode() != req.getAssetType()) {
            throw new IllegalArgumentException("本接口暂不支持非法币的出金!");
        }
        //  trackingId 同一个商户不可重复
        long count = withdrawalRecordService.count(Wrappers.lambdaQuery(WithdrawalRecordEntity.class)
                .eq(WithdrawalRecordEntity::getMerchantId, merchantId)
                .eq(WithdrawalRecordEntity::getTrackingId, req.getTrackingId()));
        if (count > 0) {
            throw new IllegalArgumentException("trackingId is duplicated, please check:" + req.getTrackingId());
        }
        if (req.getUserSelectable() == BooleanStatusEnum.ITEM_0.getCode()) {
            //效验, 防止乱传没有的配置的资产信息
            MerchantAssetDto merchantAssetDto = merchantChannelAssetService.getAssetConfigOne(merchantId, req.getAssetType(),
                    req.getAssetName(),
                    req.getNetProtocol());
            if (merchantAssetDto == null || merchantAssetDto.getWithdrawalStatus() == BooleanStatusEnum.ITEM_0.getCode()) {
                throw new IllegalArgumentException("您输入的资产信息不存在, 请检查:assetName,netProtocol");
            }
            // 最小出金校验
            BigDecimal minWithdrawalAmount = merchantAssetDto.getMinWithdrawalAmount();
            if (minWithdrawalAmount != null && req.getAmount().compareTo(minWithdrawalAmount) < 0) {
                throw new IllegalArgumentException("金额小于最小出金金额,请检查:" + minWithdrawalAmount);
            }
            // 最大出金校验
            BigDecimal maxWithdrawalAmount = merchantAssetDto.getMaxWithdrawalAmount();
            if (maxWithdrawalAmount != null && maxWithdrawalAmount.compareTo(BigDecimal.ZERO) != 0 && req.getAmount().compareTo(maxWithdrawalAmount) > 0) {
                throw new IllegalArgumentException("金额大于最大出金金额,请检查:" + maxWithdrawalAmount);
            }
            // 校验银行代码
            // 判断是否需要校验
            boolean exists = assetBankService.existWithdraw(req.getAssetName(), req.getNetProtocol());
            if (exists) {
                boolean existedDepositBankCode = assetBankService.existWithdrawBankCode(req.getAssetName(), req.getNetProtocol(), req.getBankCode());
                if (!existedDepositBankCode) {
                    throw new IllegalArgumentException("您输入的银行代码不存在, 请检查:bankCode");
                }
            }
        } else if (req.getUserSelectable() == BooleanStatusEnum.ITEM_1.getCode()) {
            boolean exists = merchantChannelAssetService.exists(merchantId, req.getAssetType(), req.getAssetName());

            if (!exists) {
                throw new IllegalArgumentException("暂不支持该支付币种!");
            }
            if (req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("金额需大于零!");
            }
        }
    }

    @Override
    public List<QueryDepositReportRsp> queryReport(QueryDepositReportReq req) {
        List<String> trackingIdList = req.getTrackingIdList();
        List<QueryDepositReportRsp> list = depositRecordService.queryReport(trackingIdList);
        return list;
    }

    @Override
    public PageRsp<MerchantWalletSnapshotRsp> queryMerchantSnapshot(QueryMerchantSnapshotReq req) {
        BasePageRsp<MerchantWalletSnapshotRsp> page = merchantWalletSnapshotService.getMerchatSnapshotPage(req);
        PageRsp rsp = new PageRsp(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent(), page.getPages(), page.getHasPrevious(), page.getHasNext());
        return rsp;
    }


    /**
     * 构建出金申请单实例
     *
     * @param merchant
     * @param req
     * @return
     */
    private WithdrawalRecordEntity generateWithdrawalAndSaveRecord(MerchantEntity merchant, WithdrawalReq req) {

        WithdrawalRecordEntity recordEntity = new WithdrawalRecordEntity();
        recordEntity.setAssetName(req.getAssetName());
        recordEntity.setFeeAssetName(req.getAssetName());
        recordEntity.setDestinationAddress(req.getAddress());
        recordEntity.setAmount(req.getAmount());
        recordEntity.setRemark(req.getRemark());
        recordEntity.setTrackingId(req.getTrackingId());
        recordEntity.setNetProtocol(req.getNetProtocol());
        recordEntity.setUserId(req.getUserId());
        recordEntity.setUserIp(req.getUserIp());
        recordEntity.setMerchantId(merchant.getId());
        recordEntity.setMerchantName(merchant.getName());
        //需要审核
        if (BooleanStatusEnum.ITEM_1.getCode() == merchant.getWithdrawalAudit()) {
            recordEntity.setStatus(WithdrawalRecordStatusEnum.ITEM_1.getCode());
            recordEntity.setAutoAudit(0);
            recordEntity.setStayReason("商户出金审核");
        } else {
            recordEntity.setStatus(WithdrawalRecordStatusEnum.ITEM_0.getCode());
            recordEntity.setAutoAudit(1);// 法币默认自动审核
        }
        //频繁出金校验
        if (StrUtil.isNotBlank(req.getUserId())) {
            Integer withdrawalNum = withdrawalRecordService.lambdaQuery().eq(WithdrawalRecordEntity::getUserId, req.getUserId())
                    .apply("create_time >= DATE_SUB(NOW(),INTERVAL 1 HOUR)").count().intValue();
            if (withdrawalNum > 3) {
                recordEntity.setStatus(WithdrawalRecordStatusEnum.ITEM_1.getCode());
                recordEntity.setAutoAudit(0);
                recordEntity.setStayReason("一小时内出金次数大于3次");
            }
        }
        recordEntity.setChannelSubType(CommonUtil.choosePaymentChannel(req.getAssetType(), req.getAssetName(), req.getNetProtocol()).getCode());
        recordEntity.setTargetCurrency(req.getAssetType() == AssetTypeEnum.CRYPTO_CURRENCY.getCode() ? AssetConstants.AN_USDT : AssetConstants.AN_USD);
        recordEntity.setAssetType(req.getAssetType());
        recordEntity.setWebhookUrl(req.getWebhookUrl());
        recordEntity.setBankCode(req.getBankCode());
        QueryExchangeRateRsp queryExchangeRateRsp = this.queryExchangeRate(req.getAssetType(), req.getAssetName(), AssetConstants.AN_USD);
        recordEntity.setRate(queryExchangeRateRsp.getExchangeRate() == null ? BigDecimal.ZERO : queryExchangeRateRsp.getExchangeRate());
        recordEntity.setBankName(req.getBankName());
        recordEntity.setAccountName(req.getAccountName());
        recordEntity.setBankNum(req.getBankNum());

        if (!Objects.isNull(req.getExtraMap())) {
            recordEntity.setExtraMap(JSONUtil.toJsonStr(req.getExtraMap()));
        }

        // 加密货币使用
        recordEntity.setFreezeEsFee(BigDecimal.ZERO);
        recordEntity.setFreezeWalletId("");
        recordEntity.setTransactionId("");
        recordEntity.setAddrBalance(BigDecimal.ZERO);
        recordEntity.setChannelAssetName("");
        recordEntity.setFeeChannelAssetName("");
        recordEntity.setFeeRate(BigDecimal.ZERO);
        recordEntity.setTxHash("");
        recordEntity.setGasFee(BigDecimal.ZERO);
        recordEntity.setChannelFee(BigDecimal.ZERO);

        MerchantWalletEntity walletEntity = merchantWalletService.getWithdrawWallet(recordEntity);
        if (walletEntity == null) {
            throw new IllegalArgumentException("商户钱包不存在, 请检查");
        }
        recordEntity.setSourceAddress(walletEntity.getWalletAddress());
        recordEntity.setAddrBalance(walletEntity.getBalance());
        withdrawalRecordService.save(recordEntity);
        if (walletEntity.getBalance().subtract(walletEntity.getFreezeAmount()).compareTo(recordEntity.getAmount()) < 0) {
            log.error("Insufficient balance!");
            //余额不足触发告警
            withdrawalRecordService.balanceAlert(recordEntity);
            withdrawalRecordService.lambdaUpdate()
                    .set(WithdrawalRecordEntity::getStatus, WithdrawalRecordStatusEnum.ITEM_2.getCode())
                    .eq(WithdrawalRecordEntity::getId, recordEntity.getId())
                    .update();
            this.legalDrawalWebHook(recordEntity);
            //余额不足状态订单增多指标监控
            JSONObject InsBalanceMonitor = new JSONObject().put("Service", "payment").put("MonitorKey",
                            "InsBalanceMonitor")
                    .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
            MonitorLogUtil.log(InsBalanceMonitor);
            throw new BusinessException("Insufficient balance!");
        }
        return recordEntity;
    }

    /**
     * 法币出金申请
     * 改动原因：
     * 隔离普通逻辑和事务逻辑
     * 按最小范围去提事务
     */
    public RetResult<WithdrawalRsp> legalTenderWithdrawal(String merchantId, String merchantName, WithdrawalReq req) {
        log.info("法币出金申请接口,merchantId:{},req:{}", merchantId, req);
        WithdrawalRsp rsp = new WithdrawalRsp();
        //合法性校验
        this.withdrawalValidate(merchantId, req);

        //出金逻辑处理
        WithdrawalRecordEntity recordEntity = self.handleLegalWithdrawal(merchantId, req, rsp);

        //出金hook(不写在事务中)
        if (recordEntity.getStatus() == WithdrawalRecordStatusEnum.ITEM_1.getCode()) {
            paymentPageServiceFacade.legalDrawalWebHook(recordEntity);
        }

        rsp.setTrackingId(req.getTrackingId());
        rsp.setRemark(req.getRemark());
        return RetResult.data(rsp);
    }


    /**
     *
     */
    //@Transactional(rollbackFor = Exception.class)
    public WithdrawalRecordEntity handleLegalWithdrawal(String merchantId, WithdrawalReq req, WithdrawalRsp rsp) {

        //获取商户信息
        MerchantEntity merchant = merchantService.getById(merchantId);

        // 生成出金申请单并保存
        WithdrawalRecordEntity recordEntity = this.generateWithdrawalAndSaveRecord(merchant, req);

        //资金快进快出指标监控
        JSONObject withdrawalDiffMonitor = new JSONObject().put("Service", "payment").put("MonitorKey", "withdrawalDiffMonitor")
                .put("userId", recordEntity.getUserId()).put("amount", recordEntity.getAmount().multiply(recordEntity.getRate()))
                .put("Time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
        MonitorLogUtil.log(withdrawalDiffMonitor);

        //不需要审核
        if (BooleanStatusEnum.ITEM_0.getCode() == merchant.getWithdrawalAudit() && recordEntity.getStatus() == WithdrawalRecordStatusEnum.ITEM_0.getCode()) {
            try {
                String redirectPageUrl = paymentPageServiceFacade.fundWithDrawal(recordEntity);
                if (!StrUtil.isEmpty(redirectPageUrl)) {
                    rsp.setRedirectPageUrl(redirectPageUrl);
                }
            } catch (IllegalArgumentException e) {
                //记录失败原因用作流水
                withdrawalRecordService.lambdaUpdate().set(WithdrawalRecordEntity::getStatus,
                                WithdrawalRecordStatusEnum.ITEM_7.getCode())
                        .set(WithdrawalRecordEntity::getStayReason, e.getMessage())
                        .eq(WithdrawalRecordEntity::getId, recordEntity.getId())
                        .update();
                throw e;
            }
        }
        return recordEntity;
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
        //自动审核的2种状态
        if (entity.getStatus() != WithdrawalRecordStatusEnum.ITEM_1.getCode()
                && entity.getStatus() != WithdrawalRecordStatusEnum.ITEM_0.getCode()) {
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
                log.info("调用fireblocks接口失败：{}", retResult.getMsg());
                entity.setStatus(WithdrawalRecordStatusEnum.ITEM_6.getCode());
            }
        } else {
            // 审核不通过
            entity.setStatus(WithdrawalRecordStatusEnum.ITEM_5.getCode());
            entity.setAuditStatus(WithdrawalAuditStatusEnum.ITEM_2.getCode());
        }
        return handlerResult(req, entity, rsp);
    }

    private Integer frozenAndHandlerRecord(String merchantId, String assetName, String netProtocol, BigDecimal amount
            , WithdrawalRecordEntity entity, boolean difFeeFlag, String feeAssetName, BigDecimal channelCost) {
        log.info("frozenAndHandlerRecord merchantId:{},assetName:{},netProtocol:{},amount:{},entity:{},difFeeFlag:{},feeAssetName:{},channelCost:{}",
                merchantId, assetName, netProtocol, amount, entity, difFeeFlag, feeAssetName, channelCost);
        //只有手续费币种不一致的时候才走多个
        EstimateFeeReq estimateFeeReq = new EstimateFeeReq();
        estimateFeeReq.setAssetName(assetName);
        estimateFeeReq.setNetProtocol(netProtocol);
        estimateFeeReq.setChannelSubType(1);
        RetResult<EstimateFeeRsp> estimateFeeRsp = assetConfigServiceFacade.estimateFeeNew(estimateFeeReq);
        // 获取手续费, 因为手续费是根据币种计算的, 所以直接取没转换的币种手续费都没问题, 因为处理时, 是按不同钱包维度处理得
        BigDecimal estimateFee = estimateFeeRsp.isSuccess() && estimateFeeRsp.getData() != null ?
                estimateFeeRsp.getData().getChainTransactionFee() : BigDecimal.ZERO;
        entity.setFreezeEsFee(estimateFee.add(channelCost));
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
    }

    //触发法币出金webhook
    private void legalDrawalWebHook(WithdrawalRecordEntity withdrawalRecord) {
        WebhookEventEntity webhookEventEntity = new WebhookEventEntity();
        webhookEventEntity.setEvent(WebhookEventConstants.WITHDRAWAL_EVENT);
        webhookEventEntity.setData(JSONUtil.toJsonStr(new WithdrawalEventDto(withdrawalRecord.getTrackingId()
                , withdrawalRecord.getStatus(), withdrawalRecord.getAmount(), withdrawalRecord.getStayReason())));
        webhookEventEntity.setTrackingId(withdrawalRecord.getTrackingId());
        webhookEventEntity.setWebhookUrl(withdrawalRecord.getWebhookUrl());
        webhookEventEntity.setMerchantId(withdrawalRecord.getMerchantId());
        webhookEventService.asyncSendWebhookEvent(webhookEventEntity);
    }
}
