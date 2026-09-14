package com.ecommerce.inventory.inventory_service.repository;

import com.ecommerce.inventory.inventory_service.domain.ReservationStatus;
import com.ecommerce.inventory.inventory_service.domain.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockReservationRepository  extends JpaRepository<StockReservation, Long> {

    Optional<StockReservation> findByReservationUuid(UUID reservationUuid);

    Optional<StockReservation> findByOrderIdAndVariantId(
            Long orderId,
            Long variantId
    );
    List<StockReservation> findByStatusAndExpiresAtLessThanEqual(
            ReservationStatus status,
            LocalDateTime expiresAt
    );

}
