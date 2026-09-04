package com.ecommerce.order_service.client.inventory;

public record InventoryReservationRequest(
        Long orderId,
        Long variantId,
        Long warehouseId,
        int quantity
) {
}