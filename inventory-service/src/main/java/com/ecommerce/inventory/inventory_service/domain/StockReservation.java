package com.ecommerce.inventory.inventory_service.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(
        name = "stock_reservations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_stock_reservation_uuid",
                        columnNames = "reservation_uuid"
                ),
                @UniqueConstraint(
                        name = "uk_stock_reservation_order_variant",
                        columnNames = {
                                "order_id",
                                "variant_id"
                        }
                )
        }
)
public class StockReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(
            name = "reservation_uuid",
            nullable = false,
            unique = true
    )
    private UUID reservationUuid;


    @Column(
            name = "order_id",
            nullable = false
    )
    private Long orderId;


    @Column(
            name = "variant_id",
            nullable = false
    )
    private Long variantId;


    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "warehouse_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_stock_reservation_warehouse"
            )
    )
    private Warehouse warehouse;


    @Column(
            nullable = false
    )
    private int quantity;


    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private ReservationStatus status;


    @Column(name = "expires_at")
    private LocalDateTime expiresAt;


    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;


    @Column(name = "created_by")
    private Long createdBy;


    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @Column(name = "updated_by")
    private Long updatedBy;


    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


    @Column(name = "deleted_by")
    private Long deletedBy;


    protected StockReservation() {
    }


    public StockReservation(
            UUID reservationUuid,
            Long orderId,
            Long variantId,
            Warehouse warehouse,
            int quantity,
            LocalDateTime expiresAt
    ) {

        if (reservationUuid == null) {
            throw new IllegalArgumentException(
                    "reservationUuid is required"
            );
        }

        if (orderId == null) {
            throw new IllegalArgumentException(
                    "orderId is required"
            );
        }

        if (variantId == null) {
            throw new IllegalArgumentException(
                    "variantId is required"
            );
        }

        if (warehouse == null) {
            throw new IllegalArgumentException(
                    "warehouse is required"
            );
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "quantity must be greater than zero"
            );
        }

        this.reservationUuid = reservationUuid;

        this.orderId = orderId;
        this.variantId = variantId;

        this.warehouse = warehouse;

        this.quantity = quantity;

        this.status = ReservationStatus.RESERVED;

        this.expiresAt = expiresAt;
    }


    @PrePersist
    protected void onCreate() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (reservationUuid == null) {
            reservationUuid = UUID.randomUUID();
        }

        if (status == null) {
            status = ReservationStatus.RESERVED;
        }
    }


    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


    // ============================================================
    // DOMAIN BEHAVIOR
    // ============================================================

    public void confirm() {

        ensureReserved();

        status = ReservationStatus.CONFIRMED;
    }


    public void release() {

        ensureReserved();

        status = ReservationStatus.RELEASED;
    }


    public void expire() {

        ensureReserved();

        status = ReservationStatus.EXPIRED;
    }


    public boolean isExpired(LocalDateTime now) {

        return status == ReservationStatus.RESERVED
                && expiresAt != null
                && !expiresAt.isAfter(now);
    }


    public boolean isActive() {
        return status == ReservationStatus.RESERVED;
    }


    private void ensureReserved() {

        if (status != ReservationStatus.RESERVED) {

            throw new IllegalStateException(
                    "Reservation is not in RESERVED state"
            );
        }
    }



}