package me.oldboy.market.aop.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Аспектный класс для обработки методов помеченных аннотацией @Loggable
 */
@Slf4j
@Aspect
public class LoggingAspect {

    /**
     * Точка среза фиксирующая аннотированные @Loggable методы
     */
    @Pointcut("@annotation(me.oldboy.market.aop.annotations.Loggable) && execution(* *(..))")
    public void loggableMethods() {
    }

    /**
     * Адвайс-метод запускающий логирование при старте метода, его финише и рассчитывающий время работы метода
     *
     * @param joinPoint точка среза перехватываемого метода.
     * @return результат "взрезанного" метода
     * @throws Throwable если возникает ошибка во время выполнения метода
     */
    @Around("loggableMethods()")
    public Object loggableMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        /*
        Можно использовать вариант конкатенации строк: log.info("Calling method (начало метода): " + methodName);
        Но, в реальных приложениях для ускорения процесса рекомендуется применять вариант с аргументами см. ниже.
        */
        log.info("Calling method (начало метода): {}", methodName);

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        log.info("Execution of method (обработка и завершение метода) {} finished. " +
                "Execution time is (время работы метода в мс.) {} ms.", methodName, (endTime - startTime));
        return result;
    }
}