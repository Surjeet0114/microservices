package com.surjeet.orderservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    @LoadBalanced
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
    /*
    Earlier
    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }
     */
    /*
    Why LoadBalancer?

Imagine later you run:
productservice
8082
productservice
8083
productservice
8084

Without LoadBalancer:

Which one should RestClient call?

It doesn't know.

With @LoadBalanced:
Request 1 → 8082
Request 2 → 8083
Request 3 → 8084
Request 4 → 8082

Round Robin automatically.
*/