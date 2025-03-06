package com.mc.payment.third.party.service.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mc.payment.third.party.api.model.constant.BlockATMWebhook;
import com.mc.payment.third.party.service.config.SpringContext;
import com.mc.payment.third.party.service.handler.EventHandler;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/blockATM")
public class BlockATMWebhookController {
    @Operation(summary = "BlockATM webhook", description = "webhook event")
    @PostMapping("/webhook")
    @PostMapping("/webhook")
    public String receiveBlockATMWebhook(@RequestHeader(BlockATMWebhook.HEADER_EVENT) String eventType, @RequestBody JSONObject data) {
        log.info("receiveBlockATMWebhook eventType:{}, data:{}", eventType, JSON.toJSONString(data));
        Map<String, EventHandler> map = SpringContext.getBeansOfType(EventHandler.class);
        EventHandler<?> eventHandler = map.values().stream()
                .filter(e -> Arrays.asList(e.eventType().split(",")).contains(eventType))
                .findFirst().orElse(null);
        if(eventHandler != null) {
            eventHandler.parseBody(data, eventType);
        } else {
            log.warn("event handler not exist,eventType:{}", eventType);
        }
        return "success";
    }
}
