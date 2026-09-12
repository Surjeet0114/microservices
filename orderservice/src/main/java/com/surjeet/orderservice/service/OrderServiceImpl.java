package com.surjeet.orderservice.service;

import com.surjeet.orderservice.dto.OrderRequestDto;
import com.surjeet.orderservice.dto.OrderResponseDto;
import com.surjeet.orderservice.dto.ProductResponseDto;
import com.surjeet.orderservice.entity.Order;
import com.surjeet.orderservice.exception.OrderNotFoundException;
import com.surjeet.orderservice.mapper.OrderMapper;
import com.surjeet.orderservice.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadFullException;

import com.surjeet.orderservice.event.OrderCreatedEvent;
import com.surjeet.orderservice.event.OrderEventProducer;

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

    // RestTemplate
    // private final RestTemplate restTemplate;

    // RestClient
    private final RestClient restClient;

    // OpenFeign
    // private final ProductClient productClient;

    //for checking of the fault tolerance
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    //OrderEventProducer
    private final OrderEventProducer orderEventProducer;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderMapper orderMapper,
            RestClient.Builder builder,
            CircuitBreakerRegistry circuitBreakerRegistry,
            OrderEventProducer orderEventProducer

//      RestTemplate restTemplate,
//      RestClient restClient,
//      ProductClient productClient
    ){

        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;

        this.restClient = builder.build();

        // this.restTemplate = restTemplate;
        // this.restClient = restClient;
        // this.productClient = productClient;

        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.orderEventProducer = orderEventProducer;
    }

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

        // Create Kafka Event
        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getProductId(),
                savedOrder.getProductName(),
                savedOrder.getQuantity(),
                savedOrder.getTotalAmount()
        );

        // Publish Event to Kafka
        orderEventProducer.sendOrderCreatedEvent(event);

        // Convert Entity -> DTO
        OrderResponseDto response =
                orderMapper.toResponse(savedOrder);

        //Fault Tolerance in postman will show
        response.setProductInstancePort(
                product.getInstancePort()
        );

        response.setCircuitBreakerState(
                circuitBreakerRegistry
                        .circuitBreaker("productService")
                        .getState()
                        .name()
        );

        //retry
        response.setRetryStatus("No Retry Required");

        response.setMessage("Order created successfully");

        //For status
        response.setStatus("SUCCESS");

        //Rate limiter Status
        response.setRateLimiterStatus("PERMITTED");

        //Bulkhead
        response.setBulkheadStatus("PERMITTED");

        return response;
    }

    @Override
    public OrderResponseDto getOrderById(Integer id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id : " + id));

        OrderResponseDto response =
                orderMapper.toResponse(order);

        ProductResponseDto product =
                getProduct(order.getProductId());

        //Fault Tolerance in postman will show
        response.setProductInstancePort(
                product.getInstancePort()
        );

        return response;
    }

    @Override
    public List<OrderResponseDto> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    public OrderResponseDto updateOrder(
            Integer id,
            OrderRequestDto orderRequestDto) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id : " + id));

        // Fetch Latest Product Details
        ProductResponseDto product =
                getProduct(orderRequestDto.getProductId());

        // Update Entity
        orderMapper.updateEntity(order, orderRequestDto);

        // Update Product Details
        order.setProductName(product.getName());
        order.setProductPrice(product.getPrice());

        // Recalculate Total
        order.setTotalAmount(
                product.getPrice() * order.getQuantity()
        );

        Order updatedOrder = orderRepository.save(order);

        // Convert Entity -> DTO
        OrderResponseDto response =
                orderMapper.toResponse(updatedOrder);

        //Fault Tolerance in postman will show
        response.setProductInstancePort(
                product.getInstancePort()
        );

        response.setMessage("Order updated successfully");
        response.setStatus("SUCCESS");

        return response;
    }

    @Override
    public void deleteOrder(Integer id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id : " + id));

        orderRepository.delete(order);
    }

    // RestTemplate
/*
private ProductResponseDto getProduct(Integer productId) {

    return restTemplate.getForObject(
            PRODUCT_SERVICE_URL,
            ProductResponseDto.class,
            productId
    );
}
*/

    // RestClient
    private ProductResponseDto getProduct(Integer productId) {

        ProductResponseDto product = restClient.get()
                .uri(PRODUCT_SERVICE_URL, productId)
                .retrieve()
                .body(ProductResponseDto.class);

        if (product == null) {
            throw new IllegalStateException(
                    "Product Service returned an empty response."
            );
        }

        return product;
    }

    // OpenFeign
/*
private ProductResponseDto getProduct(Integer productId) {

    return productClient.getProduct(productId);
}
*/


    //Circuit breaker
    private OrderResponseDto createOrderFallback(
            OrderRequestDto request,
            Exception ex) {

        System.out.println(
                "Fallback called because: "
                        + ex.getClass().getSimpleName()
        );

        OrderResponseDto response = new OrderResponseDto();

        response.setProductName("Product Service Unavailable");
        response.setProductPrice(0.0);
        response.setQuantity(request.getQuantity());
        response.setTotalAmount(0.0);

//      response.setMessage(ex.getMessage());

        response.setCircuitBreakerState(
                circuitBreakerRegistry
                        .circuitBreaker("productService")
                        .getState()
                        .name()
        );

        //for status
        response.setStatus("FAILED");

        if (ex instanceof RequestNotPermitted) {

            response.setMessage("Rate Limit Exceeded.");
            response.setRetryStatus("Not Executed");
            response.setRateLimiterStatus("BLOCKED");

            response.setBulkheadStatus("PERMITTED");


        } else if (ex instanceof CallNotPermittedException) {
            response.setMessage("Circuit Breaker is OPEN.");
            response.setRetryStatus("Retry Skipped");
            response.setRateLimiterStatus("PERMITTED");

            response.setBulkheadStatus("PERMITTED");


        } else if (ex instanceof BulkheadFullException) {

            response.setMessage("Bulkhead is FULL.");
            response.setRetryStatus("Not Executed");
            response.setRateLimiterStatus("PERMITTED");
            response.setBulkheadStatus("BLOCKED");
        }
        else {

            response.setMessage(
                    "Product Service is currently unavailable."
            );
            response.setRetryStatus("All retries exhausted");
            response.setRateLimiterStatus("PERMITTED");

            response.setBulkheadStatus("PERMITTED");

        }

        return response;
    }
}

//Time limiter , Completable future