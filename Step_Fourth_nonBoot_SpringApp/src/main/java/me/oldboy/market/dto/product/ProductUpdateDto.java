package me.oldboy.market.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;

/**
 * DTO для обновления товара (Product).
 */
@Builder
public record ProductUpdateDto(@NotNull
                               @Positive
                               @Schema(description = "Уникальный идентификатор обновляемого товара (продукта)", example = "7")
                               Long id,

                               @NotEmpty(message = "Product name can not be EMPTY")
                               @Size(min = 3, max = 256, message = "Wrong format (to short/to long)")
                               @Schema(description = "Обновленное название товара (продукта)", example = "Утюг")
                               String name,

                               @NotNull
                               @Digits(integer = 22, fraction = 2)
                               @Schema(description = "Обновленная цена товара (продукта)", example = "300.45")
                               Double price,

                               @NotEmpty(message = "Description can not be EMPTY")
                               @Size(min = 3, max = 256, message = "Wrong format (to short/to long)")
                               @Schema(description = "Обновленное описание товара (продукта)", example = "Утюги тоже летают, но низко")
                               String description,

                               @NotNull
                               @Schema(description = "Обновленное доступное количество товара (продукта)", example = "10")
                               Integer stockQuantity) {
}