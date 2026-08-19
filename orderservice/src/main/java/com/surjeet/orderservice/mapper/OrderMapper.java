package com.surjeet.orderservice.mapper;

import org.springframework.stereotype.Component;

import com.surjeet.orderservice.dto.OrderRequestDto;
import com.surjeet.orderservice.dto.OrderResponseDto;
import com.surjeet.orderservice.entity.Order;

@Component
public class OrderMapper {

    // Convert OrderRequestDto -> Order Entity
    public Order toEntity(OrderRequestDto orderRequestDto) {

        Order order = new Order();

        order.setProductId(orderRequestDto.getProductId());
        order.setQuantity(orderRequestDto.getQuantity());

        return order;
    }

    // Convert Order Entity -> OrderResponseDto
    public OrderResponseDto toResponse(Order order) {

        OrderResponseDto responseDto = new OrderResponseDto();

        responseDto.setId(order.getId());
        responseDto.setProductName(order.getProductName());
        responseDto.setProductPrice(order.getProductPrice());
        responseDto.setQuantity(order.getQuantity());
        responseDto.setTotalAmount(order.getTotalAmount());
        responseDto.setOrderDate(order.getOrderDate());

        return responseDto;
    }

    // Update existing Order Entity from Request DTO
    public void updateEntity(Order order, OrderRequestDto orderRequestDto) {

        order.setProductId(orderRequestDto.getProductId());
        order.setQuantity(orderRequestDto.getQuantity());
    }
}