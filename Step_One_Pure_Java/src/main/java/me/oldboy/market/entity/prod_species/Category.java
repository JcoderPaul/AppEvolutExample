package me.oldboy.market.entity.prod_species;

import lombok.*;

import java.io.Serializable;

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
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Уникальный идентификатор категории
     */
    private Integer id;
    /**
     * Название категории
     */
    private String name;
}
