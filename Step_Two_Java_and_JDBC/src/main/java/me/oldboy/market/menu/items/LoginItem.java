package me.oldboy.market.menu.items;

import lombok.AllArgsConstructor;
import me.oldboy.market.controlers.LoginLogoutController;
import me.oldboy.market.entity.User;
import me.oldboy.market.exceptions.WrongEnteredDataFormatException;
import me.oldboy.market.validator.InputValidator;

import java.util.Scanner;

/**
 * Класс консольного взаимодействия с пользователем для ввода логина и пароля при входе в систему
 */
@AllArgsConstructor
public class LoginItem {

    private LoginLogoutController loginLogoutController;

    /**
     * Метод консольного взаимодействия с пользователем через CLI меню
     *
     * @param scanner сканер для ввода пункта меню из консоли
     * @return User объект в случае удачного входа в систему, null - в противном случае
     */
    public User login(Scanner scanner) {
        Boolean isEntering = true;
        User loginUser = null;
        do {
            System.out.print("\nВыберите один из пунктов меню: " +
                    "\n1 - вход в систему;" +
                    "\n2 - выход из системы;" +
                    "\nСделайте выбор и нажмите ввод: ");
            String choiceMenuItem = scanner.nextLine().trim();

            switch (choiceMenuItem) {
                case "1":
                    String[] authData = enterLoginAndPassMenu(scanner);
                    loginUser = loginLogoutController.logIn(authData[0], authData[1]);
                    isEntering = false;
                    break;
                case "2":
                    loginUser = null;
                    isEntering = false;
                    break;
                default:
                    InputValidator.repeatEnterItem();
                    break;
            }
        } while (isEntering);
        return loginUser;
    }

    /**
     * Предназначен для обработки ввода логина и пароля
     *
     * @param scanner сканер для ввода данных из консоли
     * @return массив вводимых данных [email, password]
     */
    private String[] enterLoginAndPassMenu(Scanner scanner) throws WrongEnteredDataFormatException {
        String[] loginAndPass = new String[2];

        System.out.print("Введите email: ");
        loginAndPass[0] = InputValidator.emailValidator(scanner);

        System.out.print("Введите пароль: ");
        loginAndPass[1] = InputValidator.passwordValidator(scanner);

        return loginAndPass;
    }
}