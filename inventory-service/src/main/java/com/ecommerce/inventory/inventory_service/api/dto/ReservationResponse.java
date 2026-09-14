package com.ecommerce.inventory.inventory_service.api.dto;


import java.time.LocalDateTime;
import java.util.UUID;

public record ReservationResponse(

        UUID reservationUuid,

        Long orderId,

        Long variantId,

        Long warehouseId,

        int quantity,

        String status,

        LocalDateTime expiresAt,

        LocalDateTime createdAt

) {
}