package com.jairo.workflowtramites.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.jairo.workflowtramites.service..*.*(..))")
    public Object loggear(ProceedingJoinPoint pjp) throws Throwable {
        String clase = pjp.getSignature().getDeclaringType().getSimpleName();
        String metodo = pjp.getSignature().getName();
        long inicio = System.currentTimeMillis();

        log.debug("[IN]  {}.{} — args: {}", clase, metodo, Arrays.toString(pjp.getArgs()));

        try {
            Object resultado = pjp.proceed();
            log.debug("[OUT] {}.{} — result: {}", clase, metodo, resultado);
            log.info("[OK]  {}.{} — {}ms", clase, metodo, System.currentTimeMillis() - inicio);
            return resultado;
        } catch (Exception e) {
            log.error("[ERROR] {}.{} — {}", clase, metodo, e.getMessage());
            throw e;
        }
    }
}
