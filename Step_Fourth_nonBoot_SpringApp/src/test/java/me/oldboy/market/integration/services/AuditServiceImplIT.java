package me.oldboy.market.integration.services;

import me.oldboy.market.config.test_data_source.TestContainerInit;
import me.oldboy.market.dto.audit.AuditCreateDto;
import me.oldboy.market.dto.audit.AuditReadDto;
import me.oldboy.market.entity.Product;
import me.oldboy.market.entity.enums.Action;
import me.oldboy.market.entity.enums.Status;
import me.oldboy.market.services.interfaces.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditServiceImplIT extends TestContainerInit {
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @Autowired
    private AuditService auditService;
    private Long existId, nonExistId;
    private String existEmail, notExistEmail;

    @BeforeEach
    public void setUp() {
        /* Предварительные тестовые данные */
        existId = 1L;
        nonExistId = 100L;

        existEmail = "admin@admin.ru";
        notExistEmail = "non_exist@mail.de";
    }

    /* Основные тесты для реализаций методов */
    @Nested
    @DisplayName("Блок тестов на *.create() метод AuditService")
    class CreateMethodOnProductServiceTests {
        @Test
        @DisplayName("Тест процесса создания аудит-записи при верной аутентификации")
        void create_shouldReturnAuditRecordWithId_loginAudit_notAuthUser_Test() {
            AuditCreateDto auditRecordWithoutId = AuditCreateDto.builder()
                    .createAt(LocalDateTime.now())
                    .action(Action.LOGIN)
                    .isSuccess(Status.SUCCESS)
                    .userEmail("admin@admin.ru")
                    .auditableRecord(new Product().toString())
                    .build();

            AuditReadDto createdAuditRecordWithId = auditService.create(auditRecordWithoutId);

            assertThat(createdAuditRecordWithId).isNotNull();
            assertThat(createdAuditRecordWithId.id()).isGreaterThan(3);
        }

        @Test
        @DisplayName("Тест процесса создания аудит-записи для: удаления, обновления товара, создания нового товара, пользователь уже аутентифицирован")
        void create_shouldReturnAuditRecordWithId_alreadyAuthUser_Test() {
            AuditCreateDto auditRecordWithoutId = AuditCreateDto.builder()
                    .createAt(LocalDateTime.now())
                    .action(Action.UPDATE_PRODUCT)
                    .isSuccess(Status.SUCCESS)
                    .auditableRecord(new Product().toString())
                    .build();

            try (MockedStatic<SecurityContextHolder> mockedHolder = mockStatic(SecurityContextHolder.class)) {
                mockedHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                when(securityContext.getAuthentication()).thenReturn(authentication);
                when(authentication.isAuthenticated()).thenReturn(true);
                when(authentication.getName()).thenReturn(existEmail);

                AuditReadDto result = auditService.create(auditRecordWithoutId);

                assertThat(result).isNotNull();
                assertThat(existEmail).isEqualTo(result.createBy()); // проверяем, что взял из principal
            }
        }

        @Test
        @DisplayName("Тест процесса создания аудит-записи для ситуации когда пользователь не аутентифицирован или ввел не верный email")
        void create_shouldReturnNull_userEmailNotFound_notLoginNotAuth_Test() {
            AuditCreateDto auditRecordWithoutId = AuditCreateDto.builder()
                    .createAt(LocalDateTime.now())
                    .action(Action.ADD_PRODUCT)
                    .isSuccess(Status.SUCCESS)
                    .auditableRecord(new Product().toString())
                    .build();

            assertThat(auditService.create(auditRecordWithoutId)).isNull();
        }
    }

    @Test
    @DisplayName("Должен вернут найденную по ID аудит-запись - ID в БД есть")
    void findById_shouldReturnFoundAuditRecord_Test() {
        Optional<AuditReadDto> foundAuditRecord = auditService.findById(existId);
        assertThat(foundAuditRecord.isPresent()).isTrue();
        assertThat(foundAuditRecord.get().id()).isEqualTo(existId);
    }

    @Test
    @DisplayName("Должен вернут false не нашел по ID аудит-запись - ID в БД отсутствует")
    void findById_shouldReturnOptionalEmpty_recordIdNotFound_Test() {
        assertThat(auditService.findById(nonExistId).isPresent()).isFalse();
    }

    @Test
    @DisplayName("Должен вернут полный список аудит-записей")
    void findAll_shouldReturnAuditRecordList_Test() {
        assertThat(auditService.findAll().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Должен вернут список аудит-записей найденных по заданному email")
    void findAllAuditsByUserEmail_shouldReturnAuditRecordList_existEmail_Test() {
        assertThat(auditService.findAllAuditsByUserEmail(existEmail).size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Должен вернут пустой список аудит-записей - по заданному email записей нет")
    void findAllAuditsByUserEmail_shouldReturnEmptyRecordList_notExistEmail_Test() {
        assertThat(auditService.findAllAuditsByUserEmail(notExistEmail).size()).isEqualTo(0);
    }
}