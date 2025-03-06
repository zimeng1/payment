package com.mc.payment.api.config;

import com.mc.payment.api.util.AKSKUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Paymetn-service OpenAPI鉴权拦截器
 */
@Slf4j
@Configuration
public class PaymentOpenApiAuthInterceptor implements RequestInterceptor {

    @Value("${app.payment_openapi_access_key}")
    private String paymentOpenapiAccessKey;

    @Value("${app.payment_openapi_secret_key}")
    private String paymentOpenapiSecretKey;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String clientName = requestTemplate.feignTarget().name();
        // 判断是否是特定的 Feign 客户端，比如 "mc-payment-service"
        if (!"mc-payment-service".equals(clientName)) {
            log.info("Not a mc-payment-service client, skip the interceptor.");
            return;
        }
        String url = requestTemplate.url();
        log.info("Request URL: {}", url);

        String timestamp = String.valueOf(System.currentTimeMillis());
        requestTemplate.header("X-Access-Key", paymentOpenapiAccessKey);
        requestTemplate.header("X-RequestURI", url);
        requestTemplate.header("X-Signature", AKSKUtil.calculateHMAC(paymentOpenapiAccessKey + timestamp + url,
                paymentOpenapiSecretKey));
        requestTemplate.header("X-Timestamp", timestamp);
    }

}
