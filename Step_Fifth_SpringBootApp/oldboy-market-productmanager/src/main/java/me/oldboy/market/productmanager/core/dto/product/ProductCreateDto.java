package me.oldboy.market.productmanager.core.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;

/**
 * DTO для создания продукта (товара).
 */
@Builder
public record ProductCreateDto(@NotEmpty(message = "Product name can not be EMPTY")
                               @Size(min = 3, max = 256, message = "Wrong format (to short/to long)")
                               @Schema(description = "Название товара (продукта)", example = "Tank T90")
                               String name,

                               @NotNull
                               @Digits(integer = 22, fraction = 2)
                               @Schema(description = "Цена товара (продукта)", example = "1500.55")
                               Double price,

                               @NotNull
                               @Positive
                               @Schema(description = "Идентификатор категории товара (продукта)", example = "1")
                               Integer categoryId,

                               @NotNull
                               @Positive
                               @Schema(description = "Идентификатор брэнда товара (продукта)", example = "1")
                               Integer brandId,

                               @NotEmpty(message = "Description can not be EMPTY")
                               @Size(min = 3, max = 256, message = "Wrong format (to short/to long)")
                               @Schema(description = "Описание товара (продукта)", example = "Танки грязи не боятся")
                               String description,

                               @NotNull
                               @Schema(description = "Доступное количество товара (продукта)", example = "30")
                               Integer stockQuantity) {
}