package com.ecommerce.inventory.inventory_service.exception;


public class InvalidReservationStateException
        extends RuntimeException {

    public InvalidReservationStateException(
            String message
    ) {
        super(message);
    }
}