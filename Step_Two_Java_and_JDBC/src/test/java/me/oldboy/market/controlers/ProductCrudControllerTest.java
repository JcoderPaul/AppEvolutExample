package me.oldboy.market.controlers;

import me.oldboy.market.entity.Product;
import me.oldboy.market.entity.enums.Action;
import me.oldboy.market.entity.enums.Status;
import me.oldboy.market.exceptions.ProductCrudControllerException;
import me.oldboy.market.services.AuditServiceImpl;
import me.oldboy.market.services.ProductServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductCrudControllerTest {
    @Mock
    private ProductServiceImpl productService;
    @Mock
    private AuditServiceImpl auditService;
    @InjectMocks
    private ProductCrudController productCrudController;
    private String existEmail, productName, updateName, updateDescription, description;
    private Long existId, nonExistId;
    private Product newProduct, createdProduct, forUpdateProductData;

    @BeforeEach
    void setUp() {
        existEmail = "admin@market.ru";

        productName = "Уги";
        updateName = "Чуни";

        description = "Уги вуги";
        updateDescription = "Ах вы чуни мои чуни";

        existId = 1L;
        nonExistId = 100L;

        newProduct = Product.builder()
                .name(productName)
                .price(12.3)
                .categoryId(Math.toIntExact(existId))
                .brandId(Math.toIntExact(existId))
                .description(description)
                .build();

        createdProduct = new Product();
        {
            createdProduct.setId(existId);
            createdProduct.setName(newProduct.getName());
            createdProduct.setPrice(newProduct.getPrice());
            createdProduct.setCategoryId(newProduct.getCategoryId());
            createdProduct.setBrandId(newProduct.getBrandId());
            createdProduct.setDescription(newProduct.getDescription());
            createdProduct.setCreationAt(LocalDateTime.now());
        }

        forUpdateProductData = new Product();
        {
            forUpdateProductData.setId(existId);
            forUpdateProductData.setName(updateName);
            forUpdateProductData.setPrice(334.5);
            forUpdateProductData.setCategoryId(newProduct.getCategoryId());
            forUpdateProductData.setBrandId(newProduct.getBrandId());
            forUpdateProductData.setDescription(updateDescription);
            forUpdateProductData.setCreationAt(LocalDateTime.now());
            forUpdateProductData.setModifiedAt(LocalDateTime.now().plusDays(5));
        }
    }

    @AfterEach
    public void resetTestBase() {
        createdProduct = null;
    }

    /* Тестирование метода *.createProduct() класса ProductCrudController */
    @Test
    void createProduct_shouldReturnCreatedProduct_Test() {
        when(productService.findProductByBrandAndName(Math.toIntExact(existId), productName)).thenReturn(null);
        when(productService.create(newProduct)).thenReturn(createdProduct);

        Product result = productCrudController.createProduct(newProduct, existEmail);

        assertThat(result).isNotNull();
        assertThat(existId).isEqualTo(result.getId());

        verify(productService).findProductByBrandAndName(Math.toIntExact(existId), productName);
        verify(productService).create(newProduct);
        verify(auditService).create(argThat(audit ->
                audit.getAction() == Action.ADD_PRODUCT &&
                        audit.getIsSuccess() == Status.SUCCESS &&
                        audit.getCreateBy().equals(existEmail) &&
                        audit.getAuditableRecord().equals(createdProduct.toString())
        ));
    }

    @Test
    void createProduct_shouldReturnExceptionDuplicateProduct_Test() {
        when(productService.findProductByBrandAndName(Math.toIntExact(existId), productName)).thenReturn(createdProduct);

        assertThatThrownBy(() -> productCrudController.createProduct(newProduct, existEmail))
                .isInstanceOf(ProductCrudControllerException.class)
                .hasMessageContaining("Duplicate product name");

        verify(productService).findProductByBrandAndName(Math.toIntExact(existId), productName);
    }

    @Test
    void createProduct_shouldReturnFail_canNotCreateProduct_Test() {
        createdProduct.setId(null);

        when(productService.findProductByBrandAndName(Math.toIntExact(existId), productName)).thenReturn(null);
        when(productService.create(newProduct)).thenReturn(createdProduct);

        Product result = productCrudController.createProduct(newProduct, existEmail);

        assertThat(result.getId()).isNull();

        verify(productService).findProductByBrandAndName(Math.toIntExact(existId), productName);
        verify(auditService).create(argThat(audit -> audit.getIsSuccess() == Status.FAIL));
    }

    /* Тестирование метода *.updateProduct() класса ProductCrudController */

    @Test
    void updateProduct_shouldReturnSuccessAuditStatus_Test() {
        when(productService.findById(existId)).thenReturn(createdProduct);

        productCrudController.updateProduct(forUpdateProductData, existEmail);

        verify(productService).update(forUpdateProductData);
        verify(auditService).create(argThat(audit ->
                audit.getIsSuccess() == Status.SUCCESS &&
                        audit.getAction() == Action.UPDATE_PRODUCT
        ));
    }

    @Test
    void updateProduct_shouldReturnCaughtException_canNotChangeBrand_Test() {
        when(productService.findById(existId)).thenReturn(createdProduct);

        forUpdateProductData.setBrandId(3);
        productCrudController.updateProduct(forUpdateProductData, existEmail);

        verify(productService, never()).update(any(Product.class));
        verify(auditService).create(argThat(audit ->
                audit.getIsSuccess() == Status.FAIL
        ));
    }

    @Test
    void updateProduct_shouldReturnCaughtException_canNotChangeCategory_Test() {
        when(productService.findById(existId)).thenReturn(createdProduct);

        forUpdateProductData.setCategoryId(3);
        productCrudController.updateProduct(forUpdateProductData, existEmail);

        verify(productService, never()).update(any(Product.class));
        verify(auditService).create(argThat(audit ->
                audit.getIsSuccess() == Status.FAIL
        ));
    }

    /* Тестирование метода *.deleteProduct() класса ProductCrudController */

    @Test
    void deleteProduct_shouldReturnTrueAfterRemoveProduct_Test() {
        when(productService.findById(existId)).thenReturn(createdProduct);
        when(productService.delete(existId)).thenReturn(true);

        boolean result = productCrudController.deleteProduct(existId, existEmail);

        assertThat(result).isTrue();
        verify(productService).delete(existId);
        verify(auditService).create(argThat(audit ->
                audit.getIsSuccess() == Status.SUCCESS &&
                        audit.getAction() == Action.DELETE_PRODUCT
        ));
    }

    @Test
    void deleteProduct_shouldReturnFailureRemove_Test() {
        when(productService.findById(existId)).thenReturn(createdProduct);
        when(productService.delete(existId)).thenReturn(false);

        boolean result = productCrudController.deleteProduct(existId, existEmail);

        assertThat(result).isFalse();
        verify(productService).delete(existId);
        verify(auditService).create(argThat(audit ->
                audit.getIsSuccess() == Status.FAIL
        ));
    }

    /* Тестирование метода *.findProductById() класса ProductCrudController */

    @Test
    void findProductById_shouldReturnFoundProduct_Test() {
        when(productService.findById(existId)).thenReturn(createdProduct);

        Product result = productCrudController.findProductById(existId);

        assertThat(result).isNotNull();
        assertThat(existId).isEqualTo(result.getId());
        verify(productService).findById(anyLong());
    }

    @Test
    void findProductById_shouldReturnNull_productNotFound_Test() {
        when(productService.findById(existId)).thenReturn(null);

        Product result = productCrudController.findProductById(existId);

        assertThat(result).isNull();
        verify(productService).findById(anyLong());
    }
}