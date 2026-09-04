package com.ecommerce.order_service.domain;


public enum ShippingStatus {
    PENDING,
    READY_TO_SHIP,
    SHIPPED,
    DELIVERED,
    CANCELLED
}