package me.oldboy.market.dto.jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Представляет ответ, содержащий токен аутентификации.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class JwtAuthResponse {

    /**
     * Ответный ID пользователя
     */
    @Schema(description = "Идентификатор зарегистрированного пользователя", example = "30")
    private Long id;

    /**
     * Ответный (login) в нашем случае email
     */
    @Schema(description = "Электронный адрес (уникальный) зарегистрированный в системе", example = "admin@admin.ru")
    private String email;

    /**
     * Ответный сгенерированный JWT token
     */
    @Schema(description = "JWT Token доступа к системе", example = "см. пример при успешной аутентификации")
    private String accessToken;
}