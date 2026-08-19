package com.surjeet.orderservice.client;

import com.surjeet.orderservice.dto.ProductResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "productservice",
        url = "http://localhost:8082"
)
public interface ProductClient {

    @GetMapping("/products/fetchProduct/{id}")
    ProductResponseDto getProduct(
            @PathVariable Integer id
    );

}