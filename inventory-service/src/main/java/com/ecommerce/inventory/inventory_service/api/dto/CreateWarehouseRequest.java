package com.ecommerce.inventory.inventory_service.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateWarehouseRequest(
        @NotNull
        @Size(max = 150)
        String name,

        @NotNull
        String code,

        String address
) {
}
