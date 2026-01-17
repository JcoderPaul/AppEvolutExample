package me.oldboy.market.exceptions;

/**
 * Исключение, выбрасываемое при операциях в классе ProductCrudController.
 * Используется при обработке ошибок уровня отображения информации и валидации.
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
