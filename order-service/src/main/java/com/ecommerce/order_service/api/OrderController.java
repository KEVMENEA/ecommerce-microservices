package com.ecommerce.order_service.api;


import com.ecommerce.order_service.api.dto.request.CreateOrderRequest;
import com.ecommerce.order_service.api.dto.response.OrderResponse;
import com.ecommerce.order_service.application.OrderApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    public final OrderApplicationService service;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(
            @Valid @RequestBody CreateOrderRequest request
    ) {
        return service.createOrder(request);
    }


    @GetMapping("/{orderUuid}")
    public OrderResponse getOrder(@PathVariable("orderUuid")  UUID orderUuid) {
        return service.getOrder(orderUuid);
    }


    @GetMapping
    public List<OrderResponse> getOrdersByUser(@RequestParam Long userId) {
        return service.getOrdersByUser(userId);
    }

    @PostMapping("/{orderUuid}/inventory/confirm")
    public OrderResponse confirmInventory(
            @PathVariable UUID orderUuid, String reason
    ) {
        return service.confirmInventory(orderUuid,  reason);
    }

    @PostMapping("/{orderUuid}/inventory/release")
    public OrderResponse releaseInventory(
            @PathVariable UUID orderUuid,
            @RequestParam(defaultValue = "Manual compensation")
            String reason
    ) {
        return service.releaseInventory(orderUuid, reason);
    }
}
