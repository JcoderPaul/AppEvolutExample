package me.oldboy.market.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * Класс определяющий состояние (логин и пароль) входящего в систему пользователя
 */
@AllArgsConstructor
@NoArgsConstructor
public class JwtAuthRequest {

    @Getter
    @Email
    @Size(min = 3, max = 64, message = "Wrong format (to short/to long)")
    private String email;

    @Getter
    @NotEmpty
    @Size(min = 3, max = 128, message = "Wrong format (to short/to long)")
    private String password;
}
