package com.ecommerce.order_service.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "orders",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_orders_order_uuid",
                    columnNames = "order_uuid"
            ),
                @UniqueConstraint(
                        name = "uk_orders_order_number",
                        columnNames = "order_number"
                ),
                @UniqueConstraint(
                        name = "uk_orders_user_idemtopency_key",
                        columnNames = {"user_id",
                                    "idempotency_key"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "order_uuid",
            nullable = false,
            unique = true
    )
    private UUID orderUuid;

    @Column(
            name = "order_number",
            nullable = false,
            updatable = true,
            length = 255
    )
    private String orderNumber;

    @Column(
            name = "user_id",
            nullable = false
    )
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 30)
    private PaymentStatus paymentStatus;


    @Enumerated(EnumType.STRING)
    @Column(name = "shipping_status", nullable = false, length = 30)
    private ShippingStatus shippingStatus;

    @Column(
            nullable = false,
            precision = 19,
            scale = 2
    )
    // DECIMAL(19, 2)
    private BigDecimal subtotal;

    @Column(
            name = "discount_amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal discountAmount;

    @Column(
            name = "shipping_fee",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal shippingFee;

    @Column(
            name = "final_amount",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal finalAmount;

    @Column(
            name = "coupon_code",
            length = 50
    )
    private String couponCode;


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            name = "shipping_address_snapshot",
            nullable = false,
            columnDefinition = "jsonb"
    )
    private String shippingAddressSnapshot;

    @Column(
            name = "idempotency_key",
            length = 120
    )
    private String idempotencyKey;

    @Column(name = "placed_at")
    private LocalDateTime placedAt;


    @Column(name = "paid_at")
    private LocalDateTime paidAt;


    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;


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


    @Version
    @Column(nullable = false)
    private Long version;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> items =
            new ArrayList<>();


    protected Order(
            Long userId,
            String orderNumber,
            BigDecimal subtotal,
            BigDecimal discountAmount,
            BigDecimal shippingFee,
            BigDecimal finalAmount,
            String couponCode,
            String shippingAddressSnapshot,
            String idempotencyKey
    ) {

        this.userId = userId;
        this.orderNumber = orderNumber;

        this.subtotal = subtotal;
        this.discountAmount =
                discountAmount != null
                        ? discountAmount
                        : BigDecimal.ZERO;

        this.shippingFee =
                shippingFee != null
                        ? shippingFee
                        : BigDecimal.ZERO;

        this.finalAmount = finalAmount;

        this.couponCode = couponCode;

        this.shippingAddressSnapshot =
                shippingAddressSnapshot;

        this.idempotencyKey =
                idempotencyKey;

        this.status =
                OrderStatus.PENDING;

        this.paymentStatus =
                PaymentStatus.PENDING;

        this.shippingStatus =
                ShippingStatus.PENDING;
    }


    public static Order create(
            Long userId,
            String orderNumber,
            BigDecimal subtotal,
            BigDecimal discountAmount,
            BigDecimal shippingFee,
            BigDecimal finalAmount,
            String couponCode,
            String shippingAddressSnapshot,
            String idempotencyKey
    ) {

        return new Order(
                userId,
                orderNumber,
                subtotal,
                discountAmount,
                shippingFee,
                finalAmount,
                couponCode,
                shippingAddressSnapshot,
                idempotencyKey
        );
    }

    public void addItem(
            OrderItem item
    ) {

        items.add(item);

        item.assignOrder(this);
    }

    public void markInventoryReserved() {

        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException(
                    "Order must be PENDING before inventory can be reserved"
            );
        }

        this.status = OrderStatus.INVENTORY_RESERVED;
    }
    public void markPaymentPending() {
        this.status =
                OrderStatus.PAYMENT_PENDING;

        this.paymentStatus =
                PaymentStatus.PENDING;
    }

    public void markPaid(
            LocalDateTime paidAt
    ) {

        this.paymentStatus =
                PaymentStatus.PAID;

        this.paidAt =
                paidAt;
    }

    public void confirm() {

        this.status =
                OrderStatus.CONFIRMED;
    }


    public void cancel(
            LocalDateTime cancelledAt
    ) {

        this.status =
                OrderStatus.CANCELLED;

        this.cancelledAt =
                cancelledAt;
    }


    @PrePersist
    protected void onCreate() {

        LocalDateTime now =
                LocalDateTime.now();

        if (orderUuid == null) {
            orderUuid =
                    UUID.randomUUID();
        }

        if (createdAt == null) {
            createdAt = now;
        }

        if (placedAt == null) {
            placedAt = now;
        }

        if (status == null) {
            status =
                    OrderStatus.PENDING;
        }

        if (paymentStatus == null) {
            paymentStatus =
                    PaymentStatus.PENDING;
        }

        if (shippingStatus == null) {
            shippingStatus =
                    ShippingStatus.PENDING;
        }

        if (discountAmount == null) {
            discountAmount =
                    BigDecimal.ZERO;
        }

        if (shippingFee == null) {
            shippingFee =
                    BigDecimal.ZERO;
        }
    }


    @PreUpdate
    protected void onUpdate() {

        updatedAt =
                LocalDateTime.now();
    }


}
