package com.ecommerce.order_service.kafka;

import com.ecommerce.order_service.domain.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent,Long> {

    boolean existsByEventId(UUID eventId);
}
