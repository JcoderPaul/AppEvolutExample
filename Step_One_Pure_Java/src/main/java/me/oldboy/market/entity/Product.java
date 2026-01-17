package me.oldboy.market.entity;

import lombok.*;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.entity.prod_species.Category;

import java.io.Serializable;
import java.util.Objects;

/**
 * Класс, представляющий товар в системе маркетплейса.
 * Содержит всю необходимую информацию для отображения и поиска товара.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
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
     * Категория товара.
     */
    private Category category;
    /**
     * Брэнд товара.
     */
    private Brand brand;
    /**
     * Подробное описание товара, характеристики, особенности.
     */
    private String description;
    /**
     * Количество единиц товара доступных для продажи.
     */
    private int stockQuantity;
    /**
     * Дата и время добавления товара в систему (мс)
     */
    private long creationTimestamp;
    /**
     * Дата и время последнего обновления информации о товаре (мс)
     */
    private long lastModifiedTimestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id && Double.compare(product.price, price) == 0 && stockQuantity == product.stockQuantity && lastModifiedTimestamp == product.lastModifiedTimestamp && Objects.equals(name, product.name) && Objects.equals(category, product.category) && Objects.equals(brand, product.brand) && Objects.equals(description, product.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, category, brand, description, stockQuantity, lastModifiedTimestamp);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", brand=" + brand +
                ", description='" + description + '\'' +
                ", stockQuantity=" + stockQuantity +
                ", lastModifiedTimestamp=" + lastModifiedTimestamp +
                '}';
    }
}