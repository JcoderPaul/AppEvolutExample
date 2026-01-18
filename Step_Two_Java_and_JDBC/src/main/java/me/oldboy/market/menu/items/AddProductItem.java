package me.oldboy.market.menu.items;

import lombok.AllArgsConstructor;
import me.oldboy.market.controlers.ProductCrudController;
import me.oldboy.market.controlers.view.ViewBrandController;
import me.oldboy.market.controlers.view.ViewCategoryController;
import me.oldboy.market.entity.Product;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.validator.InputExistChecker;
import me.oldboy.market.validator.InputValidator;

import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * Класс консольного взаимодействия с пользователем для создания нового товара
 */
@AllArgsConstructor
public class AddProductItem {
    private ProductCrudController productCrudController;
    private ViewBrandController viewBrandController;
    private ViewCategoryController viewCategoryController;
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
        Category category = inputExistChecker.repeatsCategoryChoiceToExistingOne(scanner);

        System.out.print("\n- ID бренда (см. таблицу выше): ");
        Brand brand = inputExistChecker.repeatsBrandChoiceToExistingOne(scanner);

        System.out.print("\n- Опишите продукт: ");
        String description = scanner.nextLine().trim();

        System.out.print("\n- Введите количество продукта: ");
        Integer inputStockQuantity = InputValidator.intValidator(scanner);

        /*
        При создании продукта время его создания и обновления, в нашем понимании, "равны",
        однако ограничение в БД, "время модификации" > "времени создания", не позволяет
        сохранять равные значения. Поэтому вносим малое смещение - 1 сек.
        */
        Product newProduct = Product.builder()
                .name(productName)
                .price(inputPrice)
                .categoryId(category.getId())
                .brandId(brand.getId())
                .description(description)
                .stockQuantity(inputStockQuantity)
                .creationAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now().plusSeconds(1))
                .build();

        productCrudController.createProduct(newProduct, email);

        return newProduct;
    }
}