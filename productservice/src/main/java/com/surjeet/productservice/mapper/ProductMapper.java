package com.surjeet.productservice.mapper;

import com.surjeet.productservice.dto.ProductRequestDto;
import com.surjeet.productservice.dto.ProductResponseDto;
import com.surjeet.productservice.entity.Product;

public class ProductMapper {

    // Convert Request DTO -> Entity
    public static Product toEntity(ProductRequestDto dto) {

        Product product = new Product();

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setCategory(dto.getCategory());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());

        return product;
    }

    // Convert Entity -> Response DTO
    public static ProductResponseDto toResponse(Product product) {

        ProductResponseDto response = new ProductResponseDto();

        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setCategory(product.getCategory());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());

        return response;
    }

    // Update existing Entity using Request DTO
    public static void updateEntity(ProductRequestDto dto, Product product) {

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setCategory(dto.getCategory());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
    }
}
