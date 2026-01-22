package me.oldboy.market.auditor.core.aspects;

import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.auditor.core.dto.audit.AuditCreateDto;
import me.oldboy.market.auditor.core.dto.jwt.JwtAuthRequest;
import me.oldboy.market.auditor.core.entity.enums.Action;
import me.oldboy.market.auditor.core.entity.enums.Status;
import me.oldboy.market.auditor.core.service.interfaces.AuditService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

/**
 * Определяет правила "среза" и "внедрения" для аудита заранее определенных методов
 * (в частности у нас это вход в систему и критические манипуляции с продуктом).
 */
@Slf4j
@Aspect
public class AuditingAspect {

    private AuditService auditService;

    @Autowired
    public AuditingAspect(AuditService auditService) {
        this.auditService = auditService;
    }

    /**
     * Определяет точку среза (pointcut) для всех методов внутри контроллеров приложения.
     */
    @Pointcut("within(me.oldboy.market.controllers.*)")
    public void inControllers() {
    }

    /**
     * Определяет точку среза (pointcut) для всех методов внутри сервисов модуля управляющего продуктами приложения.
     */
    @Pointcut("within(me.oldboy.market.productmanager.core.services.*)")
    public void inServices() {
    }

    /**
     * Определяет комбинируемую точку среза (pointcut) для всех методов внутри сервисов
     * модуля управляющего продуктами приложения, с выделением конкретных методов по
     * названию отвечающих за создание, обновление и удаление.
     */
    @Pointcut("inServices() && " +
            "(execution(* create(..)) || " +
            "execution(* update(..)) || " +
            "execution(* delete(..)))")
    public void auditProductManipulation() {
    }

    /**
     * Определяет комбинируемую точку среза (pointcut) для всех методов внутри контроллеров
     * приложения, с выделением конкретного метода по названию отвечающего за аутентификацию.
     */
    @Pointcut("inControllers() && execution(* loginUser(..))")
    public void auditLoginOperation() {
    }

    /**
     * Advice для точки среза, обрабатывает методы требующие аудит-записи.
     *
     * @param joinPoint "точка контакта" - перехваченный метод.
     * @return результат перехваченного метода.
     * @throws Throwable ошибки брошенные "взрезанным" методом.
     */
    @Around("auditProductManipulation() || auditLoginOperation()")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String auditableRecord = args[0].toString();

        Action operationType = null;
        Boolean isTryToRemove = false;

        switch (methodName) {
            case "create":
                operationType = Action.ADD_PRODUCT;
                break;
            case "update":
                operationType = Action.UPDATE_PRODUCT;
                break;
            case "delete":
                operationType = Action.DELETE_PRODUCT;
                break;
            case "loginUser":
                operationType = Action.LOGIN;
                break;
            default:
                break;
        }

        AuditCreateDto auditCreateDto = AuditCreateDto.builder()
                .createAt(LocalDateTime.now())
                .action(operationType)
                .auditableRecord(auditableRecord)
                .build();

        if (args[0] instanceof JwtAuthRequest) {
            JwtAuthRequest jwtAuthRequest = (JwtAuthRequest) args[0];
            String userEmail = jwtAuthRequest.getEmail();

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