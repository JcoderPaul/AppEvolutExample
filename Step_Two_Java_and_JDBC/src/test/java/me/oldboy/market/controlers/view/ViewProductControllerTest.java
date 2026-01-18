package me.oldboy.market.controlers.view;

import me.oldboy.market.entity.Product;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.services.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ViewProductControllerTest {
    @Mock
    private ProductServiceImpl productService;
    @InjectMocks
    private ViewProductController viewProductController;
    private List<Product> productList;
    private List<Category> categoryList;
    private List<Brand> brandList;
    private Long existId_1, existId_2, existId_3, notExistId;
    private Product prod_1, prod_2, prod_3;
    private Category category_1, category_2, category_3;
    private Brand brand_1, brand_2, brand_3;

    @BeforeEach
    void setUp() {
        existId_1 = 1L;
        existId_2 = 2L;
        existId_3 = 3L;
        notExistId = 100L;

        category_1 = createTestCategory(Math.toIntExact(existId_1), "Category_1");
        category_2 = createTestCategory(Math.toIntExact(existId_2), "Category_2");
        category_3 = createTestCategory(Math.toIntExact(existId_3), "Category_3");

        brand_1 = createTestBrand(Math.toIntExact(existId_1), "Brand_1");
        brand_2 = createTestBrand(Math.toIntExact(existId_2), "Brand_2");
        brand_3 = createTestBrand(Math.toIntExact(existId_3), "Brand_3");

        prod_1 = createTestProduct(existId_1, "Product 1", Math.toIntExact(existId_1), Math.toIntExact(existId_1));
        prod_2 = createTestProduct(existId_2, "Product 2", Math.toIntExact(existId_1), Math.toIntExact(existId_1));
        prod_3 = createTestProduct(existId_3, "Product 3", Math.toIntExact(existId_2), Math.toIntExact(existId_2));

        productList = Arrays.asList(prod_1, prod_2, prod_3);
        categoryList = Arrays.asList(category_1, category_2, category_3);
        brandList = Arrays.asList(brand_1, brand_2, brand_3);
    }

    /* --- *.viewAllProduct() --- */
    @Test
    void viewAllProduct_shouldReturnExpectedScreenData_Test() {
        when(productService.findAll()).thenReturn(productList);

        String output = captureConsoleOutput(() -> {
            viewProductController.viewAllProduct();
        });

        verify(productService, times(1)).findAll();

        assertTrue(output.contains("Список товаров:"));
        assertTrue(output.contains("Product 1"));
        assertTrue(output.contains("Product 2"));
        assertTrue(output.contains("Product 3"));
    }

    @Test
    void viewAllProduct_shouldPrintEmptyListToScreen_Test() {
        when(productService.findAll()).thenReturn(Collections.emptyList());

        String output = captureConsoleOutput(() -> {
            viewProductController.viewAllProduct();
        });

        verify(productService, times(1)).findAll();

        assertTrue(output.contains("Список товаров:"));
        assertTrue(output.contains("-----------------------------------------------------------------------------"));
    }

    /* --- *.viewProductById() --- */
    @Test
    void viewProductById_shouldReturnOneProductToScreen_Test() {
        when(productService.findById(existId_1)).thenReturn(prod_1);

        String output = captureConsoleOutput(() -> {
            viewProductController.viewProductById(existId_1);
        });

        verify(productService, times(1)).findById(existId_1);

        assertTrue(output.contains("Выбранный по " + existId_1 + "товар:"));
        assertTrue(output.contains("Product 1"));
        assertTrue(output.contains("-----------------------------------------------------------------------------"));
    }

    @Test
    void viewProductById_shouldPrintNullAsTxtToScreen_Test() {
        when(productService.findById(notExistId)).thenReturn(null);

        String output = captureConsoleOutput(() -> {
            viewProductController.viewProductById(notExistId);
        });

        verify(productService, times(1)).findById(notExistId);

        assertTrue(output.contains("null"));
    }

    /* --- *.findProductByCategoryAndId() --- */
    @Test
    void findProductByCategoryAndId_shouldPrintFoundProductInfo_Test() {
        when(productService.findProductByCategoryAndId(category_1.getId(), existId_1)).thenReturn(prod_1);

        String output = captureConsoleOutput(() -> {
            viewProductController.findProductByCategoryAndId(category_1, existId_1);
        });

        // Then
        verify(productService, times(1)).findProductByCategoryAndId(category_1.getId(), existId_1);

        assertTrue(output.contains("Выбранной категории " + category_1.getName() + " и ID - " + existId_1 + " соответствует:"));
        assertTrue(output.contains(category_1.getName()));
        assertTrue(output.contains(prod_1.getId().toString()));
        assertTrue(output.contains(prod_1.getName()));
        assertTrue(output.contains("-----------------------------------------------------------------------------"));
    }

    @Test
    void findProductByCategoryAndId_shouldPrintNullToScreen_Test() {
        when(productService.findProductByCategoryAndId(category_1.getId(), existId_3)).thenReturn(null);

        String output = captureConsoleOutput(() -> {
            viewProductController.findProductByCategoryAndId(category_1, existId_3);
        });

        verify(productService, times(1)).findProductByCategoryAndId(category_1.getId(), existId_3);

        assertTrue(output.contains("Выбранной категории " + category_1.getName() + " и ID - " + existId_3 + " соответствует:"));
        assertTrue(output.contains("null"));
    }

    /* --- *.findProductByBrandAndId() --- */
    @Test
    void findProductByBrandAndId_shouldPrintFoundProductInfo_Test() {
        when(productService.findProductByBrandAndId(brand_1.getId(), existId_1)).thenReturn(prod_1);

        String output = captureConsoleOutput(() -> {
            viewProductController.findProductByBrandAndId(brand_1, existId_1);
        });

        verify(productService, times(1)).findProductByBrandAndId(anyInt(), anyLong());

        assertTrue(output.contains("Выбранному брэнду" + brand_1.getName() + " и ID - " + existId_1 + " соответствует:"));
        assertTrue(output.contains(brand_1.getName()));
        assertTrue(output.contains(prod_1.getName()));
        assertTrue(output.contains(prod_1.getId().toString()));
        assertTrue(output.contains("-----------------------------------------------------------------------------"));
    }

    @Test
    void findProductByBrandAndId_shouldPrintNull_notFoundProductInfo_Test() {
        when(productService.findProductByBrandAndId(brand_1.getId(), existId_3)).thenReturn(null);

        String output = captureConsoleOutput(() -> {
            viewProductController.findProductByBrandAndId(brand_1, existId_3);
        });

        verify(productService, times(1)).findProductByBrandAndId(anyInt(), anyLong());

        assertTrue(output.contains("Выбранному брэнду" + brand_1.getName() + " и ID - " + existId_3 + " соответствует:"));
        assertTrue(output.contains("null"));
        assertTrue(output.contains("-----------------------------------------------------------------------------"));
    }

    /* --- *.findProductByBrandAndName() --- */
    @Test
    void findProductByBrandAndName_shouldPrintToScreenFoundProductInfo_Test() {
        when(productService.findProductByBrandAndName(brand_1.getId(), prod_1.getName())).thenReturn(prod_1);

        String output = captureConsoleOutput(() -> {
            viewProductController.findProductByBrandAndName(brand_1, prod_1.getName());
        });

        verify(productService, times(1)).findProductByBrandAndName(anyInt(), anyString());

        assertTrue(output.contains("Выбранному брэнду" + brand_1.getName() + " и названию - " + prod_1.getName() + " соответствует:"));
        assertTrue(output.contains(prod_1.getName()));
        assertTrue(output.contains(brand_1.getName()));
    }

    @Test
    void findProductByBrandAndName_shouldPrintToScreenNull_notFoundProductInfo_Test() {
        when(productService.findProductByBrandAndName(brand_1.getId(), prod_3.getName())).thenReturn(null);

        String output = captureConsoleOutput(() -> {
            viewProductController.findProductByBrandAndName(brand_1, prod_3.getName());
        });

        verify(productService, times(1)).findProductByBrandAndName(anyInt(), anyString());

        assertTrue(output.contains("Выбранному брэнду" + brand_1.getName() + " и названию - " + prod_3.getName() + " соответствует:"));
        assertTrue(output.contains("null"));
    }

    /* --- *.findProductByCategory() --- */
    @Test
    void findProductByCategory_shouldPrintProductOnlyConcreteCategory_Test() {
        List<Product> expectList = productList.stream()
                .filter(product -> product.getCategoryId().equals(category_1.getId()))
                .toList();
        when(productService.findProductByCategory(category_1.getId())).thenReturn(expectList);

        String output = captureConsoleOutput(() -> {
            viewProductController.findProductByCategory(category_1);
        });

        /* Вызывается дважды для вывода на экран и для возврата из метода */
        verify(productService, times(2)).findProductByCategory(category_1.getId());

        assertTrue(output.contains("Список товаров по категории" + category_1.getName() + " :"));
        assertTrue(output.contains(prod_1.getName()));
        assertTrue(output.contains(prod_2.getName()));

        assertTrue(expectList.size() == 2);
    }

    /* --- *.findProductByBrand() --- */
    @Test
    void findProductByBrand_shouldPrintProductOnlyConcreteBrand_Test() {
        List<Product> expectList = productList.stream()
                .filter(product -> product.getBrandId().equals(brand_1.getId()))
                .toList();
        when(productService.findProductByBrand(brand_1.getId())).thenReturn(expectList);

        String output = captureConsoleOutput(() -> {
            viewProductController.findProductByBrand(brand_1);
        });

        /* Вызывается дважды для вывода на экран и для возврата из метода */
        verify(productService, times(2)).findProductByBrand(brand_1.getId());

        assertTrue(output.contains("Список товаров по брэнду" + brand_1.getName() + " :"));
        assertTrue(output.contains(prod_1.getName()));
        assertTrue(output.contains(prod_2.getName()));

        assertTrue(expectList.size() == 2);
    }

    private Product createTestProduct(Long id, String name, Integer brandId, Integer categoryId) {
        return Product.builder()
                .id(id)
                .name(name)
                .brandId(brandId)
                .categoryId(categoryId)
                .price(100.0)
                .stockQuantity(10)
                .description("Test product " + id)
                .build();
    }

    private Brand createTestBrand(Integer id, String name) {
        return Brand.builder()
                .id(id)
                .name(name)
                .build();
    }

    private Category createTestCategory(Integer id, String name) {
        return Category.builder()
                .id(id)
                .name(name)
                .build();
    }

    private String captureConsoleOutput(Runnable code) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            code.run();
            return outputStream.toString();
        } finally {
            System.setOut(originalOut);
        }
    }
}