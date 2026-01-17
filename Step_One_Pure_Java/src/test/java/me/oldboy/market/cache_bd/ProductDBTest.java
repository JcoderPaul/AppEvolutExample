package me.oldboy.market.cache_bd;

import me.oldboy.market.entity.Product;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.exceptions.ProductDBException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductDBTest {

    private ProductDB productDB;
    private Product prd_1, prd_2, prd_3;
    private Brand b1, b2, b3;
    private Category cat1, cat2, cat3;
    private String prodName_1, prodName_2, prodName_3;

    @BeforeEach
    void setUp(){
        productDB = ProductDB.getINSTANCE();

        prodName_1 = "Product_1";
        prodName_2 = "Product_2";
        prodName_3 = "Product_3";

        b1 = Brand.builder().name("Brand_1").build();
        b2 = Brand.builder().name("Brand_2").build();
        b3 = Brand.builder().name("Brand_3").build();

        cat1 = Category.builder().name("Category_1").build();
        cat2 = Category.builder().name("Category_2").build();
        cat3 = Category.builder().name("Category_3").build();

        prd_1 = Product.builder().name(prodName_1).brand(b3).category(cat3).build();
        prd_2 = Product.builder().name(prodName_2).brand(b2).category(cat2).build();
        prd_3 = Product.builder().name(prodName_3).brand(b1).category(cat1).build();
    }

    @AfterEach
    void cleanBase(){
        productDB.getProductsList().clear();
        productDB.getProductByIdIndex().clear();
        productDB.getCategoryIndex().clear();
        productDB.getBrandIndex().clear();
    }

    @Test
    void add_shouldReturnGeneratedProductId_Test() {
        Long generatedId_1 = productDB.add(prd_1);
        assertThat(generatedId_1).isEqualTo(1L);

        Long generatedId_2 = productDB.add(prd_2);
        assertThat(generatedId_2).isEqualTo(2L);

        Long generatedId_3 = productDB.add(prd_3);
        assertThat(generatedId_3).isEqualTo(3L);
    }

    @Test
    void findProductById_shouldReturnProductById_Test() {
        Long generatedId_1 = productDB.add(prd_1);
        Long generatedId_2 = productDB.add(prd_2);
        Long generatedId_3 = productDB.add(prd_3);

        assertThat(productDB.findProductById(generatedId_1).get()).isEqualTo(prd_1);
        assertThat(productDB.findProductById(generatedId_2).get()).isEqualTo(prd_2);
        assertThat(productDB.findProductById(generatedId_3).get()).isEqualTo(prd_3);
    }

    @Test
    void findProductByCategory_shouldReturnProductListByCategory_Test() {
        productDB.add(prd_1);
        productDB.add(prd_2);
        productDB.add(prd_3);

        assertThat(productDB.findProductByCategory(prd_1.getCategory()).size()).isEqualTo(1);
    }

    @Test
    void findProductByCategory_shouldReturnException_Test() {
        productDB.add(prd_2);
        productDB.add(prd_3);

        assertThatThrownBy(() -> productDB.findProductByCategory(prd_1.getCategory()))
                .isInstanceOf(ProductDBException.class)
                .hasMessageContaining(prd_1.getCategory().getName() + " category not found");
    }

    @Test
    void findProductByBrand_shouldReturnProductListByBrand_Test() {
        productDB.add(prd_1);
        productDB.add(prd_2);
        productDB.add(prd_3);

        assertThat(productDB.findProductByBrand(prd_1.getBrand()).size()).isEqualTo(1);
    }

    @Test
    void findProductByBrand_shouldReturnException_Test() {
        productDB.add(prd_1);
        productDB.add(prd_3);

        assertThatThrownBy(() -> productDB.findProductByBrand(prd_2.getBrand()))
                .isInstanceOf(ProductDBException.class)
                .hasMessageContaining(prd_2.getBrand().getName() + " brand not found");
    }

    @Test
    void delete_shouldReturnTrue_afterDelete_Test() {
        Long generatedId_1 = productDB.add(prd_1);
        productDB.add(prd_2);
        productDB.add(prd_3);

        assertThat(productDB.delete(prd_1)).isTrue();
        assertThat(productDB.getProductsList().contains(prd_1)).isFalse();
        assertThat(productDB.getProductByIdIndex().get(generatedId_1)).isNull();
        assertThat(productDB.getCategoryIndex().get(prd_1.getCategory()).contains(prd_1)).isFalse();
        assertThat(productDB.getBrandIndex().get(prd_1.getBrand()).contains(prd_1)).isFalse();
    }

    @Test
    void update_shouldReturnTrue_afterUpdate_Test() {
        Long generatedId_1 = productDB.add(prd_1);
        productDB.add(prd_2);
        productDB.add(prd_3);

        /* В принципе мы можем просто прокинуть обновления через сеттеры, без спец. метода */
        String newName = "newName";
        String newDescription = "newDescription";

        Product product = productDB.findProductById(generatedId_1).get();
        product.setName(newName);
        product.setDescription(newDescription);

        assertThat(prd_1.getDescription()).isEqualTo(newDescription);
        assertThat(prd_1.getName()).isEqualTo(newName);

        /* Брэнд и категорию менять нельзя - новый товар получается, ключом остается ID товара */
        Product updateProduct = Product.builder()
                .id(generatedId_1)
                .name("Product_1_и_2_и_3")
                .brand(b3)
                .category(cat3)
                .description(newDescription)
                .price(230.0)
                .build();

        productDB.update(updateProduct);

        assertThat(updateProduct.getId()).isEqualTo(prd_1.getId());
        assertThat(updateProduct.getPrice()).isEqualTo(prd_1.getPrice());
        assertThat(updateProduct.getDescription()).isEqualTo(prd_1.getDescription());
        assertThat(updateProduct.getStockQuantity()).isEqualTo(prd_1.getStockQuantity());
        assertThat(updateProduct.getLastModifiedTimestamp()).isEqualTo(prd_1.getLastModifiedTimestamp());

    }
}