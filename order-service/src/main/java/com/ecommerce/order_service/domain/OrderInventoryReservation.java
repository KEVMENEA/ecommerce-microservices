package com.ecommerce.order_service.domain;

import com.ecommerce.order_service.api.dto.request.OrderInventoryReservationStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "order_inventory_reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderInventoryReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "reservation_uuid", nullable = false, unique = true)
    private UUID reservationUuid;

    @Column(name = "variant_id", nullable = false)
    private Long variantId;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderInventoryReservationStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public OrderInventoryReservation(
            Order order,
            UUID reservationUuid,
            Long variantId,
            Long warehouseId,
            Integer quantity
    ) {
        this.order = order;
        this.reservationUuid = reservationUuid;
        this.variantId = variantId;
        this.warehouseId = warehouseId;
        this.quantity = quantity;
        this.status = OrderInventoryReservationStatus.RESERVED;
    }

    public void markConfirmed() {
        this.status = OrderInventoryReservationStatus.CONFIRMED;
    }

    public void markReleased() {
        this.status = OrderInventoryReservationStatus.RELEASED;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}