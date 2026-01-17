package me.oldboy.market.exceptions;

/**
 * Исключение, выбрасываемое при операциях в классе ProductService.
 * Используется на уровне обработки и преобразования информации.
 */
public class ProductServiceException extends Exception {
    /**
     * Создает новое исключение с указанным сообщением об ошибке
     *
     * @param msg детальное сообщение об ошибке
     */
    public ProductServiceException(String msg) {
        super(msg);
    }
}
