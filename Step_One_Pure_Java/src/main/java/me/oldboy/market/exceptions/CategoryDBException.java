package me.oldboy.market.exceptions;

/**
 * Исключение, выбрасываемое при операциях с "кэшем БД" категорий товаров.
 * Используется для обработки ошибок уровня доступа к данным.
 * При переходе на реляционную БД будет исключена из приложения.
 */
public class CategoryDBException extends RuntimeException {
    /**
     * Создает новое исключение с указанным сообщением об ошибке
     *
     * @param msg детальное сообщение об ошибке
     */
    public CategoryDBException(String msg) {
        super(msg);
    }
}
