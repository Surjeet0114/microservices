package com.surjeet.orderservice.dto;

import java.time.LocalDateTime;

public class OrderResponseDto {

    private Integer id;
    private String productName;
    private Double productPrice;
    private Integer quantity;
    private Double totalAmount;
    private LocalDateTime orderDate;

    //Load balancer
    private String productInstancePort;

    //For covering the Fault tolerance
    private String circuitBreakerState;
    private String retryStatus;
    private String message;

    private String status;

    //For Rate Limiter
    private String rateLimiterStatus;

    private String bulkheadStatus;

    public OrderResponseDto(){}

    public OrderResponseDto(Integer id,
                            String productName,
                            Double productPrice,
                            Integer quantity,
                            Double totalAmount,
                            LocalDateTime orderDate,
                            String productInstancePort,

                            String circuitBreakerState,
                            String retryStatus,
                            String message,

                            String status,

                            String rateLimiterStatus,

                            String bulkheadStatus

    ) {
        this.id = id;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.productInstancePort = productInstancePort;

        this.circuitBreakerState = circuitBreakerState;
        this.retryStatus = retryStatus;
        this.message = message;

        this.status = status;

        this.rateLimiterStatus = rateLimiterStatus;

        this.bulkheadStatus = bulkheadStatus;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getProductPrice() {
        return productPrice;
    }
    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getProductInstancePort() {
        return productInstancePort;
    }
    public void setProductInstancePort(String productInstancePort) {
        this.productInstancePort = productInstancePort;
    }


    public String getCircuitBreakerState() {
        return circuitBreakerState;
    }
    public void setCircuitBreakerState(String circuitBreakerState) {
        this.circuitBreakerState = circuitBreakerState;
    }

    public String getRetryStatus() {
        return retryStatus;
    }
    public void setRetryStatus(String retryStatus) {
        this.retryStatus = retryStatus;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    //Rate Limiter Status
    public String getRateLimiterStatus() {
        return rateLimiterStatus;
    }
    public void setRateLimiterStatus(String rateLimiterStatus) {
        this.rateLimiterStatus = rateLimiterStatus;
    }

    public String getBulkheadStatus() {
        return bulkheadStatus;
    }
    public void setBulkheadStatus(String bulkheadStatus) {
        this.bulkheadStatus = bulkheadStatus;
    }
}
