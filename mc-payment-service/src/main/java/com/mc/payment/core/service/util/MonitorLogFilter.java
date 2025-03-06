package com.mc.payment.core.service.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * @author nengy
 * date 2024/8/27
 * description
 */
public class MonitorLogFilter extends Filter<ILoggingEvent> {

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if ("com.mc.payment.core.service.util.MonitorLogUtil".equals(event.getLoggerName())) {
            return FilterReply.ACCEPT;
        } else {
            return FilterReply.DENY;
        }
    }
}
