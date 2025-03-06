package com.mc.payment.third.party.service.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

/**
 * 给所有请求加上Trace ID
 *
 * @author Conor
 * @since 2024-04-25 14:33:24.719
 */
@Component
public class TraceIdInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取上游服务设置的Trace ID
        String traceId = request.getHeader("X-Trace-Id");

        // 如果请求头中存在Trace ID，则将其存储在MDC中
        if (traceId != null && !traceId.isEmpty()) {
            MDC.put("traceId", traceId);
        }else{
            // 如果请求头中不存在Trace ID，则生成一个新的Trace ID
             traceId = UUID.randomUUID().toString();
            // 将Trace ID 存储在MDC中
            MDC.put("traceId", traceId);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 清理MDC中的Trace ID
        MDC.remove("traceId");
    }
}
