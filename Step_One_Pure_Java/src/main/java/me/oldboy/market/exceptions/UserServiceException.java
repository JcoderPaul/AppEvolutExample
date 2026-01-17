package me.oldboy.market.exceptions;

/**
 * Исключение, выбрасываемое при операциях в классе UserService.
 * Используется на уровне обработки и преобразования информации.
 */
public class UserServiceException extends Exception {
    /**
     * Создает новое исключение с указанным сообщением об ошибке
     *
     * @param msg детальное сообщение об ошибке
     */
    public UserServiceException(String msg) {
        super(msg);
    }
}
