package me.oldboy.market.services;

import me.oldboy.market.config.connection.ConnectionManager;
import me.oldboy.market.config.liquibase.LiquibaseManager;
import me.oldboy.market.config.utils.ConfigProvider;
import me.oldboy.market.config.utils.PropertiesReader;
import me.oldboy.market.entity.Audit;
import me.oldboy.market.entity.Product;
import me.oldboy.market.entity.enums.Action;
import me.oldboy.market.entity.enums.Status;
import me.oldboy.market.exceptions.ServiceLayerException;
import me.oldboy.market.repository.AuditRepository;
import me.oldboy.market.repository.UserRepository;
import me.oldboy.market.test_container.PostgresTestContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuditServiceImplTest extends PostgresTestContainer {

    private AuditRepository auditRepository;
    private UserRepository userRepository;
    private AuditServiceImpl auditService;
    private Connection connection;
    private LiquibaseManager liquibaseManager;
    private Long existId, nonExistId;
    private String existEmail, notExistEmail;

    @BeforeEach
    public void getConnectionToTestBaseAndInitIt() {
        connection = ConnectionManager.getTestBaseConnection(postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword()
        );

        ConfigProvider configProvider = new PropertiesReader();
        liquibaseManager = LiquibaseManager.getInstance(configProvider);
        liquibaseManager.migrationsStart(connection);

        auditRepository = new AuditRepository(connection);
        userRepository = new UserRepository(connection);
        auditService = new AuditServiceImpl(auditRepository, userRepository);

        /* Предварительные тестовые данные */
        existId = 1L;
        nonExistId = 100L;

        existEmail = "admin@admin.ru";
        notExistEmail = "non_exist@mail.de";
    }

    @AfterEach
    public void resetTestBase() {
        liquibaseManager.rollbackCreatedTables(connection);
    }

    /* Основные тесты для реализаций методов */

    @Test
    void create_shouldReturnAuditRecordWithId_Test() {
        Audit auditRecordWithoutId = Audit.builder()
                .createAt(LocalDateTime.now())
                .createBy(existEmail)
                .action(Action.UPDATE_PRODUCT)
                .isSuccess(Status.SUCCESS)
                .auditableRecord(new Product().toString())
                .build();

        Audit createdAuditRecordWithId = auditService.create(auditRecordWithoutId);

        assertThat(createdAuditRecordWithId).isNotNull();
        assertThat(createdAuditRecordWithId.getId()).isEqualTo(4);
    }

    @Test
    void create_shouldReturnException_userIdNotFound_Test() {
        Audit auditRecordWithoutId = Audit.builder()
                .createAt(LocalDateTime.now())
                .createBy(notExistEmail)
                .action(Action.UPDATE_PRODUCT)
                .isSuccess(Status.SUCCESS)
                .auditableRecord(new Product().toString())
                .build();

        assertThatThrownBy(() -> auditService.create(auditRecordWithoutId))
                .isInstanceOf(ServiceLayerException.class)
                .hasMessageContaining("Пользователь с email - " + auditRecordWithoutId.getCreateBy() + " не найден, запись действия невозможна.");
    }

    @Test
    void findById_shouldReturnFoundAuditRecord_Test() {
        Audit foundAuditRecord = auditService.findById(existId);
        assertThat(foundAuditRecord).isNotNull();
        assertThat(foundAuditRecord.getId()).isEqualTo(existId);
    }

    @Test
    void findById_shouldReturnNull_recordIdNotFound_Test() {
        assertThat(auditService.findById(nonExistId)).isNull();
    }

    @Test
    void findAll_shouldReturnAuditRecordList_Test() {
        assertThat(auditService.findAll().size()).isEqualTo(3);
    }
}