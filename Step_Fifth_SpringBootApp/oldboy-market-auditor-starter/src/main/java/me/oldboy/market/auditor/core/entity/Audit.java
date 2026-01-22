package me.oldboy.market.auditor.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import me.oldboy.market.auditor.core.entity.enums.Action;
import me.oldboy.market.auditor.core.entity.enums.Status;
import me.oldboy.market.usermanager.core.entity.User;

import java.time.LocalDateTime;

/**
 * Сущность для аудита действий пользователей с товарами.
 * Используется для отслеживания активности пользователей в системе.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
@Entity
@Table(name = "audits", schema = "my_market")
public class Audit {
    /**
     * Уникальный идентификатор записи аудита
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * Временная метка действия
     */
    @Column(name = "created_at")
    private LocalDateTime createAt;
    /**
     * Представление пользователя выполнившего действие.
     */
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "created_by", referencedColumnName = "email", nullable = false)
    private User createBy;
    /**
     * Тип выполненного действия (добавление, обновление, удаление товара)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "action")
    private Action action;
    /**
     * Статус выполнения действия (успех или неудача)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "is_success")
    private Status isSuccess;
    /**
     * Строковое представление товара, над которым было выполнено действие.
     * Может быть null для действий, не связанных с товаром, например LogIn/LogOut
     */
    @Column(name = "auditable_record")
    private String auditableRecord;
}