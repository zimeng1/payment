package com.mc.payment.core.service.config.aspect;

import cn.hutool.json.JSONObject;
import com.mc.payment.core.service.entity.ReceiveWebhookLogEntity;
import com.mc.payment.core.service.service.ReceiveWebhookLogService;
import com.mc.payment.core.service.util.IPUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.Enumeration;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class WebhookRecordAspect {
    private final ReceiveWebhookLogService receiveWebhookLogService;

    @Around("@annotation(webhookRecord)")
    public Object logWebhook(ProceedingJoinPoint joinPoint, WebhookRecord webhookRecord) throws Throwable {
        ReceiveWebhookLogEntity receiveWebhookLogEntity = new ReceiveWebhookLogEntity();
        Object result = null;
        long start = System.currentTimeMillis();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String clientIP = IPUtil.getClientIP(request);
        JSONObject headerJson = extractHeaders(request);
        String value = webhookRecord.value();
        String requestBody = null;
        String exceptionMessage = null;
        try {
            // 获取请求体
            for (Object arg : joinPoint.getArgs()) {
                if (arg instanceof String) {
                    requestBody = (String) arg;
                }
            }
            result = joinPoint.proceed();
        } catch (Exception e) {
            log.error("Error in webhook: {}", joinPoint.getSignature().getName(), e);
            exceptionMessage = e.getMessage();
        } finally {
            Date now = new Date();
            long executionTime = now.getTime() - start;
            receiveWebhookLogEntity.setWebhookType(value);
            receiveWebhookLogEntity.setRequestBody(requestBody);
            receiveWebhookLogEntity.setHeaders(headerJson.toString());
            receiveWebhookLogEntity.setIpAddress(clientIP);
            receiveWebhookLogEntity.setSignature("");
            receiveWebhookLogEntity.setReceiveTime(now);
            receiveWebhookLogEntity.setExecutionTime(executionTime);
            receiveWebhookLogEntity.setResponseBody(result != null ? result.toString() : "");
            receiveWebhookLogEntity.setExceptionMessage(exceptionMessage);
            log.info("method:{},ReceiveWebhookLog: {}", joinPoint.getSignature().getName(), receiveWebhookLogEntity);
            receiveWebhookLogService.asyncSaveLog(receiveWebhookLogEntity);
        }
        return result;
    }

    private JSONObject extractHeaders(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            jsonObject.set(headerName, headerValue);
        }
        return jsonObject;
    }

}
