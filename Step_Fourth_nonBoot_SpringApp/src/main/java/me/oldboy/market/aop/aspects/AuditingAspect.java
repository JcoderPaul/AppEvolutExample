package me.oldboy.market.aop.aspects;

import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.aop.annotations.Auditable;
import me.oldboy.market.dto.audit.AuditCreateDto;
import me.oldboy.market.dto.jwt.JwtAuthRequest;
import me.oldboy.market.entity.enums.Action;
import me.oldboy.market.entity.enums.Status;
import me.oldboy.market.services.interfaces.AuditService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Определяет правила "среза" и "внедрения" для аудита аннотированных {@link Auditable} методов.
 */
@Slf4j
@Aspect
@Component
public class AuditingAspect {

    private AuditService auditService;

    @Autowired
    public AuditingAspect(AuditService auditService) {
        this.auditService = auditService;
    }

    /**
     * Pointcut ("точка врезки") для методов, аннотированных {@link Auditable}, или классов, содержащих методы, аннотированные {@link Auditable}.
     */
    @Pointcut("@annotation(me.oldboy.market.aop.annotations.Auditable) && execution(* *(..))")
    public void auditOperation() {
    }

    /**
     * Advice для точки среза, обрабатывает методы требующие аудит-записи, помеченные как {@link Auditable}.
     *
     * @param joinPoint "точка контакта" - перехваченный метод.
     * @return результат перехваченного метода.
     * @throws Throwable ошибки брошенные "взрезанным" методом.
     */
    @Around("auditOperation()")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Auditable auditAnnotationContain = methodSignature.getMethod().getAnnotation(Auditable.class);

        Action operationType = auditAnnotationContain.operationType();

        Object[] args = joinPoint.getArgs();

        String auditableRecord = args[0].toString();
        String userEmail = null;
        Boolean isTryToRemove = false;

        AuditCreateDto auditCreateDto = AuditCreateDto.builder()
                .createAt(LocalDateTime.now())
                .action(operationType)
                .auditableRecord(auditableRecord)
                .build();

        if (args[0] instanceof JwtAuthRequest) {
            JwtAuthRequest jwtAuthRequest = (JwtAuthRequest) args[0];
            userEmail = jwtAuthRequest.getEmail();

            auditCreateDto.setUserEmail(userEmail);
            auditCreateDto.setAuditableRecord(null);
        }

        if (args[0] instanceof Long) {
            isTryToRemove = true;
            auditCreateDto.setAuditableRecord("Product with ID - " + auditableRecord + " deleted success");
        }

        try {
            Object result = joinPoint.proceed();
            auditCreateDto.setIsSuccess(Status.SUCCESS);

            auditService.create(auditCreateDto);

            return result;
        } catch (Throwable ex) {
            auditCreateDto.setIsSuccess(Status.FAIL);

            if (isTryToRemove) {
                auditCreateDto.setAuditableRecord("Product with ID - " + auditableRecord + " delete failed");
            }

            auditService.create(auditCreateDto);
            throw ex;
        }
    }
}