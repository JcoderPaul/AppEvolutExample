package me.oldboy.market.menu.items;

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
class ViewProductItemTest {

    @Mock
    private ViewProductController viewProductController;

    @InjectMocks
    private ViewProductItem viewProductItem;

    private Scanner scanner;

    @Test
    void subMenu_ViewOneProduct_Test() {
        /* Найти по ID -> ID 123 -> Выйти */
        String input = "1\n123\n3\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        viewProductItem.subMenu(scanner);

        verify(viewProductController, times(1)).viewProductById(123L);
        verify(viewProductController, never()).viewAllProduct();
    }

    @Test
    void subMenu_ViewAllProducts_Test() {
        /* Посмотреть все товары -> Выйти */
        String input = "2\n3\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        viewProductItem.subMenu(scanner);

        verify(viewProductController, times(1)).viewAllProduct();
        verify(viewProductController, never()).viewProductById(anyLong());
    }

    @Test
    void subMenu_ImmediateExit_Test() {
        /* Сразу выйти */
        String input = "3\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        viewProductItem.subMenu(scanner);

        verifyNoInteractions(viewProductController);
    }

    @Test
    void subMenu_MultipleOperations_Test() {
        /* Найти ID 100 -> Все товары -> Найти ID 200 -> Выйти */
        String input = "1\n100\n2\n1\n200\n3\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        viewProductItem.subMenu(scanner);

        verify(viewProductController, times(1)).viewProductById(100L);
        verify(viewProductController, times(1)).viewProductById(200L);
        verify(viewProductController, times(1)).viewAllProduct();
    }

    @Test
    void subMenu_InvalidChoiceThenValid_Test() {
        /* Неверный выбор -> Найти по ID -> Выйти */
        String input = "5\ninvalid\n1\n999\n3\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        viewProductItem.subMenu(scanner);

        verify(viewProductController, times(1)).viewProductById(999L);
        verify(viewProductController, never()).viewAllProduct();
    }

    @Test
    void subMenu_EmptyInputThenValid_Test() {
        /* Пустой ввод -> Все товары -> Пустой ввод -> Выйти */
        String input = "\n\n2\n\n3\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        viewProductItem.subMenu(scanner);

        verify(viewProductController, times(1)).viewAllProduct();
        verify(viewProductController, never()).viewProductById(anyLong());
    }

    @Test
    void subMenu_CycleThroughAllOptions_Test() {
        String input = "1\n111\n2\n1\n222\n1\n333\n3\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        viewProductItem.subMenu(scanner);

        verify(viewProductController, times(1)).viewProductById(111L);
        verify(viewProductController, times(1)).viewProductById(222L);
        verify(viewProductController, times(1)).viewProductById(333L);
        verify(viewProductController, times(1)).viewAllProduct();
    }
}