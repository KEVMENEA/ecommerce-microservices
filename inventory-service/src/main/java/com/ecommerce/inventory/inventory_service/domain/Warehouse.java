package com.ecommerce.inventory.inventory_service.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "warehouse",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_warehouses_code",
                columnNames = {
                "code"
        })
)
@Setter
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            length = 150
    )
    private String name;

    @Column(
            nullable = false,
            length = 50
    )
    private String code;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private WarehouseStatus status;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;


    protected Warehouse() {
    }


    public Warehouse(
            String name,
            String code,
            String address,
            WarehouseStatus status
    ) {
        this.name = name;
        this.code = code;
        this.address = address;
        this.status = WarehouseStatus.ACTIVE;
    }



    @PrePersist
    protected void onCreate() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (status == null) {
            status = WarehouseStatus.ACTIVE;
        }
    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getAddress() {
        return address;
    }

    public WarehouseStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public void activate() {
        this.status = WarehouseStatus.ACTIVE;
    }


    public void deactivate() {
        this.status = WarehouseStatus.INACTIVE;
    }
}