package me.oldboy.market.exceptions;

/**
 * Исключение, выбрасывается в случае нештатной ситуации работы методов сервисных классов.
 */
public class ServiceLayerException extends RuntimeException {
    /**
     * Создает новое исключение с указанным сообщением об ошибке
     *
     * @param msg детальное сообщение об ошибке
     */
    public ServiceLayerException(String msg) {
        super(msg);
    }
}
