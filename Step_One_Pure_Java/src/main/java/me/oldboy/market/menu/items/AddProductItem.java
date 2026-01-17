package me.oldboy.market.menu.items;

import lombok.AllArgsConstructor;
import me.oldboy.market.controlers.ProductCrudController;
import me.oldboy.market.controlers.view.ViewBrandController;
import me.oldboy.market.controlers.view.ViewCategoryController;
import me.oldboy.market.entity.Product;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.repository.BrandRepository;
import me.oldboy.market.repository.CategoryRepository;
import me.oldboy.market.validator.InputExistChecker;
import me.oldboy.market.validator.InputValidator;

import java.util.Date;
import java.util.Scanner;

/**
 * Класс консольного взаимодействия с пользователем для создания нового товара
 */
@AllArgsConstructor
public class AddProductItem {

    private ProductCrudController productCrudController;
    private ViewBrandController viewBrandController;
    private ViewCategoryController viewCategoryController;
//    private CategoryRepository categoryRepository;
//    private BrandRepository brandRepository;
    private InputExistChecker inputExistChecker;

    /**
     * Метод предварительного сбора данных о новом товаре заводимом
     * в систему для последующей передачи ее на слой контроллеров.
     *
     * @param email   электронный адрес пользователя, который завел новый товар
     * @param scanner сканер для ввода данных из консоли
     * @return новый объект товара Product с введенными данными
     */
    public Product subMenu(String email, Scanner scanner) {
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("\nВам доступны бренды: ");
        viewBrandController.printAllBrands();
        System.out.println("\nВам доступны категории: ");
        viewCategoryController.printAllCategory();
        System.out.println("\n *** Добавление нового товара *** \n");
        System.out.print("\nВведите данные на новый продукт: ");

        System.out.print("\n- Название продукта: ");
        String productName = scanner.nextLine().trim();

        System.out.print("\n- Цена продукта (пример 12.2): ");
        Double inputPrice = InputValidator.doubleValidator(scanner);

        System.out.print("\n- ID категории (см. таблицу выше): ");
        Category inputCategory = inputExistChecker.repeatsCategoryChoiceToExistingOne(scanner);

        System.out.print("\n- ID бренда (см. таблицу выше): ");
        Brand inputBrand = inputExistChecker.repeatsBrandChoiceToExistingOne(scanner);

        System.out.print("\n- Опишите продукт: ");
        String description = scanner.nextLine().trim();

        System.out.print("\n- Введите количество продукта: ");
        Integer inputStockQuantity  = InputValidator.intValidator(scanner);

        /* Без валидации, для экономии времени - считаем, что пользователь не совершает ошибок при печати */
        Product newProduct = Product.builder()
                .name(productName)
                .price(inputPrice)
                .category(inputCategory)
                .brand(inputBrand)
                .description(description)
                .stockQuantity(inputStockQuantity)
                .creationTimestamp(new Date().getTime())
                .build();

        productCrudController.createProduct(newProduct, email);

        return newProduct;
    }
}