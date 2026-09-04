package com.ecommerce.order_service.repository;


import com.ecommerce.order_service.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order,Long> {

    Optional<Order> findByOrderUuid(UUID orderUuid);

    Optional<Order> findByUserIdAndIdempotencyKey(
            Long userId,
            String idempotencyKey
    );

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
}
