package com.ecommerce.inventory.inventory_service.repository;

import com.ecommerce.inventory.inventory_service.domain.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    Optional<Warehouse> findByCode(String code);
    boolean existsByCode(String code);
}
