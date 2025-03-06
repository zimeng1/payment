package com.mc.payment.core.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.api.model.dto.WithdrawalEventDto;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.common.exception.BusinessException;
import com.mc.payment.core.service.base.BaseNoLogicalDeleteEntity;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.constant.WebhookEventConstants;
import com.mc.payment.core.service.entity.*;
import com.mc.payment.core.service.facade.WebhookEventServiceFacade;
import com.mc.payment.core.service.mapper.WithdrawalRecordMapper;
import com.mc.payment.core.service.model.dto.EmailJobParamDto;
import com.mc.payment.core.service.model.enums.*;
import com.mc.payment.core.service.model.req.MerchantQueryReq;
import com.mc.payment.core.service.model.req.WithdrawalPageReq;
import com.mc.payment.core.service.model.req.WithdrawalRecordDetailReq;
import com.mc.payment.core.service.model.req.WithdrawalStopReq;
import com.mc.payment.core.service.model.rsp.WithdrawalDetailRsp;
import com.mc.payment.core.service.model.rsp.WithdrawalRecordPageRsp;
import com.mc.payment.core.service.model.rsp.WithdrawalStopRsp;
import com.mc.payment.core.service.service.*;
import com.mc.payment.core.service.util.ThreadTraceIdUtil;
import com.mc.payment.gateway.adapter.FireBlocksPaymentGatewayAdapter;
import com.mc.payment.gateway.model.req.GatewayWithdrawalReq;
import com.mc.payment.gateway.model.rsp.GatewayWithdrawalRsp;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * <p>
 * 出金记录表 服务实现类
 * </p>
 *
 * @author conor
 * @since 2024-04-18 15:54:30
 */
@Service
@Slf4j
public class WithdrawalRecordServiceImpl extends ServiceImpl<WithdrawalRecordMapper, WithdrawalRecordEntity> implements IWithdrawalRecordService {

    @Autowired
    private MerchantWalletService merchantWalletService;


    @Autowired
    private WebhookEventServiceFacade webhookEventServiceFacade;


    @Autowired
    private IWebhookEventService webhookEventService;

    @Autowired
    private IWithdrawalRecordDetailService withdrawalRecordDetailService;


    @Autowired
    private FireBlocksPaymentGatewayAdapter fireBlocksPaymentGatewayAdapter;

    @Autowired
    private IMerchantService merchantService;

    @Autowired
    private IJobPlanService jobPlanService;

    @Override
    public WithdrawalRecordEntity getOne(String merchantId, String trackingId) {
        return this.getOne(Wrappers.lambdaQuery(WithdrawalRecordEntity.class).eq(WithdrawalRecordEntity::getMerchantId, merchantId)
                .eq(WithdrawalRecordEntity::getTrackingId, trackingId));
    }


    @Override
    public BasePageRsp<WithdrawalRecordPageRsp> page(WithdrawalPageReq req) {
        Page<WithdrawalRecordPageRsp> page = new Page<>(req.getCurrent(), req.getSize());
        baseMapper.page(page, req);
        return BasePageRsp.valueOf(page);
    }

