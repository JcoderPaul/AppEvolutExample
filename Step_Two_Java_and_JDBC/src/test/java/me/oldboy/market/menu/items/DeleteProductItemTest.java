package me.oldboy.market.menu.items;

import me.oldboy.market.controlers.ProductCrudController;
import me.oldboy.market.controlers.view.ViewProductController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteProductItemTest {
    @Mock
    private ProductCrudController productCrudController;
    @Mock
    private ViewProductController viewProductController;
    @InjectMocks
    private DeleteProductItem deleteProductItem;

    private Scanner scanner;
    private final String testEmail = "test@market.ru";
    private final Long id_1 = 100L;
    private final Long id_2 = 50L;

    @Test
    void testSubMenu_ViewProductsItem_Test() {
        /* Имитируем ввод -> Просмотреть товары -> Выйти */
        String input = "1\n3\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        /* Вызов метода */
        deleteProductItem.subMenu(testEmail, scanner);

        verify(viewProductController, times(1)).viewAllProduct();
        verify(productCrudController, never()).deleteProduct(anyLong(), anyString());
    }

    @Test
    void testSubMenu_DeleteProductAndExit() {
        String input = "2\n100\n3\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        when(productCrudController.deleteProduct(id_1, testEmail)).thenReturn(true);

        deleteProductItem.subMenu(testEmail, scanner);

        verify(productCrudController, times(1)).deleteProduct(id_1, testEmail);
        verify(viewProductController, never()).viewAllProduct();
    }

    @Test
    void testSubMenu_MultipleOperations_Test() {
        /*  Просмотр -> Удалить 100 -> Просмотр -> Удалить 50 -> Выйти */
        String input = "1\n2\n100\n1\n2\n50\n3\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        /* Два прохода по меню */
        when(productCrudController.deleteProduct(id_1, testEmail)).thenReturn(true);
        when(productCrudController.deleteProduct(id_2, testEmail)).thenReturn(false);

        deleteProductItem.subMenu(testEmail, scanner);

        verify(viewProductController, times(2)).viewAllProduct();
        verify(productCrudController, times(1)).deleteProduct(id_1, testEmail);
        verify(productCrudController, times(1)).deleteProduct(id_2, testEmail);
    }
}