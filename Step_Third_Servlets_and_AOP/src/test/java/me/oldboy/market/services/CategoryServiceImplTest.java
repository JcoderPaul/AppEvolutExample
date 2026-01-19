package me.oldboy.market.services;

import me.oldboy.market.config.connection.DbConnectionPool;
import me.oldboy.market.config.liquibase.LiquibaseManager;
import me.oldboy.market.config.utils.ConfigProvider;
import me.oldboy.market.config.utils.PropertiesReader;
import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.repository.CategoryRepositoryImpl;
import me.oldboy.market.test_container.PostgresTestContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryServiceImplTest extends PostgresTestContainer {
    private CategoryRepositoryImpl categoryRepository;
    private CategoryServiceImpl categoryService;
    private Connection connection;
    private DbConnectionPool connectionPool;
    private LiquibaseManager liquibaseManager;
    private Integer existId, nonExistId;
    private String existCategoryName, nonExistCategoryName;

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

        categoryRepository = new CategoryRepositoryImpl(connectionPool);
        categoryService = new CategoryServiceImpl(categoryRepository);

        /* Предварительные тестовые данные */
        existId = 1;
        nonExistId = 100;

        existCategoryName = "Обувь";
        nonExistCategoryName = "Кочерыжки";
    }

    @AfterEach
    public void resetTestBase() {
        liquibaseManager.rollbackCreatedTables(connection);
    }

    /* Основные тесты для реализаций методов */

    @Test
    void findById_shouldReturnFoundCategory_Test() {
        Category foundCategory = categoryService.findById(existId);
        assertThat(foundCategory).isNotNull();
        assertThat(foundCategory.getName()).isEqualTo(existCategoryName);
    }

    @Test
    void findById_shouldReturnNull_notFoundCategory_Test() {
        assertThat(categoryService.findById(nonExistId)).isNull();
    }

    @Test
    void findAll_shouldReturnAllCategoryList_Test() {
        assertThat(categoryService.findAll().size()).isEqualTo(3);
    }

    @Test
    void findByName_shouldReturnFoundCategory_existName_Test() {
        Category foundCategory = categoryService.findByName(existCategoryName);
        assertThat(foundCategory).isNotNull();
        assertThat(foundCategory.getId()).isEqualTo(existId);
    }

    @Test
    void findByName_shouldReturnNull_notExistName_Test() {
        assertThat(categoryService.findByName(nonExistCategoryName)).isNull();
    }
}