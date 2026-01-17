package me.oldboy.market.services;

import lombok.SneakyThrows;
import me.oldboy.market.cache_bd.BrandDB;
import me.oldboy.market.cache_bd.CategoryDB;
import me.oldboy.market.cache_bd.ProductDB;
import me.oldboy.market.cache_bd.loaders.BrandDBLoader;
import me.oldboy.market.cache_bd.loaders.CategoryDBLoader;
import me.oldboy.market.entity.Product;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.exceptions.ProductDBException;
import me.oldboy.market.exceptions.ProductServiceException;
import me.oldboy.market.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductServiceTest {
    private ProductDB productDB;
    private ProductRepository productRepository;
    private ProductService productService;
    private BrandDB brandDB;
    private CategoryDB categoryDB;
    private Product prd_1, prd_2, prd_3;

    @BeforeEach
    void setUp(){
        productDB = productDB.getINSTANCE();
        productRepository = new ProductRepository(productDB);
        productService = new ProductService(productRepository);

        brandDB = BrandDB.getINSTANCE();
        BrandDBLoader.initInMemoryBase(brandDB);
        categoryDB = CategoryDB.getINSTANCE();
        CategoryDBLoader.initInMemoryBase(categoryDB);

        prd_1 = Product.builder()
                .name("Веник")
                .price(12.00)
                .category(categoryDB.findById(1).get())
                .brand(brandDB.getById(3).get())
                .description("Очень крутой веник")
                .stockQuantity(12)
                .lastModifiedTimestamp(new Date().getTime())
                .build();
        prd_2 = Product.builder()
                .name("Дрель")
                .price(35.00)
                .category(categoryDB.findById(3).get())
                .brand(brandDB.getById(1).get())
                .description("Кручу-верчу")
                .stockQuantity(2)
                .lastModifiedTimestamp(new Date().getTime())
                .build();
        prd_3 = Product.builder()
                .name("Валенки")
                .price(135.00)
                .category(categoryDB.findById(1).get())
                .brand(brandDB.getById(2).get())
                .description("Танцы на льду")
                .stockQuantity(145)
                .lastModifiedTimestamp(new Date().getTime())
                .build();

        productDB.add(prd_1);
        productDB.add(prd_2);
        productDB.add(prd_3);
    }

    @AfterEach
    void cleanBase(){
        productDB.getProductsList().clear();
        productDB.getProductByIdIndex().clear();
        productDB.getBrandIndex().clear();
        productDB.getCategoryIndex().clear();

        brandDB.getBrandList().clear();
        brandDB.getIndexBrand().clear();

        categoryDB.getCategoryList().clear();
        categoryDB.getIndexCategory().clear();
    }


    @Test
    void createProduct_shouldReturnCreatedProduct_Test() {
        assertThat(productService.getAllProduct().size()).isEqualTo(3);

        Product prd_4 = Product.builder()
                .name("Пимы")
                .price(235.00)
                .category(categoryDB.findById(1).get())
                .brand(brandDB.getById(1).get())
                .description("Скользкие")
                .stockQuantity(14)
                .lastModifiedTimestamp(new Date().getTime())
                .build();

        assertThat(productService.createProduct(prd_4)).isNotNull();
        assertThat(productService.getAllProduct().size()).isEqualTo(4);
    }

    @Test
    void getAllProduct_shouldReturnProductList_Test() {
        assertThat(productService.getAllProduct().size()).isEqualTo(3);
    }

    @SneakyThrows
    @Test
    void findProductById_shouldReturnFoundByIdProduct_Test() {
        assertThat(productService.findProductById(prd_3.getId())).isEqualTo(prd_3);
    }

    @SneakyThrows
    @Test
    void findProductById_shouldReturnException_idNotFound_Test() {
        Long nonExistId = 100L;
        assertThatThrownBy(() -> productService.findProductById(nonExistId))
                .isInstanceOf(ProductServiceException.class)
                .hasMessageContaining("Product by ID - " + nonExistId + " not found");
    }

    @SneakyThrows
    @Test
    void updateProduct_shouldUpdateProduct_Test() {
        assertThat(productService.getAllProduct().size()).isEqualTo(3);

        String newName = "Пимы";
        Double price = 1253.0;
        Long productIdForUpdate = prd_2.getId();

        Product forUpdate = Product.builder()
                .id(productIdForUpdate)
                .name(newName)
                .price(price)
                .category(categoryDB.findById(3).get())
                .brand(brandDB.getById(1).get())
                .description("Цепкие")
                .stockQuantity(12)
                .lastModifiedTimestamp(new Date().getTime())
                .build();

        productService.updateProduct(forUpdate);

        assertThat(productService.findProductById(productIdForUpdate).getName()).isEqualTo(newName);
        assertThat(productService.findProductById(productIdForUpdate).getPrice()).isEqualTo(price);

        assertThat(productService.getAllProduct().size()).isEqualTo(3);
    }

    @Test
    void deleteProduct_shouldReturnTrue_Test() {
        assertThat(productService.getAllProduct().size()).isEqualTo(3);

        assertThat(productService.deleteProduct(prd_2.getId())).isTrue();

        assertThat(productService.getAllProduct().size()).isEqualTo(2);
    }

    @Test
    void deleteProduct_shouldReturnFalse_Test() {
        assertThat(productService.getAllProduct().size()).isEqualTo(3);

        assertThat(productService.deleteProduct(100L)).isFalse();

        assertThat(productService.getAllProduct().size()).isEqualTo(3);
    }

    /* ------------------------------------------------------------------------ */
    @Test
    void findProductByCategoryAndId_shouldReturnFoundProduct_Test() {
        assertThat(productService.findProductByCategoryAndId(prd_1.getCategory(), prd_1.getId())).isEqualTo(prd_1);
        assertThat(productService.findProductByCategoryAndId(prd_2.getCategory(), prd_2.getId())).isEqualTo(prd_2);
        assertThat(productService.findProductByCategoryAndId(prd_3.getCategory(), prd_3.getId())).isEqualTo(prd_3);
    }

    @Test
    void findProductByCategoryAndId_shouldReturnNull_Test() {
        assertThat(productService.findProductByCategoryAndId(prd_1.getCategory(), 100L)).isEqualTo(null);
    }

    /* ------------------------------------------------------------------------ */
    @Test
    void findProductByBrandAndId_shouldReturnFoundProduct_Test() {
        assertThat(productService.findProductByBrandAndId(prd_1.getBrand(), prd_1.getId())).isEqualTo(prd_1);
        assertThat(productService.findProductByBrandAndId(prd_2.getBrand(), prd_2.getId())).isEqualTo(prd_2);
        assertThat(productService.findProductByBrandAndId(prd_3.getBrand(), prd_3.getId())).isEqualTo(prd_3);
    }

    @Test
    void findProductByBrandAndId_shouldReturnNull_Test() {
        assertThat(productService.findProductByBrandAndId(prd_1.getBrand(), 100L)).isEqualTo(null);
    }

    /* ------------------------------------------------------------------------ */
    @Test
    void findProductByBrandAndName_shouldReturnFoundProduct_Test() {
        assertThat(productService.findProductByBrandAndName(prd_1.getBrand(), prd_1.getName())).isEqualTo(prd_1);
        assertThat(productService.findProductByBrandAndName(prd_2.getBrand(), prd_2.getName())).isEqualTo(prd_2);
        assertThat(productService.findProductByBrandAndName(prd_3.getBrand(), prd_3.getName())).isEqualTo(prd_3);
    }

    @Test
    void findProductByBrandAndName_shouldReturnNull_Test() {
        assertThat(productService.findProductByBrandAndName(prd_1.getBrand(), "Never")).isEqualTo(null);
    }

    /* ------------------------------------------------------------------------ */
    @Test
    void findProductByCategory_shouldReturnFoundCategoryList_Test() {
        assertThat(productService.findProductByCategory(prd_1.getCategory()).size()).isEqualTo(2);
    }

    @Test
    void findProductByCategory_shouldReturnException_Test() {
        Category notExistCategory = new Category(12, "Нет категории");
        assertThatThrownBy(() -> productService.findProductByCategory(notExistCategory))
                .isInstanceOf(ProductDBException.class)
                .hasMessageContaining(notExistCategory.getName() + " category not found");
    }

    /* ------------------------------------------------------------------------ */
    @Test
    void findProductByBrand_shouldReturnFoundList_Test() {
        assertThat(productService.findProductByBrand(prd_2.getBrand()).size()).isEqualTo(1);
    }

    @Test
    void findProductByBrand_shouldReturnException_Test() {
        Brand notExistBrand = new Brand(12, "Крутой брэнд");
        assertThatThrownBy(() -> productService.findProductByBrand(notExistBrand))
                .isInstanceOf(ProductDBException.class)
                .hasMessageContaining(notExistBrand.getName() + " brand not found");
    }
}