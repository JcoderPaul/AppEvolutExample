package me.oldboy.market.exceptions;

/**
 * Исключение, выбрасываемое при возникновении ошибок во время миграции базы данных с помощью Liquibase.
 */
public class LiquibaseMigrationException extends RuntimeException {
    /**
     * Создает новое исключение миграции Liquibase с указанным сообщением и причиной.
     *
     * @param msg сообщение об ошибке миграции
     * @param e оригинальное исключение, которое стало причиной ошибки миграции
     */
    public LiquibaseMigrationException(String msg, Exception e) {
        super(msg, e);
    }
}
