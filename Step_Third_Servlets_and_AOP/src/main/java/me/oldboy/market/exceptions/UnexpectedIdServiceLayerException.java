package me.oldboy.market.exceptions;

/**
 * Исключение, выбрасывается в случае нештатной ситуации в методах сервисного класса реализующего UserService.
 */
public class UnexpectedIdServiceLayerException extends RuntimeException {
    /**
     * Создает новое исключение с указанным сообщением об ошибке
     *
     * @param msg детальное сообщение об ошибке
     */
    public UnexpectedIdServiceLayerException(String msg) {
        super(msg);
    }
}
