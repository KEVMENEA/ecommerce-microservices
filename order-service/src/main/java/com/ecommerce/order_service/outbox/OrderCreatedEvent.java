package com.ecommerce.order_service.outbox;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderUuid,
        String orderNumber,
        Long userId,
        BigDecimal totalAmount
) {
}