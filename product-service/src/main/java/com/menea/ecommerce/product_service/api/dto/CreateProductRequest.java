package com.menea.ecommerce.product_service.api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateProductRequest (

        @NotBlank
        @Size(max = 100)
        String sku,

        @NotBlank
        @Size(max = 255)
        String name,

        @Size(max = 1000)
        String description,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal price,

        @NotBlank
        @Pattern(regexp = "^[A-Z]{3}$")
        String currency

        ){ }
