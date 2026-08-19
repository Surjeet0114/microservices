package com.surjeet.orderservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CircuitBreakerEventConfig {

    private static final Logger logger =
            LoggerFactory.getLogger(CircuitBreakerEventConfig.class);

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public CircuitBreakerEventConfig(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @PostConstruct
    public void registerEvents() {

        circuitBreakerRegistry
                .circuitBreaker("productService")
                .getEventPublisher()

                .onStateTransition(event ->
                        logger.info(
                                "CircuitBreaker State Changed : {}",
                                event.getStateTransition()))

                .onFailureRateExceeded(event ->
                        logger.warn(
                                "Failure Rate Exceeded : {}",
                                event.getFailureRate()))

                .onCallNotPermitted(event ->
                        logger.warn(
                                "Call Not Permitted (Circuit OPEN)")
                );
    }
}