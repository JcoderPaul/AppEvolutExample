package me.oldboy.market;

import me.oldboy.market.config.tomcat.EmbeddedTomcat;
import me.oldboy.market.config.utils.ConfigProvider;
import me.oldboy.market.config.utils.PropertiesReader;
import org.apache.catalina.startup.Tomcat;

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
        /* Создаем и настраиваем экземпляр TomCat */
        Tomcat tomcat = new Tomcat();
        EmbeddedTomcat embeddedTomcat = new EmbeddedTomcat(tomcat, configProvider);

        /*
        Запускаем TomCat контейнер в параллельном потоке, на случай
        если появится нужда в реализации его "мягкого" останова -
        метод есть.
        */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    embeddedTomcat.startServer();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        System.out.println("\n*** Программа завершена ***");
    }
}