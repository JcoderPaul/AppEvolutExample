package me.oldboy.market.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.oldboy.market.entity.enums.Role;

/**
 * Класс определяющий состояние пользователя при обращению к приложению
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthUser {
    private String email;
    private Role role;
}
