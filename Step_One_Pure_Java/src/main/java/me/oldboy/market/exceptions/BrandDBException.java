package me.oldboy.market.exceptions;

/**
 * Исключение, выбрасываемое при операциях с "кэшем БД" брэндов.
 * Используется для обработки ошибок уровня доступа к данным.
 * При переходе на реляционную БД будет исключена из приложения.
 */
public class BrandDBException extends RuntimeException {
    /**
     * Создает новое исключение с указанным сообщением об ошибке
     *
     * @param msg детальное сообщение об ошибке
     */
    public BrandDBException(String msg) {
        super(msg);
    }
}
