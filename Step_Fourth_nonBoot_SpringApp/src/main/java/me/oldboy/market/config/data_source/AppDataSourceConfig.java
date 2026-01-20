package me.oldboy.market.config.data_source;

import liquibase.integration.spring.SpringLiquibase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.config.yaml_read_adapter.YamlPropertySourceFactory;
import me.oldboy.market.exceptions.LiquibaseMigrationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Класс конфигурации источника данных, определяет параметры источника данных, миграцию,
 * настройки фреймворка: JdbcTemplate, EntityManager и TransactionManager
 */
@Slf4j
@Configuration
@PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
@EnableTransactionManagement
@RequiredArgsConstructor
public class AppDataSourceConfig {

    /* Настройка источника данных */
    @Value("${datasource.url}")
    private String url;
    @Value("${datasource.driver-class-name}")
    private String driver;
    @Value("${datasource.username}")
    private String username;
    @Value("${datasource.password}")
    private String password;

    /* Настройка Liquibase */
    @Value("${liquibase.change_log}")
    private String changeLogFile;
    @Value("${liquibase.default_schema}")
    private String defaultSchema;
    @Value("${liquibase.liquibase-schema}")
    private String liquibaseSchema;
    @Value("${liquibase.enabled}")
    private String enabledLiquibaseStart;

    /**
     * Определяет DriverManagerDataSource для DataSource.
     *
     * @return DataSource — представляет фабрику подключений к источнику данных (БД)
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }

    /**
     * Определяет настройки Liquibase.
     *
     * @return Spring-обёртка для Liquibase (миграционный фреймворк)
     */
    @Bean
    @DependsOn("jdbcTemplate")
    public SpringLiquibase liquibase() {
        String createSchemaSql = "CREATE SCHEMA IF NOT EXISTS liquibase_schema";

        try {
            jdbcTemplate().execute(createSchemaSql);
        } catch (Exception e) {
            System.err.println("Ошибка при попытке создания схемы 'liquibase_schema': " + e.getMessage());
        }

        SpringLiquibase liquibase = new SpringLiquibase();

        liquibase.setChangeLog(changeLogFile);
        liquibase.setShouldRun(Boolean.parseBoolean(enabledLiquibaseStart));
        liquibase.setDefaultSchema(defaultSchema);
        liquibase.setLiquibaseSchema(liquibaseSchema);
        liquibase.setDataSource(dataSource());
        try {
            liquibase.afterPropertiesSet();
        } catch (Exception e) {
            throw new LiquibaseMigrationException("Liquibase migration failed", e);
        }

        return liquibase;
    }

    /**
     * Определяет JdbcTemplate для доступа к данным.
     *
     * @return JdbcTemplate — автоматически обрабатывает создание и освобождение JDBC-ресурсов:
     * Connection, Statement и ResultSet, предотвращает распространённые ошибки, например, не
     * закрытые соединения.
     */
    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    /**
     * Определить конфигурацию EntityManagerFactory.
     *
     * @return LocalContainerEntityManagerFactoryBean - отвечает за создание и управление экземпляром
     * EntityManagerFactory JPA в контексте приложения Spring.
     */
    @Bean
    @DependsOn("dataSource")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean =
                new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource());
        entityManagerFactoryBean.setPackagesToScan("me.oldboy.market");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        entityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);

        return entityManagerFactoryBean;
    }

    /**
     * Определяет конфигурацию TransactionManager.
     *
     * @return JpaTransactionManager — менеджер для доступа к транзакционным данным.
     */
    @Bean
    @DependsOn("entityManagerFactory")
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());

        return transactionManager;
    }
}