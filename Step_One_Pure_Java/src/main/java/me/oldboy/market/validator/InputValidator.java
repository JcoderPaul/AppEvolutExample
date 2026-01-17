package me.oldboy.market.validator;

import java.util.Scanner;

public class InputValidator {
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

    public static void repeatEnterItem() {
        System.out.println("Пункт меню не выбран, сделайте выбор и повторите ввод.");
    }
}