package com.mc.payment.core.service.facade.handler;

import cn.hutool.json.JSONUtil;
import com.mc.payment.common.rpc.model.fireBlocks.VaultAssetVo;
import com.mc.payment.core.service.rpc.FireBlocksVaultAssetFeignController;
import com.mc.payment.fireblocksapi.model.constant.FireBlocksWebHookConstant;
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
    private FireBlocksVaultAssetFeignController vaultAssetFeignClient;

    @Override
    public String type() {
        return FireBlocksWebHookConstant.VAULT_BALANCE_UPDATE;
    }

    @Override
    public void handle(VaultAssetVo vo, String eventType) {
        try {
            log.info("[VaultAssetHandlerFireBlocks] VaultAssetVo:{}", JSONUtil.toJsonStr(vo));
            vaultAssetFeignClient.handle(vo, eventType);
        } catch (Exception e) {
            log.error("[VaultAssetHandlerFireBlocks] has bean error, Vo:{}", JSONUtil.toJsonStr(vo), e);
        }
    }
}
