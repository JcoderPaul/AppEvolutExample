package me.oldboy.market.entity.prod_species;

import jakarta.persistence.*;
import lombok.*;
import me.oldboy.market.entity.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс, представляющий брэнд товара в каталоге.
 * Содержит информацию об идентификаторе и названии брэнда.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode(exclude = "productList")
@ToString(exclude = "productList")
@Entity
@Table(name = "brands", schema = "my_market")
public class Brand {
    /**
     * Уникальный идентификатор брэнда
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brand_id")
    private Integer id;
    /**
     * Название брэнда
     */
    @Column(name = "brand_name")
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "brand")
    private List<Product> productList = new ArrayList<>();
}
