package me.oldboy.market.config.connection;

import me.oldboy.market.config.utils.ConfigProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Класс для соединения с БД
 */
public class ConnectionManager {
    private final static String LOGIN_KEY = "db.username";
    private final static String PASS_KEY = "db.password";
    private final static String BASEURL_KEY = "db.url";

    static {
        loadDriver();
    }

    private ConnectionManager() {
    }

    /**
     * Создает и возвращает соединение с базой данных используя параметры из конфигурации.
     *
     * @param configProvider поставщик конфигурационных параметров для подключения к БД
     * @return экземпляр единичного соединения с БД - активное соединение
     * @throws RuntimeException если происходит ошибка SQL при установке соединения
     */
    public static Connection getBaseConnection(ConfigProvider configProvider) {
        /* Валидация параметров перед попыткой подключения */
        validateConfiguration(configProvider);
        try {
            Connection connection = DriverManager.getConnection(configProvider.get(BASEURL_KEY),
                    configProvider.get(LOGIN_KEY),
                    configProvider.get(PASS_KEY));
            return connection;
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Получает соединение с тестовой базой данных (предназначен для работы в тестах)
     *
     * @param baseUrl      адрес тестовой БД
     * @param userLogin    логин к тестовой БД
     * @param userPassword пароль к тестовой БД
     * @return экземпляр единичного соединения с тестовой БД
     * @throws RuntimeException если происходит ошибка SQL при установке соединения
     */
    public static Connection getTestBaseConnection(String baseUrl, String userLogin, String userPassword) {
        try {
            Connection connection = DriverManager.getConnection(baseUrl, userLogin, userPassword);
            return connection;
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Загрузчик драйвера для связи с БД
     */
    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Проверяет наличие всех обязательных параметров конфигурации
     *
     * @param configProvider поставщик конфигурационных параметров для подключения к БД
     */
    private static void validateConfiguration(ConfigProvider configProvider) {
        if (configProvider.get(BASEURL_KEY) == null) {
            throw new IllegalStateException("Database URL is not configured (key: " + BASEURL_KEY + ")");
        }
        if (configProvider.get(LOGIN_KEY) == null) {
            throw new IllegalStateException("Database login is not configured (key: " + LOGIN_KEY + ")");
        }
        if (configProvider.get(PASS_KEY) == null) {
            throw new IllegalStateException("Database password is not configured (key: " + PASS_KEY + ")");
        }
    }
}
