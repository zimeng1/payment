package com.mc.payment.core.service.facade.handler;

import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
/**
 * FireBlocks 回调处理
 * @author Marty
 * @since 2024/04/15 19:29
 */
@Slf4j
public abstract class FireBlocksTypeHandler<T>  {

    public abstract String type();

    public void parseBody(JSONObject data, String eventType){
        try {
            ParameterizedType pzt = (ParameterizedType) getClass().getGenericSuperclass();
            Type type = pzt.getActualTypeArguments()[0];
            T body =  data.toBean(type);
            handle(body, eventType);
        }catch (Exception e) {
            log.error("[FireBlocksTypeHandler][parseBody] has bean error, type:{}, eventType:{}", this.type(), eventType, e);
        }
    }

    public abstract void handle(T body, String eventType);
}
