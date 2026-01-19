package me.oldboy.market.repository;

import me.oldboy.market.config.connection.DbConnectionPool;
import me.oldboy.market.config.liquibase.LiquibaseManager;
import me.oldboy.market.config.utils.ConfigProvider;
import me.oldboy.market.config.utils.PropertiesReader;
import me.oldboy.market.entity.User;
import me.oldboy.market.entity.enums.Role;
import me.oldboy.market.test_container.PostgresTestContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class UserRepositoryImplTest extends PostgresTestContainer {
    private UserRepositoryImpl userRepositoryImpl;
    private Connection connection;
    private DbConnectionPool connectionPool;
    private LiquibaseManager liquibaseManager;
    private User createNewUser, updateUser;
    private Long existId, nonExistId;
    private String existEmail, nonExistEmail;

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

        userRepositoryImpl = new UserRepositoryImpl(connectionPool);

        /* Предварительные тестовые данные */
        existId = 1L;
        nonExistId = 100L;

        existEmail = "admin@admin.ru";
        nonExistEmail = "non_exist@email.com";

        createNewUser = User.builder()
                .email("another_admin@admin.com")
                .password("1234")
                .role(Role.ADMIN)
                .build();

        /*
        В связи с использованием простой системы аудита действий пользователя, его email
        (не его ID) - как ключевой элемент идентификации "хулигана" должен оставаться
        неизменным. Любая попытка его изменить приведет к ошибке БД. Зато мы можем менять
        пароль, хотя для текущего задания это функционал не требуется. Так же можно ввести
        статус пользователя, например IS_ACTIVE, NOT_ACTIVE и оставить его данные в системе
        без удаления.
        */
        updateUser = User.builder()
                .userId(existId)
                .email(existEmail)
                .password("big_admin")
                .role(Role.ADMIN)
                .build();
    }

    @AfterEach
    public void resetTestBase() {
        liquibaseManager.rollbackCreatedTables(connection);
    }

    /* Основные тесты для реализаций методов */

    @Test
    void create_shouldReturnCreatedUserWithId_Test() {
        Optional<User> mayBeUser = userRepositoryImpl.create(createNewUser);

        assertThat(mayBeUser.isPresent()).isTrue();

        assertAll(
                () -> assertThat(mayBeUser.get().getEmail()).isEqualTo(createNewUser.getEmail()),
                () -> assertThat(mayBeUser.get().getPassword()).isEqualTo(createNewUser.getPassword()),
                () -> assertThat(mayBeUser.get().getRole()).isEqualTo(createNewUser.getRole()),
                () -> assertThat(mayBeUser.get().getUserId()).isEqualTo(4) // Мы помним текущую емкость таблицы
        );
    }

    @Test
    void findById_shouldReturnOptionalUserIfFoundIt_Test() {
        Optional<User> findUser = userRepositoryImpl.findById(existId);

        assertThat(findUser.isPresent()).isTrue();

        assertAll(
                () -> assertThat(findUser.get().getUserId()).isEqualTo(existId),
                () -> assertThat(findUser.get().getEmail()).isEqualTo("admin@admin.ru"),
                () -> assertThat(findUser.get().getRole()).isEqualTo(Role.ADMIN)
        );
    }

    @Test
    void findById_shouldReturnOptionalEmptyForNotExistId_Test() {
        Optional<User> findUser = userRepositoryImpl.findById(nonExistId);
        assertThat(findUser.isEmpty()).isTrue();
    }

    @Test
    void update_shouldReturnTrueAfterUpdate_Test() {
        Boolean isUpdateGood = userRepositoryImpl.update(updateUser);

        assertThat(isUpdateGood).isTrue();

        Optional<User> mayBeFound = userRepositoryImpl.findById(existId);

        if (mayBeFound.isPresent()) {
            assertAll(
                    () -> assertThat(updateUser.getUserId()).isEqualTo(mayBeFound.get().getUserId()),
                    () -> assertThat(updateUser.getEmail()).isEqualTo(mayBeFound.get().getEmail()),
                    () -> assertThat(updateUser.getPassword()).isEqualTo(mayBeFound.get().getPassword()),
                    () -> assertThat(updateUser.getRole()).isEqualTo(mayBeFound.get().getRole())
            );
        }
    }

    @Test
    void delete_shouldReturnTrueAfterDelete_Test() {
        Optional<User> mayBeUser = userRepositoryImpl.create(createNewUser);
        if (mayBeUser.isPresent()) {
            Integer sizeOfListBeforeDelete = userRepositoryImpl.findAll().size();

            boolean isDeleteGood = userRepositoryImpl.delete(mayBeUser.get().getUserId());
            assertThat(isDeleteGood).isTrue();

            Integer sizeOfListAfterDelete = userRepositoryImpl.findAll().size();
            assertThat(sizeOfListBeforeDelete).isGreaterThan(sizeOfListAfterDelete);
        }
    }

    @Test
    void findAll_shouldReturnAllUsersList_Test() {
        List<User> userList = userRepositoryImpl.findAll();

        assertThat(userList.size()).isEqualTo(3);

        assertAll(
                () -> assertThat(userList.get(0).getUserId()).isEqualTo(1L),
                () -> assertThat(userList.get(userList.size() - 1).getUserId()).isEqualTo(3L)
        );
    }

    @Test
    void findByEmail_shouldReturnOptionalUserIfFoundEmail_Test() {
        Optional<User> findUser = userRepositoryImpl.findByEmail(existEmail);

        assertThat(findUser.isPresent()).isTrue();

        assertAll(
                () -> assertThat(findUser.get().getEmail()).isEqualTo(existEmail),
                () -> assertThat(findUser.get().getRole()).isEqualTo(Role.ADMIN),
                () -> assertThat(findUser.get().getUserId()).isEqualTo(existId)
        );
    }

    @Test
    void findByEmail_shouldReturnOptionalEmptyFoundNotFoundEmail_Test() {
        Optional<User> findUser = userRepositoryImpl.findByEmail(nonExistEmail);
        assertThat(findUser.isPresent()).isFalse();
    }

    @Test
    void isEmailUnique_shouldReturnFalseIfEmailNotUnique_Test() {
        User foundUser = userRepositoryImpl.findById(existId).get();
        assertThat(userRepositoryImpl.isEmailUnique(foundUser.getEmail())).isFalse();
    }

    @Test
    void isEmailUnique_shouldReturnTrueIfEmailUnique_Test() {
        assertThat(userRepositoryImpl.isEmailUnique(nonExistEmail)).isTrue();
    }
}