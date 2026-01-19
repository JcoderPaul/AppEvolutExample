package me.oldboy.market.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.oldboy.market.entity.enums.Role;

/**
 * Класс определяющий состояние (ID, логин, роль и токен) возвращаемый сервером приложения на запрос пользователя
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class JwtAuthResponse {
    private Long id;
    private String email;
    private Role role;
    private String accessToken;
}
