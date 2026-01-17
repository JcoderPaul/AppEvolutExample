package me.oldboy.market.menu.items;

import me.oldboy.market.controlers.view.ViewBrandController;
import me.oldboy.market.controlers.view.ViewCategoryController;
import me.oldboy.market.controlers.view.ViewProductController;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.repository.BrandRepository;
import me.oldboy.market.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Scanner;

import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class FindProductItemTest {
    @Mock
    private ViewProductController viewProductController;

    @Mock
    private ViewCategoryController viewCategoryController;

    @Mock
    private ViewBrandController viewBrandController;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private FindProductItem findProductItem;

    private Scanner scanner;

    private final Brand testBrand = Brand.builder().id(1).name("Test Brand").build();
    private final Category testCategory = Category.builder().id(1).name("Test Category").build();

    @Test
    void subMenu_FindById_Test() {
        /* Найти по ID -> ID 1 -> Выйти (7) */
        String input = "1\n1\n7\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        findProductItem.subMenu(scanner);

        verify(viewProductController, times(1)).viewProductById(1L);
    }

    @Test
    void subMenu_FindByCategoryAndId_Test() {
        /* Найти по категории и ID -> (категория) 1 -> (продукт) 2 -> Выйти */
        String input = "2\n1\n2\n7\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        when(categoryRepository.findById(1)).thenReturn(Optional.of(testCategory));

        findProductItem.subMenu(scanner);

        verify(viewCategoryController, times(1)).printAllCategory();
        verify(categoryRepository, times(1)).findById(1);
        verify(viewProductController, times(1))
                .findProductByCategoryAndId(testCategory, 2L);
    }

    @Test
    void subMenu_FindByBrandAndId_Test() {
        /* Найти по бренду и ID -> (бренд) 2 -> (продукт) 100 -> Выйти */
        String input = "3\n2\n100\n7\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        when(brandRepository.findById(2)).thenReturn(Optional.of(testBrand));

        findProductItem.subMenu(scanner);

        verify(viewBrandController, times(1)).printAllBrands();
        verify(brandRepository, times(1)).findById(2);
        verify(viewProductController, times(1))
                .findProductByBrandAndId(testBrand, 100L);
    }

    @Test
    void subMenu_FindByBrandAndName_Test() {
        /* Найти по бренду и названию -> (бренд) 3 -> название -> Выйти */
        String input = "4\n3\nTest Product\n7\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        when(brandRepository.findById(3)).thenReturn(Optional.of(testBrand));

        findProductItem.subMenu(scanner);

        verify(viewBrandController, times(1)).printAllBrands();
        verify(brandRepository, times(1)).findById(3);
        verify(viewProductController, times(1))
                .findProductByBrandAndName(testBrand, "Test Product");
    }

    @Test
    void subMenu_FindAllByCategory_Test() {
        /* Все товары по категории -> (категория) 4 -> Выйти */
        String input = "5\n4\n7\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        when(categoryRepository.findById(4)).thenReturn(Optional.of(testCategory));

        findProductItem.subMenu(scanner);

        verify(viewCategoryController, times(1)).printAllCategory();
        verify(categoryRepository, times(1)).findById(4);
        verify(viewProductController, times(1)).findProductByCategory(testCategory);
    }

    @Test
    void subMenu_FindAllByBrand_Test() {
        /* Все товары по бренду -> (бренд) 5 -> Выйти */
        String input = "6\n5\n7\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        when(brandRepository.findById(5)).thenReturn(Optional.of(testBrand));

        findProductItem.subMenu(scanner);

        verify(viewBrandController, times(1)).printAllBrands();
        verify(brandRepository, times(1)).findById(5);
        verify(viewProductController, times(1)).findProductByBrand(testBrand);
    }

    @Test
    void subMenu_ImmediateExit_Test() {
        /* Сразу выход */
        String input = "7\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        findProductItem.subMenu(scanner);

        verifyNoInteractions(viewProductController, viewCategoryController, viewBrandController);
        verifyNoInteractions(brandRepository, categoryRepository);
    }

    @Test
    void subMenu_InvalidChoiceThenValid_Test() {
        /* Неверный ввод -> Несуществующий пункт -> Найти по ID -> Выйти */
        String input = "invalid\n8\n1\n999\n7\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        findProductItem.subMenu(scanner);

        verify(viewProductController, times(1)).viewProductById(999L);
    }

    /* --- Тесты приватных методов через Reflection API --- */

    @Test
    void findByIdChoice_Test() throws Exception {
        String input = "123\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        Method method = FindProductItem.class.getDeclaredMethod("findByIdChoice", Scanner.class);
        method.setAccessible(true);

        method.invoke(findProductItem, scanner);

        verify(viewProductController, times(1)).viewProductById(123L);
    }

    @Test
    void findByCategoryAndIdChoice_Test() throws Exception {
        String input = "1\n456\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        when(categoryRepository.findById(1)).thenReturn(Optional.of(testCategory));

        Method method = FindProductItem.class.getDeclaredMethod("findByCategoryAndIdChoice", Scanner.class);
        method.setAccessible(true);

        method.invoke(findProductItem, scanner);

        verify(viewCategoryController, times(1)).printAllCategory();
        verify(categoryRepository, times(1)).findById(1);
        verify(viewProductController, times(1)).findProductByCategoryAndId(testCategory, 456L);
    }

    @Test
    void findByBrandAndNameChoice_Test() throws Exception {
        String input = "2\nTest Product Name\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        when(brandRepository.findById(2)).thenReturn(Optional.of(testBrand));

        Method method = FindProductItem.class.getDeclaredMethod("findByBrandAndNameChoice", Scanner.class);
        method.setAccessible(true);

        method.invoke(findProductItem, scanner);

        verify(viewBrandController, times(1)).printAllBrands();
        verify(brandRepository, times(1)).findById(2);
        verify(viewProductController, times(1)).findProductByBrandAndName(testBrand, "Test Product Name");
    }

    @Test
    void findAllProductByCategoryChoice_Test() throws Exception {
        String input = "3\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        when(categoryRepository.findById(3)).thenReturn(Optional.of(testCategory));

        Method method = FindProductItem.class.getDeclaredMethod("findAllProductByCategoryChoice", Scanner.class);
        method.setAccessible(true);

        method.invoke(findProductItem, scanner);

        verify(viewCategoryController, times(1)).printAllCategory();
        verify(categoryRepository, times(1)).findById(3);
        verify(viewProductController, times(1)).findProductByCategory(testCategory);
    }

    @Test
    void findAllProductByBrandChoice_Test() throws Exception {
        String input = "4\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        when(brandRepository.findById(4)).thenReturn(Optional.of(testBrand));

        Method method = FindProductItem.class.getDeclaredMethod("findAllProductByBrandChoice", Scanner.class);
        method.setAccessible(true);

        method.invoke(findProductItem, scanner);

        verify(viewBrandController, times(1)).printAllBrands();
        verify(brandRepository, times(1)).findById(4);
        verify(viewProductController, times(1)).findProductByBrand(testBrand);
    }
}