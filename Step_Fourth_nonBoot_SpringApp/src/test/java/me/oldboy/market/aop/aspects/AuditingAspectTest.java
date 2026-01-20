package me.oldboy.market.aop.aspects;

import me.oldboy.market.aop.annotations.Auditable;
import me.oldboy.market.controllers.LoginController;
import me.oldboy.market.controllers.ProductController;
import me.oldboy.market.dto.jwt.JwtAuthRequest;
import me.oldboy.market.dto.product.ProductCreateDto;
import me.oldboy.market.entity.enums.Status;
import me.oldboy.market.services.interfaces.AuditService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditingAspectTest {

    @Mock
    private AuditService auditService;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    private AuditingAspect auditingAspect;

    @BeforeEach
    void setUp() {
        auditingAspect = new AuditingAspect(auditService);
    }

    @DisplayName("Тестируем взаимодействие (оно универсально в рамках DTO) с методами ProductController")
    @Test
    void audit_successExecutionWithDto_shouldCreateSuccessAudit_forCreateProduct_Test() throws Throwable {
        Method method = ProductController.class.getMethod("createProduct", ProductCreateDto.class);
        Auditable auditable = method.getAnnotation(Auditable.class);

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"test-record"});
        when(joinPoint.proceed()).thenReturn("success-result");

        Object result = auditingAspect.audit(joinPoint);

        assertEquals("success-result", result);
        verify(auditService).create(argThat(auditDto ->
                auditDto.getIsSuccess() == Status.SUCCESS &&
                        auditDto.getAuditableRecord().equals("test-record") &&
                        auditDto.getAction() == auditable.operationType()
        ));
    }

    @DisplayName("Тест взаимодействия с процессом аутентификации LoginController")
    @Test
    void audit_withJwtAuthRequest_shouldSetUserEmailAndClearRecord_forLogin_Test() throws Throwable {
        Method method = LoginController.class.getMethod("loginUser", JwtAuthRequest.class);
        JwtAuthRequest jwtRequest = new JwtAuthRequest("admin@admin.ru", "1234");

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
        when(joinPoint.getArgs()).thenReturn(new Object[]{jwtRequest});
        when(joinPoint.proceed()).thenReturn("auth-token");

        auditingAspect.audit(joinPoint);

        verify(auditService).create(argThat(auditDto ->
                auditDto.getUserEmail().equals("admin@admin.ru") &&
                        auditDto.getAuditableRecord() == null
        ));
    }

    @DisplayName("Тест записи провала действий, практически, одинаков для всех аудируемых методов с DTO")
    @Test
    void audit_whenMethodReturnException_shouldCreateFailedAudit_Test() throws Throwable {
        Method method = ProductController.class.getMethod("createProduct", ProductCreateDto.class);

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"test-record"});
        when(joinPoint.proceed()).thenThrow(new RuntimeException("Test error"));

        assertThrows(RuntimeException.class, () -> auditingAspect.audit(joinPoint));

        verify(auditService).create(argThat(auditDto ->
                auditDto.getIsSuccess() == Status.FAIL
        ));
    }

    @DisplayName("Тест взаимодействия с методом с числовым аргументом")
    @Test
    void audit_withLongArgument_shouldFormatRecordMessage_forDeleteProduct_Test() throws Throwable {
        Method method = ProductController.class.getMethod("deleteProduct", Long.class);
        Long productId = 123L;

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
        when(joinPoint.getArgs()).thenReturn(new Object[]{productId});
        when(joinPoint.proceed()).thenReturn(true);

        auditingAspect.audit(joinPoint);

        verify(auditService).create(argThat(auditDto ->
                auditDto.getAuditableRecord().equals("Product with ID - " + productId + " deleted success")
        ));
    }

    @DisplayName("Тест броска исключения при взаимодействии с методом с числовым аргументом")
    @Test
    void audit_withLongArgument_shouldCreateFailedAuditWithProperMessage_Test() throws Throwable {
        Method method = ProductController.class.getMethod("deleteProduct", Long.class);
        Long productId = 123L;

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
        when(joinPoint.getArgs()).thenReturn(new Object[]{productId});
        when(joinPoint.proceed()).thenThrow(new RuntimeException("Delete failed"));

        assertThrows(RuntimeException.class, () -> auditingAspect.audit(joinPoint));

        verify(auditService).create(argThat(auditDto ->
                auditDto.getIsSuccess() == Status.FAIL &&
                        auditDto.getAuditableRecord().equals("Product with ID - " + productId + " delete failed")
        ));
    }
}