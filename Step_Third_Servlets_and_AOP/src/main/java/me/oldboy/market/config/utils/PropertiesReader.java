package me.oldboy.market.config.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Реализация ConfigProvider для чтения конфигурации из properties-файлов.
 * Загружает настройки из файла application.properties в classpath.
 */
public class PropertiesReader implements ConfigProvider {
    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    /**
     * Возвращает значение свойства по ключу.
     *
     * @param key ключ свойства
     * @return значение свойства или null если ключ не найден
     */
    @Override
    public String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    /**
     * Загружает свойства из файла application.properties
     *
     * @throws RuntimeException если файл не найден или поврежден
     */
    private static void loadProperties() {
        try (InputStream inputStream = PropertiesReader.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load application.properties", e);
        }
    }
}