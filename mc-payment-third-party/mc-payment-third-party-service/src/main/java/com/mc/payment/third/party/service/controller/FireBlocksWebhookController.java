package com.mc.payment.third.party.service.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fireblocks.sdk.ApiResponse;
import com.fireblocks.sdk.Fireblocks;
import com.fireblocks.sdk.model.ResendWebhooksResponse;
import com.mc.payment.common.base.RetResult;
import com.mc.payment.third.party.api.model.constant.FireBlocksConstant;
import com.mc.payment.third.party.service.config.SpringContext;
import com.mc.payment.third.party.service.handler.FireBlocksTypeHandler;
import com.mc.payment.third.party.service.util.FireBlocksUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * fireBlocks  Webhook 入口
 *
 * @author Marty
 * @since 2024/04/15 19:01
 */
@Slf4j
@RestController
@RequestMapping("/fireBlocks")
public class FireBlocksWebhookController {

    @Resource
    private FireBlocksUtil fireBlocksUtil;

    @Operation(summary = "fireBlocks webhook", description = "fireBlocks webhook event")
    @PostMapping("/webhook")
    public String receiveBlockATMWebhook(@RequestBody JSONObject bodyData) throws Exception {
        log.info("[receiveFireBlocksWebhook] bodyData:{}", JSON.toJSONString(bodyData));
        try {
            String type = bodyData.getString("type");
            JSONObject data = bodyData.getJSONObject("data");
            //        String tenantId = data.getString("tenantId");
            Map<String, FireBlocksTypeHandler> map = SpringContext.getBeansOfType(FireBlocksTypeHandler.class);
            FireBlocksTypeHandler<?> fireBlocksTypeHandler = map.values().stream()
                    .filter(e -> Arrays.asList(e.type().split(",")).contains(type))
                    .findFirst().orElse(null);
            if (fireBlocksTypeHandler != null) {
                fireBlocksTypeHandler.parseBody(data, type);
            } else {
                log.warn("[receiveFireBlocksWebhook] type handler not exist, type:{}", type);
            }
        } catch (Exception e) {
            log.error("[receiveFireBlocksWebhook] has bean error, bodyData:{}", JSON.toJSONString(bodyData), e);
        }
        return "success";
    }


}
