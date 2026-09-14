package com.ecommerce.inventory.inventory_service.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "stock_movements")
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "variant_id", nullable = false)
    private Long variantId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "warehouse_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_stock_movement_warehouse")
    )
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "reservation_id",
            foreignKey = @ForeignKey(name = "fk_stock_movement_reservation")
    )
    private StockReservation reservation;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 40)
    private StockMovementType movementType;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "reference_type", length = 50)
    private String referenceType;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private Long createdBy;

    protected StockMovement() {
        // Required by JPA
    }

    public StockMovement(
            Long variantId,
            Warehouse warehouse,
            StockReservation reservation,
            StockMovementType movementType,
            int quantity,
            String referenceType,
            Long referenceId,
            String reason
    ) {
        if (variantId == null) {
            throw new IllegalArgumentException("variantId is required");
        }

        if (warehouse == null) {
            throw new IllegalArgumentException("warehouse is required");
        }

        if (movementType == null) {
            throw new IllegalArgumentException("movementType is required");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "quantity must be greater than zero"
            );
        }

        this.variantId = variantId;
        this.warehouse = warehouse;
        this.reservation = reservation;
        this.movementType = movementType;
        this.quantity = quantity;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.reason = reason;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}