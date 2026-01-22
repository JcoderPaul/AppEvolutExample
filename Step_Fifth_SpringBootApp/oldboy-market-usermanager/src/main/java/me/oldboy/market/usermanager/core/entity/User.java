package me.oldboy.market.usermanager.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import me.oldboy.market.usermanager.core.entity.enums.Role;
import org.hibernate.annotations.NaturalId;

/**
 * Сущность пользователя "системы маркетплейса".
 * Представляет учетную запись пользователя для аутентификации и авторизации.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
@Entity
@Table(name = "users", schema = "my_market")
public class User {
    /**
     * Уникальный идентификатор пользователя в системе
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    /**
     * Email пользователя. Используется как уникальный логин для входа.
     */
    @NaturalId
    @Column(name = "email", unique = true, nullable = false, length = 128)
    private String email;
    /**
     * Пароль пользователя.
     */
    @Column(name = "user_pass")
    private String password;
    /**
     * Роль пользователя (фактор допуска к совершению операций с товарами)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;
}