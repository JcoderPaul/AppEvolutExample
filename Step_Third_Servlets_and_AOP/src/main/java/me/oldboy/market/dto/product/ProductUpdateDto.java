package me.oldboy.market.dto.product;

import lombok.Builder;

import javax.validation.constraints.*;

/**
 * DTO для обновления товара (Product).
 */
@Builder
public record ProductUpdateDto(@NotNull
                               @Positive
                               Long id,

                               @NotEmpty(message = "Product name can not be EMPTY")
                               @Size(min = 3, max = 256, message = "Wrong format (to short/to long)")
                               String name,

                               @NotNull
                               @Digits(integer = 22, fraction = 2)
                               Double price,

                               @NotEmpty(message = "Description can not be EMPTY")
                               @Size(min = 3, max = 256, message = "Wrong format (to short/to long)")
                               String description,

                               @NotNull
                               Integer stockQuantity) {
}