package me.oldboy.market.integration.services;

import me.oldboy.market.integration.TestContainerInit;
import me.oldboy.market.productmanager.core.dto.product.ProductCreateDto;
import me.oldboy.market.productmanager.core.dto.product.ProductReadDto;
import me.oldboy.market.productmanager.core.dto.product.ProductUpdateDto;
import me.oldboy.market.productmanager.core.exceptions.ProductManagerModuleException;
import me.oldboy.market.productmanager.core.services.interfaces.ProductService;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductServiceImplIT extends TestContainerInit {

    @Autowired
    private ProductService productService;
    private Long existProdId, nonExistProdId;
    private Integer existCatOrBrandId, nonExistCatOrBrandId;
    private String existProductName, nonExistProductName;

    @BeforeEach
    public void setUp() {
        /* Предварительные тестовые данные */
        existProdId = 1L;
        nonExistProdId = 100L;

        existCatOrBrandId = 1;
        nonExistCatOrBrandId = 100;

        existProductName = "Парка";
        nonExistProductName = "Ледоруб";
    }

    @Nested
    @DisplayName("Блок тестов на *.find...() методы ProductService")
    class MainFindPrefixMethodOnProductServiceTests {
        @Test
        @DisplayName("Должен вернуть список всех продуктов")
        void findAll_shouldReturnProductList_Test() {
            assertThat(productService.findAll().size()).isEqualTo(9);
        }

        @Test
        @DisplayName("Должен вернуть продукт по его ID")
        void findById_shouldReturnFoundProduct_Test() {
            Optional<ProductReadDto> foundProduct = productService.findById(existProdId);

            SoftAssertions softly = new SoftAssertions();

            softly.assertThat(foundProduct.isPresent()).isTrue();
            softly.assertThat(foundProduct.get().id()).isEqualTo(existProdId);
            softly.assertThat(foundProduct.get().name()).isEqualTo(existProductName);

            softly.assertAll();
        }

        @Test
        @DisplayName("Должен вернуть пустую запись для несуществующего ID")
        void findById_shouldReturnOptionalEmpty_notFoundProduct_Test() {
            assertThat(productService.findById(nonExistProdId).isPresent()).isFalse();
        }

        @Test
        @DisplayName("Должен вернуть false для продукта с неуникальным названием")
        void isProductNameUnique_shouldReturnFalse_forExistProductName_Test() {
            assertThat(productService.isProductNameUnique(existProductName)).isFalse();
        }

        @Test
        @DisplayName("Должен вернуть true для уникального названия продукта")
        void isProductNameUnique_shouldReturnTrue_forNonExistentProductName_Test() {
            assertThat(productService.isProductNameUnique(nonExistProductName)).isTrue();
        }
    }

    @Nested
    @DisplayName("Блок тестов на *.create() метод ProductService")
    class CreateMethodOnProductServiceTests {
        @Test
        @DisplayName("Должен вернуть созданный и сохраненный в БД товар с присвоенным ID")
        void create_shouldReturnCreatedProductWithId_Test() {
            ProductCreateDto productDataForSave = ProductCreateDto.builder()
                    .name(nonExistProductName)
                    .price(23.5)
                    .categoryId(existCatOrBrandId)
                    .brandId(existCatOrBrandId)
                    .description("Описание")
                    .stockQuantity(4)
                    .build();

            ProductReadDto createdProductWithId = productService.create(productDataForSave);

            assertThat(createdProductWithId).isNotNull();
            assertThat(createdProductWithId.id()).isGreaterThan(9);
        }

        @Test
        @DisplayName("Должен бросить ServiceLayerException - не уникальное название продукта")
        void create_shouldReturnException_notUniqueName_Test() {
            ProductCreateDto productDataForSave = ProductCreateDto.builder()
                    .name(existProductName)
                    .price(23.5)
                    .categoryId(existCatOrBrandId)
                    .brandId(existCatOrBrandId)
                    .description("Описание")
                    .stockQuantity(4)
                    .build();


            assertThatThrownBy(() -> productService.create(productDataForSave))
                    .isInstanceOf(ProductManagerModuleException.class)
                    .hasMessageContaining("Имя продукта" + productDataForSave.name() + " не уникально, сохранение товара не возможно");
        }

        @Test
        @DisplayName("Должен бросить ServiceLayerException - не найдена заданная категория")
        void create_shouldReturnException_notExistCategory_Test() {
            ProductCreateDto productDataForSave = ProductCreateDto.builder()
                    .name(nonExistProductName)
                    .price(23.5)
                    .categoryId(nonExistCatOrBrandId)
                    .brandId(existCatOrBrandId)
                    .description("Описание")
                    .stockQuantity(4)
                    .build();


            assertThatThrownBy(() -> productService.create(productDataForSave))
                    .isInstanceOf(ProductManagerModuleException.class)
                    .hasMessageContaining("Категория с ID - " + nonExistCatOrBrandId + " не найдена, сохранение товара не возможно");
        }

        @Test
        @DisplayName("Должен бросить ServiceLayerException - не найден выбранный брэнд")
        void create_shouldReturnException_notExistBrand_Test() {
            ProductCreateDto productDataForSave = ProductCreateDto.builder()
                    .name(nonExistProductName)
                    .price(23.5)
                    .categoryId(existCatOrBrandId)
                    .brandId(nonExistCatOrBrandId)
                    .description("Описание")
                    .stockQuantity(4)
                    .build();

            assertThatThrownBy(() -> productService.create(productDataForSave))
                    .isInstanceOf(ProductManagerModuleException.class)
                    .hasMessageContaining("Брэнд с ID - " + nonExistCatOrBrandId + " не найден, сохранение товара не возможно");
        }
    }

    @Nested
    @DisplayName("Блок тестов на *.update() метод ProductService")
    class UpdateMethodOnProductServiceTests {
        @Test
        @DisplayName("Должен вернуть данные обновленного продукта соответствующие заданным при обновлении")
        void update_shouldReturnUpdatedProduct_afterUpdate_Test() {
            ProductUpdateDto productDataForUpdate = ProductUpdateDto.builder()
                    .id(existProdId)
                    .name(nonExistProductName)
                    .price(23.5)
                    .description("Новое описание")
                    .stockQuantity(4)
                    .build();

            ProductReadDto updatedProduct = productService.update(productDataForUpdate);

            SoftAssertions softly = new SoftAssertions();

            softly.assertThat(updatedProduct).isNotNull();
            softly.assertThat(updatedProduct.name()).isEqualTo(productDataForUpdate.name());
            softly.assertThat(updatedProduct.price()).isEqualTo(productDataForUpdate.price());
            softly.assertThat(updatedProduct.description()).isEqualTo(productDataForUpdate.description());
            softly.assertThat(updatedProduct.stockQuantity()).isEqualTo(productDataForUpdate.stockQuantity());

            softly.assertAll();
        }

        @Test
        @DisplayName("Должен бросить ServiceLayerException - не найден ID продукта для обновления")
        void update_shouldReturnException_productIdNotFound_Test() {
            ProductUpdateDto productDataForUpdate = ProductUpdateDto.builder()
                    .id(nonExistProdId)
                    .name(nonExistProductName)
                    .price(23.5)
                    .description("Новое описание")
                    .stockQuantity(4)
                    .build();

            assertThatThrownBy(() -> productService.update(productDataForUpdate))
                    .isInstanceOf(ProductManagerModuleException.class)
                    .hasMessageContaining("Не найден ID - " + nonExistProdId + " продукта, обновление невозможно");
        }

        @Test
        @DisplayName("Должен бросить ServiceLayerException - не уникальное название продукта заданное при обновлении")
        void update_shouldReturnException_notUniqueProductName_Test() {
            ProductUpdateDto productDataForUpdate = ProductUpdateDto.builder()
                    .id(existProdId)
                    .name(existProductName)
                    .price(23.5)
                    .description("Новое описание")
                    .stockQuantity(4)
                    .build();

            assertThatThrownBy(() -> productService.update(productDataForUpdate))
                    .isInstanceOf(ProductManagerModuleException.class)
                    .hasMessageContaining("Имя продукта " + existProductName + " не уникально, обновление невозможно");
        }
    }

    @Nested
    @DisplayName("Блок тестов на *.delete() метод ProductService")
    class DeleteMethodOnProductServiceTests {
        @Test
        @DisplayName("Должен вернут true - подтвердить удаление, состояние БД измениться")
        void delete_shouldReturnTrue_afterSuccessRemove_Test() {
            assertThat(productService.findAll().size()).isEqualTo(9);
            assertThat(productService.delete(existProdId)).isTrue();
            assertThat(productService.findAll().size()).isEqualTo(8);
        }

        @Test
        @DisplayName("Должен бросить ServiceLayerException - ID продукта для удаления не найден")
        void delete_shouldThrownException_withNotExistProductId_Test() {
            assertThatThrownBy(() -> productService.delete(nonExistProdId))
                    .isInstanceOf(ProductManagerModuleException.class)
                    .hasMessageContaining("Not found product with ID - " + nonExistProdId + " for delete!");
        }
    }

    @Nested
    @DisplayName("Блок тестов на *.findProductByCategoryAndId() метод ProductService")
    class FindProductByCategoryAndIdMethodOnProductServiceTests {
        @Test
        @DisplayName("Должен вернуть продукт найденный по собственному ID и ID категории")
        void findProductByCategoryAndId_success_Test() {
            Optional<ProductReadDto> foundProduct = productService.findProductByCategoryAndId(2, existProdId);
            assertThat(foundProduct.isPresent()).isTrue();
        }

        @Test
        @DisplayName("Должен вернуть пустую запись - продукт по собственному ID и ID категории не найден (нет категории)")
        void findProductByCategoryAndId_shouldReturnOptionalEmpty_categoryNotExist_Test() {
            Optional<ProductReadDto> foundProduct = productService.findProductByCategoryAndId(6, existProdId);
            assertThat(foundProduct.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("Должен вернуть пустую запись - продукт по собственному ID и ID категории не найден (нет продукта)")
        void findProductByCategoryAndId_shouldReturnOptionalEmpty_productNotExist_Test() {
            Optional<ProductReadDto> foundProduct = productService.findProductByCategoryAndId(2, nonExistProdId);
            assertThat(foundProduct.isEmpty()).isTrue();
        }
    }

    @Nested
    @DisplayName("Блок тестов на *.findProductByBrandAndId() метод ProductService")
    class FindProductByBrandAndIdMethodOnProductServiceTests {
        @Test
        @DisplayName("Должен вернуть продукт найденный по собственному ID и ID брэнда")
        void findProductByBrandAndId_success_Test() {
            Optional<ProductReadDto> foundProduct = productService.findProductByBrandAndId(1, 2L);
            assertThat(foundProduct.isPresent()).isTrue();
        }

        @Test
        @DisplayName("Должен вернуть пустую запись - продукт по собственному ID и ID брэнда не найден (нет брэнда)")
        void findProductByBrandAndId_shouldReturnOptionalEmpty_brandNotExist_Test() {
            Optional<ProductReadDto> foundProduct = productService.findProductByBrandAndId(6, existProdId);
            assertThat(foundProduct.isPresent()).isFalse();
        }

        @Test
        @DisplayName("Должен вернуть пустую запись - продукт по собственному ID и ID категории не найден (нет продукта)")
        void findProductByBrandAndId_shouldReturnOptionalEmpty_productNotExist_Test() {
            Optional<ProductReadDto> foundProduct = productService.findProductByBrandAndId(1, nonExistProdId);
            assertThat(foundProduct.isPresent()).isFalse();
        }
    }

    @Nested
    @DisplayName("Блок тестов на *.findProductByBrandAndName() метод ProductService")
    class FindProductByBrandAndNameMethodOnProductServiceTests {
        @Test
        @DisplayName("Должен вернуть продукт по его названию и ID брэнда")
        void findProductByBrandAndName_success_Test() {
            Optional<ProductReadDto> foundProduct = productService.findProductByBrandAndName(1, existProductName);
            assertThat(foundProduct.isPresent()).isTrue();
        }

        @Test
        @DisplayName("Должен вернуть пустую запись - продукт по его названию и ID брэнда не найден (нет брэнда)")
        void findProductByBrandAndName_shouldReturnOptionalEmpty_brandNotExist_Test() {
            Optional<ProductReadDto> foundProduct = productService.findProductByBrandAndName(6, existProductName);
            assertThat(foundProduct.isPresent()).isFalse();
        }

        @Test
        @DisplayName("Должен вернуть пустую запись - продукт по его названию и ID брэнда не найден (нет продукта)")
        void findProductByBrandAndName_shouldReturnOptionalEmpty_productNotExist_Test() {
            Optional<ProductReadDto> foundProduct = productService.findProductByBrandAndName(1, nonExistProductName);
            assertThat(foundProduct.isPresent()).isFalse();
        }
    }

    @Nested
    @DisplayName("Блок тестов на оставшиеся *.findProductBy...() методы ProductService")
    class FindProductByPrefixMethodsOnProductServiceTests {
        @Test
        @DisplayName("Должен вернуть список продуктов по ID категории")
        void findProductByCategory_shouldReturnFoundList_Test() {
            List<ProductReadDto> foundList = productService.findProductByCategory(existCatOrBrandId);
            assertThat(foundList.size()).isEqualTo(3);
        }

        @Test
        @DisplayName("Должен вернуть пустой список - ID категории не существует")
        void findProductByCategory_shouldReturnEmptyList_notExistCategory_Test() {
            List<ProductReadDto> foundList = productService.findProductByCategory(nonExistCatOrBrandId);
            assertThat(foundList.size()).isEqualTo(0);
        }

        @Test
        @DisplayName("Должен вернуть список продуктов по ID брэнда")
        void findProductByBrand_shouldReturnFoundList_Test() {
            List<ProductReadDto> foundList = productService.findProductByBrand(existCatOrBrandId);
            assertThat(foundList.size()).isEqualTo(3);
        }

        @Test
        @DisplayName("Должен вернуть пустой список - ID брэнда не существует")
        void findProductByBrand_shouldReturnEmptyList_notExistBrand_Test() {
            List<ProductReadDto> foundList = productService.findProductByBrand(nonExistCatOrBrandId);
            assertThat(foundList.size()).isEqualTo(0);
        }

        @Test
        @DisplayName("Должен вернуть список продуктов соответствующий заданным ID брэнда и категории")
        void findProductByBrandAndCategory_shouldReturnFoundList_Test() {
            List<ProductReadDto> foundList =
                    productService.findProductByBrandAndCategory(existCatOrBrandId, 2);
            assertThat(foundList.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("Должен вернуть пустой список продуктов - нет пересечения по заданным брэнду и категории")
        void findProductByBrandAndCategory_shouldReturnEmptyList_noCrossingExist_Test() {
            List<ProductReadDto> foundList =
                    productService.findProductByBrandAndCategory(existCatOrBrandId, existCatOrBrandId);
            assertThat(foundList.size()).isEqualTo(0);
        }

        @Test
        @DisplayName("Должен вернуть пустой список продуктов - найдена категория, нет брэнда")
        void findProductByBrandAndCategory_shouldReturnEmptyList_existCategoryNoBrand_Test() {
            List<ProductReadDto> foundList =
                    productService.findProductByBrandAndCategory(nonExistCatOrBrandId, existCatOrBrandId);
            assertThat(foundList.size()).isEqualTo(0);
        }

        @Test
        @DisplayName("Должен вернуть пустой список продуктов - найден брэнд, нет категории")
        void findProductByBrandAndCategory_shouldReturnEmptyList_existBrandNoCategory_Test() {
            List<ProductReadDto> foundList =
                    productService.findProductByBrandAndCategory(existCatOrBrandId, nonExistCatOrBrandId);
            assertThat(foundList.size()).isEqualTo(0);
        }
    }
}