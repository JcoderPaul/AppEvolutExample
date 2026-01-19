package me.oldboy.market.services;

import me.oldboy.market.config.connection.DbConnectionPool;
import me.oldboy.market.config.liquibase.LiquibaseManager;
import me.oldboy.market.config.utils.ConfigProvider;
import me.oldboy.market.config.utils.PropertiesReader;
import me.oldboy.market.entity.Product;
import me.oldboy.market.exceptions.ServiceLayerException;
import me.oldboy.market.repository.BrandRepositoryImpl;
import me.oldboy.market.repository.CategoryRepositoryImpl;
import me.oldboy.market.repository.ProductRepositoryImpl;
import me.oldboy.market.test_container.PostgresTestContainer;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductServiceImplTest extends PostgresTestContainer {
    private ProductRepositoryImpl productRepository;
    private CategoryRepositoryImpl categoryRepository;
    private BrandRepositoryImpl brandRepository;
    private ProductServiceImpl productService;
    private Connection connection;
    private DbConnectionPool connectionPool;
    private LiquibaseManager liquibaseManager;
    private Long existId, nonExistId;
    private String existProductName, nonExistProductName;

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
        categoryRepository = new CategoryRepositoryImpl(connectionPool);
        brandRepository = new BrandRepositoryImpl(connectionPool);

        productService = new ProductServiceImpl(productRepository, categoryRepository, brandRepository);

        /* Предварительные тестовые данные */
        existId = 1L;
        nonExistId = 100L;

        existProductName = "Парка";
        nonExistProductName = "Ледоруб";
    }

    @AfterEach
    public void resetTestBase() {
        liquibaseManager.rollbackCreatedTables(connection);
    }

    /* Основные тесты для реализаций методов */

    @Test
    void findAll_shouldReturnProductList_Test() {
        assertThat(productService.findAll().size()).isEqualTo(3);
    }

    @Test
    void findById_shouldReturnFoundProduct_Test() {
        Product foundProduct = productService.findById(existId);
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getId()).isEqualTo(existId);
        assertThat(foundProduct.getName()).isEqualTo(existProductName);
    }

    @Test
    void findById_shouldReturnNull_notFoundProduct_Test() {
        assertThat(productService.findById(nonExistId)).isNull();
    }

    @Test
    void isProductNameUnique_shouldReturnFalse_forExistProductName_Test() {
        assertThat(productService.isProductNameUnique(existProductName)).isFalse();
    }

    @Test
    void isProductNameUnique_shouldReturnTrue_forNonExistentProductName_Test() {
        assertThat(productService.isProductNameUnique(nonExistProductName)).isTrue();
    }

    @Nested
    @DisplayName("Блок тестов на *.create() метод ProductService")
    class CreateMethodOnProductServiceTests {
        @Test
        void create_shouldReturnCreatedProductWithId_Test() {
            /* Для целей сохранения продукт "прилетает" со слоя "контроллеров" полностью сформированным */
            Product productDataForSave = Product.builder()
                    .name(nonExistProductName)
                    .price(23.5)
                    .categoryId(1)
                    .brandId(2)
                    .description("Описание")
                    .stockQuantity(4)
                    .creationAt(LocalDateTime.now())
                    .modifiedAt(LocalDateTime.now().plusSeconds(1))
                    .build();

            Product createdProductWithId = productService.create(productDataForSave);
            assertThat(createdProductWithId).isNotNull();
            assertThat(createdProductWithId.getId()).isEqualTo(4);
        }

        @Test
        void create_shouldReturnException_notUniqueName_Test() {
            Product productDataForSave = Product.builder()
                    .name(existProductName)
                    .price(23.5)
                    .categoryId(Math.toIntExact(existId))
                    .brandId(Math.toIntExact(existId))
                    .description("Описание")
                    .stockQuantity(4)
                    .build();

            assertThatThrownBy(() -> productService.create(productDataForSave))
                    .isInstanceOf(ServiceLayerException.class)
                    .hasMessageContaining("Имя продукта" + productDataForSave.getName() + " не уникально, сохранение товара не возможно");
        }

        @Test
        void create_shouldReturnException_notExistCategory_Test() {
            Product productDataForSave = Product.builder()
                    .name(nonExistProductName)
                    .price(23.5)
                    .categoryId(Math.toIntExact(nonExistId))
                    .brandId(Math.toIntExact(existId))
                    .description("Описание")
                    .stockQuantity(4)
                    .build();

            assertThatThrownBy(() -> productService.create(productDataForSave))
                    .isInstanceOf(ServiceLayerException.class)
                    .hasMessageContaining("Категория с ID - " + Math.toIntExact(nonExistId) + " не найдена, сохранение товара не возможно");
        }

        @Test
        void create_shouldReturnException_notExistBrand_Test() {
            Product productDataForSave = Product.builder()
                    .name(nonExistProductName)
                    .price(23.5)
                    .categoryId(Math.toIntExact(existId))
                    .brandId(Math.toIntExact(nonExistId))
                    .description("Описание")
                    .stockQuantity(4)
                    .build();

            assertThatThrownBy(() -> productService.create(productDataForSave))
                    .isInstanceOf(ServiceLayerException.class)
                    .hasMessageContaining("Брэнд с ID - " + Math.toIntExact(nonExistId) + " не найден, сохранение товара не возможно");
        }
    }

    @Nested
    @DisplayName("Блок тестов на *.update() метод ProductService")
    class UpdateMethodOnProductServiceTests {
        @Test
        void update_shouldReturnTrueAfterUpdate_Test() {
            Product productDataForUpdate = Product.builder()
                    .id(existId)
                    .name(nonExistProductName)
                    .price(23.5)
                    .description("Описание")
                    .stockQuantity(4)
                    .build();

            boolean isUpdated = productService.update(productDataForUpdate);
            assertThat(isUpdated).isTrue();

            Product afterUpdateProduct = productService.findById(existId);

            assertThat(afterUpdateProduct.getName()).isEqualTo(productDataForUpdate.getName());
            assertThat(afterUpdateProduct.getPrice()).isEqualTo(productDataForUpdate.getPrice());
            assertThat(afterUpdateProduct.getDescription()).isEqualTo(productDataForUpdate.getDescription());
            assertThat(afterUpdateProduct.getStockQuantity()).isEqualTo(productDataForUpdate.getStockQuantity());
        }

        @Test
        void update_shouldReturnException_productIdNotFound_Test() {
            Product productDataForUpdate = Product.builder()
                    .id(nonExistId)
                    .name(nonExistProductName)
                    .price(23.5)
                    .description("Описание")
                    .stockQuantity(4)
                    .build();

            assertThatThrownBy(() -> productService.update(productDataForUpdate))
                    .isInstanceOf(ServiceLayerException.class)
                    .hasMessageContaining("Не найден ID - " + productDataForUpdate.getId() + " продукта, обновление невозможно");
        }

        @Test
        void update_shouldReturnException_notUniqueProductName_Test() {
            Product productDataForUpdate = Product.builder()
                    .id(existId)
                    .name(existProductName)
                    .price(23.5)
                    .description("Описание")
                    .stockQuantity(4)
                    .build();

            assertThatThrownBy(() -> productService.update(productDataForUpdate))
                    .isInstanceOf(ServiceLayerException.class)
                    .hasMessageContaining("Имя продукта " + productDataForUpdate.getName() + " не уникально, обновление невозможно");
        }
    }

    @Nested
    @DisplayName("Блок тестов на *.delete() метод ProductService")
    class DeleteMethodOnProductServiceTests {
        @Test
        void delete_shouldReturnTrue_afterSuccessRemove_Test() {
            assertThat(productService.findAll().size()).isEqualTo(3);
            assertThat(productService.delete(existId)).isTrue();
            assertThat(productService.findAll().size()).isEqualTo(2);
        }

        @Test
        void delete_shouldReturnFalse_withNotExistProductId_Test() {
            assertThat(productService.findAll().size()).isEqualTo(3);
            assertThat(productService.delete(nonExistId)).isFalse();
            assertThat(productService.findAll().size()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("Блок тестов на *.findProductByCategoryAndId() метод ProductService")
    class FindProductByCategoryAndIdMethodOnProductServiceTests {
        @Test
        void findProductByCategoryAndId_success_Test() {
            Product foundProduct = productService.findProductByCategoryAndId(2, existId);
            assertThat(foundProduct).isNotNull();
        }

        @Test
        void findProductByCategoryAndId_shouldReturnNull_categoryNotExist_Test() {
            Product foundProduct = productService.findProductByCategoryAndId(6, existId);
            assertThat(foundProduct).isNull();
        }

        @Test
        void findProductByCategoryAndId_shouldReturnNull_productNotExist_Test() {
            Product foundProduct = productService.findProductByCategoryAndId(2, nonExistId);
            assertThat(foundProduct).isNull();
        }
    }

    @Nested
    @DisplayName("Блок тестов на *.findProductByBrandAndId() метод ProductService")
    class FindProductByBrandAndIdMethodOnProductServiceTests {
        @Test
        void findProductByBrandAndId_success_Test() {
            Product foundProduct = productService.findProductByBrandAndId(1, 2L);
            assertThat(foundProduct).isNotNull();
        }

        @Test
        void findProductByBrandAndId_shouldReturnNull_brandNotExist_Test() {
            Product foundProduct = productService.findProductByBrandAndId(6, existId);
            assertThat(foundProduct).isNull();
        }

        @Test
        void findProductByBrandAndId_shouldReturnNull_productNotExist_Test() {
            Product foundProduct = productService.findProductByBrandAndId(1, nonExistId);
            assertThat(foundProduct).isNull();
        }
    }

    @Nested
    @DisplayName("Блок тестов на *.findProductByBrandAndName() метод ProductService")
    class FindProductByBrandAndNameMethodOnProductServiceTests {
        @Test
        void findProductByBrandAndName_success_Test() {
            Product foundProduct = productService.findProductByBrandAndName(4, existProductName);
            assertThat(foundProduct).isNotNull();
        }

        @Test
        void findProductByBrandAndName_shouldReturnNull_brandNotExist_Test() {
            Product foundProduct = productService.findProductByBrandAndName(6, existProductName);
            assertThat(foundProduct).isNull();
        }

        @Test
        void findProductByBrandAndName_shouldReturnNull_productNotExist_Test() {
            Product foundProduct = productService.findProductByBrandAndName(1, nonExistProductName);
            assertThat(foundProduct).isNull();
        }
    }

    @Nested
    @DisplayName("Блок тестов на оставшиеся *.findProductBy...() методы ProductService")
    class FindProductByPrefixMethodsOnProductServiceTests {
        @Test
        void findProductByCategory_shouldReturnFoundList_Test() {
            List<Product> foundList = productService.findProductByCategory(Math.toIntExact(existId));
            assertThat(foundList.size()).isEqualTo(1);
        }

        @Test
        void findProductByCategory_shouldReturnEmptyList_notExistCategory_Test() {
            List<Product> foundList = productService.findProductByCategory(Math.toIntExact(nonExistId));
            assertThat(foundList.size()).isEqualTo(0);
        }

        @Test
        void findProductByBrand_shouldReturnFoundList_Test() {
            List<Product> foundList = productService.findProductByBrand(Math.toIntExact(existId));
            assertThat(foundList.size()).isEqualTo(1);
        }

        @Test
        void findProductByBrand_shouldReturnEmptyList_notExistBrand_Test() {
            List<Product> foundList = productService.findProductByBrand(Math.toIntExact(nonExistId));
            assertThat(foundList.size()).isEqualTo(0);
        }
    }
}