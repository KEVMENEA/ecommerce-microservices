package com.ecommerce.order_service.client.inventory;


import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.UUID;

@HttpExchange("/api/v1/inventory/reservations")
public interface InventoryReservationClient {

    @PostExchange
    InventoryReservationResponse reserve(@RequestBody InventoryReservationRequest request);

    @GetExchange("/{reservationUuid}")
    InventoryReservationResponse getReservation(@PathVariable UUID reservationUuid);

    @PostExchange("/{reservationUuid}/confirm")
    InventoryReservationResponse confirm(@PathVariable UUID reservationUuid);

    @PostExchange("/{reservationUuid}/release")
    InventoryReservationResponse release(@PathVariable UUID reservationUuid);
}