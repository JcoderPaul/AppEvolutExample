package me.oldboy.market.menu.items;

import lombok.AllArgsConstructor;
import me.oldboy.market.controlers.view.ViewBrandController;
import me.oldboy.market.controlers.view.ViewCategoryController;
import me.oldboy.market.controlers.view.ViewProductController;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.repository.BrandRepository;
import me.oldboy.market.repository.CategoryRepository;
import me.oldboy.market.validator.InputValidator;

import java.util.Scanner;

/**
 * Класс консольного взаимодействия с пользователем для поиска товара с разными условиями
 */
@AllArgsConstructor
public class FindProductItem {
    private ViewProductController viewProductController;
    private ViewCategoryController viewCategoryController;
    private ViewBrandController viewBrandController;
    private BrandRepository brandRepository;
    private CategoryRepository categoryRepository;

    /**
     * Метод, консольного взаимодействия с пользователем,
     * выводящий меню для выбора способа поиска товара в
     * "кэше" БД.
     *
     * @param scanner сканер для ввода пункта меню из консоли
     */
    public void subMenu(Scanner scanner) {
        Boolean isEntering = true;
        System.out.println("-----------------------------------------------------------------------------");
        do {
            System.out.print("Варианты поиска товара : " +
                    "\n1 - найти товар по ID;" +
                    "\n2 - найти товар по категории и ID;" +
                    "\n3 - найти товар по брэнду и ID;" +
                    "\n4 - найти товар по брэнду и названию;" +
                    "\n5 - посмотреть все товары по категории;" +
                    "\n6 - посмотреть все товары по брэнду;" +
                    "\n7 - покинуть раздел;\n\n" +
                    "Сделайте выбор и нажмите ввод: ");
            String choiceMenuItem = scanner.nextLine().trim();

            switch (choiceMenuItem) {
                case "1":
                    findByIdChoice(scanner);
                    break;
                case "2":
                    findByCategoryAndIdChoice(scanner);
                    break;
                case "3":
                    findByBrandAndIdChoice(scanner);
                    break;
                case "4":
                    findByBrandAndNameChoice(scanner);
                    break;
                case "5":
                    findAllProductByCategoryChoice(scanner);
                    break;
                case "6":
                    findAllProductByBrandChoice(scanner);
                    break;
                case "7":
                    isEntering = false;
                    break;
                default:
                    InputValidator.repeatEnterItem();
                    break;
            }
        } while (isEntering);
    }

    /**
     * По запросу из консоли, ищет продукт по его уникальному ID и выводит на экран.
     *
     * @param scanner сканер для ввода из консоли ID удаляемого товара
     */
    private void findByIdChoice(Scanner scanner) {
        System.out.print("Введите ID искомого продукта: ");
        Long inputId = InputValidator.longValidator(scanner);
        viewProductController.viewProductById(inputId);
    }

    /**
     * По запросу из консоли, ищет продукт по уникальному ID категории, самого продукта и выводит на экран.
     *
     * @param scanner сканер для ввода из консоли ID искомого товара (продукта) и ID категории товара
     */
    private void findByCategoryAndIdChoice(Scanner scanner) {
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("\nДоступны категории: ");
        viewCategoryController.printAllCategory();
        System.out.println("-----------------------------------------------------------------------------");

        System.out.print("Введите ID категории: ");
        Integer inputCategoryId = InputValidator.intValidator(scanner);
        Category foundCat = categoryRepository.findById(inputCategoryId).get();

        System.out.print("Введите ID продукта: ");
        Long inputId = InputValidator.longValidator(scanner);
        viewProductController.findProductByCategoryAndId(foundCat, inputId);
    }

    /**
     * По запросу из консоли, ищет продукт по уникальному ID брэнда, самого продукта и выводит на экран.
     *
     * @param scanner сканер для ввода из консоли ID искомого товара (продукта) и ID брэнда товара
     */
    private void findByBrandAndIdChoice(Scanner scanner) {
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("\nДоступны брэнды: ");
        viewBrandController.printAllBrands();
        System.out.println("-----------------------------------------------------------------------------");
        System.out.print("Введите ID брэнда: ");
        Integer inputBrandId = InputValidator.intValidator(scanner);
        Brand foundBrand = brandRepository.findById(inputBrandId).get();
        System.out.print("Введите ID продукта: ");
        Long inputProductId = InputValidator.longValidator(scanner);
        viewProductController.findProductByBrandAndId(foundBrand, inputProductId);
    }

    /**
     * По запросу из консоли, ищет продукт по уникальному ID брэнда, названию продукта и выводит на экран.
     *
     * @param scanner сканер для ввода из консоли названия искомого товара (продукта) и ID брэнда товара
     */
    private void findByBrandAndNameChoice(Scanner scanner) {
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("\nДоступны брэнды: ");
        viewBrandController.printAllBrands();
        System.out.println("-----------------------------------------------------------------------------");

        System.out.print("Введите ID брэнда: ");
        Integer inputBrandId = InputValidator.intValidator(scanner);
        Brand foundBrand = brandRepository.findById(inputBrandId).get();

        System.out.print("Введите название продукта: ");
        String prodName = scanner.nextLine().trim();
        viewProductController.findProductByBrandAndName(foundBrand, prodName);
    }

    /**
     * По запросу из консоли, ищет все продукт по уникальному ID брэнда и выводит на экран.
     *
     * @param scanner сканер для ввода из консоли ID брэнда товара
     */
    private void findAllProductByBrandChoice(Scanner scanner) {
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("\nДоступны брэнды: ");
        viewBrandController.printAllBrands();
        System.out.println("-----------------------------------------------------------------------------");
        System.out.print("Введите ID брэнда: ");
        Integer inputBrandId = InputValidator.intValidator(scanner);
        Brand foundBrand = brandRepository.findById(inputBrandId).get();
        viewProductController.findProductByBrand(foundBrand);
    }

    /**
     * По запросу из консоли, ищет все продукт по уникальному ID категории товара и выводит на экран.
     *
     * @param scanner сканер для ввода из консоли ID категории товара
     */
    private void findAllProductByCategoryChoice(Scanner scanner) {
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("\nДоступны категории: ");
        viewCategoryController.printAllCategory();
        System.out.println("-----------------------------------------------------------------------------");
        System.out.print("Введите ID категории: ");
        Integer inputCategoryId = InputValidator.intValidator(scanner);
        Category foundCategory = categoryRepository.findById(inputCategoryId).get();
        viewProductController.findProductByCategory(foundCategory);
    }
}