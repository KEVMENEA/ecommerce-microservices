package com.ecommerce.order_service.outbox;

import com.ecommerce.order_service.kafka.KafkaEventEnvelope;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;



@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {
    private final OutboxEventRepository  outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    public void publish() {
        List<OutboxEvent> events = outboxEventRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        for (OutboxEvent event : events) {
            try {
                JsonNode payload = new ObjectMapper().readTree(event.getPayload());

                KafkaEventEnvelope envelope = new KafkaEventEnvelope(
                        event.getEventId(),
                        event.getEventType(),
                        event.getAggregateType(),
                        event.getAggregateId(),
                       payload
                );

                String message = objectMapper.writeValueAsString(envelope);


                kafkaTemplate.send(
                        event.getTopic(),
                        event.getAggregateId(),
                        message
                ).get();

                event.markPublished();
                outboxEventRepository.save(event);

                log.info(
                        "Published event eventId={}, type={}, topic={}",
                        event.getEventId(),
                        event.getEventType(),
                        event.getTopic()
                );

            } catch (Exception ex) {
                log.error(
                        "Failed to publish event eventId={}",
                        event.getEventId(),
                        ex
                );
            }
        }
    }
}
