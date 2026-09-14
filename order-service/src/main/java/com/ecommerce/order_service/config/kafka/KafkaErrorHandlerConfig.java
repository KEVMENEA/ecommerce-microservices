package com.ecommerce.order_service.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorHandlerConfig {

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<String,String> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate, ((consumerRecord, exception) ->
                new TopicPartition(consumerRecord.topic() + "-dlt",
                        consumerRecord.partition())
                )
        );
        FixedBackOff backOff =
                new FixedBackOff(
                        1000L,
                        2L
                );

        return new DefaultErrorHandler(
                recoverer,
                backOff
        );
    }
}


