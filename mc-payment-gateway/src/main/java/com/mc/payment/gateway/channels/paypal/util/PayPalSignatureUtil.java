package com.mc.payment.gateway.channels.paypal.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;

/**
 * PayPal签名工具类
 * <p>
 * https://developer.paypal.com/api/rest/webhooks/rest/
 *
 * @author Conor
 * @since 2024-09-25 10:47:01.084
 */
@Slf4j
public class PayPalSignatureUtil {
    // Map用于保存URL和公钥的对应关系
    private static final Map<String, byte[]> PUBLIC_KEY_MAP = new HashMap<>();

    private PayPalSignatureUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 验证签名
     *
     * @param payload         Webhook请求的主体,请勿进行任何更改
     * @param transmissionId  The unique ID of the transmission, from the paypal-transmission-id HTTP header.
     * @param timeStamp       The date and time when the message was transmitted, from the paypal-transmission-time HTTP header.
     * @param webhookId       The ID of the webhook from when the listener URL subscribed to events. This does not come in the header or body of the message but rather where the webhook listener URL's event subscriptions are configured, such as Application management
     * @param certUrl         utilize the public key in the certificate file specified by the header paypal-cert-url. This file should automatically be downloaded and cached for future use.Use it to validate the signature given by the header paypal-transmission-sig for the original message string.
     * @param transmissionSig signature given by the header paypal-transmission-sig
     * @param authAlgo        The algorithm used to sign the message, from the paypal-auth-algo HTTP header.
     * @return
     */
    public static boolean verifySignature(String payload, String transmissionId, String timeStamp, String webhookId, String certUrl, String transmissionSig, String authAlgo) {
        boolean result = false;
        try {
            // 参数校验
            if (StrUtil.isBlank(payload) || StrUtil.isBlank(transmissionId) || StrUtil.isBlank(timeStamp) || StrUtil.isBlank(webhookId) || StrUtil.isBlank(certUrl) || StrUtil.isBlank(transmissionSig) || StrUtil.isBlank(authAlgo)) {
                return result;
            }
            // 如果公钥缓存中不存在该URL对应的公钥，则下载并保存
            PUBLIC_KEY_MAP.computeIfAbsent(certUrl, k -> HttpUtil.downloadBytes(certUrl));

            String crc32 = getCRC32FromPayload(payload);

            String expectedSignature = String.format("%s|%s|%s|%s", transmissionId, timeStamp, webhookId, crc32);

            Signature shaWithRsa = Signature.getInstance(authAlgo);
            byte[] certData = PUBLIC_KEY_MAP.get(certUrl);

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate certificate = cf.generateCertificate(new ByteArrayInputStream(certData));
            shaWithRsa.initVerify(certificate.getPublicKey());
            shaWithRsa.update(expectedSignature.getBytes());

            byte[] actualSignature = Base64.decode(transmissionSig.getBytes());

            return shaWithRsa.verify(actualSignature);
        } catch (Exception e) {
            log.error("verifySignature", e);
        }
        return result;
    }


    /**
     * 从 Payload 中获取 CRC32 值的方法
     *
     * @param payload
     * @return
     */
    private static String getCRC32FromPayload(String payload) {
        byte[] bytes = payload.getBytes();

        CRC32 checkSum = new CRC32();
        checkSum.update(bytes, 0, bytes.length);

        return String.valueOf(checkSum.getValue());
    }


