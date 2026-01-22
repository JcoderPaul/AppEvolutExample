package me.oldboy.market.productmanager.core.dto.product;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * DTO для представления (чтения) товара.
 */
@Builder
public record ProductReadDto(Long id,
                             String name,
                             double price,
                             String categoryName,
                             String brandName,
                             String description,
                             int stockQuantity,
                             LocalDateTime creationAt,
                             LocalDateTime modifiedAt) {
}

