package me.oldboy.market.repository;

import me.oldboy.market.config.connection.ConnectionManager;
import me.oldboy.market.config.liquibase.LiquibaseManager;
import me.oldboy.market.config.utils.ConfigProvider;
import me.oldboy.market.config.utils.PropertiesReader;
import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.test_container.PostgresTestContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class CategoryRepositoryTest extends PostgresTestContainer {

    private CategoryRepository categoryRepository;
    private Connection connection;
    private LiquibaseManager liquibaseManager;
    private Category createNewCategory, updateCategory;
    private Integer existId, nonExistId;
    private String existName, nonExistName;

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

        /* Предварительные тестовые данные */
        existId = 1;
        nonExistId = 100;

        existName = "Обувь";
        nonExistName = "Инструменты";

        createNewCategory = Category.builder()
                .name(nonExistName)
                .build();

        updateCategory = Category.builder()
                .id(existId)
                .name(nonExistName)
                .build();
    }

    @AfterEach
    public void resetTestBase() {
        liquibaseManager.rollbackCreatedTables(connection);
    }

    /* Основные тесты для реализаций методов */

    @Test
    void create_shouldReturnCreatedCategory_Test() {
        Optional<Category> mayBeCreated = categoryRepository.create(createNewCategory);

        assertThat(mayBeCreated.isPresent()).isTrue();

        assertAll(
                () -> assertThat(mayBeCreated.get().getName()).isEqualTo(createNewCategory.getName()),
                () -> assertThat(mayBeCreated.get().getId()).isEqualTo(4)
        );
    }

    @Test
    void findById_shouldReturnCategoryIfFoundIt_Test() {
        Optional<Category> foundSomething = categoryRepository.findById(existId);

        assertThat(foundSomething.isPresent()).isTrue();

        assertAll(
                () -> assertThat(foundSomething.get().getId()).isEqualTo(existId),
                () -> assertThat(foundSomething.get().getName()).isEqualTo("Обувь")
        );
    }

    @Test
    void findById_shouldReturnOptionalEmptyForNotExistId_Test() {
        Optional<Category> foundAnything = categoryRepository.findById(nonExistId);
        assertThat(foundAnything.isEmpty()).isTrue();
    }

    @Test
    void update_shouldReturnTrueAfterUpdate_Test() {
        Boolean isUpdateGood = categoryRepository.update(updateCategory);

        assertThat(isUpdateGood).isTrue();

        Optional<Category> mayBeFound = categoryRepository.findById(existId);

        if (mayBeFound.isPresent()) {
            assertAll(
                    () -> assertThat(updateCategory.getId()).isEqualTo(mayBeFound.get().getId()),
                    () -> assertThat(updateCategory.getName()).isEqualTo(mayBeFound.get().getName())
            );
        }
    }

    @Test
    void delete_shouldReturnTrueAfterDelete_Test() {
        Optional<Category> mayBeCreate = categoryRepository.create(createNewCategory);
        if (mayBeCreate.isPresent()) {
            Integer sizeOfListBeforeDelete = categoryRepository.findAll().size();

            boolean isDeleteGood = categoryRepository.delete(mayBeCreate.get().getId());
            assertThat(isDeleteGood).isTrue();

            Integer sizeOfListAfterDelete = categoryRepository.findAll().size();
            assertThat(sizeOfListBeforeDelete).isGreaterThan(sizeOfListAfterDelete);
        }
    }

    @Test
    void delete_shouldReturnFalseAfterDelete_catchAndHandleException_Test() {
        Integer sizeOfListBeforeDelete = categoryRepository.findAll().size();

        boolean isDeleteGood = categoryRepository.delete(existId);
        assertThat(isDeleteGood).isFalse();

        Integer sizeOfListAfterDelete = categoryRepository.findAll().size();
        assertThat(sizeOfListBeforeDelete).isEqualTo(sizeOfListAfterDelete);
    }

    @Test
    void findAll_shouldReturnAllFoundElementList_Test() {
        List<Category> categoryList = categoryRepository.findAll();

        assertThat(categoryList.size()).isEqualTo(3);

        assertAll(
                () -> assertThat(categoryList.get(0).getId()).isEqualTo(1),
                () -> assertThat(categoryList.get(categoryList.size() - 1).getId()).isEqualTo(3)
        );
    }

    @Test
    void findByName_shouldReturnOptionalCategoryIfFoundItByName_Test() {
        Optional<Category> foundElement = categoryRepository.findByName(existName);

        assertThat(foundElement.isPresent()).isTrue();

        assertAll(
                () -> assertThat(foundElement.get().getName()).isEqualTo(existName),
                () -> assertThat(foundElement.get().getId()).isEqualTo(1)
        );
    }

    @Test
    void findByName_shouldReturnOptionalEmpty_ifNotFoundCategoryByName_Test() {
        Optional<Category> notFoundRecord = categoryRepository.findByName(nonExistName);
        assertThat(notFoundRecord.isEmpty()).isTrue();
    }
}