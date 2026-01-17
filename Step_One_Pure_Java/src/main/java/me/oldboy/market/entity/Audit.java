package me.oldboy.market.entity;

import lombok.*;
import me.oldboy.market.entity.log_enum.Action;
import me.oldboy.market.entity.log_enum.Status;

import java.io.Serializable;

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
public class Audit implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Уникальный идентификатор записи аудита
     */
    private Long id;
    /**
     * Временная метка действия (Unix timestamp в миллисекундах)
     */
    private Long timestamp;
    /**
     * Email пользователя, выполнившего действие.
     * Используется для идентификации пользователя в системе.
     */
    private String userEmail;
    /**
     * Тип выполненного действия (добавление, обновление, удаление товара)
     */
    private Action action;
    /**
     * Статус выполнения действия (успех или неудача)
     */
    private Status isSuccess;
    /**
     * Товар, над которым было выполнено действие.
     * Может быть null для действий, не связанных с товаром, например LogIn/LogOut
     */
    private Product product;
}