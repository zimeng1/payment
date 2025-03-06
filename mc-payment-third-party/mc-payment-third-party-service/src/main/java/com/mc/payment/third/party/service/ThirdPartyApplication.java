package com.mc.payment.third.party.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author conor
 * @since 2024/01/23 14:29
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableCaching
@EnableFeignClients(basePackages = "com.mc.payment")
public class ThirdPartyApplication {
    public static void main(String[] args) {
        SpringApplication.run(ThirdPartyApplication.class, args);
    }
}
