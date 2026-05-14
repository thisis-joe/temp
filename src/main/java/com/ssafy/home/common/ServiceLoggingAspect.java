package com.ssafy.home.common;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Pattern;

@Aspect
@Slf4j
@Component
public class ServiceLoggingAspect {
    private static final int MAX_LOG_LENGTH = 3_000;
    private static final Pattern SENSITIVE_VALUE = Pattern.compile(
            "(?i)(password|serviceKey|consumer_secret|key)=([^,)}\\]]+)");

    @Pointcut("within(com.ssafy.home..*Service)")
    public void serviceLayer() {
    }

    @Before("serviceLayer()")
    public void logServiceCall(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        log.info("SERVICE_CALL class={} method={} signature={} params={}",
                className(signature),
                signature.getName(),
                signature.toShortString(),
                sanitize(Arrays.toString(joinPoint.getArgs())));
    }

    @AfterReturning(pointcut = "serviceLayer()", returning = "returnValue")
    public void logServiceReturn(JoinPoint joinPoint, Object returnValue) {
        Signature signature = joinPoint.getSignature();
        log.info("SERVICE_RETURN class={} method={} signature={} returnValue={}",
                className(signature),
                signature.getName(),
                signature.toShortString(),
                sanitize(String.valueOf(returnValue)));
    }

    private static String className(Signature signature) {
        if (signature instanceof MethodSignature methodSignature) {
            return methodSignature.getDeclaringType().getSimpleName();
        }
        return signature.getDeclaringTypeName();
    }

    private static String sanitize(String value) {
        if (value == null) {
            return "null";
        }
        String masked = SENSITIVE_VALUE.matcher(value).replaceAll("$1=***");
        if (masked.length() <= MAX_LOG_LENGTH) {
            return masked;
        }
        return masked.substring(0, MAX_LOG_LENGTH) + "...(truncated)";
    }
}
