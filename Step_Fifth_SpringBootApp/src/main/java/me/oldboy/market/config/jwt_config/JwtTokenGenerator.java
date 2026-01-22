package me.oldboy.market.config.jwt_config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;

/**
 * Отвечает за обработку токенов JWT:
 * - генерация токена;
 * - извлечение имени пользователя из токена;
 * - проверка корректности токена;
 */
@Component
@AllArgsConstructor
@NoArgsConstructor
public class JwtTokenGenerator {

    @Value("${security.jwt.secret}")
    private String jwtSignature;
    @Value("${security.jwt.life-time}")
    private Long jwtTokenLifeTime;
    private TemporalUnit lifeTimeTemporalUnit = ChronoUnit.HOURS;

    /**
     * Возвращает сгенерированный токен.
     *
     * @param accountId идентификатор учётной записи пользователя
     * @param email     электронной почты пользователя для авторизации
     * @return строковое представление сгенерированного токена
     */
    public String getToken(Long accountId, String email) {
        return generateJwtToken(accountId, email);
    }

    /**
     * Возвращает, действителен ли токен или нет
     *
     * @param token       строковое представление токена запроса
     * @param userDetails удобное представление данных пользователя
     * @return true - токен действителен, false - токен недействителен
     */
    public boolean isValid(String token, UserDetails userDetails) {
        Jws<Claims> claims = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token);
        final String userName = extractUserName(token);

        return (claims.getPayload().getExpiration().after(new Date()) &&
                userName.equals(userDetails.getUsername()));
    }

    /**
     * Возвращает имя пользователя (логин) из токена запроса
     *
     * @param token строковое представление токена запроса
     * @return логин пользователя (имя пользователя)
     */
    public String extractUserName(String token) {
        return getLogin(token);
    }

    /**
     * Возвращает секретный ключ для проверки подписи выданных и полученных токенов
     *
     * @return секретный ключ для проверки
     */
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSignature));
    }

    /**
     * Возвращает логин пользователя (у нас email) из полученного токена
     *
     * @param token строковое представление токена
     * @return логин пользователя (у нас email)
     */
    private String getLogin(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Генерирует токен JWT
     *
     * @param accountId идентификатор учётной записи пользователя
     * @param email     логин, имя пользователя (у нас email)
     * @return строковое представление сгенерированного токена
     */
    private String generateJwtToken(Long accountId, String email) {
        Claims claims = Jwts.claims()
                .subject(email)
                .add("id", accountId)
                .build();

        return Jwts.builder()
                .claims(claims)
                .expiration(Date.from(Instant.now().plus(jwtTokenLifeTime, lifeTimeTemporalUnit)))
                .signWith(getKey())
                .compact();
    }
}