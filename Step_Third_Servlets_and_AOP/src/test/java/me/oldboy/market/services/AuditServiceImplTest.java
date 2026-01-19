package me.oldboy.market.services;

import me.oldboy.market.config.connection.DbConnectionPool;
import me.oldboy.market.config.liquibase.LiquibaseManager;
import me.oldboy.market.config.utils.ConfigProvider;
import me.oldboy.market.config.utils.PropertiesReader;
import me.oldboy.market.entity.Audit;
import me.oldboy.market.entity.Product;
import me.oldboy.market.entity.enums.Action;
import me.oldboy.market.entity.enums.Status;
import me.oldboy.market.repository.AuditRepositoryImpl;
import me.oldboy.market.repository.UserRepositoryImpl;
import me.oldboy.market.test_container.PostgresTestContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AuditServiceImplTest extends PostgresTestContainer {
    private AuditRepositoryImpl auditRepository;
    private UserRepositoryImpl userRepositoryImpl;
    private AuditServiceImpl auditService;
    private Connection connection;
    private DbConnectionPool connectionPool;
    private LiquibaseManager liquibaseManager;
    private Long existId, nonExistId;
    private String existEmail, notExistEmail;

    @BeforeEach
    public void getConnectionToTestBaseAndInitIt() {
        ConfigProvider configProvider = new PropertiesReader();
        connectionPool = DbConnectionPool.getINSTANCE();
        connectionPool.initTestPool(configProvider.get("db.driver"),
                postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword());

        try {
            connection = connectionPool.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        liquibaseManager = LiquibaseManager.getInstance(configProvider);
        liquibaseManager.migrationsStart(connection);

        auditRepository = new AuditRepositoryImpl(connectionPool);
        userRepositoryImpl = new UserRepositoryImpl(connectionPool);
        auditService = new AuditServiceImpl(auditRepository, userRepositoryImpl);

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
    void create_shouldReturnNull_userEmailNotFound_Test() {
        Audit auditRecordWithoutId = Audit.builder()
                .createAt(LocalDateTime.now())
                .createBy(notExistEmail)
                .action(Action.UPDATE_PRODUCT)
                .isSuccess(Status.SUCCESS)
                .auditableRecord(new Product().toString())
                .build();

        assertThat(auditService.create(auditRecordWithoutId)).isNull();
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