package me.oldboy.market.services;

import me.oldboy.market.config.connection.ConnectionManager;
import me.oldboy.market.config.liquibase.LiquibaseManager;
import me.oldboy.market.config.utils.ConfigProvider;
import me.oldboy.market.config.utils.PropertiesReader;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.repository.BrandRepository;
import me.oldboy.market.repository.CategoryRepository;
import me.oldboy.market.test_container.PostgresTestContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BrandServiceImplTest extends PostgresTestContainer {

    private BrandRepository brandRepository;
    private BrandServiceImpl brandService;
    private Connection connection;
    private LiquibaseManager liquibaseManager;
    private Integer existId, nonExistId;
    private String existBrandName, nonExistBrandName;

    @BeforeEach
    public void getConnectionToTestBaseAndInitIt() {
        connection = ConnectionManager.getTestBaseConnection(postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword()
        );

        ConfigProvider configProvider = new PropertiesReader();
        liquibaseManager = LiquibaseManager.getInstance(configProvider);
        liquibaseManager.migrationsStart(connection);

        brandRepository = new BrandRepository(connection);
        brandService = new BrandServiceImpl(brandRepository);

        /* Предварительные тестовые данные */
        existId = 1;
        nonExistId = 100;

        existBrandName = "Puma";
        nonExistBrandName = "Kocherizky";
    }

    @AfterEach
    public void resetTestBase() {
        liquibaseManager.rollbackCreatedTables(connection);
    }

    /* Основные тесты для реализаций методов */

    @Test
    void findById_shouldReturnFoundBrand_Test() {
        Brand foundBrand = brandService.findById(existId);
        assertThat(foundBrand).isNotNull();
        assertThat(foundBrand.getName()).isEqualTo(existBrandName);
    }

    @Test
    void findById_shouldReturnNull_notFoundBrand_Test() {
        assertThat(brandService.findById(nonExistId)).isNull();
    }

    @Test
    void findAll_shouldReturnAllBrandList_Test() {
        assertThat(brandService.findAll().size()).isEqualTo(4);
    }

    @Test
    void findByName_shouldReturnFoundBrand_existName_Test() {
        Brand foundBrand = brandService.findByName(existBrandName);
        assertThat(foundBrand).isNotNull();
        assertThat(foundBrand.getId()).isEqualTo(existId);
    }

    @Test
    void findByName_shouldReturnNull_notExistName_Test() {
        assertThat(brandService.findByName(nonExistBrandName)).isNull();
    }
}