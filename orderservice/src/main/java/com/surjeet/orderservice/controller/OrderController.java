package com.surjeet.orderservice.controller;

import com.surjeet.orderservice.dto.OrderRequestDto;
import com.surjeet.orderservice.dto.OrderResponseDto;
import com.surjeet.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/addOrder")
    public OrderResponseDto createOrder(
            @Valid @RequestBody OrderRequestDto orderRequestDto) {

        return orderService.createOrder(orderRequestDto);
    }

    @GetMapping("/fetchOrder/{id}")
    public OrderResponseDto getOrderById(
            @PathVariable Integer id) {

        return orderService.getOrderById(id);
    }

    @GetMapping("/fetchOrders")
    public List<OrderResponseDto> getAllOrders() {

        return orderService.getAllOrders();
    }

    @PutMapping("/updateOrder/{id}")
    public OrderResponseDto updateOrder(
            @PathVariable Integer id,
            @Valid @RequestBody OrderRequestDto orderRequestDto) {

        return orderService.updateOrder(id, orderRequestDto);
    }

    @DeleteMapping("/deleteOrder/{id}")
    public void deleteOrder(
            @PathVariable Integer id) {

        orderService.deleteOrder(id);
    }
}