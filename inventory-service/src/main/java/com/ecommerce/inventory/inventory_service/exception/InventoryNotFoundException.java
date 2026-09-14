package com.ecommerce.inventory.inventory_service.exception;

public class InventoryNotFoundException extends RuntimeException{

    public InventoryNotFoundException(Long variantId, Long warehouseId) {
        super("Inventory not found with id " + variantId + ", warehouse id " + warehouseId );
    }
}
