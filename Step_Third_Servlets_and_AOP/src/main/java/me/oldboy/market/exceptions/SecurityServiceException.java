package me.oldboy.market.exceptions;

/**
 * Исключение, выбрасывается в случае нештатной ситуации работы методов сервиса отвечающего за безопасность приложения.
 */
public class SecurityServiceException extends RuntimeException {
    /**
     * Создает новое исключение с указанным сообщением об ошибке
     *
     * @param msg детальное сообщение об ошибке
     */
    public SecurityServiceException(String msg) {
        super(msg);
    }
}
