package me.oldboy.market.productmanager.core.exceptions;

/**
 * Исключение, выбрасывается в случае нештатной ситуации работы методов сервисных классов.
 */
public class ProductManagerModuleException extends RuntimeException {
    /**
     * Создает новое исключение с указанным сообщением об ошибке
     *
     * @param msg детальное сообщение об ошибке
     */
    public ProductManagerModuleException(String msg) {
        super(msg);
    }
}
