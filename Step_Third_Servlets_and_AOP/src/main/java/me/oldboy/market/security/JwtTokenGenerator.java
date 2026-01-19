package me.oldboy.market.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import me.oldboy.market.entity.User;
import me.oldboy.market.entity.enums.Role;

import javax.crypto.SecretKey;
import java.nio.file.AccessDeniedException;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Класс для генерации и управления JWT токеном.
 */
@RequiredArgsConstructor
public class JwtTokenGenerator {
    private final String secret;

    /**
     * Возвращает сгенерированный JWT токен
     *
     * @param accountId ID пользователя из базы данных для генерации JWT токена
     * @param email     email пользователя применяемый для генерации JWT токена
     * @param role      роль пользователя используемая для генерации JWT токена
     * @return сгенерированный JWT токен
     */
    public String getToken(Long accountId, String email, Role role) {
        return generateJwtToken(accountId, email, role);
    }

    /**
     * Возвращает логин (в нашем случае email)
     *
     * @param token JWT токен из которого будет извлекаться логин
     * @return login / userName / у нас email
     */
    public String extractUserName(String token) {
        return getEmail(token);
    }

    /**
     * Генерирует секретный ключ для подписи JWT
     *
     * @return возвращает сгенерированный ключ
     */
    SecretKey signKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Генерирует токен для полученных данных (Имя пользователя (Логин), ID пользователя в БД, Роль (ADMIN или USER))
     *
     * @param userId ID пользователя из базы данных
     * @param email  email для которого будет сгенерирован токен
     * @param role   роль пользователя указанная при регистрации (ADMIN/USER/...)
     * @return сгенерированный JWT токен
     */
    private String generateJwtToken(Long userId, String email, Role role) {
        Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(600).toInstant());

        Claims claims = Jwts.claims()
                .subject(email)
                .add("id", userId)
                .add("role", role)
                .build();

        return Jwts.builder()
                .claims(claims)
                .expiration(expirationDate)
                .signWith(signKey())
                .compact();
    }

    /**
     * Извлекает логин (имя пользователя) из полученного JWT
     *
     * @param token токен из которого будет извлекаться логин
     * @return извлекаемые "клема" - email
     */
    private String getEmail(String token) {
        return Jwts.parser()
                .verifyWith(signKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Аутентифицирует пользователя на основе полученного JWT
     *
     * @param token токен для аутентификации
     * @param user  данные из БД для сравнения с данными из payload ключа
     * @return если JWT ключ валиден, возвращаем результат аутентификации
     * @throws AccessDeniedException if the JWT is invalid or the user does not exist
     */
    public JwtAuthUser authentication(String token, User user) throws AccessDeniedException {
        if (!isValid(token, user)) {
            throw new AccessDeniedException("Access denied: Invalid token!");
        }
        return new JwtAuthUser(user.getEmail(), user.getRole());
    }

    /**
     * Проверяем JWT ключ на валидность
     *
     * @param token токен для проверки валидности
     * @param user  сведения о пользователе для сравнения со структурой payload
     * @return true - токен валиден / false - токен просрочен или данные не совпали
     */
    public boolean isValid(String token, User user) {
        Jws<Claims> claims = Jwts.parser()
                .verifyWith(signKey())
                .build()
                .parseSignedClaims(token);
        final String email = extractUserName(token);

        return (claims.getPayload().getExpiration().after(new Date()) &&
                email.equals(user.getEmail()));
    }
}