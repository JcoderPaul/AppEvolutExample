package me.oldboy.market.entity;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Класс, представляющий товар в системе маркетплейса.
 * Содержит всю необходимую информацию для отображения и поиска товара.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Product {
    /**
     * Уникальный идентификатор товара в системе
     */
    private Long id;
    /**
     * Название товара. Должно быть уникальным (т.к. нет артикула).
     */
    private String name;
    /**
     * Цена товара. Должна быть положительной.
     */
    private double price;
    /**
     * Идентификатор категории товара.
     */
    private Integer categoryId;
    /**
     * Идентификатор брэнд товара.
     */
    private Integer brandId;
    /**
     * Подробное описание товара, характеристики, особенности.
     */
    private String description;
    /**
     * Количество единиц товара доступных для продажи.
     */
    private int stockQuantity;
    /**
     * Дата и время добавления товара в систему
     */
    private LocalDateTime creationAt;
    /**
     * Дата и время последнего обновления информации о товаре
     */
    private LocalDateTime modifiedAt;
}