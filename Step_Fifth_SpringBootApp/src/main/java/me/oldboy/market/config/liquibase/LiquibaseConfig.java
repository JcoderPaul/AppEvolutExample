package me.oldboy.market.config.liquibase;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Configuration
@EnableConfigurationProperties(LiquibaseProperties.class)
public class LiquibaseConfig {

    private final DataSource dataSource;
    private final LiquibaseProperties liquibaseProperties;

    @Autowired
    public LiquibaseConfig(DataSource dataSource, LiquibaseProperties liquibaseProperties) {
        this.dataSource = dataSource;
        this.liquibaseProperties = liquibaseProperties;
    }

    @Bean
    public SpringLiquibase liquibase() {
        createLiquibaseSchema();

        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(liquibaseProperties.getChangeLog());
        liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
        liquibase.setLiquibaseSchema(liquibaseProperties.getLiquibaseSchema());
        liquibase.setShouldRun(liquibaseProperties.isEnabled());

        return liquibase;
    }

    private void createLiquibaseSchema() {
        String schemaName = liquibaseProperties.getLiquibaseSchema();
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Liquibase schema: " + schemaName, e);
        }
    }
}
