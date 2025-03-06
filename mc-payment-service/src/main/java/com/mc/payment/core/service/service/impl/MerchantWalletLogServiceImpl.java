package com.mc.payment.core.service.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.MerchantWalletLogEntity;
import com.mc.payment.core.service.mapper.MerchantWalletLogMapper;
import com.mc.payment.core.service.model.req.MerchantWalletLogReq;
import com.mc.payment.core.service.model.req.MerchantWalletQueryLogReq;
import com.mc.payment.core.service.service.MerchantWalletLogService;
import com.mc.payment.core.service.util.ThreadTraceIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.Future;

/**
 * @author Conor
 * @description 针对表【mcp_merchant_wallet_log(商户钱包日志表)】的数据库操作Service实现
 * @createDate 2024-08-14 21:08:06
 */
@Slf4j
@Service
public class MerchantWalletLogServiceImpl extends ServiceImpl<MerchantWalletLogMapper, MerchantWalletLogEntity>
        implements MerchantWalletLogService {
    /**
     * 保存变动日志
     *
     * @param walletId
     * @param changeBalance
     * @param changeFreezeAmount
     * @param msg
     * @param date
     */
    @Override
    public void asyncSaveLog(String walletId, BigDecimal changeBalance, BigDecimal changeFreezeAmount, String msg, Date date) {
        // 如果变动金额和冻结金额都为0，也保存日志 方便核查
        ThreadTraceIdUtil.execute(() -> {
            if (changeBalance.compareTo(BigDecimal.ZERO) == 0 && changeFreezeAmount.compareTo(BigDecimal.ZERO) == 0) {
                // 目前通道余额变更的场景只有定时任务定期和支付通道同步一种,出现未变更属于正常情况,为了节省存储空间,不保存日志
                log.debug("changeBalance and changeFreezeAmount is 0,not save log");
                return ;
            }
            MerchantWalletLogEntity logEntity = null;
            try {
                logEntity = new MerchantWalletLogEntity();
                logEntity.setWalletId(walletId);
                logEntity.setChangeBalance(changeBalance);
                logEntity.setChangeFreezeAmount(changeFreezeAmount);
                logEntity.setWalletUpdateMsg(msg);
                logEntity.setWalletUpdateTime(date);
                this.save(logEntity);
            } catch (Exception e) {
                log.error("异步保存变动日志失败:{}", logEntity, e);
            } finally {
                log.info("MerchantWalletLog logEntity:{}", logEntity);
            }
        });
    }

    /**
     * 异步保存变动日志
     *
     * @param req
     */
    @Override
    public Future<String> asyncSaveLog(MerchantWalletLogReq req) {
        // 如果变动金额和冻结金额都为0，也保存日志 方便核查
       return ThreadTraceIdUtil.execAsync(() -> {
            String logId = "";
           if (req.getChangeBalance().compareTo(BigDecimal.ZERO) == 0 && req.getChangeFreezeAmount().compareTo(BigDecimal.ZERO) == 0) {
               // 目前通道余额变更的场景只有定时任务定期和支付通道同步一种,出现未变更属于正常情况,为了节省存储空间,不保存日志
               log.debug("changeBalance and changeFreezeAmount is 0,not save log");
               return logId;
           }
            MerchantWalletLogEntity logEntity = null;
            try {
                logEntity = MerchantWalletLogEntity.valueOf(req);
                this.save(logEntity);
                logId = logEntity.getId();
            } catch (Exception e) {
                log.error("异步保存变动日志失败:{}", logEntity, e);
            } finally {
                log.info("MerchantWalletLog logEntity:{}", logEntity);
            }
            return logId;
        });
    }

    @Override
    public BasePageRsp<MerchantWalletLogEntity> page(MerchantWalletQueryLogReq req) {
        Page<MerchantWalletLogEntity> page = new Page<>(req.getCurrent(), req.getSize());
        this.page(page, Wrappers.lambdaQuery(MerchantWalletLogEntity.class)
                .eq(StrUtil.isNotBlank(req.getWalletId()), MerchantWalletLogEntity::getWalletId, req.getWalletId())
                .eq(req.getChangeEventTypeEnum()!=null, MerchantWalletLogEntity::getChangeEventType, req.getChangeEventTypeEnum())
                .eq(req.getChangeBalance()!=null, MerchantWalletLogEntity::getChangeBalance, req.getChangeBalance())
                .eq(req.getChangeFreezeAmount()!=null, MerchantWalletLogEntity::getChangeFreezeAmount, req.getChangeFreezeAmount())
                .gt(req.getWalletCreateTimeLeft()!=null,MerchantWalletLogEntity::getCreateTime, req.getWalletCreateTimeLeft())
                .lt(req.getWalletCreateTimeRight()!=null,MerchantWalletLogEntity::getCreateTime, req.getWalletCreateTimeRight())
                .gt(req.getWalletUpdateTimeLeft()!=null,MerchantWalletLogEntity::getWalletUpdateTime, req.getWalletUpdateTimeLeft())
                .lt(req.getWalletUpdateTimeRight()!=null,MerchantWalletLogEntity::getWalletUpdateTime, req.getWalletUpdateTimeRight())
                .orderByDesc(MerchantWalletLogEntity::getCreateTime));
        return BasePageRsp.valueOf(page);
    }


}




