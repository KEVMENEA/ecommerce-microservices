package com.ecommerce.order_service.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(

        @NotNull
        Long userId,

        @NotEmpty
        List<@Valid CreateOrderItemRequest> items,

        @DecimalMin("0.00")
        BigDecimal discountAmount,

        @DecimalMin("0.00")
        BigDecimal shippingFee,

        String couponCode,

        @NotNull
        String shippingAddressSnapshot,

        String idempotencyKey

) {
}