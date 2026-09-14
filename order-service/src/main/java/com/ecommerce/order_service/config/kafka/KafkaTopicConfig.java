package com.ecommerce.order_service.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String ORDER_EVENTS_TOPIC = "order.events";

    public static final String ORDER_EVENTS_DLT_TOPIC = "order.events-dlt";

    @Bean
    public NewTopic orderEventsTopic() {
        return TopicBuilder
                .name(ORDER_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }


    @Bean
    public NewTopic orderEventsDLTTopic() {
        return TopicBuilder
                .name(ORDER_EVENTS_DLT_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}