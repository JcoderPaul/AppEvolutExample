package me.oldboy.market.entity.prod_species;

import lombok.*;

import java.io.Serializable;

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
public class Brand implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Уникальный идентификатор брэнда
     */
    private Integer id;
    /**
     * Название брэнда
     */
    private String name;
}
