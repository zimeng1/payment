package com.mc.payment.core.service.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.core.service.base.BasePageRsp;
import com.mc.payment.core.service.entity.ChannelWalletLogEntity;
import com.mc.payment.core.service.mapper.ChannelWalletLogMapper;
import com.mc.payment.core.service.model.req.ChannelWalletQueryLogPageReq;
import com.mc.payment.core.service.service.ChannelWalletLogService;
import com.mc.payment.core.service.util.ThreadTraceIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.Future;

/**
 * @author Conor
 * @description 针对表【mcp_channel_wallet_log(通道钱包日志表)】的数据库操作Service实现
 * @createDate 2024-08-15 10:28:03
 */
@Slf4j
@Service
public class ChannelWalletLogServiceImpl extends ServiceImpl<ChannelWalletLogMapper, ChannelWalletLogEntity>
        implements ChannelWalletLogService {
    /**
     * 异步保存变动日志
     *
     * @param walletId
     * @param changeBalance
     * @param changeFreezeAmount
     * @param msg
     * @param date
     */
    @Override
    public Future<String> asyncSaveLog(String walletId, BigDecimal changeBalance, BigDecimal changeFreezeAmount, String msg, Date date) {
        return ThreadTraceIdUtil.execAsync(() -> {
            String logId = "";
            if (changeBalance.compareTo(BigDecimal.ZERO) == 0 && changeFreezeAmount.compareTo(BigDecimal.ZERO) == 0) {
                // 目前通道余额变更的场景只有定时任务定期和支付通道同步一种,出现未变更属于正常情况,为了节省存储空间,不保存日志
                log.debug("changeBalance and changeFreezeAmount is 0,not save log");
                return logId;
            }
            ChannelWalletLogEntity logEntity = null;
            try {
                logEntity = new ChannelWalletLogEntity();
                logEntity.setWalletId(walletId);
                logEntity.setChangeBalance(changeBalance);
                logEntity.setChangeFreezeAmount(changeFreezeAmount);
                logEntity.setWalletUpdateMsg(msg);
                logEntity.setWalletUpdateTime(date);
                this.save(logEntity);
                logId = logEntity.getId();
            } catch (Exception e) {
                log.error("异步保存变动日志失败:{}", logEntity, e);
            } finally {
                log.info("ChannelWalletLog logEntity:{}", logEntity);
            }
            return logId;
        });
    }

    public BasePageRsp<ChannelWalletLogEntity> page(@RequestBody ChannelWalletQueryLogPageReq req) {
        Page<ChannelWalletLogEntity> page = new Page<>(req.getCurrent(), req.getSize());
        this.page(page, Wrappers.lambdaQuery(ChannelWalletLogEntity.class)
                .eq(StrUtil.isNotBlank(req.getWalletId()), ChannelWalletLogEntity::getWalletId, req.getWalletId())
                .eq(req.getChangeBalance()!=null, ChannelWalletLogEntity::getChangeBalance, req.getChangeBalance())
                .eq(req.getChangeFreezeAmount()!=null, ChannelWalletLogEntity::getChangeFreezeAmount, req.getChangeFreezeAmount())
                .gt(req.getWalletCreateTimeLeft()!=null,ChannelWalletLogEntity::getCreateTime, req.getWalletCreateTimeLeft())
                .lt(req.getWalletCreateTimeRight()!=null,ChannelWalletLogEntity::getCreateTime, req.getWalletCreateTimeRight())
                .gt(req.getWalletUpdateTimeLeft()!=null,ChannelWalletLogEntity::getWalletUpdateTime, req.getWalletUpdateTimeLeft())
                .lt(req.getWalletUpdateTimeRight()!=null,ChannelWalletLogEntity::getWalletUpdateTime, req.getWalletUpdateTimeRight())
                .orderByDesc(ChannelWalletLogEntity::getCreateTime));
        return BasePageRsp.valueOf(page);
    }
}




