package com.ecommerce.inventory.inventory_service.exception;

import java.util.UUID;

public class ReservationNotFoundException
        extends RuntimeException {

    public ReservationNotFoundException(
            UUID reservationUuid
    ) {
        super(
                "Reservation not found: "
                        + reservationUuid
        );
    }
}