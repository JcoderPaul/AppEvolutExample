package me.oldboy.market.entity;

import lombok.*;
import me.oldboy.market.entity.enums.Action;
import me.oldboy.market.entity.enums.Status;

import java.time.LocalDateTime;

/**
 * Сущность для аудита действий пользователей с товарами.
 * Используется для отслеживания активности пользователей в системе.
 */
@Builder
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
public class Audit {
    /**
     * Уникальный идентификатор записи аудита
     */
    private Long id;
    /**
     * Временная метка действия
     */
    private LocalDateTime createAt;
    /**
     * Представление пользователя выполнившего действие - в нашем случае Email.
     */
    private String createBy;
    /**
     * Тип выполненного действия (добавление, обновление, удаление товара)
     */
    private Action action;
    /**
     * Статус выполнения действия (успех или неудача)
     */
    private Status isSuccess;
    /**
     * Строковое представление товара, над которым было выполнено действие.
     * Может быть null для действий, не связанных с товаром, например LogIn/LogOut
     */
    private String auditableRecord;
}