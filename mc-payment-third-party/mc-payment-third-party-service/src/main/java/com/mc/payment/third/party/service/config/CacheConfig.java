package com.mc.payment.third.party.service.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author leo
 * @date 2024/1/27
 * @description  本地缓存 配置caffeine
 */
@Configuration
public class CacheConfig  {

    /**
     * 配置缓存管理器
     * Primary 优先加载
     * @return 缓存管理器
     */
    @Bean("caffeineCacheManager")
    @Primary
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 设置最后一次写入或访问后经过固定时间过期
                .expireAfterAccess(12, TimeUnit.HOURS)
                // 初始的缓存空间大小
                .initialCapacity(100)
                // 缓存的最大条数
                .maximumSize(1000));
        return cacheManager;
    }


}


