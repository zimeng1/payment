package com.mc.payment.third.party.service.handler;

import com.alibaba.fastjson.JSON;
import com.mc.payment.common.rpc.model.PaymentRspVo;
import com.mc.payment.core.api.IInOutFeignClient;
import com.mc.payment.third.party.api.model.constant.BlockATMWebhook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentHandler extends EventHandler<PaymentRspVo>{

    @Autowired
    private IInOutFeignClient inOutRpcService;

    @Override
    public String eventType() {
        return BlockATMWebhook.WEBHOOK_EVENT_PAYMENT;
    }

    @Override
    public void handle(PaymentRspVo paymentRspVo, String eventType) {
        log.info("Standard Payment transaction:{}", JSON.toJSONString(paymentRspVo));
        inOutRpcService.payInNotify(paymentRspVo);
    }
}
