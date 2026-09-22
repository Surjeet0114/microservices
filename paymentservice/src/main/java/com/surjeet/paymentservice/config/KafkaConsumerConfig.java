package com.surjeet.paymentservice.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.backoff.FixedBackOff;
import com.surjeet.paymentservice.exception.InvalidOrderEventException;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(
            KafkaTemplate<String, String> kafkaTemplate) {

        return new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, exception) ->
                        new TopicPartition(
                                record.topic() + ".DLT",
                                record.partition()
                        )
        );
    }

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(
            DeadLetterPublishingRecoverer recoverer) {

        FixedBackOff backOff = new FixedBackOff(
                2000L,
                2L
        );

        DefaultErrorHandler errorHandler =
                new DefaultErrorHandler(
                        recoverer,
                        backOff
                );

        errorHandler.addNotRetryableExceptions(
                InvalidOrderEventException.class
        );

        return errorHandler;
    }
}