package me.oldboy.market.usermanager.core.exceptions;

/**
 * Исключение, выбрасывается в случае нештатной ситуации работы методов сервисных классов.
 */
public class UserManagerModuleException extends RuntimeException {
    /**
     * Создает новое исключение с указанным сообщением об ошибке
     *
     * @param msg детальное сообщение об ошибке
     */
    public UserManagerModuleException(String msg) {
        super(msg);
    }
}
