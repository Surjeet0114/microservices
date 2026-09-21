package com.surjeet.orderservice.dto;

public class OrderCreatedEvent {

    private Long eventId;
    private Long orderId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double totalAmount;

    public OrderCreatedEvent() {
    }

    public OrderCreatedEvent(
            Long eventId,
            Long orderId,
            Long productId,
            String productName,
            Integer quantity,
            Double totalAmount
    ) {
        this.eventId = eventId;
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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
}