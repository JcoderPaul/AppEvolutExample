package me.oldboy.market.entity;

import jakarta.persistence.*;
import lombok.*;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.entity.prod_species.Category;

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
@Entity
@Table(name = "products", schema = "my_market")
public class Product {
    /**
     * Уникальный идентификатор товара в системе
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * Название товара. Должно быть уникальным (т.к. нет артикула).
     */
    @Column(name = "product_name")
    private String name;
    /**
     * Цена товара. Должна быть положительной.
     */
    @Column(name = "price")
    private double price;
    /**
     * Категория товара.
     */
    @ManyToOne(targetEntity = Category.class)
    @JoinColumn(name="category_id", nullable=false)
    private Category category;
    /**
     * Брэнд товара.
     */
    @ManyToOne(targetEntity = Brand.class)
    @JoinColumn(name="brand_id", nullable=false)
    private Brand brand;
    /**
     * Подробное описание товара, характеристики, особенности.
     */
    @Column(name = "description")
    private String description;
    /**
     * Количество единиц товара доступных для продажи.
     */
    @Column(name = "stock_quantity")
    private int stockQuantity;
    /**
     * Дата и время добавления товара в систему
     */
    @Column(name = "creation_at")
    private LocalDateTime creationAt;
    /**
     * Дата и время последнего обновления информации о товаре
     */
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;
}