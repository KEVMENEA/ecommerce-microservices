package com.ecommerce.order_service.application;

import com.ecommerce.order_service.api.dto.request.CreateOrderItemRequest;
import com.ecommerce.order_service.api.dto.request.CreateOrderRequest;
import com.ecommerce.order_service.api.dto.response.OrderResponse;
import com.ecommerce.order_service.client.inventory.InventoryReservationRequest;
import com.ecommerce.order_service.client.inventory.InventoryReservationResponse;
import com.ecommerce.order_service.client.inventory.ResilientInventoryClient;
import com.ecommerce.order_service.domain.*;
import com.ecommerce.order_service.exception.OrderNotFoundException;
import com.ecommerce.order_service.mapper.OrderMapper;
import com.ecommerce.order_service.repository.OrderInventoryReservationRepository;
import com.ecommerce.order_service.repository.OrderRepository;
import com.ecommerce.order_service.repository.OrderSagaRepository;
import com.ecommerce.order_service.repository.OrderStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderApplicationService {

    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;
    private final OrderSagaRepository sagaRepository;
    private final OrderMapper mapper;
    private final OrderInventoryReservationRepository
            orderInventoryReservationRepository;

    private final ResilientInventoryClient inventoryClient;

    private static final Long DEFAULT_WAREHOUSE_ID = 1L;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {

        // 1. Idempotency check
        OrderResponse existing = findExistingIdempotentOrder(request);

        if (existing != null) {
            return existing;
        }

        // 2. Calculate subtotal
        BigDecimal subtotal = calculateSubtotal(request.items());

        // 3. Normalize discount
        BigDecimal discountAmount =
                request.discountAmount() != null
                        ? request.discountAmount()
                        : BigDecimal.ZERO;

        // 4. Normalize shipping fee
        BigDecimal shippingFee =
                request.shippingFee() != null
                        ? request.shippingFee()
                        : BigDecimal.ZERO;

        // 5. Validate money
        if (discountAmount.compareTo(subtotal) > 0) {
            throw new IllegalArgumentException(
                    "Discount amount cannot exceed subtotal"
            );
        }

        // 6. Calculate final amount
        BigDecimal finalAmount = subtotal
                .subtract(discountAmount)
                .add(shippingFee);

        // 7. Generate business order number
        String orderNumber = generateOrderNumber();

        // 8. Create Order aggregate
        Order order = Order.create(
                request.userId(),
                orderNumber,
                subtotal,
                discountAmount,
                shippingFee,
                finalAmount,
                request.couponCode(),
                request.shippingAddressSnapshot(),
                request.idempotencyKey()
        );

        // 9. Create and attach items
        for (CreateOrderItemRequest itemRequest : request.items()) {

            OrderItem item = new OrderItem(
                    itemRequest.productId(),
                    itemRequest.variantId(),
                    itemRequest.productName(),
                    itemRequest.sku(),
                    itemRequest.price(),
                    itemRequest.quantity(),
                    itemRequest.imageUrl()
            );

            order.addItem(item);
        }

        // 10. Save order
        Order savedOrder = orderRepository.save(order);

        // 11. Save initial status history
        OrderStatusHistory history = new OrderStatusHistory(
                savedOrder,
                null,
                savedOrder.getStatus(),
                "Order created"
        );

        statusHistoryRepository.save(history);

        // 12. Create initial saga
        OrderSaga saga = new OrderSaga(savedOrder);

        sagaRepository.save(saga);

        List<UUID> successfulReservationUuids = new java.util.ArrayList<>();

        try {
            for (OrderItem item : savedOrder.getItems()) {

                InventoryReservationRequest reservationRequest =
                        new InventoryReservationRequest(
                                savedOrder.getId(),
                                item.getVariantId(),
                                DEFAULT_WAREHOUSE_ID,
                                item.getQuantity()
                        );

                InventoryReservationResponse reservationResponse =
                        inventoryClient.reserve(reservationRequest);

                if (!"RESERVED".equalsIgnoreCase(
                        reservationResponse.status()
                )) {
                    throw new IllegalStateException(
                            "Inventory reservation failed for variant: "
                                    + item.getVariantId()
                    );
                }

                // Remember UUID for possible compensation
                // ---------------------------------------------

                successfulReservationUuids.add(
                        reservationResponse.reservationUuid()
                );

                // Track every successful Inventory reservation
                OrderInventoryReservation tracking =
                        new OrderInventoryReservation(
                                savedOrder,
                                reservationResponse.reservationUuid(),
                                item.getVariantId(),
                                reservationResponse.warehouseId(),
                                item.getQuantity()
                        );

                orderInventoryReservationRepository.save(tracking);

                // Keep existing Saga behavior for now
                saga.inventoryReserved(
                        reservationResponse.reservationUuid()
                );
            }

            OrderStatus previousStatus =
                    savedOrder.getStatus();

            savedOrder.markInventoryReserved();

            // 15. Save status history
            OrderStatusHistory inventoryReservedHistory =
                    new OrderStatusHistory(
                            savedOrder,
                            previousStatus,
                            savedOrder.getStatus(),
                            "Inventory reserved"
                    );

            statusHistoryRepository.save(
                    inventoryReservedHistory
            );
            Order updatedOrder =
                    orderRepository.save(savedOrder);

            return mapper.toResponse(updatedOrder);

        } catch (Exception originalException) {

            compensateReservations(successfulReservationUuids);

            throw originalException;
        }
    }


    @Transactional(readOnly = true)
    public OrderResponse getOrder(UUID orderUuid) {

        Order order = findOrderByUuid(orderUuid);

        return mapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser(Long userId) {

        return orderRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }


    @Transactional
    public OrderResponse confirmInventory(UUID orderUuid) {

        Order order = findOrderByUuid(orderUuid);

        OrderSaga saga = sagaRepository.findByOrder(order)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Saga not found for order: " + orderUuid
                        )
                );

        UUID reservationUuid = saga.getInventoryReservationUuid();

        if (reservationUuid == null) {
            throw new IllegalStateException(
                    "Inventory reservation UUID not found for order: "
                            + orderUuid
            );
        }

        saga.inventoryConfirmationPending();

        InventoryReservationResponse response =
                inventoryClient.confirm(reservationUuid);

        if (!"CONFIRMED".equalsIgnoreCase(response.status())) {
            throw new IllegalStateException(
                    "Inventory confirmation failed for reservation: "
                            + reservationUuid
            );
        }

        saga.inventoryConfirmed();

        sagaRepository.save(saga);

        return mapper.toResponse(order);
    }

    @Transactional
    public OrderResponse releaseInventory(
            UUID orderUuid,
            String reason
    ) {

        Order order = findOrderByUuid(orderUuid);

        OrderSaga saga = sagaRepository.findByOrder(order)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Saga not found for order: " + orderUuid
                        )
                );

        UUID reservationUuid = saga.getInventoryReservationUuid();

        if (reservationUuid == null) {
            throw new IllegalStateException(
                    "Inventory reservation UUID not found for order: "
                            + orderUuid
            );
        }

        saga.startCompensation(reason);

        InventoryReservationResponse response =
                inventoryClient.release(reservationUuid);

        if (!"RELEASED".equalsIgnoreCase(response.status())) {
            throw new IllegalStateException(
                    "Inventory release failed for reservation: "
                            + reservationUuid
            );
        }

        saga.inventoryReleased();

        sagaRepository.save(saga);

        return mapper.toResponse(order);
    }
    private Order findOrderByUuid(UUID orderUuid) {

        return orderRepository
                .findByOrderUuid(orderUuid)
                .orElseThrow(() ->
                        new OrderNotFoundException(orderUuid)
                );
    }

    private OrderResponse findExistingIdempotentOrder(
            CreateOrderRequest request
    ) {

        if (request.idempotencyKey() == null
                || request.idempotencyKey().isBlank()) {
            return null;
        }

        return orderRepository
                .findByUserIdAndIdempotencyKey(
                        request.userId(),
                        request.idempotencyKey()
                )
                .map(mapper::toResponse)
                .orElse(null);
    }

    private BigDecimal calculateSubtotal(
            List<CreateOrderItemRequest> items
    ) {

        return items.stream()
                .map(item ->
                        item.price().multiply(
                                BigDecimal.valueOf(item.quantity())
                        )
                )
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    private String generateOrderNumber() {

        return "ORD-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }

    private void compensateReservations(List<UUID> reservationUuids) {

        for (UUID reservationUuid : reservationUuids) {

            try {
                InventoryReservationResponse response =
                        inventoryClient.release(reservationUuid);

                if (!"RELEASED".equalsIgnoreCase(response.status())) {
                    System.err.println(
                            "Compensation failed for reservation: "
                                    + reservationUuid
                                    + ", status="
                                    + response.status()
                    );
                }

                // compensation means “undo a successful step because a later step failed.”

            } catch (Exception compensationException) {

                // Do not hide the original reservation failure.
                System.err.println(
                        "Failed to release inventory reservation: "
                                + reservationUuid
                );

                compensationException.printStackTrace();
            }
        }
    }
}