package com.mc.payment.core.service.facade.handler;

import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Slf4j
public abstract class EventHandler<T> {
    public abstract String eventType();

    public void parseBody(JSONObject data, String eventType) {
        ParameterizedType pzt = (ParameterizedType) getClass().getGenericSuperclass();
        Type type = pzt.getActualTypeArguments()[0];
        T body = data.toBean(type);
        try {
            handle(body, eventType);
        } catch (Exception e) {
            log.error("EventHandler parseBody error!!! eventType:{}, exception:", this.eventType(), e);
        }
    }

    public abstract void handle(T body, String eventType);
}
