package com.mc.payment.core.service.config.aspect;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.mc.payment.core.service.util.XxlJobUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
@Slf4j
public class JobHandlerAspect {
    private static final TimeInterval timer = DateUtil.timer();

    @Before("@annotation(com.xxl.job.core.handler.annotation.XxlJob) && execution(* *(..))")
    public void beforeXxlJobExecution(JoinPoint joinPoint) {
        timer.restart();
        // 生成链路追踪ID
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        String jobName = joinPoint.getSignature().getName();

        XxlJobUtil.log("{} 开始", jobName);
    }

    @After("@annotation(com.xxl.job.core.handler.annotation.XxlJob) && execution(* *(..))")
    public void afterXxlJobExecution(JoinPoint joinPoint) {
        String jobName = joinPoint.getSignature().getName();
        // 清理MDC中的Trace ID
        MDC.remove("traceId");
        XxlJobUtil.log("{} 结束,执行耗时:{}ms", jobName, timer.interval());
    }
}
