package com.surjeet.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.surjeet.orderservice.dto.OrderRequestDto;
import com.surjeet.orderservice.dto.OrderResponseDto;
import com.surjeet.orderservice.dto.ProductResponseDto;
import com.surjeet.orderservice.entity.Order;
import com.surjeet.orderservice.entity.OutboxEvent;
import com.surjeet.orderservice.event.OrderCreatedEvent;
import com.surjeet.orderservice.exception.OrderNotFoundException;
import com.surjeet.orderservice.mapper.OrderMapper;
import com.surjeet.orderservice.repository.OrderRepository;
import com.surjeet.orderservice.repository.OutboxEventRepository;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final String PRODUCT_SERVICE_URL =
            "http://productservice/products/fetchProduct/{id}";

    // Before Eureka
    // private static final String PRODUCT_SERVICE_URL =
    //         "http://localhost:8082/products/fetchProduct/{id}";

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    // RestClient
    private final RestClient restClient;

    // Fault tolerance
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    // Outbox Pattern
    private final OutboxEventRepository outboxEventRepository;

    // Jackson JSON serialization
    private final ObjectMapper objectMapper;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderMapper orderMapper,
            RestClient.Builder builder,
            CircuitBreakerRegistry circuitBreakerRegistry,
            OutboxEventRepository outboxEventRepository,
            ObjectMapper objectMapper
    ) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;

        this.restClient = builder.build();

        this.circuitBreakerRegistry = circuitBreakerRegistry;

        this.outboxEventRepository = outboxEventRepository;

        this.objectMapper = objectMapper;
    }

    @Transactional
    @Override
    @Retry(
            name = "productService",
            fallbackMethod = "createOrderFallback"
    )
    @CircuitBreaker(
            name = "productService",
            fallbackMethod = "createOrderFallback"
    )
    @RateLimiter(
            name = "productService",
            fallbackMethod = "createOrderFallback"
    )
    @Bulkhead(
            name = "productService",
            type = Bulkhead.Type.SEMAPHORE,
            fallbackMethod = "createOrderFallback"
    )
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {

        // Fetch Product from Product Service
        ProductResponseDto product =
                getProduct(orderRequestDto.getProductId());

        // Convert DTO -> Entity
        Order order = orderMapper.toEntity(orderRequestDto);

        // Set Product Details
        order.setProductName(product.getName());
        order.setProductPrice(product.getPrice());

        // Calculate Total Amount
        order.setTotalAmount(
                product.getPrice() * order.getQuantity()
        );

        // Save Order
        Order savedOrder = orderRepository.save(order);

        // Create Domain Event
        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getProductId(),
                savedOrder.getProductName(),
                savedOrder.getQuantity(),
                savedOrder.getTotalAmount()
        );

        // Convert Event -> JSON
        String payload;

        try {

            payload = objectMapper.writeValueAsString(event);

        } catch (JsonProcessingException e) {

            throw new IllegalStateException(
                    "Failed to serialize OrderCreatedEvent",
                    e
            );
        }

        // Create Outbox Event
        OutboxEvent outboxEvent = new OutboxEvent();

        outboxEvent.setEventType("ORDER_CREATED");
        outboxEvent.setAggregateType("ORDER");

        // Order ID is Integer while Outbox aggregateId is Long
        outboxEvent.setAggregateId(
                savedOrder.getId().longValue()
        );

        outboxEvent.setPayload(payload);
        outboxEvent.setStatus("NEW");
        outboxEvent.setCreatedAt(LocalDateTime.now());

        // Save Outbox Event
        outboxEventRepository.save(outboxEvent);

        /*
         * Kafka is NOT called here anymore.
         *
         * The Outbox Publisher will later read this NEW event
         * and publish it to Kafka.
         */

        // Convert Entity -> Response DTO
        OrderResponseDto response =
                orderMapper.toResponse(savedOrder);

        // Fault Tolerance Information
        response.setProductInstancePort(
                product.getInstancePort()
        );

        response.setCircuitBreakerState(
                circuitBreakerRegistry
                        .circuitBreaker("productService")
                        .getState()
                        .name()
        );

        response.setRetryStatus(
                "No Retry Required"
        );

        response.setMessage(
                "Order created successfully"
        );

        response.setStatus(
                "SUCCESS"
        );

        response.setRateLimiterStatus(
                "PERMITTED"
        );

        response.setBulkheadStatus(
                "PERMITTED"
        );

        return response;
    }

    // Get Order By ID
    @Override
    public OrderResponseDto getOrderById(Integer id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id : " + id
                        )
                );

        OrderResponseDto response =
                orderMapper.toResponse(order);

        ProductResponseDto product =
                getProduct(order.getProductId());

        response.setProductInstancePort(
                product.getInstancePort()
        );

        return response;
    }

    // Get All Orders
    @Override
    public List<OrderResponseDto> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    // Update Order
    @Override
    public OrderResponseDto updateOrder(
            Integer id,
            OrderRequestDto orderRequestDto
    ) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id : " + id
                        )
                );

        // Fetch Latest Product Details
        ProductResponseDto product =
                getProduct(orderRequestDto.getProductId());

        // Update Entity
        orderMapper.updateEntity(
                order,
                orderRequestDto
        );

        // Update Product Details
        order.setProductName(
                product.getName()
        );

        order.setProductPrice(
                product.getPrice()
        );

        // Recalculate Total
        order.setTotalAmount(
                product.getPrice() * order.getQuantity()
        );

        // Save Updated Order
        Order updatedOrder =
                orderRepository.save(order);

        // Convert Entity -> Response DTO
        OrderResponseDto response =
                orderMapper.toResponse(updatedOrder);

        response.setProductInstancePort(
                product.getInstancePort()
        );

        response.setMessage(
                "Order updated successfully"
        );

        response.setStatus(
                "SUCCESS"
        );

        return response;
    }

    // Delete Order
    @Override
    public void deleteOrder(Integer id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id : " + id
                        )
                );

        orderRepository.delete(order);
    }

    // Get Product from Product Service
    private ProductResponseDto getProduct(Integer productId) {

        ProductResponseDto product = restClient.get()
                .uri(
                        PRODUCT_SERVICE_URL,
                        productId
                )
                .retrieve()
                .body(ProductResponseDto.class);

        if (product == null) {

            throw new IllegalStateException(
                    "Product Service returned an empty response."
            );
        }

        return product;
    }

    // Fallback for Circuit Breaker / Retry / Rate Limiter / Bulkhead
    private OrderResponseDto createOrderFallback(
            OrderRequestDto request,
            Exception ex
    ) {

        System.out.println(
                "Fallback called because: "
                        + ex.getClass().getSimpleName()
        );

        OrderResponseDto response =
                new OrderResponseDto();

        response.setProductName(
                "Product Service Unavailable"
        );

        response.setProductPrice(
                0.0
        );

        response.setQuantity(
                request.getQuantity()
        );

        response.setTotalAmount(
                0.0
        );

        response.setCircuitBreakerState(
                circuitBreakerRegistry
                        .circuitBreaker("productService")
                        .getState()
                        .name()
        );

        response.setStatus(
                "FAILED"
        );

        // Rate Limiter
        if (ex instanceof RequestNotPermitted) {

            response.setMessage(
                    "Rate Limit Exceeded."
            );

            response.setRetryStatus(
                    "Not Executed"
            );

            response.setRateLimiterStatus(
                    "BLOCKED"
            );

            response.setBulkheadStatus(
                    "PERMITTED"
            );
        }

        // Circuit Breaker
        else if (ex instanceof CallNotPermittedException) {

            response.setMessage(
                    "Circuit Breaker is OPEN."
            );

            response.setRetryStatus(
                    "Retry Skipped"
            );

            response.setRateLimiterStatus(
                    "PERMITTED"
            );

            response.setBulkheadStatus(
                    "PERMITTED"
            );
        }

        // Bulkhead
        else if (ex instanceof BulkheadFullException) {

            response.setMessage(
                    "Bulkhead is FULL."
            );

            response.setRetryStatus(
                    "Not Executed"
            );

            response.setRateLimiterStatus(
                    "PERMITTED"
            );

            response.setBulkheadStatus(
                    "BLOCKED"
            );
        }

        // General Product Service Failure
        else {

            response.setMessage(
                    "Product Service is currently unavailable."
            );

            response.setRetryStatus(
                    "All retries exhausted"
            );

            response.setRateLimiterStatus(
                    "PERMITTED"
            );

            response.setBulkheadStatus(
                    "PERMITTED"
            );
        }

        return response;
    }
}