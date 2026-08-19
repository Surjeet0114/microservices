package com.surjeet.orderservice.config;

import io.github.resilience4j.retry.RetryRegistry;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RetryEventConfig {

    private static final Logger logger =
            LoggerFactory.getLogger(RetryEventConfig.class);

    private final RetryRegistry retryRegistry;

    public RetryEventConfig(RetryRegistry retryRegistry) {
        this.retryRegistry = retryRegistry;
    }

    @PostConstruct
    public void registerRetryEvents() {

        retryRegistry.retry("productService")
                .getEventPublisher()

                .onRetry(event ->
                        logger.info(
                                "Retry Attempt : {}",
                                event.getNumberOfRetryAttempts()));
    }
}