    @Override
    public List<WithdrawalRecordEntity> listByMerchantIdsAntTime(Set<String> accountIdSet, MerchantQueryReq req) {
        LambdaQueryWrapper<WithdrawalRecordEntity> query = Wrappers.lambdaQuery(WithdrawalRecordEntity.class);
        query.eq(WithdrawalRecordEntity::getStatus, WithdrawalRecordStatusEnum.ITEM_4.getCode());
        if (CollectionUtils.isNotEmpty(req.getMerchantIdList())) {
            query.in(WithdrawalRecordEntity::getMerchantId, req.getMerchantIdList());
        }
        if (CollectionUtils.isNotEmpty(accountIdSet)) {
            // 如果赛选账户的条件都为空, 就不调用in查询账户
            if (CollectionUtils.isNotEmpty(req.getAssetNameList()) || CollectionUtils.isNotEmpty(req.getAddrList()) || CollectionUtils.isNotEmpty(req.getAccountTypeList()) || CollectionUtils.isNotEmpty(req.getAccountIdList())) {
                query.in(WithdrawalRecordEntity::getAccountId, accountIdSet);
            }
        }
        if (CollectionUtils.isNotEmpty(req.getAssetNameList())) {
            query.in(WithdrawalRecordEntity::getAssetName, req.getAssetNameList());
        }
        if (CollectionUtils.isNotEmpty(req.getAddrList())) {
            query.in(WithdrawalRecordEntity::getWalletId, req.getAddrList());
        }
        if (req.getTimeStart() != null) {
            query.ge(WithdrawalRecordEntity::getCreateTime, req.getTimeStart());
        }
        if (req.getTimeEnd() != null) {
            query.le(WithdrawalRecordEntity::getCreateTime, req.getTimeEnd());
        }
        if (CollUtil.isNotEmpty(req.getUserIdList())) {
            query.in(WithdrawalRecordEntity::getUserId, req.getUserIdList());
        }
        query.select(WithdrawalRecordEntity::getAccountId, WithdrawalRecordEntity::getAssetName, WithdrawalRecordEntity::getSourceAddress, WithdrawalRecordEntity::getDestinationAddress, WithdrawalRecordEntity::getAmount,
                WithdrawalRecordEntity::getGasFee, WithdrawalRecordEntity::getChannelFee, WithdrawalRecordEntity::getAddrBalance, WithdrawalRecordEntity::getWalletId,
                WithdrawalRecordEntity::getRate, WithdrawalRecordEntity::getFeeRate, WithdrawalRecordEntity::getCreateTime);
        query.orderByDesc(WithdrawalRecordEntity::getCreateTime);
        return this.getBaseMapper().selectList(query);
    }


    /**
     * 查询过去的hour小时内,提币金额超过了amount金额的出金目标地址
     * 比如:
     * 在过去的24小时内，提币金额超过了1500 U
     * 在过去的7天内，提币金额超过了5000 U
     *
     * @param hour
     * @param amount
     * @return
     */
    @Override
    public List<String> listOverAmountAddress(int hour, int amount) {
        return this.list(Wrappers.lambdaQuery(WithdrawalRecordEntity.class)
                        .select(WithdrawalRecordEntity::getDestinationAddress)
                        .ge(BaseNoLogicalDeleteEntity::getCreateTime, DateUtil.offsetHour(new Date(), -hour))
                        .gtSql(WithdrawalRecordEntity::getAmount, amount + "/rate"))
                .stream()
                .map(WithdrawalRecordEntity::getDestinationAddress)
                .distinct()
                .toList();
    }

    /**
     * 查询过去的hour小时内,提币次数操作了size次数的出金目标地址
     * 在过去的24小时内，提币次数超过了5次
     *
     * @param hour
     * @param times
     */
    @Override
    public List<String> listOverTimesAddress(int hour, int times) {
        return this.list(Wrappers.lambdaQuery(WithdrawalRecordEntity.class)
                        .select(WithdrawalRecordEntity::getDestinationAddress)
                        .ge(BaseNoLogicalDeleteEntity::getCreateTime, DateUtil.offsetHour(new Date(), -hour))
                        .groupBy(WithdrawalRecordEntity::getDestinationAddress)
                        .having("count(1) > " + times))
                .stream()
                .map(WithdrawalRecordEntity::getDestinationAddress)
                .distinct()
                .toList();
    }

