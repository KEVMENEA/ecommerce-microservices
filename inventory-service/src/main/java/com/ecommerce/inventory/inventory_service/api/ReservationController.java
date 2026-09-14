package com.ecommerce.inventory.inventory_service.api;

import com.ecommerce.inventory.inventory_service.api.dto.ReservationResponse;
import com.ecommerce.inventory.inventory_service.api.dto.ReserveStockRequest;
import com.ecommerce.inventory.inventory_service.application.InventoryApplicationService;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final InventoryApplicationService service;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse reserve(@Valid @RequestBody ReserveStockRequest request
    ) {

        return service.reserve(request);
    }


    @GetMapping("/{reservationUuid}")
    public ReservationResponse getReservation(@PathVariable UUID reservationUuid) {

        return service.getReservation(reservationUuid);
    }


    @PostMapping("/{reservationUuid}/confirm")
    public ReservationResponse confirm(@PathVariable UUID reservationUuid) {

        return service.confirm(
                reservationUuid
        );
    }


    @PostMapping("/{reservationUuid}/release")
    public ReservationResponse release(
            @PathVariable
            UUID reservationUuid
    ) {

        return service.release(
                reservationUuid
        );
    }
}