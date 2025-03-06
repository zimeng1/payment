package com.mc.payment.gateway.channels.passtopay.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignatureGenerator {
    private static final String ENCODING_CHARSET = "UTF-8";

    public static void main(String[] args) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("key1", "value1");
        parameters.put("key2", "value2");
        parameters.put("key3", "value3");
        // Add more parameters
        String signature = generateSignature(parameters, "your_secret_key");
        System.out.println("Generated Signature: " + signature);
    }

    public static String generateSignature(Map<String, String> parameters, String secretKey) {
        ArrayList<String> list = new ArrayList<>(parameters.size());
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        list.sort(String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for (String element : list) {
            sb.append(element);
        }
        String result = sb.toString();
        result += "key=" + secretKey;
        result = computeMD5(result, ENCODING_CHARSET).toUpperCase();
        return result;
    }


    private static String computeMD5(String value, String charset) {
        try {
            byte[] data = value.getBytes(charset);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digestData = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : digestData) {
                int current = b & 0xff;
                if (current < 16) {
                    sb.append("0");
                }
                sb.append(Integer.toString(current, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
