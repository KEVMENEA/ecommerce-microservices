package com.ecommerce.order_service.repository;

import com.ecommerce.order_service.domain.Order;
import com.ecommerce.order_service.domain.OrderInventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderInventoryReservationRepository
        extends JpaRepository<OrderInventoryReservation, Long> {

    List<OrderInventoryReservation> findByOrder(Order order);
}