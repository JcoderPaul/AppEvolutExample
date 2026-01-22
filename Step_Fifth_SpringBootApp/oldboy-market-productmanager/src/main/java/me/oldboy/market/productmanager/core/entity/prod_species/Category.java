package me.oldboy.market.productmanager.core.entity.prod_species;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import me.oldboy.market.productmanager.core.entity.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс, представляет категорию товара в каталоге.
 * Содержит информацию об идентификаторе и названии категории.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode(exclude = "productList")
@ToString(exclude = "productList")
@Entity
@Table(name = "categories", schema = "my_market")
public class Category {
    /**
     * Уникальный идентификатор категории
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer id;
    /**
     * Название категории
     */
    @Column(name = "category_name")
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "category")
    private List<Product> productList = new ArrayList<>();
}
