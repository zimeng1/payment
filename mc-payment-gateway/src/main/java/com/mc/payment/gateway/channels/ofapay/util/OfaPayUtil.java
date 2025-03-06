package com.mc.payment.gateway.channels.ofapay.util;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class OfaPayUtil {

    public static String generateSignature(Map<String, String> data, String key) {
        // 将字段按照键进行升序排序
        Map<String, String> sortedData = new TreeMap<>(data);

        // 构建待加密字符串
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedData.entrySet()) {
            if (!"sign".equals(entry.getKey())) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        sb.append("key=").append(key);
        return SecureUtil.md5(sb.toString());
    }

    public static String generateSignature(Object data, String key) {
        return generateSignature(beanToMap(data), key);
    }

    public static Map<String, String> beanToMap(Object obj) {
        Map<String, String> dataMap = new TreeMap<>();
        Class<?> clazz = obj.getClass();

        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    if (value != null) {
                        dataMap.put(field.getName(), value.toString());
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            clazz = clazz.getSuperclass();
        }

        return dataMap;
    }

    public static void main(String[] args) {
        // 模拟请求数据
        Map<String, String> requestData = new TreeMap<>();
        requestData.put("amount", "10.00000");
        requestData.put("currency", "KRW");
        requestData.put("memo", "memo");
        requestData.put("orderid", "20190122153138");
        requestData.put("orderno", "20190122000003");
        requestData.put("paytype", "ZZ");
        requestData.put("productname", "商品");
        requestData.put("respcode", "00");
        requestData.put("resptime", "2019-01-22 15:33:04");
        requestData.put("rmbrate", "0");
        requestData.put("scode", "S00001");
        requestData.put("status", "1");

        // 设置密钥
        String key = "1234qwer";

        // 生成签名
        String signature = generateSignature(requestData, key);
        System.out.println("Generated Signature: " + signature);
        String data = "{\"amount\":\"69.00000\",\"currency\":\"MYR\",\"memo\":\"123\",\"orderid\":\"1826089163228090370\",\"orderno\":\"20240821104925tfrv9\",\"paytype\":\"BE2\",\"productname\":\"TTF\",\"respcode\":\"00\",\"resptime\":\"2024-08-21 10:53:03\",\"scode\":\"865471343805\",\"status\":\"1\",\"sign\":\"375384b8d13b1203d0f02d224cd8cfde\"}";
        JSONObject jsonObject = JSONUtil.parseObj(data);
        HashMap bean = jsonObject.toBean(HashMap.class);
        key = "Usfj7pZLdazZd1J";
        String signature2 = generateSignature(bean, key);
        System.out.println("Generated Signature2: " + signature2);
    }
}
