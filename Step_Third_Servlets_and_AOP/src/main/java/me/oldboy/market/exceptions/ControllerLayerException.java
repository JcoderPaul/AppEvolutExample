package me.oldboy.market.exceptions;

/**
 * Исключение, выбрасывается в случае нештатной ситуации на слое контроллеров.
 * Используется на уровне запроса и отображения данных.
 */
public class ControllerLayerException extends RuntimeException {
    /**
     * Создает новое исключение с указанным сообщением об ошибке
     *
     * @param msg детальное сообщение об ошибке
     */
    public ControllerLayerException(String msg) {
        super(msg);
    }
}
