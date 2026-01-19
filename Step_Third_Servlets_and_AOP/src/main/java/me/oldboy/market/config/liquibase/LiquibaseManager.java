package me.oldboy.market.config.liquibase;

import liquibase.Liquibase;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionArgumentsCommandStep;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import me.oldboy.market.config.utils.ConfigProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Класс для управления созданием/изменением/удалением таблиц БД.
 */
public class LiquibaseManager {

    private static LiquibaseManager instance;
    /* Путь к основному файлу миграций */
    private String CHANGELOG_PATH;
    /* Название схемы куда будут помещены таблицы databasechangelog и databasechangeloglock */
    private String SCHEMA_NAME;
    /* SQL команда для создания схемы, где будут храниться файлы Liquibase */
    private String SQL_CREATE_SCHEMA;

    private LiquibaseManager(ConfigProvider configProvider) {
        CHANGELOG_PATH = configProvider.get("liquibase.changeLogFile");
        SCHEMA_NAME = configProvider.get("liquibase.schemaName");
        SQL_CREATE_SCHEMA = "CREATE SCHEMA IF NOT EXISTS " + SCHEMA_NAME;
    }

    public static LiquibaseManager getInstance(ConfigProvider configProvider) {
        if (instance == null) {
            instance = new LiquibaseManager(configProvider);
        }
        return instance;
    }

    /**
     * Метод для создания схемы/таблиц и заполнения данными БД
     *
     * @param connection The database connection (соединение с БД).
     */
    public void migrationsStart(Connection connection) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_CREATE_SCHEMA)) {
            preparedStatement.execute();
            Database database =
                    DatabaseFactory.getInstance()
                            .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            database.setLiquibaseSchemaName(SCHEMA_NAME);
            CommandScope updateCommand = new CommandScope(UpdateCommandStep.COMMAND_NAME);
            updateCommand.addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, database);
            updateCommand.addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, CHANGELOG_PATH);
            updateCommand.execute();

            System.out.println("Migration is completed successfully");
        } catch (SQLException | LiquibaseException exception) {
            System.out.println("SQL Exception in migration:" + exception.getMessage());
        }
    }

    /**
     * Метод удаляющий все созданные ранее таблицы и схемы методом *.migrationsStart()
     *
     * @param connection The database connection (связь с БД).
     */
    public void rollbackCreatedTables(Connection connection) {
        try {
            Database database =
                    DatabaseFactory.getInstance()
                            .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            database.setLiquibaseSchemaName(SCHEMA_NAME);
            Liquibase liquibase =
                    new Liquibase(CHANGELOG_PATH, new ClassLoaderResourceAccessor(), database);
            liquibase.rollback(13, null);
            System.out.println("Migrations successfully cancelled!");
        } catch (LiquibaseException exception) {
            System.out.println("SQL Exception in migration:" + exception.getMessage());
        }
    }
}