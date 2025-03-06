package com.mc.payment.third.party.service.handler;

import com.alibaba.fastjson.JSON;
import com.mc.payment.common.rpc.model.PayoutRspVo;
import com.mc.payment.core.api.IInOutFeignClient;
import com.mc.payment.third.party.api.model.constant.BlockATMWebhook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PayoutHandler extends EventHandler<PayoutRspVo> {
    @Autowired
    private IInOutFeignClient inOutRpcService;

    @Override
    public String eventType() {
        return BlockATMWebhook.WEBHOOK_EVENT_PAYOUT;
    }

    @Override
    public void handle(PayoutRspVo payoutRspVo, String eventType) {
        log.info("Payout transaction:{}", JSON.toJSONString(payoutRspVo));
        inOutRpcService.payOutNotify(payoutRspVo);
    }
}
