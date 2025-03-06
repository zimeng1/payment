package com.mc.payment.api.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AKSKUtil {

    public static final String HMAC_SHA_512 = "HmacSHA512";

    public static String calculateHMAC(String data, String secretKey) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_SHA_512);
            Mac mac = Mac.getInstance(HMAC_SHA_512);
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to calculate HMAC: " + e.getMessage(), e);
        }
    }
}
