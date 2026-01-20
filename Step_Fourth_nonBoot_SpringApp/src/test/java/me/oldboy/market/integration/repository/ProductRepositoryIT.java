package me.oldboy.market.integration.repository;

import me.oldboy.market.config.test_data_source.TestContainerInit;
import me.oldboy.market.entity.Product;
import me.oldboy.market.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ProductRepositoryIT extends TestContainerInit {

    @Autowired
    private ProductRepository productRepository;
    private Long existProductId, notExistProductId;
    private Integer existBrandCatId, existAnotherBrandCatId, notExistingBrandCatId;

    @BeforeEach
    void setUp() {
        existProductId = 1L;
        notExistProductId = 200L;

        existBrandCatId = 1;
        existAnotherBrandCatId = 2;
        notExistingBrandCatId = 200;
    }

    @Test
    @DisplayName("Должен подтвердить, что найденный по ID продукт действительно существует - ID есть в БД")
    void findById_shouldReturnTrue_forExistingProduct_Test() {
        Optional<Product> mayBeExistProduct = productRepository.findById(existProductId);
        if (mayBeExistProduct.isPresent()) {
            assertThat(mayBeExistProduct.get()).isNotNull();
        }
    }

    @Test
    @DisplayName("Должен вернуть пустой результат поиска, продукт по ID не найден - ID в БД нет")
    void findById_shouldReturnEmpty_forNotExistingProduct_Test() {
        assertThat(productRepository.findById(notExistProductId)).isEmpty();
    }

    @Test
    @DisplayName("Должен вернуть полный список продуктов")
    void findAll_shouldReturnProductList_Test() {
        assertThat(productRepository.findAll()).isNotNull();
        assertThat(productRepository.findAll().size()).isEqualTo(9);
    }

    @Test
    @DisplayName("Должен вернуть список продуктов из выбранной категории")
    void findByCategory_shouldReturnOptionalProductList_Test() {
        assertThat(productRepository.findByCategory(existBrandCatId)).isNotEmpty();
        assertThat(productRepository.findByCategory(existBrandCatId).get().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Должен вернуть пустой список продуктов если выбранной категории нет или она пуста")
    void findByCategory_shouldReturnOptionalEmptyList_noProductInCategory_Test() {
        assertThat(productRepository.findByCategory(notExistingBrandCatId)).isNotEmpty();
        assertThat(productRepository.findByCategory(notExistingBrandCatId).get().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Должен вернуть список продуктов под выбранным брэндом")
    void findByBrand_shouldReturnOptionalProductList_Test() {
        assertThat(productRepository.findByBrand(existBrandCatId)).isNotEmpty();
        assertThat(productRepository.findByBrand(existBrandCatId).get().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Должен вернуть пустой список продуктов если выбранный брэнд отсутствует или под ним нет товаров")
    void findByBrand_shouldReturnOptionalEmptyProductList_Test() {
        assertThat(productRepository.findByBrand(notExistingBrandCatId)).isNotEmpty();
        assertThat(productRepository.findByBrand(notExistingBrandCatId).get().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Должен вернуть список продуктов из выбранной категории выбранного брэнда")
    void findByBrandAndCategory_shouldReturnOptionalProductList_forExistingBrandAndCategory_Test() {
        assertThat(productRepository.findByBrandAndCategory(existBrandCatId, existAnotherBrandCatId)).isNotEmpty();
        assertThat(productRepository.findByBrandAndCategory(existBrandCatId, existAnotherBrandCatId).get().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Должен вернуть пустой список продуктов если нет категории или брэнда или текущая комбинация не содержит продуктов")
    void findByBrandAndCategory_shouldReturnOptionalEmptyProductList_foundCategoryNotFoundBrand_Test() {
        assertThat(productRepository.findByBrandAndCategory(notExistingBrandCatId, existAnotherBrandCatId)).isNotEmpty();
        assertThat(productRepository.findByBrandAndCategory(notExistingBrandCatId, existAnotherBrandCatId).get().size()).isEqualTo(0);
    }
}