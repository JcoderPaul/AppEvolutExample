package me.oldboy.market.dto.jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Объект передачи данных (DTO), представляющий информацию аутентификации.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtAuthRequest {

    /**
     * Email (логин) пользователя для аутентификации
     */
    @NotEmpty
    @Size(min = 3, max = 64, message = "Wrong format (to short/to long)")
    @Schema(description = "Email (as логин) зарегистрированный в 'системе'", example = "admin@admin.ru")
    private String email;

    /**
     * Пароль пользователя для аутентификации
     */
    @NotEmpty
    @Size(min = 3, max = 128, message = "Wrong format (to short/to long)")
    @Schema(description = "Пароль для доступа 'в систему'", example = "1234")
    private String password;
}