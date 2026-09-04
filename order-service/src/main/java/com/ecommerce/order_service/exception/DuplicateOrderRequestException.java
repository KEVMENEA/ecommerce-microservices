package com.ecommerce.order_service.exception;

public class DuplicateOrderRequestException
        extends RuntimeException {

    public DuplicateOrderRequestException(
            String message
    ) {

        super(message);
    }
}