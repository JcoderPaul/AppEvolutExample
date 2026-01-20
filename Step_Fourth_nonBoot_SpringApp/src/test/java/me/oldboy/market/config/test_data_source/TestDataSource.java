package me.oldboy.market.config.test_data_source;

import liquibase.integration.spring.SpringLiquibase;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.config.yaml_read_adapter.YamlPropertySourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Slf4j
@Configuration
@PropertySource(value = "classpath:application-test.yml", factory = YamlPropertySourceFactory.class)
@EnableTransactionManagement
@AllArgsConstructor
@NoArgsConstructor
public class TestDataSource {

    @Autowired
    private Environment env;
    /* Настройка источника данных */
    private String url; // Подгружаем динамически из параметров окружения
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

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        url = env.getProperty("datasource.url");

        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }

    @Bean
    public SpringLiquibase liquibase(JdbcTemplate jdbcTemplate) {
        String createSchemaSql = "CREATE SCHEMA IF NOT EXISTS liquibase_schema";

        try {
            jdbcTemplate.execute(createSchemaSql);
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
            liquibase.afterPropertiesSet(); // Manually trigger migration
        } catch (Exception e) {
            throw new RuntimeException("Liquibase migration failed", e);
        }

        return liquibase;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean =
                new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource());
        entityManagerFactoryBean.setPackagesToScan("me.oldboy.market.entity");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        entityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);

        return entityManagerFactoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());

        return transactionManager;
    }
}