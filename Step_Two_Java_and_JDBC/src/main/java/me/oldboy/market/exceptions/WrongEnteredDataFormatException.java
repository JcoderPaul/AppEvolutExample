package me.oldboy.market.exceptions;

/**
 * Исключение, выбрасываемое при операциях в меню приложения - InputValidator.
 * Используется на уровне взаимодействия с пользователем.
 */
public class WrongEnteredDataFormatException extends RuntimeException {
    /**
     * Создает новое исключение с указанным сообщением об ошибке
     *
     * @param msg детальное сообщение об ошибке
     */
    public WrongEnteredDataFormatException(String msg) {
        super(msg);
    }
}
