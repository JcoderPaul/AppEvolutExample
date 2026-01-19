package me.oldboy.market.config.connection;

import me.oldboy.market.config.utils.ConfigProvider;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Класс для управления пулом соединений с базой данных.
 * Реализует паттерн Singleton для обеспечения единственного экземпляра пула в приложении.
 * Использует Apache Tomcat JDBC Connection Pool для управления соединениями.
 */
public class DbConnectionPool {
    /**
     * Единственный экземпляр класса (Singleton pattern)
     */
    private static DbConnectionPool INSTANCE;

    /**
     * Приватный конструктор для реализации паттерна Singleton.
     * Экземпляр класса должен создаваться через метод {@link #getINSTANCE()}.
     */
    private DbConnectionPool() {

    }

    /**
     * Возвращает единственный экземпляр класса DbConnectionPool.
     * Если экземпляр еще не создан, создает новый (ленивая инициализация).
     *
     * @return единственный экземпляр DbConnectionPool
     */
    public static DbConnectionPool getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new DbConnectionPool();
        }
        return INSTANCE;
    }

    /**
     * Источник данных для управления пулом соединений
     */
    private static DataSource dataSource;

    /**
     * Инициализирует пул соединений с параметрами из конфигурации.
     *
     * @param configProvider поставщик конфигурационных параметров
     * @throws IllegalStateException если произошла ошибка при инициализации пула
     */
    public void initPool(ConfigProvider configProvider) {
        PoolProperties poolProperties = new PoolProperties();

        setConnectionParam(poolProperties, configProvider);
        setAdditionalParam(poolProperties);

        dataSource = new DataSource();
        dataSource.setPoolProperties(poolProperties);
    }

    /**
     * Инициализирует тестовый пул соединений с прямым указанием параметров.
     * Предназначен для использования в тестовой среде.
     *
     * @param dbDriver     класс драйвера базы данных
     * @param baseUrl      URL базы данных
     * @param userLogin    имя пользователя для подключения
     * @param userPassword пароль для подключения
     * @throws IllegalStateException если произошла ошибка при инициализации пула
     */
    public void initTestPool(String dbDriver, String baseUrl, String userLogin, String userPassword) {
        PoolProperties poolProperties = new PoolProperties();

        setTestConnectionParam(poolProperties, dbDriver, baseUrl, userLogin, userPassword);
        setAdditionalParam(poolProperties);

        dataSource = new DataSource();
        dataSource.setPoolProperties(poolProperties);
    }

    /**
     * Возвращает соединение из пула.
     *
     * @return активное соединение с базой данных
     * @throws SQLException          если произошла ошибка при получении соединения
     * @throws IllegalStateException если пул не был инициализирован
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Закрывает пул соединений и освобождает все ресурсы.
     * Должен вызываться при завершении работы приложения.
     */
    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    /**
     * Устанавливает дополнительные параметры пула соединений.
     *
     * @param poolProperties свойства пула для настройки
     */
    private void setAdditionalParam(PoolProperties poolProperties) {
        /* Размеры пула */
        poolProperties.setInitialSize(5);           // сколько соединений создать при старте
        poolProperties.setMaxActive(50);            // максимум одновременно открытых соединений (Tomcat 9+)
        poolProperties.setMinIdle(5);               // минимум соединений, которые всегда держатся открытыми
        poolProperties.setMaxIdle(20);              // максимум idle-соединений

        /* Таймауты */
        poolProperties.setMaxWait(10000);           // сколько ждать соединение, если пул исчерпан (мс)
        poolProperties.setTimeBetweenEvictionRunsMillis(30000); // как часто проверять idle-соединения
        poolProperties.setMinEvictableIdleTimeMillis(60000);     // сколько соединение может быть idle перед закрытием

        /* Валидация соединений */
        poolProperties.setTestOnBorrow(true);       // проверять при взятии из пула
        poolProperties.setTestOnReturn(false);
        poolProperties.setTestWhileIdle(true);      // проверять во время eviction
        poolProperties.setValidationQuery("SELECT 1");
        poolProperties.setValidationInterval(30000); // не проверять одно и то же соединение чаще 30 сек

        /* Защита от утечек */
        poolProperties.setRemoveAbandonedTimeout(60); // секунд — закрыть соединение, если оно "забыто" >60 сек
        poolProperties.setLogAbandoned(true);         // логировать stack trace забытых соединений

        /* JDBC интерцепторы */
        poolProperties.setJdbcInterceptors(
                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" +
                        "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;" +
                        "org.apache.tomcat.jdbc.pool.interceptor.SlowQueryReport(threshold=2000)" // лог медленных запросов >2 сек
        );
    }

    /**
     * Устанавливает параметры подключения к базе данных из конфигурации.
     *
     * @param poolProperties свойства пула для настройки
     * @param configProvider поставщик конфигурационных параметров
     */
    private void setConnectionParam(PoolProperties poolProperties, ConfigProvider configProvider) {
        /* Основные параметры подключения */
        poolProperties.setUrl(configProvider.get("db.url"));
        poolProperties.setDriverClassName(configProvider.get("db.driver"));
        poolProperties.setUsername(configProvider.get("db.username"));
        poolProperties.setPassword(configProvider.get("db.password"));
    }

    /**
     * Устанавливает параметры подключения для тестового пула.
     *
     * @param poolProperties свойства пула для настройки
     * @param dbDriver       класс драйвера базы данных
     * @param baseUrl        URL базы данных
     * @param userLogin      имя пользователя для подключения
     * @param userPassword   пароль для подключения
     */
    private void setTestConnectionParam(PoolProperties poolProperties, String dbDriver, String baseUrl, String userLogin, String userPassword) {
        poolProperties.setUrl(baseUrl);
        poolProperties.setDriverClassName(dbDriver);
        poolProperties.setUsername(userLogin);
        poolProperties.setPassword(userPassword);
    }
}