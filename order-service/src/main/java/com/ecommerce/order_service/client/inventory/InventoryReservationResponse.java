package com.ecommerce.order_service.client.inventory;

import java.time.LocalDateTime;
import java.util.UUID;

public record InventoryReservationResponse(
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