   /* public static void main(String[] args) {
        long t = System.currentTimeMillis();
        boolean b = verifySignature("{\"id\":\"WH-1FF46076823432921-43U95138WF375045F\",\"event_version\":\"1.0\",\"create_time\":\"2024-09-20T15:13:21.402Z\",\"resource_type\":\"checkout-order\",\"resource_version\":\"2.0\",\"event_type\":\"CHECKOUT.ORDER.APPROVED\",\"summary\":\"An order has been approved by buyer\",\"resource\":{\"create_time\":\"2024-09-20T15:13:05Z\",\"purchase_units\":[{\"reference_id\":\"default\",\"amount\":{\"currency_code\":\"JPY\",\"value\":\"100\"},\"payee\":{\"email_address\":\"sb-zbab732658182@business.example.com\"}}],\"links\":[{\"href\":\"https://api.sandbox.paypal.com/v2/checkout/orders/9XP360514S760592T\",\"rel\":\"self\",\"method\":\"GET\"},{\"href\":\"https://api.sandbox.paypal.com/v2/checkout/orders/9XP360514S760592T\",\"rel\":\"update\",\"method\":\"PATCH\"},{\"href\":\"https://api.sandbox.paypal.com/v2/checkout/orders/9XP360514S760592T/capture\",\"rel\":\"capture\",\"method\":\"POST\"}],\"id\":\"9XP360514S760592T\",\"payment_source\":{\"paypal\":{\"email_address\":\"sb-zamso32719702@personal.example.com\",\"account_id\":\"KZPDUEVCGU47A\",\"account_status\":\"VERIFIED\",\"name\":{\"given_name\":\"John\",\"surname\":\"Doe\"},\"address\":{\"country_code\":\"C2\"}}},\"intent\":\"CAPTURE\",\"payer\":{\"name\":{\"given_name\":\"John\",\"surname\":\"Doe\"},\"email_address\":\"sb-zamso32719702@personal.example.com\",\"payer_id\":\"KZPDUEVCGU47A\",\"address\":{\"country_code\":\"C2\"}},\"status\":\"APPROVED\"},\"links\":[{\"href\":\"https://api.sandbox.paypal.com/v1/notifications/webhooks-events/WH-1FF46076823432921-43U95138WF375045F\",\"rel\":\"self\",\"method\":\"GET\"},{\"href\":\"https://api.sandbox.paypal.com/v1/notifications/webhooks-events/WH-1FF46076823432921-43U95138WF375045F/resend\",\"rel\":\"resend\",\"method\":\"POST\"}]}",
                "e6e6cb00-7762-11ef-b731-dd00113dd441",
                "2024-09-20T15:13:33Z",
                "25B219670C731603M",
                "https://api.sandbox.paypal.com/v1/notifications/certs/CERT-360caa42-fca2a594-ab66f33d",
                "Rulomdv1PJc7nge7dcDMuMabCFoH9T34Wc0YQNIgU4QEMfLqjwKPSTzRmLNVpT+/ApBiG4fO25OkWxbCCZ4kKrfc9vpWu/g3i4BqBlp8y6mUV7TCKzl5xebO2Soj+ZuAj9qOV2eHFAKZtoMGHZUYgVddTlrQnwgRryKTnhXRAXaIahyCxT12urr9WpO27QcfH0tYkNIA0Q3oN38wNUsppWa4C8j+iR6rzuD7LRAL8YhOWlegi9YHluUELeNo+d601iZ9szLdzGSIQkx2fPuDSsD+UIXNNTc44RDEpwVjbznpJwxUgAmPynG991zIa/ADRKxWlVKAMlWmaOyie+0avg==",
                "SHA256withRSA");
        long t1 = System.currentTimeMillis();
        System.out.println(t1 - t);
        b = verifySignature("{\"id\":\"WH-1FF46076823432921-43U95138WF375045F\",\"event_version\":\"1.0\",\"create_time\":\"2024-09-20T15:13:21.402Z\",\"resource_type\":\"checkout-order\",\"resource_version\":\"2.0\",\"event_type\":\"CHECKOUT.ORDER.APPROVED\",\"summary\":\"An order has been approved by buyer\",\"resource\":{\"create_time\":\"2024-09-20T15:13:05Z\",\"purchase_units\":[{\"reference_id\":\"default\",\"amount\":{\"currency_code\":\"JPY\",\"value\":\"100\"},\"payee\":{\"email_address\":\"sb-zbab732658182@business.example.com\"}}],\"links\":[{\"href\":\"https://api.sandbox.paypal.com/v2/checkout/orders/9XP360514S760592T\",\"rel\":\"self\",\"method\":\"GET\"},{\"href\":\"https://api.sandbox.paypal.com/v2/checkout/orders/9XP360514S760592T\",\"rel\":\"update\",\"method\":\"PATCH\"},{\"href\":\"https://api.sandbox.paypal.com/v2/checkout/orders/9XP360514S760592T/capture\",\"rel\":\"capture\",\"method\":\"POST\"}],\"id\":\"9XP360514S760592T\",\"payment_source\":{\"paypal\":{\"email_address\":\"sb-zamso32719702@personal.example.com\",\"account_id\":\"KZPDUEVCGU47A\",\"account_status\":\"VERIFIED\",\"name\":{\"given_name\":\"John\",\"surname\":\"Doe\"},\"address\":{\"country_code\":\"C2\"}}},\"intent\":\"CAPTURE\",\"payer\":{\"name\":{\"given_name\":\"John\",\"surname\":\"Doe\"},\"email_address\":\"sb-zamso32719702@personal.example.com\",\"payer_id\":\"KZPDUEVCGU47A\",\"address\":{\"country_code\":\"C2\"}},\"status\":\"APPROVED\"},\"links\":[{\"href\":\"https://api.sandbox.paypal.com/v1/notifications/webhooks-events/WH-1FF46076823432921-43U95138WF375045F\",\"rel\":\"self\",\"method\":\"GET\"},{\"href\":\"https://api.sandbox.paypal.com/v1/notifications/webhooks-events/WH-1FF46076823432921-43U95138WF375045F/resend\",\"rel\":\"resend\",\"method\":\"POST\"}]}",
                "e6e6cb00-7762-11ef-b731-dd00113dd441",
                "2024-09-20T15:13:33Z",
                "25B219670C731603M",
                "https://api.sandbox.paypal.com/v1/notifications/certs/CERT-360caa42-fca2a594-ab66f33d",
                "Rulomdv1PJc7nge7dcDMuMabCFoH9T34Wc0YQNIgU4QEMfLqjwKPSTzRmLNVpT+/ApBiG4fO25OkWxbCCZ4kKrfc9vpWu/g3i4BqBlp8y6mUV7TCKzl5xebO2Soj+ZuAj9qOV2eHFAKZtoMGHZUYgVddTlrQnwgRryKTnhXRAXaIahyCxT12urr9WpO27QcfH0tYkNIA0Q3oN38wNUsppWa4C8j+iR6rzuD7LRAL8YhOWlegi9YHluUELeNo+d601iZ9szLdzGSIQkx2fPuDSsD+UIXNNTc44RDEpwVjbznpJwxUgAmPynG991zIa/ADRKxWlVKAMlWmaOyie+0avg==",
                "SHA256withRSA");
        long t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);
        System.out.println(b);
    }*/
}
