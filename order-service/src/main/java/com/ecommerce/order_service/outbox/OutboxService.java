package com.ecommerce.order_service.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private static  final String ORDER_TOPIC = "order.events";

    private final OutboxEventRepository outboxEventRepository;

    private final ObjectMapper objectMapper;

    public void saveOrderCreatedEvent(OrderCreatedEvent event){

        try {
            String payload = objectMapper.writeValueAsString(event);
            OutboxEvent outboxEvent = new OutboxEvent(
                    "ORDER",
                    event.orderUuid().toString(),
                    "ORDER_CREATED",
                    ORDER_TOPIC,
                    payload
            );
            outboxEventRepository.save(outboxEvent);

        }catch (JsonProcessingException exception){
            throw new IllegalStateException(
                    "Failed to serialize ORDER_CREATED event",
                    exception
            );
        }

    }
}
