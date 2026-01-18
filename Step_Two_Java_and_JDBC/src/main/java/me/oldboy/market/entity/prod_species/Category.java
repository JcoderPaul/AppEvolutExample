package me.oldboy.market.entity.prod_species;

import lombok.*;

/**
 * Класс, представляет категорию товара в каталоге.
 * Содержит информацию об идентификаторе и названии категории.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Category {
    /**
     * Уникальный идентификатор категории
     */
    private Integer id;
    /**
     * Название категории
     */
    private String name;
}
