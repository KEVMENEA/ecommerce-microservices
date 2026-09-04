package com.ecommerce.order_service.mapper;

import com.ecommerce.order_service.api.dto.response.OrderItemResponse;
import com.ecommerce.order_service.api.dto.response.OrderResponse;
import com.ecommerce.order_service.domain.OrderItem;
import com.ecommerce.order_service.domain.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {

        List<OrderItemResponse> items =
                order.getItems()
                        .stream()
                        .map(this::toItemResponse)
                        .toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderUuid(),
                order.getOrderNumber(),
                order.getUserId(),

                order.getStatus().name(),
                order.getPaymentStatus().name(),
                order.getShippingStatus().name(),

                order.getSubtotal(),
                order.getDiscountAmount(),
                order.getShippingFee(),
                order.getFinalAmount(),

                order.getCouponCode(),
                order.getShippingAddressSnapshot(),

                order.getPlacedAt(),
                order.getCreatedAt(),

                items
        );
    }

    private OrderItemResponse toItemResponse(
            OrderItem item
    ) {

        return new OrderItemResponse(
                item.getId(),
                item.getProductId(),
                item.getVariantId(),
                item.getProductNameSnapshot(),
                item.getSkuSnapshot(),
                item.getPriceSnapshot(),
                item.getQuantity(),
                item.getSubtotal(),
                item.getImageSnapshot()
        );
    }
}