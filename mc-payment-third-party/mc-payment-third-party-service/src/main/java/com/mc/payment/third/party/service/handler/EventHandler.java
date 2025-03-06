package com.mc.payment.third.party.service.handler;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Slf4j
public abstract class EventHandler<T> {
    public abstract String eventType();

    public void parseBody(JSONObject data, String eventType){
        ParameterizedType pzt = (ParameterizedType) getClass().getGenericSuperclass();
        Type type = pzt.getActualTypeArguments()[0];
        Gson gson = new Gson();
        T body = gson.fromJson(gson.toJson(data), type);
        try {
            handle(body, eventType);
        }
        catch (Exception e) {
            log.error("EventHandler parseBody error!!! eventType:{}, exception:", this.eventType(), e);
        }
    }

    public abstract void handle(T body, String eventType);
}
