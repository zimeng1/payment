package com.mc.payment.core.service.facade.handler;

import cn.hutool.json.JSONUtil;
import com.mc.payment.common.rpc.model.fireBlocks.TransactionVo;
import com.mc.payment.common.rpc.model.fireBlocks.nested.TraPeerPathVo;
import com.mc.payment.core.service.rpc.FireBlocksTransactionFeignController;
import com.mc.payment.fireblocksapi.model.constant.FireBlocksWebHookConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Transaction Status Updated
 * Notification is sent when there is any change in a transaction's status or when Fireblocks detects an update to the number of confirmations.
 * url:https://developers.fireblocks.com/reference/transaction-webhooks
 *
 * @author Marty
 * @since 2024/04/15 19:31
 */
@Component
@Slf4j
public class TransactionHandlerFireBlocks extends FireBlocksTypeHandler<TransactionVo> {

    @Autowired
    private FireBlocksTransactionFeignController feignClient;

    @Override
    public String type() {
        return FireBlocksWebHookConstant.TRANSACTION_STATUS_UPDATED;
    }

    @Override
    public void handle(TransactionVo vo, String eventType) {
        String voJson = JSONUtil.toJsonStr(vo);
        try {
            TraPeerPathVo source = vo.getSource();
            TraPeerPathVo destination = vo.getDestination();
            if (source == null || destination == null) {
                log.warn("[TransactionHandlerFireBlocks] Do not handle source or destination is null, Vo:{}", voJson);
            }
            //只处理MC_PAYMENT的通知.
            if (source.getName().startsWith("MC_") || destination.getName().startsWith("MC_")) {
                log.info("[TransactionHandlerFireBlocks] TransactionVo:{}", voJson);
                feignClient.handle(vo, eventType);
            } else {
                log.info("[TransactionHandlerFireBlocks] Do not handle non-MC_PAYMENT, Vo:{}", voJson);
            }
        } catch (Exception e) {
            log.error("[TransactionHandlerFireBlocks] has bean error, Vo:{}", voJson, e);
        }
    }
}
