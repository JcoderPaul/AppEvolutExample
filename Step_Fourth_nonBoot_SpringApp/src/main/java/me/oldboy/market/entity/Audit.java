package me.oldboy.market.entity;

import jakarta.persistence.*;
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

    /*
    Так не принято делать, обычно ссылка идет на ID сущности, а не на некое поле. И Hibernate, по умолчанию,
    использует тип поля @Id (user_id: BIGSERIAL/long) сущности User для внешнего ключа. Мы должны явно указать
    referencedColumnName = "email". Однако, поскольку у нас поле created_by в БД — это VARCHAR, Hibernate все
    равно пытается вставить туда long ID, мы хапаем ошибку при чтении или записи данных (попытка превратить один
    тип данных в другой - облом). Обязательно нужно в сущности User пометить соответствующее поле как @NaturalId,
    у нас это поле email. Ну, или сделать как принято.
    */

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