package me.oldboy.market.dto.product;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * DTO для представления (чтения) товара.
 */
@Builder
public record ProductReadDto(Long id,
                             String name,
                             double price,
                             Integer categoryId,
                             Integer brandId,
                             String description,
                             int stockQuantity,
                             LocalDateTime creationAt,
                             LocalDateTime modifiedAt) {
}

