package me.oldboy.market.aop.aspects;

import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.aop.annotations.Auditable;
import me.oldboy.market.entity.Audit;
import me.oldboy.market.entity.enums.Action;
import me.oldboy.market.entity.enums.Status;
import me.oldboy.market.services.interfaces.AuditService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.time.LocalDateTime;

/**
 * Аспект для аудита действий пользователей и времени выполнения методов.
 */
@Slf4j
@Aspect
public class AuditingAspect {
    private static AuditService auditService;

    public static void setAuditService(AuditService auditService) {
        AuditingAspect.auditService = auditService;
    }

    /**
     * Данная точка среза нужна для отключения advice-a при проведении Unit-тестов над аудируемыми классами
     *
     * @return true - если инициализирован сервисный слой аудита ("боевой режим"), false - в противном случае (тесты)
     */
    @Pointcut("if()")
    public static boolean isActive() {
        return auditService != null;
    }

    /**
     * Точка среза соответствует всем методам помеченным аннотацией @Auditable
     */
    @Pointcut("@annotation(me.oldboy.market.aop.annotations.Auditable) && execution(* *(..))")
    public void auditOperation() {
    }

    /**
     * Адвайс-метод который аудирует (записывает) действия пользователей и время выполнения методов помеченных аннотацией @Auditable
     *
     * @param joinPoint точка внедрения
     * @return результат выполнения метода
     * @throws Throwable если возникает ошибка во время выполнения метода
     */
    @Around("isActive() && auditOperation()")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Auditable auditAnnotationContain = methodSignature.getMethod().getAnnotation(Auditable.class);

        Action operationType = auditAnnotationContain.operationType();
        String userEmail = "";
        String auditableRecord = "";
        Boolean isTryToRemove = false;

        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (methodSignature.getParameterNames()[i].equals("email")) {
                userEmail = (String) args[i];
            }
            if (methodSignature.getParameterNames()[i].equals("productCreateDto")) {
                auditableRecord = args[i].toString();
            }
            if (methodSignature.getParameterNames()[i].equals("productUpdateDto")) {
                auditableRecord = args[i].toString();
            }
            if (methodSignature.getParameterNames()[i].equals("productId")) {
                isTryToRemove = true;
                auditableRecord = args[i].toString();
            }
        }

        Audit auditRecord = Audit.builder()
                .createAt(LocalDateTime.now())
                .createBy(userEmail)
                .action(operationType)
                .auditableRecord(auditableRecord)
                .build();

        try {
            Object result = joinPoint.proceed();
            auditRecord.setIsSuccess(Status.SUCCESS);
            if (isTryToRemove) {
                auditRecord.setAuditableRecord("Product with ID - " + auditableRecord + " deleted");
            }
            auditService.create(auditRecord);
            return result;
        } catch (Throwable ex) {
            auditRecord.setIsSuccess(Status.FAIL);
            if (isTryToRemove) {
                auditRecord.setAuditableRecord("Product with ID - " + auditableRecord + " tried to remove");
            }
            auditService.create(auditRecord);
            throw ex;
        }
    }
}