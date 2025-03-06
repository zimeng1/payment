package com.mc.payment.core.service;

import com.mc.payment.core.service.util.MonitorLogUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author conor
 * @since 2024/01/23 14:29
 */
@ComponentScan({"com.mc.payment","com.mc.message.*","com.mc.base"})
@EnableFeignClients(basePackages = "com.mc")
@EnableDiscoveryClient
@SpringBootApplication
public class CoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
        MonitorLogUtil.log("Payment service started successfully");
    }
}
