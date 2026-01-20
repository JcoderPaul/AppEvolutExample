package me.oldboy.market.entity;

import jakarta.persistence.*;
import lombok.*;
import me.oldboy.market.entity.enums.Role;
import org.hibernate.annotations.NaturalId;

import java.util.ArrayList;
import java.util.List;

/**
 * Сущность пользователя "системы маркетплейса".
 * Представляет учетную запись пользователя для аутентификации и авторизации.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = "auditList")
@ToString(exclude = "auditList")
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

    /*
    Помним, что поле email в нашем User должно быть помечено аннотацией @NaturalId
    (из org.hibernate.annotations.NaturalId). Это помогает Hibernate правильно
    обрабатывать ссылки на уникальные, но не первичные ключи.
    */

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

    @Builder.Default
    @OneToMany(mappedBy = "createBy")
    private List<Audit> auditList = new ArrayList<>();
}