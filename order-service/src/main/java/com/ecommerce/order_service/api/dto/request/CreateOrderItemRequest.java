package com.ecommerce.order_service.api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;


public record CreateOrderItemRequest(

        @NotNull
        Long productId,

        @NotNull
        Long variantId,

        @NotBlank
        String productName,

        @NotBlank
        String sku,

        @NotNull
        @DecimalMin("0.00")
        BigDecimal price,

        @NotNull
        @Min(1)
        Integer quantity,

        String imageUrl

) {}