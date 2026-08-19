package com.surjeet.orderservice.service;

import com.surjeet.orderservice.dto.OrderRequestDto;
import com.surjeet.orderservice.dto.OrderResponseDto;

import java.util.List;

public interface OrderService {

    OrderResponseDto createOrder(OrderRequestDto orderRequestDto);

    OrderResponseDto getOrderById(Integer id);

    List<OrderResponseDto> getAllOrders();

    OrderResponseDto updateOrder(Integer id, OrderRequestDto orderRequestDto);

    void deleteOrder(Integer id);
}