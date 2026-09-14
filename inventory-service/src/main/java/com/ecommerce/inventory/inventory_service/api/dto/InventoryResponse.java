package com.ecommerce.inventory.inventory_service.api.dto;

public record InventoryResponse(
        Long id,

        Long variantId,

        Long warehouseId,

        int onHandQuantity,

        int reservedQuantity,

        int availableQuantity,

        int lowStockThreshold,

        boolean lowStock,

        String status,

        Long version


){
}
