package me.oldboy.market.menu.items;

import lombok.AllArgsConstructor;
import me.oldboy.market.controlers.ProductCrudController;
import me.oldboy.market.controlers.view.ViewProductController;
import me.oldboy.market.validator.InputValidator;

import java.util.Scanner;

/**
 * Класс консольного взаимодействия с пользователем для удаления товара
 */
@AllArgsConstructor
public class DeleteProductItem {

    private ProductCrudController productCrudController;
    private ViewProductController viewProductController;

    /**
     * Метод предварительного сбора данных об удаляемом товаре из
     * системы для последующей передачи ее на слой контроллеров.
     *
     * @param email   электронный адрес пользователя, который удаляет товар
     * @param scanner сканер для ввода данных из консоли
     */
    public void subMenu(String email, Scanner scanner) {
        Boolean isEntering = true;
        System.out.println("-----------------------------------------------------------------------------");
        do {
            System.out.print("Выберите товар по ID : " +
                    "\n1 - просмотреть список товаров доступных для удаления;" +
                    "\n2 - выбрать товар для удаления по ID;" +
                    "\n3 - покинуть раздел;\n\n" +
                    "Сделайте выбор и нажмите ввод: ");
            String choiceMenuItem = scanner.nextLine().trim();

            switch (choiceMenuItem) {
                case "1":
                    viewProductController.viewAllProduct();
                    break;
                case "2":
                    selectProductForDelete(email, scanner);
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
     * Метод позволяет через Scanner ввести ID удаляемого товара
     *
     * @param email   электронный адрес пользователя, который удаляет товар
     * @param scanner сканер для ввода данных из консоли
     * @return true - товар удален, в противном случае - false
     */
    private boolean selectProductForDelete(String email, Scanner scanner) {
        System.out.print("Введите ID удаляемого продукта: ");
        Long inputProductId = InputValidator.longValidator(scanner);

        return productCrudController.deleteProduct(inputProductId, email);
    }
}