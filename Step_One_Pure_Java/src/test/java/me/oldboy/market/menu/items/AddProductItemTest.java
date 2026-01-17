package me.oldboy.market.menu.items;

import me.oldboy.market.controlers.ProductCrudController;
import me.oldboy.market.controlers.view.ViewBrandController;
import me.oldboy.market.controlers.view.ViewCategoryController;
import me.oldboy.market.entity.Product;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.repository.BrandRepository;
import me.oldboy.market.repository.CategoryRepository;
import me.oldboy.market.validator.InputExistChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.util.Optional;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddProductItemTest {
    @Mock
    private ProductCrudController productCrudController;
    @Mock
    private ViewBrandController viewBrandController;
    @Mock
    private ViewCategoryController viewCategoryController;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BrandRepository brandRepository;
    @Mock
    private InputExistChecker inputExistChecker;
    private AddProductItem addProductItem;

    private Scanner scanner;
    private final String testEmail = "test@market.ru";

    @BeforeEach
    void setUp() {
        inputExistChecker = new InputExistChecker(categoryRepository, brandRepository);
        addProductItem = new AddProductItem(productCrudController, viewBrandController,
                viewCategoryController, inputExistChecker);
    }

    @Test
    void testSubMenu_SuccessfulProductCreation_Test() {
        /* Имитируем ввод с клавиатуры */
        String input = "Рюкзак\n100.50\n1\n2\nОчень крутой рюкзак\n10\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        Category mockCategory = Category.builder().id(1).name("Снаряжение").build();
        Brand mockBrand = Brand.builder().id(2).name("Contact").build();

        when(categoryRepository.findById(1)).thenReturn(Optional.of(mockCategory));
        when(brandRepository.findById(2)).thenReturn(Optional.of(mockBrand));

        /* Запускаем тестируемый метод */
        Product result = addProductItem.subMenu(testEmail, scanner);

        /* Проверяем утверждения */
        assertThat(result).isNotNull();
        assertThat("Рюкзак").isEqualTo(result.getName());
        assertThat(100.50).isEqualTo(result.getPrice());
        assertThat(mockCategory).isEqualTo(result.getCategory());
        assertThat(mockBrand).isEqualTo(result.getBrand());
        assertThat("Очень крутой рюкзак").isEqualTo(result.getDescription());
        assertThat(10).isEqualTo(result.getStockQuantity());

        verify(viewBrandController, times(1)).printAllBrands();
        verify(viewCategoryController, times(1)).printAllCategory();
        verify(productCrudController, times(1)).createProduct(any(Product.class), eq(testEmail));
    }

    @Test
    void testSubMenu_InvalidCategoryId_Test() {
        /* Имитируем ввод с клавиатуры */
        String input = "Рюкзак\n100.50\n1\n2\nОчень крутой рюкзак\n10\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        when(categoryRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addProductItem.subMenu(testEmail, scanner))
                .isInstanceOf(RuntimeException.class);
    }
}