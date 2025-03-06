package com.mc.payment.third.party.service.config.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;

/**
 * 在发起Feign请求时，将Trace ID传递给目标服务
 * 在Feign客户端中设置请求头
 *
 * @author Conor
 * @since 2024-04-25 14:32:09.793
 */
@Configuration
public class FeignTraceIdInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        String traceId = MDC.get("traceId");
        if (traceId != null) {
            template.header("X-Trace-Id", traceId);
        }
    }
}
