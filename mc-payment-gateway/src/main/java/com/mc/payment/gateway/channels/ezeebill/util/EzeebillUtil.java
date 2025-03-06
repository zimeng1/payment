package com.mc.payment.gateway.channels.ezeebill.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Map;

/**
 * EzeebillUtil
 *
 * @author GZM
 * @since 2024/10/20 上午3:16
 */
public class EzeebillUtil {

    private static final Logger log = LoggerFactory.getLogger(EzeebillUtil.class);

    public static String getCurrencyCode(final String currency) {
        Map<String, String> map = Map.of(
                "INR", "356",
                "MYR", "458",
                "IDR", "360",
                "VND", "704",
                "THB", "764"
        );
        return map.get(currency);
    }

    /**
     * 构建签名
     * @param map
     * @param secretKey
     * @return
     */
    public static String generateSignature(Map<String, Object> map, String secretKey) {
        if(secretKey == null) {
            log.error("Failed to generate signature,secretKey is null");
            throw new IllegalArgumentException("Failed to generate signature,secretKey is null");
        }
        ArrayList<String> list = new ArrayList<>(map.size());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() != null) {
                list.add(entry.getValue().toString());
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String element : list) {
            sb.append(element);
        }
        String str = sb.toString();
        try {
            return calculateHmacSha1(str, secretKey);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to generate signature", e);
            throw new IllegalArgumentException("Failed to generate signature", e);
        }
    }

    /**
     * 计算HmacSha1值
     * @param data
     * @param key
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    private static String calculateHmacSha1(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(secretKeySpec);
        byte[] result = mac.doFinal(data.getBytes());
        // Convert the byte array to a hexadecimal string
        Formatter formatter = new Formatter();
        for (byte b : result) {
            formatter.format("%02x", b);
        }
        String hexString = formatter.toString();
        formatter.close();
        return hexString;
    }


}
