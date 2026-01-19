package me.oldboy.market.services;

import me.oldboy.market.config.connection.DbConnectionPool;
import me.oldboy.market.config.liquibase.LiquibaseManager;
import me.oldboy.market.config.utils.ConfigProvider;
import me.oldboy.market.config.utils.PropertiesReader;
import me.oldboy.market.entity.User;
import me.oldboy.market.exceptions.ServiceLayerException;
import me.oldboy.market.exceptions.UnexpectedIdServiceLayerException;
import me.oldboy.market.repository.UserRepositoryImpl;
import me.oldboy.market.test_container.PostgresTestContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceImplTest extends PostgresTestContainer {
    private UserRepositoryImpl userRepositoryImpl;
    private UserServiceImpl userService;
    private Connection connection;
    private DbConnectionPool connectionPool;
    private LiquibaseManager liquibaseManager;
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
        userService = new UserServiceImpl(userRepositoryImpl);

        /* Предварительные тестовые данные */
        existId = 1L;
        nonExistId = 100L;

        existEmail = "admin@admin.ru";
        nonExistEmail = "non_exist@email.com";
    }

    @AfterEach
    public void resetTestBase() {
        liquibaseManager.rollbackCreatedTables(connection);
    }

    /* Основные тесты для реализаций методов */

    @Test
    void findById_shouldReturnFoundUser_Test() {
        assertThat(userService.findById(existId)).isNotNull();
    }

    @Test
    void findById_shouldReturnExceptionNotFoundUserId_Test() {
        assertThatThrownBy(() -> userService.findById(nonExistId))
                .isInstanceOf(UnexpectedIdServiceLayerException.class)
                .hasMessageContaining("User with ID - " + nonExistId + " not found");
    }

    @Test
    void findAll_shouldReturnAllUserList_Test() {
        assertThat(userService.findAll().size()).isEqualTo(3);
    }

    @Test
    void getUserByEmail_shouldReturnFoundUser_Test() {
        User foundUser = userService.getUserByEmail(existEmail);
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo(existEmail);
    }

    @Test
    void getUserByEmail_shouldReturnException_Test() {
        assertThatThrownBy(() -> userService.getUserByEmail(nonExistEmail))
                .isInstanceOf(ServiceLayerException.class)
                .hasMessageContaining("User with email - " + nonExistEmail + " not found");
    }

    @Test
    void isEmailUnique_shouldReturnFalse_Test() {
        assertThat(userService.isEmailUnique(existEmail)).isFalse();
    }

    @Test
    void isEmailUnique_shouldReturnTrue_Test() {
        assertThat(userService.isEmailUnique(nonExistEmail)).isTrue();
    }
}