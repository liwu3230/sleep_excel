package org.example.backend.web.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class ElapsedLogAspect {

    @Around("@annotation(org.example.backend.web.aspect.ElapsedLog)")
    public Object doMetricProcess(ProceedingJoinPoint joinPoint) throws Throwable {
        long startMs = System.currentTimeMillis();
        Signature sign = joinPoint.getSignature();
        String name = sign.getDeclaringTypeName() + "#" + sign.getName();
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            log.error("invoke method filter failed by ", e);
            throw e;
        } finally {
            log.info("current method [{}] invoke elapsed {} milliseconds!", name, Long.valueOf(System.currentTimeMillis() - startMs));
        }
    }
}

