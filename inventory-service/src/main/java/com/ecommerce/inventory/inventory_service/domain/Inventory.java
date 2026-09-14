package com.ecommerce.inventory.inventory_service.domain;

import com.ecommerce.inventory.inventory_service.domain.InventoryStatus;
import com.ecommerce.inventory.inventory_service.domain.Warehouse;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "inventory",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_inventory_variant_warehouse",
                        columnNames = {
                                "variant_id",
                                "warehouse_id"
                        }
                )
        }
)
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


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
                    name = "fk_inventory_warehouse"
            )
    )
    private Warehouse warehouse;


    @Column(
            name = "on_hand_quantity",
            nullable = false
    )
    private int onHandQuantity;


    @Column(
            name = "reserved_quantity",
            nullable = false
    )
    private int reservedQuantity;


    @Column(
            name = "low_stock_threshold",
            nullable = false
    )
    private int lowStockThreshold;


    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private InventoryStatus status;


    @Version
    @Column(nullable = false)
    private Long version;


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


    protected Inventory() {
    }


    public Inventory(
            Long variantId,
            Warehouse warehouse,
            int onHandQuantity,
            int lowStockThreshold
    ) {

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

        if (onHandQuantity < 0) {
            throw new IllegalArgumentException(
                    "onHandQuantity cannot be negative"
            );
        }

        if (lowStockThreshold < 0) {
            throw new IllegalArgumentException(
                    "lowStockThreshold cannot be negative"
            );
        }

        this.variantId = variantId;
        this.warehouse = warehouse;

        this.onHandQuantity = onHandQuantity;
        this.reservedQuantity = 0;

        this.lowStockThreshold = lowStockThreshold;

        this.status = InventoryStatus.ACTIVE;
    }


    @PrePersist
    protected void onCreate() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (status == null) {
            status = InventoryStatus.ACTIVE;
        }
    }


    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


    // ============================================================
    // DOMAIN BEHAVIOR
    // ============================================================

    public void reserve(int quantity) {

        validatePositiveQuantity(quantity);

        if (status != InventoryStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Inventory is not active"
            );
        }

        if (availableQuantity() < quantity) {
            throw new IllegalStateException(
                    "Insufficient available inventory"
            );
        }

        reservedQuantity += quantity;
    }


    public void release(int quantity) {

        validatePositiveQuantity(quantity);

        if (reservedQuantity < quantity) {
            throw new IllegalStateException(
                    "Cannot release more than reserved quantity"
            );
        }

        reservedQuantity -= quantity;
    }


    public void confirm(int quantity) {

        validatePositiveQuantity(quantity);

        if (reservedQuantity < quantity) {
            throw new IllegalStateException(
                    "Cannot confirm more than reserved quantity"
            );
        }

        if (onHandQuantity < quantity) {
            throw new IllegalStateException(
                    "Insufficient on-hand inventory"
            );
        }

        reservedQuantity -= quantity;
        onHandQuantity -= quantity;
    }


    public void addStock(int quantity) {

        validatePositiveQuantity(quantity);

        onHandQuantity += quantity;
    }


    public void removeStock(int quantity) {

        validatePositiveQuantity(quantity);

        if (availableQuantity() < quantity) {
            throw new IllegalStateException(
                    "Cannot remove reserved inventory"
            );
        }

        onHandQuantity -= quantity;
    }


    public int availableQuantity() {
        return onHandQuantity - reservedQuantity;
    }


    public boolean isLowStock() {
        return availableQuantity() <= lowStockThreshold;
    }


    private void validatePositiveQuantity(int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }
    }


    // ============================================================
    // GETTERS
    // ============================================================

    public Long getId() {
        return id;
    }

    public Long getVariantId() {
        return variantId;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public int getOnHandQuantity() {
        return onHandQuantity;
    }

    public int getReservedQuantity() {
        return reservedQuantity;
    }

    public int getLowStockThreshold() {
        return lowStockThreshold;
    }

    public InventoryStatus getStatus() {
        return status;
    }

    public Long getVersion() {
        return version;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public Long getDeletedBy() {
        return deletedBy;
    }
}