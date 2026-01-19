package me.oldboy.market.controllers;

import me.oldboy.market.dto.product.ProductCreateDto;
import me.oldboy.market.dto.product.ProductReadDto;
import me.oldboy.market.dto.product.ProductUpdateDto;
import me.oldboy.market.entity.Product;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.exceptions.ControllerLayerException;
import me.oldboy.market.services.BrandServiceImpl;
import me.oldboy.market.services.CategoryServiceImpl;
import me.oldboy.market.services.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {
    @Mock
    private ProductServiceImpl productService;
    @Mock
    private CategoryServiceImpl categoryService;
    @Mock
    private BrandServiceImpl brandService;
    @InjectMocks
    private ProductController productController;

    private ProductCreateDto validProductCreateDto;
    private ProductUpdateDto validProductUpdateDto;
    private Product testProduct;
    private Category testCategory;
    private Brand testBrand;
    private String authEmail, existProductName, nonExistProductName,
            updateProductName, existProdDescription, updateProdDescription;
    private Long testIdLong;
    private Integer testIdInt;

    @BeforeEach
    void setUp() {
        testIdLong = 1L;
        testIdInt = Math.toIntExact(testIdLong);

        authEmail = "test@oldboy.me";

        existProductName = "Скатерть-самобранка";
        nonExistProductName = "Меч-поднимец";
        updateProductName = "Нож-нескладец";
        existProdDescription = "Пробная версия, изготовитель НИИ 'ЧАВО'";
        updateProdDescription = "Кузнечных дел мастер Г.Гефестов";

        validProductCreateDto =
                new ProductCreateDto(existProductName, 100.0, testIdInt, testIdInt, existProdDescription, 10);
        validProductUpdateDto =
                new ProductUpdateDto(testIdLong, updateProductName, 150.0, updateProdDescription, 5);

        testProduct = Product.builder()
                .id(testIdLong)
                .name(existProductName)
                .price(100.0)
                .categoryId(testIdInt)
                .brandId(testIdInt)
                .description(existProdDescription)
                .stockQuantity(10)
                .creationAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now().plusSeconds(1))
                .build();

        testCategory = new Category(testIdInt, "Electronics");
        testBrand = new Brand(testIdInt, "Simsimg");
    }

    @Nested
    @DisplayName("Набор тестов для *.createProduct()")
    class CreateProductTests {

        @Test
        void createProduct_shouldCreateProduct_withValidData_Test() {
            when(categoryService.findById(testIdInt)).thenReturn(testCategory);
            when(brandService.findById(testIdInt)).thenReturn(testBrand);
            when(productService.findProductByBrandAndName(testIdInt, existProductName)).thenReturn(null);
            when(productService.create(any(Product.class))).thenReturn(testProduct);

            Product result = productController.createProduct(validProductCreateDto, authEmail);

            assertThat(result).isNotNull();
            assertThat(testIdLong).isEqualTo(result.getId());
            assertThat(existProductName).isEqualTo(result.getName());

            verify(categoryService).findById(testIdInt);
            verify(brandService).findById(testIdInt);
            verify(productService).findProductByBrandAndName(testIdInt, existProductName);
            verify(productService).create(any(Product.class));
        }

        @Test
        void createProduct_shouldThrowException_withNonExistentCategory_Test() {
            when(categoryService.findById(testIdInt)).thenReturn(null);

            assertThatThrownBy(() -> productController.createProduct(validProductCreateDto, authEmail))
                    .isInstanceOf(ControllerLayerException.class)
                    .hasMessageContaining("The specified category was not found, ID - " + testIdInt + " not correct");

            verify(categoryService).findById(testIdInt);
            verify(brandService, never()).findById(anyInt());
            verify(productService, never()).create(any(Product.class));
        }

        @Test
        void createProduct_shouldThrowException_withNonExistentBrand_Test() {
            when(categoryService.findById(testIdInt)).thenReturn(testCategory);
            when(brandService.findById(testIdInt)).thenReturn(null);

            assertThatThrownBy(() -> productController.createProduct(validProductCreateDto, authEmail))
                    .isInstanceOf(ControllerLayerException.class)
                    .hasMessageContaining("The selected brand was not found, ID - " + testIdInt + " not correct");

            verify(categoryService, times(1)).findById(testIdInt);
            verify(brandService, times(1)).findById(testIdInt);
            verify(productService, never()).create(any(Product.class));
        }

        @Test
        void createProduct_shouldThrowException_withDuplicateProduct_Test() {
            when(categoryService.findById(testIdInt)).thenReturn(testCategory);
            when(brandService.findById(testIdInt)).thenReturn(testBrand);
            when(productService.findProductByBrandAndName(testIdInt, existProductName)).thenReturn(testProduct);

            assertThatThrownBy(() -> productController.createProduct(validProductCreateDto, authEmail))
                    .isInstanceOf(ControllerLayerException.class)
                    .hasMessageContaining("Duplicate product name");

            verify(productService, never()).create(any(Product.class));
        }
    }

    @Nested
    @DisplayName("Набор тестов для *.updateProduct()")
    class UpdateProductTests {

        @Test
        void updateProduct_shouldUpdateAndAuditSuccess_withValidData_Test() {
            when(productService.findById(testIdLong)).thenReturn(testProduct);
            when(productService.update(any(Product.class))).thenReturn(true);

            boolean result = productController.updateProduct(validProductUpdateDto, authEmail);

            assertThat(result).isTrue();

            verify(productService).findById(testIdLong);
            verify(productService).update(any(Product.class));
        }

        @Test
        void updateProduct_shouldThrowException_withNonExistentProduct_Test() {
            when(productService.findById(testIdLong)).thenReturn(null);

            assertThatThrownBy(() -> productController.updateProduct(validProductUpdateDto, authEmail))
                    .isInstanceOf(ControllerLayerException.class)
                    .hasMessageContaining("Updating product with ID - " + testIdLong + " not found");

            verify(productService, never()).update(any(Product.class));
        }
    }

    @Nested
    @DisplayName("Набор тестов для *.deleteProduct()")
    class DeleteProductTests {

        @Test
        void deleteProduct_shouldDeleteSuccess_withExistingProduct_Test() {
            when(productService.findById(testIdLong)).thenReturn(testProduct);
            when(productService.delete(testIdLong)).thenReturn(true);

            boolean result = productController.deleteProduct(testIdLong, authEmail);

            assertThat(result).isTrue();
            verify(productService, times(1)).findById(anyLong());
            verify(productService, times(1)).delete(anyLong());
        }

        @Test
        void deleteProduct_shouldReturnException_productIdNotFound_Test() {
            when(productService.findById(testIdLong)).thenReturn(null);

            assertThatThrownBy(() -> productController.deleteProduct(testIdLong, authEmail))
                    .isInstanceOf(ControllerLayerException.class)
                    .hasMessageContaining("Produced with ID - " + testIdLong + " not found");
        }
    }

    @Nested
    @DisplayName("Тесты для *.find...() методов")
    class ForFindPrefixMethodsTests {
        @Test
        void findProductById_shouldReturnProduct_withExistingId_Test() {
            when(productService.findById(testIdLong)).thenReturn(testProduct);

            ProductReadDto result = productController.findProductById(testIdLong);

            assertThat(result).isNotNull();
            assertThat(testProduct.getId()).isEqualTo(result.id());
            assertThat(testProduct.getName()).isEqualTo(result.name());
        }

        @Test
        void findProductById_shouldReturnNull_withNonExistingId_Test() {
            when(productService.findById(testIdLong)).thenReturn(null);

            ProductReadDto result = productController.findProductById(testIdLong);

            assertThat(result).isNull();
        }

        @Test
        void findAllProduct_shouldReturnAllProducts_Test() {
            List<Product> products = Arrays.asList(testProduct);
            when(productService.findAll()).thenReturn(products);

            List<ProductReadDto> result = productController.findAllProduct();

            assertThat(result).isNotNull();
            assertThat(1).isEqualTo(result.size());
            assertThat(testProduct.getName()).isEqualTo(result.get(0).name());
        }

        @Test
        void findProductsByCategory_shouldReturnProductsListByCategory_Test() {
            List<Product> products = Arrays.asList(testProduct);
            when(productService.findProductByCategory(testIdInt)).thenReturn(products);

            List<ProductReadDto> result = productController.findProductsByCategory(testIdInt);

            assertThat(result).isNotNull();
            assertThat(1).isEqualTo(result.size());
        }

        @Test
        void findProductByName_shouldReturnProduct_withExistingName_Test() {
            List<Product> products = Arrays.asList(testProduct);
            when(productService.findAll()).thenReturn(products);

            ProductReadDto result = productController.findProductByName(testProduct.getName());

            assertThat(result).isNotNull();
            assertThat(testProduct.getName()).isEqualTo(result.name());
        }

        @Test
        void findProductByName_shouldReturnNull_withNonExistingName_Test() {
            List<Product> products = Arrays.asList(testProduct);
            when(productService.findAll()).thenReturn(products);

            ProductReadDto result = productController.findProductByName(nonExistProductName);

            assertThat(result).isNull();
        }

        @Test
        void findProductsByBrand_shouldReturnProductsListByBrand_Test() {
            List<Product> products = Arrays.asList(testProduct);
            when(productService.findProductByBrand(testIdInt)).thenReturn(products);

            List<ProductReadDto> result = productController.findProductsByBrand(testIdInt);

            assertThat(result).isNotNull();
            assertThat(1).isEqualTo(result.size());
        }
    }
}