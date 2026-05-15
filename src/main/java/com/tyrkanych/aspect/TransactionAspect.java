package com.tyrkanych.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TransactionAspect {

    @Around("execution(* com.tyrkanych.service.impl.*.*(..))")
    public Object handleTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        long startTime = System.currentTimeMillis();

        System.out.println("Початок транзакції: " + methodName);

        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - startTime;
            System.out.println(" Транзакція завершена: " + methodName + " (" + elapsed + "ms)");
            return result;

        } catch (IllegalArgumentException e) {
            System.out.println("Бізнес-помилка у " + methodName + ": " + e.getMessage());
            throw e;

        } catch (Exception e) {
            System.out.println(" Помилка транзакції у " + methodName + ": " + e.getMessage());
            throw e;
        }
    }
}