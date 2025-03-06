package com.mc.payment.core.service.util;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 简单的缓存实现-带过期时间
 * 注意:请勿缓存会受到集群环境的影响的数据,比如当前登录人数据
 *
 * @author Conor
 * @since 2024-06-05 11:23:34.845
 */
public class SimpleCache<K, V> {
    // 使用 ConcurrentHashMap 来存储缓存的键值对
    private final Map<K, V> cache = new ConcurrentHashMap<>();

    // 使用 ConcurrentHashMap 来存储每个键的过期时间
    private final Map<K, Long> expiryMap = new ConcurrentHashMap<>();

    // 使用 ScheduledExecutorService 来定期执行清理过期缓存项的任务
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public SimpleCache() {
        // 在构造方法中，我们设置了一个定期任务，每秒执行一次，用于清理过期的缓存项
        executor.scheduleAtFixedRate(this::removeExpiredEntries, 0, 1, TimeUnit.SECONDS);
    }

    // put 方法用于将一个键值对放入缓存，并设置该键值对的过期时间
    public void put(K key, V value, int expiryInSeconds) {
        cache.put(key, value);
        expiryMap.put(key, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(expiryInSeconds));
    }

    // get 方法用于从缓存中获取一个键对应的值
    public V get(K key) {
        return cache.get(key);
    }

    // removeExpiredEntries 方法用于清理过期的缓存项
    private void removeExpiredEntries() {
        long currentTimeMillis = System.currentTimeMillis();
        expiryMap.entrySet().removeIf(entry -> currentTimeMillis > entry.getValue());
        cache.keySet().retainAll(expiryMap.keySet());
    }
}
