package com.ecommerce.order_service.repository;

import com.ecommerce.order_service.domain.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory,Long> {
}
