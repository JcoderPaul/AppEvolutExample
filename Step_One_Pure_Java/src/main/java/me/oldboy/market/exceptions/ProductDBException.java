package me.oldboy.market.exceptions;

/**
 * Исключение, выбрасываемое при операциях с "кэшем БД" товаров (продуктов).
 * Используется для обработки ошибок уровня доступа к данным.
 * При переходе на реляционную БД будет исключена из приложения.
 */
public class ProductDBException extends RuntimeException {
    /**
     * Создает новое исключение с указанным сообщением об ошибке
     *
     * @param msg детальное сообщение об ошибке
     */
    public ProductDBException(String msg) {
        super(msg);
    }
}
