package com.mc.payment.third.party.service.handler;

import com.alibaba.fastjson.JSON;
import com.mc.payment.common.rpc.model.fireBlocks.VaultAssetVo;
import com.mc.payment.core.api.IFireBlocksVaultAssetFeignClient;
import com.mc.payment.third.party.api.model.constant.FireBlocksWebHookConstant;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Notification is sent when a vault account's asset balance is updated.
 * url:https://developers.fireblocks.com/reference/vault-webhooks
 *
 * @author Marty
 * @since 2024/04/15 19:33
 */
@Component
@Slf4j
public class VaultAssetHandlerFireBlocks extends FireBlocksTypeHandler<VaultAssetVo> {

    @Resource
    private IFireBlocksVaultAssetFeignClient vaultAssetFeignClient;

    @Override
    public String type() {
        return FireBlocksWebHookConstant.VAULT_BALANCE_UPDATE;
    }

    @Override
    public void handle(VaultAssetVo vo, String eventType) {
        try {
            log.info("[VaultAssetHandlerFireBlocks] VaultAssetVo:{}", JSON.toJSONString(vo));
            vaultAssetFeignClient.handle(vo, eventType);
        }catch (Exception e){
            log.error("[VaultAssetHandlerFireBlocks] has bean error, Vo:{}", JSON.toJSONString(vo), e);
        }
    }
}
