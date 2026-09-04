package com.ecommerce.order_service.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "order_sagas")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderSaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(name = "inventory_reservation_uuid")
    private UUID inventoryReservationUuid;
    private Long paymentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private SagaStep currentStep;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SagaStatus sagaStatus;

    @Column(columnDefinition = "text")
    private String failureReason;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public OrderSaga(Order order) {
        this.order = order;
        this.currentStep = SagaStep.ORDER_CREATED;
        this.sagaStatus = SagaStatus.STARTED;
    }

    public void inventoryReservationPending() {
        moveTo(SagaStep.INVENTORY_RESERVATION_PENDING, SagaStatus.IN_PROGRESS);
    }

    public void inventoryReserved(UUID reservationUuid) {
        this.inventoryReservationUuid = reservationUuid;
        moveTo(
                SagaStep.INVENTORY_RESERVED,
                SagaStatus.IN_PROGRESS
        );
    }

    public void paymentPending() {
        moveTo(SagaStep.PAYMENT_PENDING, SagaStatus.IN_PROGRESS);
    }

    public void paymentCompleted(Long paymentId) {
        this.paymentId = paymentId;
        moveTo(SagaStep.PAYMENT_COMPLETED, SagaStatus.IN_PROGRESS);
    }

    public void inventoryConfirmationPending() {
        currentStep = SagaStep.INVENTORY_CONFIRMATION_PENDING;
    }

    public void inventoryConfirmed() {
        currentStep = SagaStep.INVENTORY_CONFIRMED;
    }

    public void complete() {
        moveTo(SagaStep.COMPLETED, SagaStatus.COMPLETED);
    }

    public void startCompensation(String reason) {
        failureReason = reason;
        moveTo(SagaStep.COMPENSATION_PENDING, SagaStatus.COMPENSATING);
    }

    public void inventoryReleased() {
        currentStep = SagaStep.INVENTORY_RELEASED;
    }

    public void fail(String reason) {
        failureReason = reason;
        moveTo(SagaStep.FAILED, SagaStatus.FAILED);
    }

    private void moveTo(SagaStep step, SagaStatus status) {
        this.currentStep = step;
        this.sagaStatus = status;
    }

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}