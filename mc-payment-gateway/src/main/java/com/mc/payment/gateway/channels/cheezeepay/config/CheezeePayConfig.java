package com.mc.payment.gateway.channels.cheezeepay.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CheezeePayConfig {

    @Value("${app.cheezeepay-private-secret}")
    private String privateKey;

    @Value("${app.cheezeepay-public-secret}")
    private String publicKey;

    @Value("${app.cheezeepay-mchId}")
    private String cheezeepayMchId;

    @Value("${app.cheezeepay-appId}")
    private String cheezeepayAppId;

    @Value("${app.cheezeepay-api-base-url}")
    private String cheezeepayAppBaseUrl;



    public String getPrivateKey() {
        return privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getCheezeepayMchId() {
        return cheezeepayMchId;
    }
    public String getCheezeepayAppId() {
        return cheezeepayAppId;
    }

    public String getCheezeepayAppBaseUrl() {
        return cheezeepayAppBaseUrl;
    }


}
