package me.oldboy.market.menu;

import me.oldboy.market.config_context.ContextApp;
import me.oldboy.market.controlers.LoginLogoutController;
import me.oldboy.market.controlers.view.ViewAuditRecordController;
import me.oldboy.market.entity.User;
import me.oldboy.market.menu.items.*;
import me.oldboy.market.validator.InputValidator;

import java.util.Scanner;

/**
 * The main menu of the program for interaction with the user.
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
     * Constructor
     *
     * @param contextApp main application classes configurator
     */
    public MainMenu(ContextApp contextApp) {
        this.contextApp = contextApp;
        initComponent();
    }

    /**
     * Initialize application component (menu items)
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
                contextApp.getBrandRepository(),
                contextApp.getCategoryRepository());
        this.viewAuditRecordController = contextApp.getViewAuditRecordController();
    }

    /**
     * Start main menu method for login and product manage
     *
     * @param scanner scanner for console menu item selection
     * @return false after exit method (for possible outer manage)
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
     * Product manage CLI menu method
     *
     * @param email   email address of the authenticated user (for audit purposes)
     * @param scanner scanner for console menu item selection
     * @return true - using menu (1-6 items), false - exit menu (7)
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