package me.oldboy.market.repository.handlers;

import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.exceptions.RepositoryLayerException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Утилитный класс для обработки отката транзакций при возникновении исключений в работе с базой данных.
 * Обеспечивает безопасный откат транзакций и единообразную обработку ошибок на уровне репозитория.
 */
@Slf4j
public class ConnectionRollbackHandler {
    /**
     * Обрабатывает исключение, выполняя откат транзакции и генерируя стандартизированное исключение.
     *
     * @param exception        исходное исключение, вызвавшее необходимость отката
     * @param eventDescription описание операции, во время которой произошла ошибка
     *                         (например, "save user", "update product", "delete order")
     * @param connection       соединение с базой данных, для которого выполняется откат транзакции
     *                         (может быть null - в этом случае откат не выполняется)
     * @throws RepositoryLayerException всегда - исключение уровня репозитория,
     *                                  содержащее исходное исключение как причину и информативное сообщение
     */
    public static void handle(Exception exception, String eventDescription, Connection connection) {
        log.error("Failed to {} in the database.", eventDescription, exception);
        try {
            if (connection != null) {
                connection.rollback();
                log.warn("Transaction rolled back successfully after failed {}.", eventDescription);
            }
        } catch (SQLException rollbackEx) {
            log.error("Failed to rollback transaction.", rollbackEx);
        }
        throw new RepositoryLayerException("Database operation failed during " + eventDescription, exception);
    }
}