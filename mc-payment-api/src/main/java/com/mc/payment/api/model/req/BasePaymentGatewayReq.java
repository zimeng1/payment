package com.mc.payment.api.model.req;

import lombok.Data;

import java.util.Map;

@Data
public class BasePaymentGatewayReq {
    /**
     * 额外参数
     */
    protected Map<String, Object> extraMap;
}
