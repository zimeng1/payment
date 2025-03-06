package com.mc.payment.core.service.rpc;

import com.mc.payment.common.constant.ApiVersionConstants;
import com.mc.payment.common.rpc.model.fireBlocks.VaultAssetVo;
import com.mc.payment.core.api.IFireBlocksVaultAssetFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author conor
 * @since 2024/2/21 11:18:26
 */
@Slf4j
@RestController
@RequestMapping(ApiVersionConstants.API_V1_PREFIX + "/fireBlocks/vaultAsset")
public class FireBlocksVaultAssetFeignController implements IFireBlocksVaultAssetFeignClient {

    @Override
    @PostMapping("/handle")
    public void handle(@RequestBody VaultAssetVo vo,  @RequestParam("eventType") String eventType) {
        log.info("FireBlocksVaultAssetFeignController handle vo:{},eventType:{}", vo, eventType);
    }
}
