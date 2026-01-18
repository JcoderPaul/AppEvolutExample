package me.oldboy.market;

import me.oldboy.market.config.connection.ConnectionManager;
import me.oldboy.market.config.context.ContextApp;
import me.oldboy.market.config.liquibase.LiquibaseManager;
import me.oldboy.market.config.utils.ConfigProvider;
import me.oldboy.market.config.utils.PropertiesReader;
import me.oldboy.market.menu.MainMenu;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Главный класс приложения "Маркетплейс".
 * Содержит точку входа и "оркестратор" процесса запуска системы.
 */
public class MainApp {
    /**
     * Точка входа в приложение. Выполняет инициализацию системы и запуск главного меню.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        System.out.println("*** Старт программы ***");

        ConfigProvider configProvider = new PropertiesReader();
        try (Connection connection = ConnectionManager.getBaseConnection(configProvider)) {
            LiquibaseManager.getInstance(configProvider).migrationsStart(connection);

            ContextApp mainContext = new ContextApp(connection);

            String consoleEncoding = System.getProperty("sun.jnu.encoding", "windows-1251");
            String inputEncoding = "Cp1251".equalsIgnoreCase(consoleEncoding) ? "windows-1251" : "UTF-8";
            Scanner scanner = new Scanner(System.in, inputEncoding);

            MainMenu mainMenu = new MainMenu(mainContext);
            mainMenu.startMainMenu(scanner);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        System.out.println("\n*** Программа завершена ***");
    }
}