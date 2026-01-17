package me.oldboy.market.repository;

import me.oldboy.market.cache_bd.ProductDB;
import me.oldboy.market.entity.Product;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.exceptions.ProductDBException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductRepositoryTest {

    private ProductDB productDB;
    private ProductRepository productRepository;
    private Product prd_1, prd_2, prd_3;
    private Brand b1, b2, b3;
    private Category cat1, cat2, cat3;

    @BeforeEach
    void setUp(){
        productDB = ProductDB.getINSTANCE();
        productRepository = new ProductRepository(productDB);

        b1 = Brand.builder().name("Brand_1").build();
        b2 = Brand.builder().name("Brand_2").build();
        b3 = Brand.builder().name("Brand_3").build();

        cat1 = Category.builder().name("Category_1").build();
        cat2 = Category.builder().name("Category_2").build();
        cat3 = Category.builder().name("Category_3").build();

        prd_1 = Product.builder().name("Product_1").brand(b3).category(cat3).build();
        prd_2 = Product.builder().name("Product_2").brand(b2).category(cat2).build();
        prd_3 = Product.builder()
                .name("Product_3")
                .price(123)
                .lastModifiedTimestamp(new Date().getTime())
                .description("Ayhhhh")
                .brand(b1)
                .category(cat1)
                .stockQuantity(23)
                .build();
    }

    @AfterEach
    void cleanBase(){
        productDB.getProductsList().clear();
        productDB.getProductByIdIndex().clear();
        productDB.getCategoryIndex().clear();
        productDB.getBrandIndex().clear();
    }

    @Test
    void save_shouldReturnSavedProduct_Test() {
        Product savedProduct = productRepository.save(prd_1);
        assertThat(savedProduct).isNotNull();

        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getCategory()).isEqualTo(prd_1.getCategory());
        assertThat(savedProduct.getBrand()).isEqualTo(prd_1.getBrand());
    }

    @Test
    void findAll_shouldReturnAllProductList_Test() {
        /* База пуста */
        assertThat(productRepository.findAll().size()).isEqualTo(0);
        /* Заполняем БД */
        productDB.add(prd_1);
        productDB.add(prd_2);
        productDB.add(prd_3);
        /* Размер БД - 3и записи */
        assertThat(productRepository.findAll().size()).isEqualTo(3);
    }

    @Test
    void findById_shouldReturnProductById_Test() {
        /* Заполняем БД */
        Product saved_1 = productRepository.save(prd_1);
        Product saved_2 = productRepository.save(prd_2);
        Product saved_3 = productRepository.save(prd_3);

        /* Объект Optional не пустой */
        assertThat(productRepository.findById(saved_1.getId())).isNotEmpty();
        assertThat(productRepository.findById(saved_2.getId())).isNotEmpty();
        assertThat(productRepository.findById(saved_3.getId())).isNotEmpty();

        /* Сгенерированный ID уже проброшен в каждую из созданных записей при сохранении */
        assertThat(productRepository.findById(saved_1.getId()).get().getId()).isEqualTo(prd_1.getId());
        assertThat(productRepository.findById(saved_2.getId()).get().getId()).isEqualTo(prd_2.getId());
        assertThat(productRepository.findById(saved_3.getId()).get().getId()).isEqualTo(prd_3.getId());
    }

    @Test
    void findById_shouldReturnOptionalEmpty_notExistId_Test() {
        /* Заполняем БД */
        productRepository.save(prd_1);
        productRepository.save(prd_2);
        productRepository.save(prd_3);

        /* Объект Optional не пустой */
        assertThat(productRepository.findById(4L)).isEmpty();
    }

    @Test
    void update_shouldUpdateProduct_Test() {
        /* Заполняем БД "напрямую" */
        Long gen_3 = productDB.add(prd_3);
        Product oldProd = productRepository.findById(gen_3).get();

        /*
        В товаре, по нашей задумке, нельзя менять (обновлять) id, категорию и брэнд,
        остальное можно и нужно, и как писалось ранее достаточно получить ссылку на
        продукт и сеттерами прокинуть нужные обновления.
        */
        Product updateProd = Product.builder()
                .id(gen_3)
                .price(230.0)
                .description("Wow Product")
                .category(prd_3.getCategory())
                .brand(prd_3.getBrand())
                .stockQuantity(34)
                .lastModifiedTimestamp(new Date().getTime())
                .build();
        /* Тут мы это и делаем */
        productRepository.update(updateProd);
        /* И тут это видно */
        assertThat(updateProd.getId()).isEqualTo(oldProd.getId());
        assertThat(updateProd.getPrice()).isEqualTo(oldProd.getPrice());
        assertThat(updateProd.getDescription()).isEqualTo(oldProd.getDescription());
        assertThat(updateProd.getStockQuantity()).isEqualTo(oldProd.getStockQuantity());
        assertThat(updateProd.getLastModifiedTimestamp()).isEqualTo(oldProd.getLastModifiedTimestamp());
    }

    @Test
    void delete_shouldReturnTrueAfterRemove_Test() {
        Product saved_2 = productRepository.save(prd_2);
        productRepository.save(prd_1);
        productRepository.save(prd_3);

        assertThat(productRepository.delete(saved_2.getId())).isTrue();
        assertThat(productRepository.findById(saved_2.getId())).isEmpty();
    }

    @Test
    void delete_shouldReturnFalseAfterRemoveNotExistProduct_Test() {
        productRepository.save(prd_1);
        productRepository.save(prd_2);
        productRepository.save(prd_3);

        assertThat(productRepository.delete(120L)).isFalse();
    }

    /* ------------------------------------------------------- */
    @Test
    void findByCategoryAndId_shouldReturnFoundProduct_Test() {
        productRepository.save(prd_1);
        assertThat(productRepository.findByCategoryAndId(prd_1.getCategory(), prd_1.getId())).contains(prd_1);
    }

    @Test
    void findByCategoryAndId_shouldReturnOptionalEmpty_Test() {
        productRepository.save(prd_1);
        assertThat(productRepository.findByCategoryAndId(prd_1.getCategory(), 100L)).isEmpty();
    }

    @Test
    void findByCategoryAndId_shouldReturnException_Test() {
        assertThatThrownBy(() -> productRepository.findByCategoryAndId(prd_1.getCategory(), 1L))
                .isInstanceOf(ProductDBException.class)
                .hasMessageContaining(prd_1.getCategory().getName() + " category not found");
    }

    /* ------------------------------------------------------- */
    @Test
    void findByBrandAndId_shouldReturnFoundProduct_Test() {
        productRepository.save(prd_2);
        assertThat(productRepository.findByBrandAndId(prd_2.getBrand(), prd_2.getId())).contains(prd_2);
    }

    @Test
    void findByBrandAndId_shouldReturnOptionalEmpty_Test() {
        productRepository.save(prd_2);
        assertThat(productRepository.findByBrandAndId(prd_2.getBrand(), 100L)).isEmpty();
    }

    @Test
    void findByBrandAndId_shouldReturnException_Test() {
        assertThatThrownBy(() -> productRepository.findByBrandAndId(prd_2.getBrand(), 1L))
                .isInstanceOf(ProductDBException.class)
                .hasMessageContaining(prd_2.getBrand().getName() + " brand not found");
    }

    /* ------------------------------------------------------- */
    @Test
    void findByBrandAndName_shouldReturnFoundProduct_Test() {
        productRepository.save(prd_1);
        assertThat(productRepository.findByBrandAndName(prd_1.getBrand(), prd_1.getName())).contains(prd_1);
    }

    @Test
    void findByBrandAndName_shouldReturnOptionalEmpty_Test() {
        assertThatThrownBy(() -> productRepository.findByBrandAndName(prd_2.getBrand(), "Волынка"))
                .isInstanceOf(ProductDBException.class)
                .hasMessageContaining(prd_2.getBrand().getName() + " brand not found");
    }

    @Test
    void findByBrandAndName_shouldReturnException_notFoundProductName_Test() {
        productRepository.save(prd_2);
        assertThat(productRepository.findByBrandAndName(prd_2.getBrand(), "Волынка")).isEmpty();
    }

    /* ------------------------------------------------------- */
    @Test
    void findByCategory_shouldReturnProductListWithSameCategory_Test(){
        productRepository.save(prd_2);
        assertThat(productRepository.findByCategory(prd_2.getCategory())).isNotEmpty();
        assertThat(productRepository.findByCategory(prd_2.getCategory()).size()).isEqualTo(1);
    }

    @Test
    void findByBrand_shouldReturnProductListWithSameBrand_Test(){
        productRepository.save(prd_2);
        assertThat(productRepository.findByBrand(prd_2.getBrand())).isNotEmpty();
        assertThat(productRepository.findByBrand(prd_2.getBrand()).size()).isEqualTo(1);
    }
}