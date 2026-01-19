package me.oldboy.market.entity.prod_species;

import lombok.*;

/**
 * Класс, представляющий брэнд товара в каталоге.
 * Содержит информацию об идентификаторе и названии брэнда.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Brand {
    /**
     * Уникальный идентификатор брэнда
     */
    private Integer id;
    /**
     * Название брэнда
     */
    private String name;
}
