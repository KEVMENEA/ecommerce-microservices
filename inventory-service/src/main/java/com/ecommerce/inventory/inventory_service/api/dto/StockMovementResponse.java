package com.ecommerce.inventory.inventory_service.api.dto;

import java.time.LocalDateTime;

public record StockMovementResponse(

        Long id,

        Long variantId,

        Long warehouseId,

        Long reservationId,

        String movementType,

        int quantity,

        String referenceType,

        Long referenceId,

        String reason,

        LocalDateTime createdAt

) {
}