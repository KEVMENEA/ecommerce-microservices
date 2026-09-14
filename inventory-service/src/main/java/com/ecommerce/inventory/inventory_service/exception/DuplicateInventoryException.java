package com.ecommerce.inventory.inventory_service.exception;

public class DuplicateInventoryException
        extends RuntimeException {

    public DuplicateInventoryException(
            Long variantId,
            Long warehouseId
    ) {
        super(
                "Inventory already exists for variantId="
                        + variantId
                        + ", warehouseId="
                        + warehouseId
        );
    }
}