package com.ecommerce.order_service.api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(

        Long id,
        UUID orderUuid,
        String orderNumber,
        Long userId,

        String status,
        String paymentStatus,
        String shippingStatus,

        BigDecimal subtotal,
        BigDecimal discountAmount,
        BigDecimal shippingFee,
        BigDecimal finalAmount,

        String couponCode,
        String shippingAddressSnapshot,

        LocalDateTime placedAt,
        LocalDateTime createdAt,

        List<OrderItemResponse> items

) {
}