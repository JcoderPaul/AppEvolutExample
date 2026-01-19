package me.oldboy.market.repository;

import me.oldboy.market.config.connection.DbConnectionPool;
import me.oldboy.market.config.liquibase.LiquibaseManager;
import me.oldboy.market.config.utils.ConfigProvider;
import me.oldboy.market.config.utils.PropertiesReader;
import me.oldboy.market.entity.Product;
import me.oldboy.market.test_container.PostgresTestContainer;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ProductRepositoryImplTest extends PostgresTestContainer {
    private ProductRepositoryImpl productRepository;
    private Connection connection;
    private DbConnectionPool connectionPool;
    private LiquibaseManager liquibaseManager;
    private Product createNewProduct, updateProduct;
    private Long existId, nonExistId;

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

        productRepository = new ProductRepositoryImpl(connectionPool);

        /* Предварительные тестовые данные */
        existId = 1L;
        nonExistId = 100L;

        /*
        Для того чтобы не было временного проскальзывания при тестировании
        "обрежем" наносекунды, иначе расхождение в наносекунды при создании
        объекта валит тест.
        */
        createNewProduct = Product.builder()
                .name("Парус")
                .price(324.5)
                .categoryId(2)
                .brandId(3)
                .description("Парус солнечный")
                .stockQuantity(4)
                .creationAt(LocalDateTime.now().withNano(0))
                .modifiedAt(LocalDateTime.now().plusDays(1).withNano(0))
                .build();

        /*
        Помним, с прошлого шага, обновляемый по ID товар не может сменить
        категорию и брэнд, за этим будет следить сервисный или контрольный
        слой, но для интересов теста мы делаем полное обновление.
        */
        updateProduct = Product.builder()
                .id(existId)
                .name("Парус")
                .price(324.5)
                .categoryId(2)
                .brandId(3)
                .description("Парус солнечный")
                .stockQuantity(4)
                .creationAt(LocalDateTime.now().withNano(0))
                .modifiedAt(LocalDateTime.now().plusDays(1).withNano(0))
                .build();
    }

    @AfterEach
    public void resetTestBase() {
        liquibaseManager.rollbackCreatedTables(connection);
    }

    /* Основные тесты для реализаций методов */

    @Test
    void create_shouldReturnCreatedBrand_Test() {
        Optional<Product> mayBeCreate = productRepository.create(createNewProduct);

        assertThat(mayBeCreate.isPresent()).isTrue();

        assertAll(
                () -> assertThat(mayBeCreate.get().getName()).isEqualTo(createNewProduct.getName()),
                () -> assertThat(mayBeCreate.get().getPrice()).isEqualTo(createNewProduct.getPrice()),
                () -> assertThat(mayBeCreate.get().getCategoryId()).isEqualTo(createNewProduct.getCategoryId()),
                () -> assertThat(mayBeCreate.get().getBrandId()).isEqualTo(createNewProduct.getBrandId()),
                () -> assertThat(mayBeCreate.get().getDescription()).isEqualTo(createNewProduct.getDescription()),
                () -> assertThat(mayBeCreate.get().getStockQuantity()).isEqualTo(createNewProduct.getStockQuantity()),
                () -> assertThat(mayBeCreate.get().getCreationAt()).isEqualTo(createNewProduct.getCreationAt()),
                () -> assertThat(mayBeCreate.get().getModifiedAt()).isEqualTo(createNewProduct.getModifiedAt()),
                () -> assertThat(mayBeCreate.get().getId()).isEqualTo(4) // Мы помним текущую емкость таблицы
        );
    }

    @Nested
    @DisplayName("Набор тестов для *.findById() метод в классе ProductRepositoryImpl")
    class FindByIdMethodTests {
        @Test
        void findById_shouldReturnProductIfFoundIt_Test() {
            Optional<Product> foundSomething = productRepository.findById(existId);

            assertThat(foundSomething.isPresent()).isTrue();
            assertThat(foundSomething.get().getId()).isEqualTo(existId);
        }

        @Test
        void findById_shouldReturnOptionalEmptyForNotExistId_Test() {
            Optional<Product> noFoundAnything = productRepository.findById(nonExistId);
            assertThat(noFoundAnything.isEmpty()).isTrue();
        }
    }

    @Test
    void update_shouldReturnTrueAfterUpdate_Test() {
        Boolean isUpdateGood = productRepository.update(updateProduct);

        assertThat(isUpdateGood).isTrue();

        Optional<Product> mayBeFound = productRepository.findById(existId);

        if (mayBeFound.isPresent()) {
            assertAll(
                    () -> assertThat(mayBeFound.get().getId()).isEqualTo(existId),
                    () -> assertThat(existId).isEqualTo(updateProduct.getId()),
                    () -> assertThat(mayBeFound.get().getId()).isEqualTo(updateProduct.getId()),
                    () -> assertThat(mayBeFound.get().getName()).isEqualTo(updateProduct.getName()),
                    () -> assertThat(mayBeFound.get().getPrice()).isEqualTo(updateProduct.getPrice()),
                    () -> assertThat(mayBeFound.get().getCategoryId()).isEqualTo(updateProduct.getCategoryId()),
                    () -> assertThat(mayBeFound.get().getBrandId()).isEqualTo(updateProduct.getBrandId()),
                    () -> assertThat(mayBeFound.get().getDescription()).isEqualTo(updateProduct.getDescription()),
                    () -> assertThat(mayBeFound.get().getStockQuantity()).isEqualTo(updateProduct.getStockQuantity()),
                    () -> assertThat(mayBeFound.get().getCreationAt()).isEqualTo(updateProduct.getCreationAt()),
                    () -> assertThat(mayBeFound.get().getModifiedAt()).isEqualTo(updateProduct.getModifiedAt())
            );
        }
    }

    @Nested
    @DisplayName("Набор тестов для *.delete() метод в классе ProductRepositoryImpl")
    class DeleteMethodTests {
        @Test
        void delete_shouldReturnTrueAfterDelete_Test() {
            Integer sizeOfListBeforeDelete = productRepository.findAll().size();

            boolean isDeleteGood = productRepository.delete(existId);
            assertThat(isDeleteGood).isTrue();

            Integer sizeOfListAfterDelete = productRepository.findAll().size();
            assertThat(sizeOfListBeforeDelete).isGreaterThan(sizeOfListAfterDelete);
        }

        @Test
        void delete_shouldReturnFalseAfterDelete_Test() {
            Integer sizeOfListBeforeDelete = productRepository.findAll().size();

            boolean isDeleteGood = productRepository.delete(nonExistId);
            assertThat(isDeleteGood).isFalse();

            Integer sizeOfListAfterDelete = productRepository.findAll().size();
            assertThat(sizeOfListBeforeDelete).isEqualTo(sizeOfListAfterDelete);
        }
    }

    @Test
    void findAll_shouldReturnAllBrandList_Test() {
        List<Product> auditList = productRepository.findAll();

        assertThat(auditList.size()).isEqualTo(3);

        assertAll(
                () -> assertThat(auditList.get(0).getId()).isEqualTo(1L),
                () -> assertThat(auditList.get(auditList.size() - 1).getId()).isEqualTo(3L)
        );
    }

    @Nested
    @DisplayName("Набор тестов для *.findByCategoryId() метода в классе ProductRepositoryImpl")
    class FindByCategoryIdMethodTests {
        @Test
        void findByCategoryId_shouldReturnProductListFoundByCategory_Test() {
            int categoryId = Math.toIntExact(existId);

            Optional<List<Product>> byCategoryList_1 = productRepository.findByCategoryId(categoryId);
            assertThat(byCategoryList_1.get().size()).isEqualTo(1);

            createNewProduct.setCategoryId(categoryId);
            productRepository.create(createNewProduct);

            Optional<List<Product>> byCategoryList_2 = productRepository.findByCategoryId(categoryId);
            assertThat(byCategoryList_2.get().size()).isEqualTo(2);
        }

        @Test
        void findByCategoryId_shouldReturnEmptyList_forNotExistCategory_Test() {
            int categoryId = Math.toIntExact(nonExistId);

            Optional<List<Product>> byCategoryList = productRepository.findByCategoryId(categoryId);
            assertThat(byCategoryList.get().size()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Набор тестов для *.findByBrandId() метода в классе ProductRepositoryImpl")
    class FindByBrandIdMethodTests {
        @Test
        void findByBrandId_shouldReturnProductListFoundByBrand_Test() {
            int brandId = Math.toIntExact(existId);

            Optional<List<Product>> byBrandList_1 = productRepository.findByBrandId(brandId);
            assertThat(byBrandList_1.get().size()).isEqualTo(1);

            createNewProduct.setBrandId(brandId);
            productRepository.create(createNewProduct);

            Optional<List<Product>> byBrandList_2 = productRepository.findByBrandId(brandId);
            assertThat(byBrandList_2.get().size()).isEqualTo(2);
        }

        @Test
        void findByBrandId_shouldReturnEmptyList_forNotExistBrand_Test() {
            int brandId = Math.toIntExact(nonExistId);

            Optional<List<Product>> byBrandList = productRepository.findByBrandId(brandId);
            assertThat(byBrandList.get().size()).isEqualTo(0);
        }
    }
}