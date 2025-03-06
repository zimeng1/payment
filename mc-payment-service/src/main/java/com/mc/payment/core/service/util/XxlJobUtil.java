package com.mc.payment.core.service.util;

import com.xxl.job.core.context.XxlJobHelper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Conor
 * @since 2024/5/21 下午4:13
 */
@Slf4j
public class XxlJobUtil {

    public static void log(String appendLogPattern, Object... appendLogArguments) {
        log.info(appendLogPattern, appendLogArguments);
        XxlJobHelper.log(appendLogPattern, appendLogArguments);
    }

    public static void log(Throwable e) {
        log.error("XxlJob error", e);
        XxlJobHelper.log(e);
    }
}
