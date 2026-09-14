package com.ecommerce.inventory.inventory_service.repository;

import com.ecommerce.inventory.inventory_service.domain.Inventory;
import com.ecommerce.inventory.inventory_service.domain.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByVariantIdAndWarehouseId(
            Long variantId,
            Long warehouseId
    );

    Optional<Inventory> findByVariantId(Long variantId);

    boolean existsByVariantIdAndWarehouseId(Long variantId, Long warehouseId);

//    So low-stock rows still stay in the database. The query does not delete anything.
    @Query("""
        select i
        from Inventory i
        where (i.onHandQuantity - i.reservedQuantity)
              <= i.lowStockThreshold
          and i.deletedAt is null
    """)
    List<Inventory> findLowStockInventory();

}
