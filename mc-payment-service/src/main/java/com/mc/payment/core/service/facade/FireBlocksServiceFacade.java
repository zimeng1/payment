package com.mc.payment.core.service.facade;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.core.service.config.AppConfig;
import com.mc.payment.core.service.entity.MerchantWalletEntity;
import com.mc.payment.core.service.entity.WithdrawalRecordEntity;
import com.mc.payment.core.service.model.req.WithdrawalCheckReq;
import com.mc.payment.core.service.model.req.WithdrawalQueryReq;
import com.mc.payment.core.service.model.rsp.WithdrawalCheckRsp;
import com.mc.payment.core.service.model.rsp.WithdrawalQueryRsp;
import com.mc.payment.core.service.service.*;
import com.mc.payment.core.service.service.impl.WithdrawalRecordServiceImpl;
import com.mc.payment.core.service.util.Mt5util;
import com.mc.payment.fireblocksapi.FireBlocksAPI;
import com.mc.payment.gateway.adapter.FireBlocksPaymentGatewayAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class FireBlocksServiceFacade {

    private final IWithdrawalRecordService withdrawalRecordService;
    private final MerchantWalletService merchantWalletService;


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

}
