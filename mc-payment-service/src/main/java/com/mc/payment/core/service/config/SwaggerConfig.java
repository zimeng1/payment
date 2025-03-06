package com.mc.payment.core.service.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${server.port}")
    private String serverPort;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("MC-Payment")
                        .description("加密资产管理系统")
                        .version("v0.0.1")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                // 添加本地服务器信息
                .addServersItem(new Server().url("http://localhost:" + serverPort).description("本地开发服务"))
                // 添加网关服务器信息
                .addServersItem(new Server().url("https://test-gateway.mcconnects.com/mc-payment").description("API 网关服务"))
                // 添加外部文档
                .externalDocs(new ExternalDocumentation()
                        .description("MC-Payment Wiki Documentation")
                        .url("https://docs.magiccompasspay.com/"));
    }

}