package com.ecommerce.order_service.outbox;

public enum OutboxStatus{
        PENDING,
        PUBLISHED,
        FAILED
}
