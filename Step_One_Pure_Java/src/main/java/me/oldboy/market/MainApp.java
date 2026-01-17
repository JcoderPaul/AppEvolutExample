package me.oldboy.market;

import me.oldboy.market.cache_bd.loaders.AuditDBLoader;
import me.oldboy.market.cache_bd.loaders.ProductBDLoader;
import me.oldboy.market.config_context.ContextApp;
import me.oldboy.market.menu.MainMenu;

import java.util.Scanner;

/**
 * Основной запускаемый класс (точка входя в приложение)
 */
public class MainApp {
    public static void main(String[] args) {
        System.out.println("*** Старт программы ***");

        ContextApp mainContext = new ContextApp();

        String consoleEncoding = System.getProperty("sun.jnu.encoding", "windows-1251");
        String inputEncoding = "Cp1251".equalsIgnoreCase(consoleEncoding) ? "windows-1251" : "UTF-8";
        Scanner scanner = new Scanner(System.in, inputEncoding);

        MainMenu mainMenu = new MainMenu(mainContext);
        mainMenu.startMainMenu(scanner);

        /* Сохраняем данные в файлы */
        AuditDBLoader.writeToExternalFile(mainContext.getAuditDB());
        ProductBDLoader.writeToExternalFile(mainContext.getProductDB());

        System.out.println("\n*** Программа завершена ***");
    }
}
