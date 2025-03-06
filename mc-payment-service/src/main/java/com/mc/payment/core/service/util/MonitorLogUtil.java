package com.mc.payment.core.service.util;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 监控日志工具类
 *
 * @author Conor
 * @since 2024-08-26 18:15:10.655
 */
@Slf4j
public class MonitorLogUtil {

    private MonitorLogUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void log(String message) {
        log.info(message);
    }

    public static void log(Object message) {
        log.info(JSONUtil.toJsonStr(message));
    }

}
