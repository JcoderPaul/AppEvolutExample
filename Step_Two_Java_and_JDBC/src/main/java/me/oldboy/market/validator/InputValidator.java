package me.oldboy.market.validator;

import me.oldboy.market.exceptions.WrongEnteredDataFormatException;

import java.util.Scanner;

/**
 * Утилитный класс для интерактивной валидации пользовательского ввода в консоли приложения.
 * Все методы являются статическими и обеспечивают повторный запрос ввода до получения валидных данных.
 *
 * @see WrongEnteredDataFormatException
 */
public class InputValidator {
    /**
     * Валидирует ввод целого числа с повторными запросами при ошибках.
     *
     * @param scanner сканер для чтения пользовательского ввода
     * @return валидное целое число
     */
    public static Integer intValidator(Scanner scanner) {
        while (true) {
            try {
                String inputStr = scanner.nextLine().trim();
                return Integer.parseInt(inputStr);
            } catch (NumberFormatException e) {
                System.out.println("\nОшибка ввода!\nВведите целое число:");
            }
        }
    }

    /**
     * Валидирует ввод числа с плавающей точкой с повторными запросами при ошибках.
     *
     * @param scanner сканер для чтения пользовательского ввода
     * @return валидное число с плавающей точкой
     */
    public static Double doubleValidator(Scanner scanner) {
        while (true) {
            try {
                String inputStr = scanner.nextLine().trim();
                return Double.parseDouble(inputStr);
            } catch (NumberFormatException e) {
                System.out.println("\nОшибка ввода!\nВведите число с точкой (пример 99.9):");
            }
        }
    }

    /**
     * Валидирует ввод Long числа с повторными запросами при ошибках.
     *
     * @param scanner сканер для чтения пользовательского ввода
     * @return валидное Long целое число
     */
    public static Long longValidator(Scanner scanner) {
        while (true) {
            try {
                String inputStr = scanner.nextLine().trim();
                return Long.parseLong(inputStr);
            } catch (NumberFormatException e) {
                System.out.println("\nОшибка ввода!\nВведите число:");
            }
        }
    }

    /**
     * Валидирует ввод email адреса по регулярному выражению.
     *
     * @param scanner сканер для чтения пользовательского ввода
     * @return валидный email адрес
     */
    public static String emailValidator(Scanner scanner) {
        while (true) {
            try {
                String inputStr = scanner.nextLine().trim();
                if (inputStr.matches("\\w+@\\w+.(ru|com)")) {
                    return inputStr;
                } else {
                    throw new WrongEnteredDataFormatException("\nОшибка ввода!\nВведите email (пример формата ввода test@test.ru):");
                }
            } catch (WrongEnteredDataFormatException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Валидирует ввод пароля. Проверяет, что пароль не состоит только из пробелов.
     *
     * @param scanner сканер для чтения пользовательского ввода
     * @return валидный пароль
     */
    public static String passwordValidator(Scanner scanner) {
        while (true) {
            try {
                String inputStr = scanner.nextLine().trim();
                if (!inputStr.matches("\\s+")) {
                    return inputStr;
                } else {
                    throw new WrongEnteredDataFormatException("Password cannot contain only spaces! Повторите ввод:");
                }
            } catch (WrongEnteredDataFormatException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Выводит стандартное сообщение о необходимости повторного ввода пункта меню.
     */
    public static void repeatEnterItem() {
        System.out.println("Пункт меню не выбран, сделайте выбор и повторите ввод.");
    }
}