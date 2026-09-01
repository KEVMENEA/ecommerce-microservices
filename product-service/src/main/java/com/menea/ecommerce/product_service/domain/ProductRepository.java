package com.menea.ecommerce.product_service.domain;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    boolean existsBySku(String sku);

    Optional<Product> findByIdAndActiveTrue(UUID id);

    Page<Product> findAllByActiveTrue(Pageable pageable);
}