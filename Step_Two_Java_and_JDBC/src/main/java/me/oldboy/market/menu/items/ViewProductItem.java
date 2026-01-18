package me.oldboy.market.menu.items;

import lombok.AllArgsConstructor;
import me.oldboy.market.controlers.view.ViewProductController;
import me.oldboy.market.validator.InputValidator;

import java.util.Scanner;

/**
 * Класс консольного взаимодействия с пользователем для целей просмотра доступных продуктов (Product)
 */
@AllArgsConstructor
public class ViewProductItem {

    private ViewProductController viewProductController;

    /**
     * Метод консольного взаимодействия с пользователем через CLI меню
     * для целей просмотра списка продуктов и одного выбранного по ID.
     *
     * @param scanner сканер для консольного выбора пункта меню
     */
    public void subMenu(Scanner scanner) {
        Boolean isEntering = true;
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("\n *** Просмотр товаров *** \n");
        do {
            System.out.print("Выберите один из пунктов меню: " +
                    "\n1 - найти товар по ID;" +
                    "\n2 - посмотреть список всех товаров;" +
                    "\n3 - покинуть раздел;\n\n" +
                    "Сделайте выбор и нажмите ввод: ");
            String choiceMenuItem = scanner.nextLine().trim();

            switch (choiceMenuItem) {
                case "1":
                    viewOneProduct(scanner);
                    break;
                case "2":
                    viewProductController.viewAllProduct();
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
     * Метод консольного взаимодействия с пользователем для выбора
     * просматриваемого по ID продукта.
     *
     * @param scanner сканер для ввода идентификатора ID просматриваемого продукта
     */
    private void viewOneProduct(Scanner scanner) {
        System.out.print("Введите ID интересующего продукта: ");
        Long inputProductId = InputValidator.longValidator(scanner);
        viewProductController.viewProductById(inputProductId);
    }
}