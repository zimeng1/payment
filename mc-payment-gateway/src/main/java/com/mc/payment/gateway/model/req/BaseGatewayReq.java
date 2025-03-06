package com.mc.payment.gateway.model.req;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class BaseGatewayReq {
    /**
     * 额外参数
     */
    protected Map<String, Object> extraMap;

    public void addExtraField(String key, Object value) {
        if (extraMap == null) {
            extraMap = new HashMap<>();
        }
        extraMap.put(key, value);
    }
}
