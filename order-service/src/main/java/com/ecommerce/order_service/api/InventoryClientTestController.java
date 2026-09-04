package com.ecommerce.order_service.api;

import com.ecommerce.order_service.client.inventory.InventoryReservationClient;
import com.ecommerce.order_service.client.inventory.InventoryReservationRequest;
import com.ecommerce.order_service.client.inventory.InventoryReservationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/test/inventory")
@RequiredArgsConstructor
public class InventoryClientTestController {

    private final InventoryReservationClient inventoryClient;

    @PostMapping("/reserve")
    public InventoryReservationResponse reserve(
            @RequestBody InventoryReservationRequest request
    ) {
        return inventoryClient.reserve(request);
    }
}