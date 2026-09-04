package com.ecommerce.order_service.api.dto.response;


import java.math.BigDecimal;

public record OrderItemResponse(

        Long id,
        Long productId,
        Long variantId,
        String productNameSnapshot,
        String skuSnapshot,
        BigDecimal priceSnapshot,
        int quantity,
        BigDecimal subtotal,
        String imageSnapshot

) {
}