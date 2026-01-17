package me.oldboy.market.menu.items;

import lombok.AllArgsConstructor;
import me.oldboy.market.controlers.ProductCrudController;
import me.oldboy.market.controlers.view.ViewBrandController;
import me.oldboy.market.controlers.view.ViewCategoryController;
import me.oldboy.market.controlers.view.ViewProductController;
import me.oldboy.market.entity.Product;
import me.oldboy.market.repository.BrandRepository;
import me.oldboy.market.repository.CategoryRepository;
import me.oldboy.market.validator.InputValidator;

import java.util.Date;
import java.util.Scanner;

/**
 * Класс консольного взаимодействия с пользователем для целей обновления продукта (товара, Product)
 */
@AllArgsConstructor
public class UpdateProductItem {
    private ProductCrudController productCrudController;
    private BrandRepository brandRepository;
    private CategoryRepository categoryRepository;
    private ViewProductController viewProductController;

    /**
     * Метод консольного взаимодействия с пользователем через CLI меню
     * для целей просмотра списка продуктов и обновления выбранного.
     *
     * @param email   электронный адрес пользователя, который обновляет продукт
     * @param scanner сканер для консольного выбора пункта меню
     */
    public void subMenu(String email, Scanner scanner) {
        Boolean isEntering = true;
        System.out.println("-----------------------------------------------------------------------------");
        do {
            System.out.print("Выберите товар по ID : " +
                    "\n1 - просмотреть список товаров доступных для обновления;" +
                    "\n2 - выбрать товар для обновления по ID;" +
                    "\n3 - покинуть раздел;\n\n" +
                    "Сделайте выбор и нажмите ввод: ");
            String choiceMenuItem = scanner.nextLine().trim();

            switch (choiceMenuItem) {
                case "1":
                    viewProductController.viewAllProduct();
                    break;
                case "2":
                    selectProductForUpdate(email, scanner);
                    break;
                case "3":
                    isEntering = false;
                    break;
                default:
                    InputValidator.repeatEnterItem();
                    break;
            }
        } while (isEntering);
    }

    /**
     * Метод предварительного сбора данных об обновляемом товаре в
     * системе для последующей передачи их на слой контроллеров.
     *
     * @param email   электронный адрес пользователя, который обновляет товар
     * @param scanner сканер для ввода обновляемых данных из консоли
     */
    private Product selectProductForUpdate(String email, Scanner scanner) {
        ViewBrandController viewBrandController = new ViewBrandController(brandRepository);
        ViewCategoryController viewCategoryController = new ViewCategoryController(categoryRepository);
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("\nВам доступны бренды: ");
        viewBrandController.printAllBrands();
        System.out.println("\nВам доступны категории: ");
        viewCategoryController.printAllCategory();
        System.out.println("-----------------------------------------------------------------------------");

        System.out.print("Введите ID изменяемого продукта: ");
        Long inputProductId = InputValidator.longValidator(scanner);
        System.out.println(productCrudController.findProductById(inputProductId));
        System.out.println("-----------------------------------------------------------------------------");

        System.out.print("\nВведите новую информацию на выбранный продукт: ");
        System.out.print("\n- Название продукта: ");
        String productName = scanner.nextLine().trim();
        System.out.print("\n- Цена продукта (пример 12.2): ");
        Double inputPrice = InputValidator.doubleValidator(scanner);
        System.out.print("\n- Опишите продукт: ");
        String description = scanner.nextLine().trim();
        System.out.print("\n- Введите количество продукта: ");
        Integer inputStockQuantity = InputValidator.intValidator(scanner);

        Product forUpdateProduct = productCrudController.findProductById(inputProductId);

        /* Без валидации, для экономии времени - считаем, что пользователь не совершает ошибок при печати */
        Product updatedData = Product.builder()
                .id(forUpdateProduct.getId())
                .name(productName)
                .price(inputPrice)
                .category(forUpdateProduct.getCategory())
                .brand(forUpdateProduct.getBrand())
                .description(description)
                .stockQuantity(inputStockQuantity)
                .creationTimestamp(forUpdateProduct.getCreationTimestamp())
                .lastModifiedTimestamp(new Date().getTime())
                .build();

        productCrudController.updateProduct(updatedData, email);

        return updatedData;
    }
}