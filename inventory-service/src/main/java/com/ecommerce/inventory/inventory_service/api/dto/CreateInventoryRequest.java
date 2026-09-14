package com.ecommerce.inventory.inventory_service.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateInventoryRequest(

        @NotNull
        Long variantId,

        @NotNull
        Long warehouseId,

        @NotNull
        @Min(0)
        Integer onHandQuantity,

        @NotNull
        @Min(0)
        Integer lowStockThreshold

) {
}