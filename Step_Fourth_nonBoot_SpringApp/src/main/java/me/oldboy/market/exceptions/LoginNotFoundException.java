package me.oldboy.market.exceptions;

/**
 * Исключение для случаев, когда пользователь не найден.
 */
public class LoginNotFoundException extends RuntimeException {
    /**
     * Создает исключение с сообщением об ошибке.
     *
     * @param msg сообщение об ошибке
     */
    public LoginNotFoundException(String msg) {
        super(msg);
    }
}
