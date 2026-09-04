package com.ecommerce.order_service.repository;

import com.ecommerce.order_service.domain.Order;
import com.ecommerce.order_service.domain.OrderSaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderSagaRepository extends JpaRepository<OrderSaga,Long> {

    Optional<OrderSaga> findByOrderId(Long orderId);

    Optional<OrderSaga> findByOrder(Order order);
}
