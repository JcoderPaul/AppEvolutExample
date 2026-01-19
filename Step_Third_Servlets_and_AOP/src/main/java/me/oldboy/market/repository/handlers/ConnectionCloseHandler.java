package me.oldboy.market.repository.handlers;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Утилитный класс для безопасного закрытия соединений с базой данных.
 * Обеспечивает корректное освобождение ресурсов соединения с обработкой возможных исключений.
 */
@Slf4j
public class ConnectionCloseHandler {
    /**
     * Безопасно закрывает соединение с базой данных.
     *
     * @param connection соединение с базой данных для закрытия; может быть null -
     *                   в этом случае метод не выполняет никаких действий
     */
    public static void handle(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException closeEx) {
                log.error("Failed to close database connection.", closeEx);
            }
        }
    }
}
