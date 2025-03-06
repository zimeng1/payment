package com.mc.payment.core.service.config.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author Marty
 * @since 2024/6/27 11:26
 */

@Slf4j
@Aspect
@Component
public class LogExecutionTimeAspect {

    @Around("@within(LogExecutionTime) || @annotation(LogExecutionTime)") // 处理类级别和方法级别
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        log.info("execution method: [{}], execution time: [{} ms]", joinPoint.getSignature().getName(), executionTime);
        return proceed;
    }
}
