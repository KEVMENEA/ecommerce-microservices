package com.ecommerce.order_service.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.UUID;

public record KafkaEventEnvelope (
        UUID eventId,
        String eventType,
        String aggregateType,
        String aggregateId,
        JsonNode payload
){
}
