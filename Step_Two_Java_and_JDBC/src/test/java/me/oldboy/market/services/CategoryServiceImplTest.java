package me.oldboy.market.services;

import me.oldboy.market.config.connection.ConnectionManager;
import me.oldboy.market.config.liquibase.LiquibaseManager;
import me.oldboy.market.config.utils.ConfigProvider;
import me.oldboy.market.config.utils.PropertiesReader;
import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.repository.BrandRepository;
import me.oldboy.market.repository.CategoryRepository;
import me.oldboy.market.repository.ProductRepository;
import me.oldboy.market.test_container.PostgresTestContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CategoryServiceImplTest extends PostgresTestContainer {

    private CategoryRepository categoryRepository;
    private CategoryServiceImpl categoryService;
    private Connection connection;
    private LiquibaseManager liquibaseManager;
    private Integer existId, nonExistId;
    private String existCategoryName, nonExistCategoryName;

    @BeforeEach
    public void getConnectionToTestBaseAndInitIt() {
        connection = ConnectionManager.getTestBaseConnection(postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword()
        );

        ConfigProvider configProvider = new PropertiesReader();
        liquibaseManager = LiquibaseManager.getInstance(configProvider);
        liquibaseManager.migrationsStart(connection);

        categoryRepository = new CategoryRepository(connection);
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