package me.oldboy.market.exceptions;

/**
 * Исключение, выбрасывается в случае нештатной ситуации работы методов ProductCrudController класса.
 */
public class ProductCrudControllerException extends RuntimeException {
    /**
     * Создает новое исключение с указанным сообщением об ошибке
     *
     * @param msg детальное сообщение об ошибке
     */
    public ProductCrudControllerException(String msg) {
        super(msg);
    }
}
