package me.oldboy.market.menu;

import me.oldboy.market.config.context.ContextApp;
import me.oldboy.market.controlers.LoginLogoutController;
import me.oldboy.market.controlers.view.ViewAuditRecordController;
import me.oldboy.market.entity.User;
import me.oldboy.market.menu.items.*;
import me.oldboy.market.validator.InputValidator;

import java.util.Scanner;

/**
 * Основное меню взаимодействия с пользователем
 */
public class MainMenu {
    private ContextApp contextApp;
    private LoginLogoutController loginLogoutController;
    private LoginItem loginMenu;
    private AddProductItem addProductItem;
    private ViewProductItem viewProductItem;
    private UpdateProductItem updateProductItem;
    private DeleteProductItem deleteProductItem;
    private FindProductItem findProductItem;
    private ViewAuditRecordController viewAuditRecordController;

    /**
     * Конструктор
     *
     * @param contextApp основной класс конфигуратор приложения
     */
    public MainMenu(ContextApp contextApp) {
        this.contextApp = contextApp;
        initComponent();
    }

    /**
     * Инициализация компонентов меню
     */
    private void initComponent() {
        this.loginLogoutController = contextApp.getLoginLogoutController();
        this.loginMenu = new LoginItem(loginLogoutController);
        this.addProductItem = new AddProductItem(contextApp.getProductCrudController(),
                contextApp.getViewBrandController(),
                contextApp.getViewCategoryController(),
                contextApp.getInputExistChecker());
        this.viewProductItem = new ViewProductItem(contextApp.getViewProductController());
        this.updateProductItem = new UpdateProductItem(contextApp.getProductCrudController(),
                contextApp.getBrandRepository(),
                contextApp.getCategoryRepository(),
                contextApp.getViewProductController());
        this.deleteProductItem = new DeleteProductItem(contextApp.getProductCrudController(),
                contextApp.getViewProductController());
        this.findProductItem = new FindProductItem(contextApp.getViewProductController(),
                contextApp.getViewCategoryController(),
                contextApp.getViewBrandController(),
                contextApp.getInputExistChecker());
        this.viewAuditRecordController = contextApp.getViewAuditRecordController();
    }

    /**
     * Запускает основное меню приложения
     *
     * @param scanner сканер для ввода данных из консоли (выбор пунктов меню)
     * @return false - всегда после завершения работы метода (для целей взаимодействия с внешним окружением)
     */
    public boolean startMainMenu(Scanner scanner) {
        Boolean repeatMenu = true;
        System.out.println("*** Добро пожаловать в систему управления товарами ***\n");
        System.out.println("---------------------------------------------------------------------");

        User loggedInUser = loginMenu.login(scanner);

        if (loggedInUser == null) {
            repeatMenu = false;
        } else {
            repeatMenu = true;
        }

        while (repeatMenu) {
            repeatMenu = productAndLogMenu(loggedInUser.getEmail(), scanner);
        }

        System.out.println("---------------------------------------------------------------------");
        System.out.println("Всего хорошего, ждем вас снова!\n");
        scanner.close();

        return false;
    }

    /**
     * Консольное меню CLI для управления товарами
     *
     * @param email   email адрес аутентифицированного пользователя (для целей аудита)
     * @param scanner сканер для ввода данных из консоли (выбор пунктов меню, ввод данных)
     * @return true - при работе "внутри меню" (1-6 items), false - при выходе из меню (7)
     */
    private boolean productAndLogMenu(String email, Scanner scanner) {
        boolean repeatMenu = true;
        String userMenu = "\nВыберите пункт меню: \n" +
                "1 - Добавить товар;\n" +
                "2 - Посмотреть товар(ы); \n" +
                "3 - Коррекция товара; \n" +
                "4 - Удалить товар; \n" +
                "5 - Найти товар; \n" +
                "6 - Посмотреть аудит-логи; \n" +
                "7 - Покинуть программу;\n\n" +
                "Сделайте выбор и нажмите ввод: ";
        System.out.print(userMenu);

        String command = scanner.nextLine().trim();

        switch (command) {
            case "1":
                addProductItem.subMenu(email, scanner);
                break;
            case "2":
                viewProductItem.subMenu(scanner);
                break;
            case "3":
                updateProductItem.subMenu(email, scanner);
                break;
            case "4":
                deleteProductItem.subMenu(email, scanner);
                break;
            case "5":
                findProductItem.subMenu(scanner);
                break;
            case "6":
                viewAuditRecordController.printAllAuditRecord();
                break;
            case "7":
                loginLogoutController.logOut(email);
                repeatMenu = false;
                break;
            default:
                InputValidator.repeatEnterItem();
                break;
        }
        return repeatMenu;
    }
}