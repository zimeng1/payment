package com.mc.payment.gateway.channels.ofapay.config;

import cn.hutool.json.JSONUtil;
import com.mc.payment.common.util.SecureUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OfaPayConfig {
    /**
     * ofapya 加密密钥 长度要为16
     */
    @Value("${app.ofapay_key}")
    private String ofapaykey;

    @Value("#{${app.ofapay_key_map}}")
    private Map<String, String> ofapayKeyMap;
    public Map<String, String> getKeyMap() {
        return ofapayKeyMap;
    }

    public String encrypt(String text) throws Exception {
        return SecureUtil.encrypt(ofapaykey, text);
    }

    public String decrypt(String encryptedText) throws Exception {
        return SecureUtil.decrypt(ofapaykey, encryptedText);
    }

    public static void main(String[] args) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("xxx",	"s@xxxx");


            String key = "%^&FGH$%Rkhg65Y&";
            String originalText = "Hello, this is a secret message!";

            // 加密
            String encryptedText = SecureUtil.encrypt(key, originalText);
            System.out.println("Encrypted Text: " + encryptedText);

            // 解密
            String decryptedText = SecureUtil.decrypt(key, encryptedText);
            System.out.println("Decrypted Text: " + decryptedText);

            Map<String, String> keyMap = new HashMap<>();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                keyMap.put(entry.getKey(), SecureUtil.encrypt(key, entry.getValue()));
            }
            System.out.println(JSONUtil.toJsonStr(keyMap));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
