package com.ecommerce.order_service.kafka;

import com.ecommerce.order_service.domain.ProcessedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ObjectMapper objectMapper;
    private final ProcessedEventRepository processedEventRepository;

    @KafkaListener(
            topics = "order.events",
            groupId = "order-service-consumer"
    )
    public void consume(String message) throws Exception {

        KafkaEventEnvelope envelope = objectMapper.readValue(
                        message,
                        KafkaEventEnvelope.class);

        if (processedEventRepository.existsByEventId(envelope.eventId())) {

            log.info(
                    "Skipping duplicate event eventId={}",
                    envelope.eventId()
            );

            return;
        }

        log.info(
                "Processing event eventId={}, type={}",
                envelope.eventId(),
                envelope.eventType()
        );

        processedEventRepository.save(new ProcessedEvent(envelope.eventId()));
        log.info(
                "Event processed eventId={}",
                envelope.eventId()
        );
    }
}