    @Override
    public void withdrawalExport(WithdrawalPageReq req, HttpServletResponse response) throws UnsupportedEncodingException {
        BasePageRsp<WithdrawalRecordPageRsp> withdrawalPage = this.page(req);
        List<WithdrawalRecordPageRsp> list = withdrawalPage.getRecords();
        LocalDateTime now = LocalDateTime.now();
        String formatDate = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        String fileName = "出金记录" + formatDate + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        try {
            WriteCellStyle style = new WriteCellStyle();
            style.setHorizontalAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            EasyExcel.write(response.getOutputStream(), WithdrawalRecordPageRsp.class).sheet("出金记录")
                    .registerWriteHandler(new HorizontalCellStyleStrategy(null, style)).doWrite(list);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RetResult<GatewayWithdrawalRsp> fireblocksWithdrawal(WithdrawalRecordEntity withdrawalRecord, String assetId, AccountEntity accountEntity) {
        String externalTxId = "MC_" + withdrawalRecord.getMerchantId() + "_" + withdrawalRecord.getTrackingId();

        GatewayWithdrawalReq gatewayWithdrawalReq = new GatewayWithdrawalReq();
        //
        gatewayWithdrawalReq.setTransactionId(externalTxId);
        gatewayWithdrawalReq.setChannelId(assetId);
        gatewayWithdrawalReq.setAmount(withdrawalRecord.getAmount().toString());
        Map<String, Object> extraMap = new HashMap<>();
        extraMap.put("vaultAccountId", accountEntity.getExternalId());
        extraMap.put("vaultAccountName", accountEntity.getExternalId());
        // 旧的直接用uuid会有安全隐患,正确做法是使用出金记录id这种唯一性的id来防止重复提交 新的支付网关使用这种方式做处理
        extraMap.put("idempotencyKey", withdrawalRecord.getId());
        gatewayWithdrawalReq.setAddress(withdrawalRecord.getDestinationAddress());
        gatewayWithdrawalReq.setExtraMap(extraMap);

        return fireBlocksPaymentGatewayAdapter.withdrawal(gatewayWithdrawalReq);
    }

    @Override
    public WithdrawalStopRsp stopWithdrawal(WithdrawalStopReq req) {
        WithdrawalRecordEntity withdrawalRecord = this.getById(req.getId());
        if (withdrawalRecord.getStatus() == null || WithdrawalRecordStatusEnum.ITEM_2.getCode() != withdrawalRecord.getStatus()) {
            throw new BusinessException("出金记录不存在或者状态不是余额不足");
        }
        lambdaUpdate().set(WithdrawalRecordEntity::getStatus, WithdrawalRecordStatusEnum.ITEM_7.getCode())
                .set(WithdrawalRecordEntity::getAuditStatus, WithdrawalAuditStatusEnum.ITEM_3.getCode())
                .eq(WithdrawalRecordEntity::getId, withdrawalRecord.getId())
                .update();

        // 余额不足有两种情况 1. payment校验发现余额不足 2. 向上游发起出金后上游返回余额不足
        // 情况1 不会冻结钱包的金额 所以不用解冻  情况2 webhook接收到就会解冻,所以这里不需要
        // this.unfreezeWallet(withdrawalRecord);
        // 触发webhook
        WebhookEventEntity webhookEventEntity = new WebhookEventEntity();
        if (AssetTypeEnum.CRYPTO_CURRENCY.getCode() == withdrawalRecord.getAssetType()) {
            withdrawalRecord.setStatus(WithdrawalRecordStatusEnum.ITEM_7.getCode());
            webhookEventServiceFacade.saveAndTriggerWebhook(withdrawalRecord);
        } else if (AssetTypeEnum.FIAT_CURRENCY.getCode() == withdrawalRecord.getAssetType()) {
            webhookEventEntity.setEvent(WebhookEventConstants.WITHDRAWAL_EVENT);
            webhookEventEntity.setData(JSONUtil.toJsonStr(new WithdrawalEventDto(withdrawalRecord.getTrackingId()
                    , WithdrawalRecordStatusEnum.ITEM_7.getCode(), withdrawalRecord.getAmount(), null)));
            webhookEventEntity.setTrackingId(withdrawalRecord.getTrackingId());
            webhookEventEntity.setWebhookUrl(withdrawalRecord.getWebhookUrl());
            webhookEventEntity.setMerchantId(withdrawalRecord.getMerchantId());
            webhookEventService.asyncSendWebhookEvent(webhookEventEntity);
        }
        WithdrawalStopRsp rsp = new WithdrawalStopRsp();
        rsp.setTrackingId(withdrawalRecord.getTrackingId());
        return rsp;
    }

    /**
     * 此方法只有失败的情况，写了一个新的在下面，暂时不动原方法
     *
     * @param recordEntity
     */
    @Override
    public void unfreezeWallet(WithdrawalRecordEntity recordEntity) {
        // 出金成功或失败 解冻对应金额 如果冻结金额被扣为0 则解冻
        // 处理金额解冻问题, 先解冻异种币手续费, 再解冻出金账号
        // 如果是本币手续费的话, 需要加上手续费, 如果是异种币手续费,就不需要加上手续费
        BigDecimal amount = recordEntity.getAmount();
        BigDecimal freezeDifEsFee = recordEntity.getFreezeEsFee();//这里冻结的金额=手续费+平台费
        String freezeWalletId = recordEntity.getFreezeWalletId();
        // freezeWalletId="0" 为历史数据 这里做特殊处理
        if (StringUtils.isNotBlank(freezeWalletId) && !freezeWalletId.equals(recordEntity.getWalletId()) && !"0".equals(freezeWalletId)) {
            merchantWalletService.changeBalanceAndAmount(ChangeEventTypeEnum.WITHDRAWAL_FAIL, recordEntity.getId(), recordEntity.getWalletId(), BigDecimal.ZERO, amount.negate(), "解冻异种币余额");
            merchantWalletService.changeBalanceAndAmount(ChangeEventTypeEnum.WITHDRAWAL_FAIL, recordEntity.getId(), freezeWalletId, freezeDifEsFee, freezeDifEsFee.negate(), "解冻异种币手续费");
        } else {
            // 如果是本币手续费的话, 需要加上手续费
            amount = amount.add(freezeDifEsFee);
            merchantWalletService.changeBalanceAndAmount(ChangeEventTypeEnum.WITHDRAWAL_FAIL, recordEntity.getId(), recordEntity.getWalletId(), BigDecimal.ZERO, amount.negate(), "解冻本币余额及手续费");
        }
    }

    /**
     * 法币解冻金额，并修改余额的方法(无手续费版)
     */
    @Transactional(rollbackFor = Exception.class)
    public void legalTenderUnfreezeAndChangeBalance(WithdrawalRecordEntity withdrawalRecord, ChangeEventTypeEnum changeEventTypeEnum) {

        String recordId = withdrawalRecord.getId();
        String walletId = withdrawalRecord.getWalletId();
        BigDecimal amount = withdrawalRecord.getAmount();

        // 读取钱包时加锁
        MerchantWalletEntity walletEntity = merchantWalletService.selectByIdForUpdate(walletId);
        /**
         * 1.不校验余额，金额不足是内部异常，应在钱包余额不够的时候就提前告警，而不是业务代码再去处理。
         * 2.能下单说明金额是够的，不够就是其他问题
         */
        if (ChangeEventTypeEnum.WITHDRAWAL_SUCCESS.getCode() == changeEventTypeEnum.getCode()) {
            //出金成功，解冻金额并扣减余额
            merchantWalletService.changeBalanceAndAmount(changeEventTypeEnum, recordId, walletId, amount.negate(), amount.negate(), "解冻法币金额，并扣减余额");
        }

        if (ChangeEventTypeEnum.WITHDRAWAL_FAIL.getCode() == changeEventTypeEnum.getCode()) {
            //出金失败 解冻金额，余额不动
            merchantWalletService.changeBalanceAndAmount(changeEventTypeEnum, recordId, walletId, new BigDecimal(0), amount.negate(), "解冻法币金额，余额不动");
        }
    }


    @Override
    public BigDecimal getWithdrawalAmount(String merchantId, Integer assetType, String assetName, String netProtocol) {
        List<WithdrawalRecordEntity> list = lambdaQuery().eq(WithdrawalRecordEntity::getMerchantId, merchantId)
                .eq(WithdrawalRecordEntity::getAssetType, assetType)
                .eq(WithdrawalRecordEntity::getAssetName, assetName)
                .eq(WithdrawalRecordEntity::getNetProtocol, netProtocol)
                .eq(WithdrawalRecordEntity::getStatus, WithdrawalRecordStatusEnum.ITEM_4.getCode())
                .list();
        BigDecimal amount = BigDecimal.ZERO;
        if (CollUtil.isNotEmpty(list)) {
            amount = list.stream().map(WithdrawalRecordEntity::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return amount;
    }

    @Override
    public void withdrawalDetailExport(WithdrawalRecordDetailReq req, HttpServletResponse response) {
        BasePageRsp<WithdrawalDetailRsp> withdrawalDetailPage = withdrawalRecordDetailService.getDetailPageList(req);
        List<WithdrawalDetailRsp> list = withdrawalDetailPage.getRecords();
        LocalDateTime now = LocalDateTime.now();
        String formatDate = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        String fileName = "出金记录明细" + formatDate + ".xlsx";
        try {
            ClassPathResource resource = new ClassPathResource("/template/withdrawalDetail.xlsx");
            InputStream withdrawalDetailStream = resource.getInputStream();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).withTemplate(withdrawalDetailStream).build();
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            FillConfig fillConfig = FillConfig.builder().forceNewRow(true).build();
            excelWriter.fill(list, fillConfig, writeSheet);
            excelWriter.finish();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 余额不足告警
     *
     * @param recordEntity
     */
    @Override
    public void balanceAlert(WithdrawalRecordEntity recordEntity) {
        ThreadTraceIdUtil.execute(() -> {
            try {
                MerchantEntity merchantEntity = merchantService.getById(recordEntity.getMerchantId());
                log.info("商户余额不足以{}出金时,生成告警邮箱任务计划,跟踪ID:{}", merchantEntity.getName(), recordEntity.getTrackingId());
                EmailJobParamDto emailJobParamDto = new EmailJobParamDto(merchantEntity.getAlarmEmail(),
                        "出金操作失败，订单挂起，请尽快处理",
                        "您好：\n" +
                                "跟踪ID：[" + recordEntity.getTrackingId() + "]\n" +
                                "出金商户：[" + recordEntity.getMerchantName() + "]\n" +
                                "挂起原因：出金账户地址余额不足。\n" +
                                "建议您尽快处理挂起的订单并完成出金操作。");
                jobPlanService.addJobPlan(JobPlanHandlerEnum.SEND_EMAIL, emailJobParamDto);
            } catch (Exception e) {
                log.error("余额不足告警失败", e);
            }
        });
    }

    /**
     * 超时终止出金
     */
    public void payoutTimeoutLimit() {
        List<WithdrawalRecordEntity> list = this.lambdaQuery()
                .eq(WithdrawalRecordEntity::getStatus, WithdrawalRecordStatusEnum.ITEM_2.getCode())
                .lt(WithdrawalRecordEntity::getUpdateTime, LocalDateTime.now().minusHours(72))
                .list();

        if (CollectionUtils.isEmpty(list)) {
            log.info("没有超时的余额不足出金记录，定时任务终止。");
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            WithdrawalRecordEntity withdrawalRecord = list.get(i);
            //更新数据
            this.lambdaUpdate().eq(WithdrawalRecordEntity::getId, withdrawalRecord.getId())
                    .set(WithdrawalRecordEntity::getStatus, WithdrawalRecordStatusEnum.ITEM_7.getCode())
                    .set(WithdrawalRecordEntity::getAuditStatus, WithdrawalAuditStatusEnum.ITEM_3.getCode())
                    .set(WithdrawalRecordEntity::getUpdateBy, "system(payoutJob)")
                    .update();

            WebhookEventEntity webhookEventEntity = new WebhookEventEntity();
            webhookEventEntity.setEvent(WebhookEventConstants.WITHDRAWAL_EVENT);
            webhookEventEntity.setData(JSONUtil.toJsonStr(new WithdrawalEventDto(withdrawalRecord.getTrackingId()
                    , WithdrawalRecordStatusEnum.ITEM_7.getCode(), withdrawalRecord.getAmount(), null)));
            webhookEventEntity.setTrackingId(withdrawalRecord.getTrackingId());
            webhookEventEntity.setWebhookUrl(withdrawalRecord.getWebhookUrl());
            webhookEventEntity.setMerchantId(withdrawalRecord.getMerchantId());
            webhookEventService.asyncSendWebhookEvent(webhookEventEntity);
        }
    }
}
