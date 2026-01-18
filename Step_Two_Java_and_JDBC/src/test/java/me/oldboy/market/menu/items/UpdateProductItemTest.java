package me.oldboy.market.menu.items;

import me.oldboy.market.controlers.ProductCrudController;
import me.oldboy.market.controlers.view.ViewProductController;
import me.oldboy.market.entity.Product;
import me.oldboy.market.repository.BrandRepository;
import me.oldboy.market.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.Scanner;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProductItemTest {
    @Mock
    private ProductCrudController productCrudController;
    @Mock
    private ViewProductController viewProductController;
    @Mock
    private BrandRepository brandRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private UpdateProductItem updateProductItem;

    private Scanner scanner;

    /* Тестовые данные */
    private final String tEmail = "test@market.ru";
    private final Product existingProduct = Product.builder()
            .id(1L)
            .name("Старый товар")
            .price(50.0)
            .categoryId(1)
            .brandId(1)
            .description("Лучший контраст картинки")
            .stockQuantity(5)
            .creationAt(LocalDateTime.now())
            .build();

    @Test
    void subMenu_ViewProducts_Test() {
        /* Просмотреть товары -> Выйти */
        String input = "1\n3\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        updateProductItem.subMenu(tEmail, scanner);

        verify(viewProductController, times(1)).viewAllProduct();
        verify(productCrudController, never()).findProductById(anyLong());
        verify(productCrudController, never()).updateProduct(any(Product.class), anyString());
    }

    @Test
    void subMenu_UpdateProductAndExit_Test() {
        String input = "2\n1\nТелевизор КВН-49\n99.99\nНикуда без аквариума\n10\n3\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        when(productCrudController.findProductById(1L)).thenReturn(existingProduct);

        updateProductItem.subMenu(tEmail, scanner);

        verify(productCrudController, times(2)).findProductById(1L);
        verify(productCrudController, times(1)).updateProduct(any(Product.class), eq(tEmail));
        verify(viewProductController, never()).viewAllProduct();
    }

    @Test
    void subMenu_MultipleOperations_Test() {
        String input = "1\n2\n1\nPhilco Predicta\n150.0\nПрощай глаза\n20\n3\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        when(productCrudController.findProductById(1L)).thenReturn(existingProduct);

        updateProductItem.subMenu(tEmail, scanner);

        verify(viewProductController, times(1)).viewAllProduct();
        verify(productCrudController, times(2)).findProductById(1L);
        verify(productCrudController, times(1)).updateProduct(any(Product.class), eq(tEmail));
    }

    @Test
    void subMenu_InvalidChoiceThenValid_Test() {
        String input = "5\n2\n1\nТелевизор\n100.0\nУгадай модель и...\n15\n3\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        when(productCrudController.findProductById(1L)).thenReturn(existingProduct);

        updateProductItem.subMenu(tEmail, scanner);

        verify(productCrudController, times(2)).findProductById(1L);
        verify(productCrudController, times(1)).updateProduct(any(Product.class), eq(tEmail));
    }

    @Test
    void subMenu_ImmediateExit_Test() {
        String input = "3\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        updateProductItem.subMenu(tEmail, scanner);

        verifyNoInteractions(productCrudController);
        verifyNoInteractions(viewProductController);
    }
}