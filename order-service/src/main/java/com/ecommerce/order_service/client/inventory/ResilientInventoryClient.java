package com.ecommerce.order_service.client.inventory;

import com.ecommerce.order_service.exception.InventoryServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.UUID;

import static reactor.core.Exceptions.unwrap;

@Component
@RequiredArgsConstructor
public class ResilientInventoryClient {

    private final InventoryReservationClient inventoryReservationClient;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    private CircuitBreaker inventoryCircuitBreaker() {
        return circuitBreakerFactory.create("inventoryService");
    }

    public InventoryReservationResponse reserve(InventoryReservationRequest request
    ) {
        return inventoryCircuitBreaker().run(() -> inventoryReservationClient.reserve(request),
                throwable -> handleFailure(
                        throwable,
                        "Inventory reservation service is unavailable"
                )
        );
    }

    public InventoryReservationResponse getReservation(
            UUID reservationUuid
    ) {
        return inventoryCircuitBreaker().run(
                () -> inventoryReservationClient.confirm(
                        reservationUuid
                ),
                throwable -> handleFailure(
                        throwable,
                        "Inventory service is unavailable"
                )
        );
    }


    public InventoryReservationResponse confirm(
            UUID reservationUuid
    ) {
        return inventoryCircuitBreaker().run(
                () -> inventoryReservationClient.confirm(
                        reservationUuid
                ),
                throwable -> handleFailure(
                        throwable,
                        "Unable to confirm inventory reservation"
                )
        );
    }


    public InventoryReservationResponse release(
            UUID reservationUuid
    ) {
        return inventoryCircuitBreaker().run(
                () -> inventoryReservationClient.release(
                        reservationUuid
                ),
                throwable -> handleFailure(
                        throwable,
                        "Unable to release inventory reservation"
                )
        );
    }

    private <T> T handleFailure(Throwable throwable, String unavailableMessage){
        Throwable cause = unwrap(throwable);

        // 409 INSUFFICIENT_STOCK
        if (cause instanceof HttpClientErrorException clientError) {
            throw clientError;
        }

        /*
         * Timeout, connection refused,
         * discovery failure, circuit open, etc.
         */
        throw new InventoryServiceUnavailableException(unavailableMessage, cause);
    }


    private Throwable unwrap(Throwable throwable) {
        Throwable current = throwable;

        while (current.getCause() != null && current.getCause() != current) {

            current = current.getCause();
        }

        return current;
    }
}