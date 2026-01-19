package me.oldboy.market.security;

/**
 * Исключение, выбрасывается в случае нештатной ситуации в JwtTokenFilter.
 */
public class TokenFilterException extends RuntimeException {
    /**
     * Создает новое исключение с указанным сообщением об ошибке
     *
     * @param msg детальное сообщение об ошибке
     */
    public TokenFilterException(String msg) {
        super(msg);
    }
}
