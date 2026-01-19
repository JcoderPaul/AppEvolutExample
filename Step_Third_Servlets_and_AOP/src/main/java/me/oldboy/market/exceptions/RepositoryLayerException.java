package me.oldboy.market.exceptions;

/**
 * Исключение, выбрасывается в случае нештатной ситуации работы методов классов слоя репозиториев (взаимодействия с БД).
 */
public class RepositoryLayerException extends RuntimeException {
    /**
     * Создает новое исключение с указанным сообщением об ошибке
     *
     * @param msg детальное сообщение об ошибке
     */
    public RepositoryLayerException(String msg, Exception exception) {
        super(msg, exception);
    }
}
