package me.oldboy.market.repository;

import me.oldboy.market.config.connection.ConnectionManager;
import me.oldboy.market.config.liquibase.LiquibaseManager;
import me.oldboy.market.config.utils.ConfigProvider;
import me.oldboy.market.config.utils.PropertiesReader;
import me.oldboy.market.entity.Audit;
import me.oldboy.market.entity.Product;
import me.oldboy.market.entity.enums.Action;
import me.oldboy.market.entity.enums.Status;
import me.oldboy.market.test_container.PostgresTestContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class AuditRepositoryTest extends PostgresTestContainer {
    private AuditRepository auditRepository;
    private Connection connection;
    private LiquibaseManager liquibaseManager;
    private Audit createNewAuditRecord, updateAudit;
    private Long existId, nonExistId;
    private String existEmail, nonExistEmail;

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

        /* Предварительные тестовые данные */
        existId = 1L;
        nonExistId = 100L;

        existEmail = "admin@admin.ru";
        nonExistEmail = "non_exist@mial.de";

        createNewAuditRecord = Audit.builder()
                .createAt(LocalDateTime.now())
                .createBy(existEmail)
                .action(Action.ADD_PRODUCT)
                .isSuccess(Status.SUCCESS)
                .auditableRecord(new Product().toString())
                .build();

        updateAudit = Audit.builder()
                .id(existId)
                .createAt(LocalDateTime.now())
                .createBy(existEmail)
                .action(Action.UPDATE_PRODUCT)
                .isSuccess(Status.FAIL)
                .auditableRecord(new Product().toString())
                .build();
    }

    @AfterEach
    public void resetTestBase() {
        liquibaseManager.rollbackCreatedTables(connection);
    }

    /* Основные тесты для реализаций методов */

    @Test
    void create_shouldReturnCreatedAuditRecord_Test() {
        Optional<Audit> mayBeCreate = auditRepository.create(createNewAuditRecord);

        assertThat(mayBeCreate.isPresent()).isTrue();

        assertAll(
                () -> assertThat(mayBeCreate.get().getCreateBy()).isEqualTo(createNewAuditRecord.getCreateBy()),
                () -> assertThat(mayBeCreate.get().getAction()).isEqualTo(createNewAuditRecord.getAction()),
                () -> assertThat(mayBeCreate.get().getIsSuccess()).isEqualTo(createNewAuditRecord.getIsSuccess()),
                () -> assertThat(mayBeCreate.get().getAuditableRecord()).isEqualTo(createNewAuditRecord.getAuditableRecord()),
                () -> assertThat(mayBeCreate.get().getId()).isEqualTo(4)
        );
    }

    @Test
    void findById_shouldReturnAuditRecordIfFoundIt_Test() {
        Optional<Audit> foundSomething = auditRepository.findById(existId);

        assertThat(foundSomething.isPresent()).isTrue();
        assertThat(foundSomething.get().getId()).isEqualTo(existId);
    }

    @Test
    void findById_shouldReturnOptionalEmptyForNotExistId_Test() {
        Optional<Audit> notFoundAnything = auditRepository.findById(nonExistId);
        assertThat(notFoundAnything.isEmpty()).isTrue();
    }

    @Test
    void update_shouldReturnTrueAfterUpdate_Test() {
        Boolean isUpdateGood = auditRepository.update(updateAudit);

        assertThat(isUpdateGood).isTrue();

        Optional<Audit> mayBeFound = auditRepository.findById(existId);

        if (mayBeFound.isPresent()) {
            assertAll(
                    () -> assertThat(updateAudit.getId()).isEqualTo(mayBeFound.get().getId()),
                    () -> assertThat(updateAudit.getAuditableRecord()).isEqualTo(mayBeFound.get().getAuditableRecord())
            );
        }
    }

    @Test
    void delete_shouldReturnTrueAfterDelete_Test() {
            Integer sizeOfListBeforeDelete = auditRepository.findAll().size();

            boolean isDeleteGood = auditRepository.delete(existId);
            assertThat(isDeleteGood).isTrue();

            Integer sizeOfListAfterDelete = auditRepository.findAll().size();
            assertThat(sizeOfListBeforeDelete).isGreaterThan(sizeOfListAfterDelete);
    }

    @Test
    void delete_shouldReturnFalseAfterDelete_Test() {
        Integer sizeOfListBeforeDelete = auditRepository.findAll().size();

        boolean isDeleteGood = auditRepository.delete(nonExistId);
        assertThat(isDeleteGood).isFalse();

        Integer sizeOfListAfterDelete = auditRepository.findAll().size();
        assertThat(sizeOfListBeforeDelete).isEqualTo(sizeOfListAfterDelete);
    }

    @Test
    void findAll_shouldReturnAllBrandList_Test() {
        List<Audit> auditList = auditRepository.findAll();

        assertThat(auditList.size()).isEqualTo(3);

        assertAll(
                () -> assertThat(auditList.get(0).getId()).isEqualTo(1L),
                () -> assertThat(auditList.get(auditList.size() - 1).getId()).isEqualTo(3L)
        );
    }

    @Test
    void findByCreationUserEmail_shouldReturnOptionalAuditList_Test() {
        Optional<List<Audit>> foundAuditList = auditRepository.findByCreationUserEmail(existEmail);

        assertThat(foundAuditList.isPresent()).isTrue();
        assertThat(foundAuditList.get().size()).isEqualTo(2);
    }

    @Test
    void findByCreationUserId_shouldReturnOptionalEmpty_Test() {
        Optional<List<Audit>> notFoundList = auditRepository.findByCreationUserEmail(nonExistEmail);
        assertThat(notFoundList.isEmpty()).isFalse();
        assertThat(notFoundList.get().size()).isEqualTo(0);
    }
}