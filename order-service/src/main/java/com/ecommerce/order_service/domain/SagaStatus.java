package com.ecommerce.order_service.domain;

public enum SagaStatus {
    STARTED,
    IN_PROGRESS,
    COMPLETED,

    //
    COMPENSATING,
    FAILED
}