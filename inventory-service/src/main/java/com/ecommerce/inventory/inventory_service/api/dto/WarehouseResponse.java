package com.ecommerce.inventory.inventory_service.api.dto;

import java.time.LocalDateTime;

public record WarehouseResponse(

        Long id,

        String name,

        String code,

        String address,

        String status,

        LocalDateTime createdAt

) {
}