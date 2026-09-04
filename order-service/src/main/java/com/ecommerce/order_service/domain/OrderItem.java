package com.ecommerce.order_service.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "order_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_order_items_order"
            )
    )
    private Order order;


    @Column(
            name = "product_id",
            nullable = false
    )
    private Long productId;


    @Column(
            name = "variant_id",
            nullable = false
    )
    private Long variantId;


    @Column(
            name = "product_name_snapshot",
            nullable = false,
            length = 255
    )
    private String productNameSnapshot;


    @Column(
            name = "sku_snapshot",
            nullable = false,
            length = 100
    )
    private String skuSnapshot;


    @Column(
            name = "price_snapshot",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal priceSnapshot;


    @Column(
            nullable = false
    )
    private Integer quantity;


    @Column(
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal subtotal;


    @Column(
            name = "image_snapshot"
    )
    private String imageSnapshot;


    @Column(
            name = "created_at",
            nullable = false
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

    public OrderItem(Long productId,
                     Long variantId,
                     String productNameSnapshot,
                     String skuSnapshot,
                     BigDecimal priceSnapshot,
                     Integer quantity,
                     String imageSnapshot ) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Order item quantity must be greater than zero"
            );
        }
        if (priceSnapshot == null || priceSnapshot.signum() < 0) {
            throw new IllegalArgumentException("Order item price snapshot must be greater than zero");
        }
        this.productId = productId;
        this.variantId = variantId;
        this.productNameSnapshot = productNameSnapshot;
        this.skuSnapshot = skuSnapshot;
        this.priceSnapshot = priceSnapshot;
        this.quantity = quantity;
        this.subtotal = priceSnapshot.multiply(BigDecimal.valueOf(quantity));
        this.imageSnapshot = imageSnapshot;
    }

    void assignOrder(
            Order order
    ) {

        this.order =
                order;
    }

    @PrePersist
    protected void onCreate() {

        if (createdAt == null) {
            createdAt =
                    LocalDateTime.now();
        }
    }


    @PreUpdate
    protected void onUpdate() {

        updatedAt =
                LocalDateTime.now();
    }

}
