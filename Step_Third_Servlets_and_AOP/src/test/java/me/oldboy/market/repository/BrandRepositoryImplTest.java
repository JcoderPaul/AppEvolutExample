package me.oldboy.market.repository;

import me.oldboy.market.config.connection.DbConnectionPool;
import me.oldboy.market.config.liquibase.LiquibaseManager;
import me.oldboy.market.config.utils.ConfigProvider;
import me.oldboy.market.config.utils.PropertiesReader;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.exceptions.RepositoryLayerException;
import me.oldboy.market.test_container.PostgresTestContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class BrandRepositoryImplTest extends PostgresTestContainer {
    private BrandRepositoryImpl brandRepository;

    private Connection connection;
    private DbConnectionPool connectionPool;

    private LiquibaseManager liquibaseManager;
    private Brand createNewBrand, updateBrand;
    private Integer existId, nonExistId;
    private String existName, nonExistName;

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

        brandRepository = new BrandRepositoryImpl(connectionPool);

        /* Предварительные тестовые данные */
        existId = 1;
        nonExistId = 100;

        existName = "Puma";
        nonExistName = "Abiboss";

        createNewBrand = Brand.builder()
                .name("JBL")
                .build();

        updateBrand = Brand.builder()
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
    void create_shouldReturnCreatedBrand_Test() {
        Optional<Brand> mayBeCreate = brandRepository.create(createNewBrand);

        assertThat(mayBeCreate.isPresent()).isTrue();

        assertAll(
                () -> assertThat(mayBeCreate.get().getName()).isEqualTo(createNewBrand.getName()),
                () -> assertThat(mayBeCreate.get().getId()).isEqualTo(5) // Мы помним текущую емкость таблицы
        );
    }

    @Test
    void findById_shouldReturnBrandIfFoundIt_Test() {
        Optional<Brand> foundSomething = brandRepository.findById(existId);

        assertThat(foundSomething.isPresent()).isTrue();

        assertAll(
                () -> assertThat(foundSomething.get().getId()).isEqualTo(existId),
                () -> assertThat(foundSomething.get().getName()).isEqualTo("Puma")
        );
    }

    @Test
    void findById_shouldReturnOptionalEmptyForNotExistId_Test() {
        Optional<Brand> foundAnything = brandRepository.findById(nonExistId);
        assertThat(foundAnything.isEmpty()).isTrue();
    }

    @Test
    void update_shouldReturnTrueAfterUpdate_Test() {
        Boolean isUpdateGood = brandRepository.update(updateBrand);

        assertThat(isUpdateGood).isTrue();

        Optional<Brand> mayBeFound = brandRepository.findById(existId);

        if (mayBeFound.isPresent()) {
            assertAll(
                    () -> assertThat(updateBrand.getId()).isEqualTo(mayBeFound.get().getId()),
                    () -> assertThat(updateBrand.getName()).isEqualTo(mayBeFound.get().getName())
            );
        }
    }

    @Test
    void delete_shouldReturnTrueAfterDelete_Test() {
        Optional<Brand> mayBeCreate = brandRepository.create(createNewBrand);
        if (mayBeCreate.isPresent()) {
            Integer sizeOfListBeforeDelete = brandRepository.findAll().size();

            boolean isDeleteGood = brandRepository.delete(mayBeCreate.get().getId());
            assertThat(isDeleteGood).isTrue();

            Integer sizeOfListAfterDelete = brandRepository.findAll().size();
            assertThat(sizeOfListBeforeDelete).isGreaterThan(sizeOfListAfterDelete);
        }
    }

    @Test
    void delete_shouldReturnException_brandCanNotDeleteUsingBrand_Test() {
        assertThatThrownBy(() -> brandRepository.delete(existId))
                .isInstanceOf(RepositoryLayerException.class)
                .hasMessageContaining("Database operation failed during deleting brand");
    }

    @Test
    void findAll_shouldReturnAllBrandList_Test() {
        List<Brand> brandList = brandRepository.findAll();

        assertThat(brandList.size()).isEqualTo(4);

        assertAll(
                () -> assertThat(brandList.get(0).getId()).isEqualTo(1),
                () -> assertThat(brandList.get(brandList.size() - 1).getId()).isEqualTo(4)
        );
    }

    @Test
    void findByName_shouldReturnOptionalBrandIfFoundName_Test() {
        Optional<Brand> foundBrand = brandRepository.findByName(existName);

        assertThat(foundBrand.isPresent()).isTrue();

        assertAll(
                () -> assertThat(foundBrand.get().getName()).isEqualTo(existName),
                () -> assertThat(foundBrand.get().getId()).isEqualTo(1)
        );
    }

    @Test
    void findByName_shouldReturnOptionalEmpty_ifNotFoundBrandName_Test() {
        Optional<Brand> foundBrand = brandRepository.findByName(nonExistName);
        assertThat(foundBrand.isEmpty()).isTrue();
    }
}