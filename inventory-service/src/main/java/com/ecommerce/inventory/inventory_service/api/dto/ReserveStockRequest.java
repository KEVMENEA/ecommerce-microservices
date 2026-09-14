package com.ecommerce.inventory.inventory_service.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReserveStockRequest(

        @NotNull
        Long orderId,

        @NotNull
        Long variantId,

        @NotNull
        Long warehouseId,

        @Min(1)
        int quantity

) {
}
