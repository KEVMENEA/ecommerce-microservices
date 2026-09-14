package com.ecommerce.inventory.inventory_service.mapper;


import com.ecommerce.inventory.inventory_service.api.dto.InventoryResponse;
import com.ecommerce.inventory.inventory_service.api.dto.ReservationResponse;
import com.ecommerce.inventory.inventory_service.api.dto.StockMovementResponse;
import com.ecommerce.inventory.inventory_service.api.dto.WarehouseResponse;
import com.ecommerce.inventory.inventory_service.domain.Inventory;
import com.ecommerce.inventory.inventory_service.domain.StockMovement;
import com.ecommerce.inventory.inventory_service.domain.StockReservation;
import com.ecommerce.inventory.inventory_service.domain.Warehouse;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public InventoryResponse toResponse(
            Inventory inventory
    ) {

        return new InventoryResponse(
                inventory.getId(),
                inventory.getVariantId(),
                inventory.getWarehouse().getId(),
                inventory.getOnHandQuantity(),
                inventory.getReservedQuantity(),
                inventory.availableQuantity(),
                inventory.getLowStockThreshold(),
                inventory.isLowStock(),
                inventory.getStatus().name(),
                inventory.getVersion()
        );
    }


    public ReservationResponse toResponse(
            StockReservation reservation
    ) {

        return new ReservationResponse(
                reservation.getReservationUuid(),
                reservation.getOrderId(),
                reservation.getVariantId(),
                reservation.getWarehouse().getId(),
                reservation.getQuantity(),
                reservation.getStatus().name(),
                reservation.getExpiresAt(),
                reservation.getCreatedAt()
        );
    }

    public StockMovementResponse toResponse(
            StockMovement movement
    ) {

        return new StockMovementResponse(
                movement.getId(),
                movement.getVariantId(),
                movement.getWarehouse().getId(),

                movement.getReservation() != null
                        ? movement.getReservation().getId()
                        : null,

                movement.getMovementType().name(),
                movement.getQuantity(),
                movement.getReferenceType(),
                movement.getReferenceId(),
                movement.getReason(),
                movement.getCreatedAt()
        );
    }


    public WarehouseResponse toResponse(
            Warehouse warehouse
    ) {

        return new WarehouseResponse(
                warehouse.getId(),
                warehouse.getName(),
                warehouse.getCode(),
                warehouse.getAddress(),
                warehouse.getStatus().name(),
                warehouse.getCreatedAt()
        );
    }


}
