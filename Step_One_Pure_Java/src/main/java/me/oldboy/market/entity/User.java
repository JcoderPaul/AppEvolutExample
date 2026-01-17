package me.oldboy.market.entity;

import lombok.*;

/**
 * Сущность пользователя системы маркетплейса.
 * Представляет учетную запись пользователя для аутентификации и авторизации.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class User {
    /**
     * Уникальный идентификатор пользователя в системе
     */
    private Long userId;
    /**
     * Email пользователя. Используется как уникальный логин для входа.
     */
    private String email;
    /**
     * Пароль пользователя.
     */
    private String password;
}
