package me.oldboy.market.auditor.core.aspects;

import me.oldboy.market.auditor.core.dto.jwt.JwtAuthRequest;
import me.oldboy.market.auditor.core.entity.enums.Action;
import me.oldboy.market.auditor.core.entity.enums.Status;
import me.oldboy.market.auditor.core.service.interfaces.AuditService;
import me.oldboy.market.productmanager.core.exceptions.ProductManagerModuleException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @DisplayName("Тестируем взаимодействие (оно универсально в рамках DTO) с методами ProductService")
    @Test
    void audit_successExecutionWithDto_shouldCreateSuccessAudit_forCreateProduct_Test() throws Throwable {
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getName()).thenReturn("create");
        when(joinPoint.getArgs()).thenReturn(new Object[]{"test-record"});
        when(joinPoint.proceed()).thenReturn("success-result");

        Object result = auditingAspect.audit(joinPoint);

        assertThat("success-result").isEqualTo(result);
        verify(auditService).create(argThat(auditDto ->
                auditDto.getIsSuccess() == Status.SUCCESS &&
                        auditDto.getAuditableRecord().equals("test-record") &&
                        auditDto.getAction().equals(Action.ADD_PRODUCT)
        ));
    }

    @DisplayName("Тест взаимодействия с процессом аутентификации LoginController - успешная аутентификация")
    @Test
    void audit_withJwtAuthRequest_shouldSetUserEmailAndSuccess_forLogin_Test() throws Throwable {
        JwtAuthRequest jwtRequest = new JwtAuthRequest("admin@admin.ru", "1234");

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getName()).thenReturn("loginUser");
        when(joinPoint.getArgs()).thenReturn(new Object[]{jwtRequest});
        when(joinPoint.proceed()).thenReturn("auth-token");

        auditingAspect.audit(joinPoint);

        verify(auditService)
                .create(argThat(auditDto -> auditDto.getUserEmail().equals("admin@admin.ru")
                        && auditDto.getAuditableRecord() == null
                        && auditDto.getIsSuccess().equals(Status.SUCCESS)));
    }

    @DisplayName("Тест взаимодействия с процессом аутентификации LoginController - бросок ошибки")
    @Test
    void audit_withJwtAuthRequest_shouldSetUserEmailAndFail_forLogin_Test() throws Throwable {
        JwtAuthRequest jwtRequest = new JwtAuthRequest("admin@admin.ru", "4321");

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getName()).thenReturn("loginUser");
        when(joinPoint.getArgs()).thenReturn(new Object[]{jwtRequest});
        when(joinPoint.proceed()).thenThrow(new RuntimeException("Entered wrong password!"));

        assertThatThrownBy(() -> auditingAspect.audit(joinPoint))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Entered wrong password!");

        verify(auditService)
                .create(argThat(auditDto -> auditDto.getUserEmail().equals("admin@admin.ru")
                        && auditDto.getAuditableRecord() == null
                        && auditDto.getIsSuccess().equals(Status.FAIL)));
    }

    @DisplayName("Тест записи провала действий, практически, одинаков для всех аудируемых методов с DTO")
    @Test
    void audit_whenMethodReturnException_shouldCreateFailedAudit_Test() throws Throwable {
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getName()).thenReturn("create");
        when(joinPoint.getArgs()).thenReturn(new Object[]{"test-record"});
        when(joinPoint.proceed()).thenThrow(new RuntimeException("Test error"));

        assertThatThrownBy(() -> auditingAspect.audit(joinPoint))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Test error");

        verify(auditService)
                .create(argThat(auditDto -> auditDto.getIsSuccess().equals(Status.FAIL)));
    }

    @DisplayName("Тест взаимодействия с методом с числовым аргументом")
    @Test
    void audit_withLongArgument_shouldFormatRecordMessage_forDeleteProduct_Test() throws Throwable {
        Long productId = 123L;

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getName()).thenReturn("delete");
        when(joinPoint.getArgs()).thenReturn(new Object[]{productId});
        when(joinPoint.proceed()).thenReturn(true);

        auditingAspect.audit(joinPoint);

        verify(auditService)
                .create(argThat(auditDto -> auditDto.getAuditableRecord().equals("Product with ID - " + productId + " deleted success")
                        && auditDto.getIsSuccess().equals(Status.SUCCESS)));
    }

    @DisplayName("Тест броска исключения при взаимодействии с методом с числовым аргументом")
    @Test
    void audit_withLongArgument_shouldCreateFailedAuditWithProperMessage_Test() throws Throwable {
        Long productId = 123L;

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getName()).thenReturn("delete");
        when(joinPoint.getArgs()).thenReturn(new Object[]{productId});
        when(joinPoint.proceed()).thenThrow(new ProductManagerModuleException("Не найден ID - " + productId + " продукта, обновление невозможно"));

        assertThatThrownBy(() -> auditingAspect.audit(joinPoint))
                .isInstanceOf(ProductManagerModuleException.class)
                .hasMessageContaining("Не найден ID - " + productId + " продукта, обновление невозможно");

        verify(auditService)
                .create(argThat(auditDto -> auditDto.getIsSuccess().equals(Status.FAIL)
                        && auditDto.getAuditableRecord().equals("Product with ID - " + productId + " delete failed")));
    }
}