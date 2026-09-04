package com.ecommerce.order_service.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_status_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderStatusHistory {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_status_history_order"))
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", length = 30)
     private OrderStatus oldStatus;


    @Enumerated(EnumType.STRING)
    @Column(
            name = "new_status",
            nullable = false,
            length = 30
    )
    private OrderStatus newStatus;

    @Column(length = 255)
    private String reason;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;


    @Column(name = "created_by")
    private Long createdBy;


    public OrderStatusHistory(
            Order order,
            OrderStatus oldStatus,
            OrderStatus newStatus,
            String reason
    ) {

        this.order =
                order;

        this.oldStatus =
                oldStatus;

        this.newStatus =
                newStatus;

        this.reason =
                reason;
    }


    @PrePersist
    protected void onCreate() {

        if (createdAt == null) {
            createdAt =
                    LocalDateTime.now();
        }
    }



}
