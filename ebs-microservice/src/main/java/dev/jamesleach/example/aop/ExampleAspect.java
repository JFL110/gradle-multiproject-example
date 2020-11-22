package dev.jamesleach.example.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class ExampleAspect {

    private final Logger log = LoggerFactory.getLogger(ExampleAspect.class);

    @Around(value = "@annotation(dev.jamesleach.example.aop.ExampleAspectAnnotation)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        ExampleAspectAnnotation exampleAnnotation = method.getAnnotation(ExampleAspectAnnotation.class);

        log.info("Before " + exampleAnnotation.value());
        try {
            return joinPoint.proceed();
        } finally {
            log.info("After " + exampleAnnotation.value());
        }
    }
}
