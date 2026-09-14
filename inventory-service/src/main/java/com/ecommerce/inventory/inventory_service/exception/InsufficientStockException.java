package com.ecommerce.inventory.inventory_service.exception;

public class InsufficientStockException extends RuntimeException{


    public InsufficientStockException(
            Long variantId,
            int requested,
            int available
    ) {
        super(
                "Insufficient stock for variantId="
                        + variantId
                        + ". Requested="
                        + requested
                        + ", available="
                        + available
        );
    }
}
