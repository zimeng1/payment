package com.mc.payment.core.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mc.payment.core.service.entity.MerchantWalletEntity;
import com.mc.payment.core.service.entity.WalletBalanceLogEntity;
import com.mc.payment.core.service.facade.WebhookEventServiceFacade;
import com.mc.payment.core.service.mapper.WalletBalanceLogMapper;
import com.mc.payment.core.service.model.dto.WalletBalanceEventVo;
import com.mc.payment.core.service.service.IWalletBalanceLogService;
import com.mc.payment.core.service.service.MerchantWalletService;
import com.xxl.job.core.context.XxlJobHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Conor
 * @description 针对表【mcp_wallet_balance_log(钱包余额变动表)】的数据库操作Service实现
 * @createDate 2024-05-21 13:44:46
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class WalletBalanceLogServiceImpl extends ServiceImpl<WalletBalanceLogMapper, WalletBalanceLogEntity>
        implements IWalletBalanceLogService {
//    private final IWalletService walletService;
    private final WebhookEventServiceFacade webhookEventServiceFacade;
    private final MerchantWalletService merchantWalletService;



    /**
     * 监控钱包余额变动
     * 然后记录日志
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void monitorWalletBalanceChangeJob() {
        List<WalletBalanceLogEntity> addLogList = new ArrayList<>();
        // 查询钱包集合
//        List<WalletEntity> walletEntityList = walletService.shardList(shardIndex, shardTotal);
        List<MerchantWalletEntity> walletEntityList = merchantWalletService.list();

        // 查询每个钱包最新一次更新的记录
        List<WalletBalanceLogEntity> walletBalanceLogs = baseMapper.queryLatestRecordOfWallet();
        Map<String, WalletBalanceLogEntity> walletBalanceLogMap = walletBalanceLogs.stream().collect(Collectors.toMap(WalletBalanceLogEntity::getWalletId, Function.identity()));
        for (MerchantWalletEntity walletEntity : walletEntityList) {
            WalletBalanceLogEntity oldWalletBalanceLogEntity = walletBalanceLogMap.get(walletEntity.getId());
            WalletBalanceLogEntity newWalletBalanceLogEntity = new WalletBalanceLogEntity();
            newWalletBalanceLogEntity.setWalletId(walletEntity.getId());
            newWalletBalanceLogEntity.setCurrentBalance(walletEntity.getBalance());
            newWalletBalanceLogEntity.setCurrentFreezeAmount(walletEntity.getFreezeAmount());
            newWalletBalanceLogEntity.setWalletUpdateTime(new Date());
            if (oldWalletBalanceLogEntity == null) {
                newWalletBalanceLogEntity.setPreviousBalance(walletEntity.getBalance());
                newWalletBalanceLogEntity.setPreviousFreezeAmount(walletEntity.getBalance());
                // 初始化也记录日志
                addLogList.add(newWalletBalanceLogEntity);
                XxlJobHelper.log("初始化的余额变更日志:{}", newWalletBalanceLogEntity);
            } else {
                newWalletBalanceLogEntity.setPreviousBalance(oldWalletBalanceLogEntity.getCurrentBalance());
                newWalletBalanceLogEntity.setPreviousFreezeAmount(oldWalletBalanceLogEntity.getCurrentFreezeAmount());

                if (oldWalletBalanceLogEntity.getCurrentBalance().compareTo(walletEntity.getBalance()) != 0 ||
                        oldWalletBalanceLogEntity.getCurrentFreezeAmount().compareTo(walletEntity.getFreezeAmount()) != 0) {
                    XxlJobHelper.log("钱包有余额变更:{}", newWalletBalanceLogEntity);
                    // 有变化才记录日志
                    addLogList.add(newWalletBalanceLogEntity);
                    // 触发钱包余额变动事件
                    WalletBalanceEventVo walletBalanceEventVo = new WalletBalanceEventVo();
                    walletBalanceEventVo.setCurrentBalance(newWalletBalanceLogEntity.getCurrentBalance());
                    walletBalanceEventVo.setCurrentFreezeAmount(newWalletBalanceLogEntity.getCurrentFreezeAmount());
                    walletBalanceEventVo.setPreviousBalance(newWalletBalanceLogEntity.getPreviousBalance());
                    walletBalanceEventVo.setPreviousFreezeAmount(newWalletBalanceLogEntity.getPreviousFreezeAmount());
                    walletBalanceEventVo.setWalletAddress(walletEntity.getWalletAddress());
                    walletBalanceEventVo.setAssetName(walletEntity.getAssetName());
                    walletBalanceEventVo.setNetProtocol(walletEntity.getNetProtocol());
                    walletBalanceEventVo.setWalletUpdateTime(newWalletBalanceLogEntity.getWalletUpdateTime());
                    //walletBalanceEventVo.setShardIndex(shardIndex);
                    webhookEventServiceFacade.asyncSaveAndTriggerWebhook(walletEntity.getMerchantId(), walletBalanceEventVo);
                }
            }
        }
        if (!addLogList.isEmpty()) {
            this.saveBatch(addLogList);
        }
    }
}




