package com.ecommerce.inventory.inventory_service.repository;

import com.ecommerce.inventory.inventory_service.domain.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findByVariantIdOrderByCreatedAtDesc(
            Long variantId
    );

    List<StockMovement> findByVariantIdAndWarehouseIdOrderByCreatedAtDesc(
            Long variantId,
            Long warehouseId
    );
}