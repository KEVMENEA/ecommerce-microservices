package com.ecommerce.inventory.inventory_service.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AdjustStockRequest (

        @Min(1)
        int quantity,

        @NotBlank
        String reason
){
}
