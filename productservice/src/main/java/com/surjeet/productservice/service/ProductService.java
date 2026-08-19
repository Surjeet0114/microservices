package com.surjeet.productservice.service;

import com.surjeet.productservice.dto.ProductRequestDto;
import com.surjeet.productservice.dto.ProductResponseDto;

import java.util.List;

public interface ProductService {

    ProductResponseDto createProduct(ProductRequestDto request);

    ProductResponseDto getProductById(Integer id);

    List<ProductResponseDto> getAllProducts();

    ProductResponseDto updateProduct(Integer id, ProductRequestDto request);

    void deleteProduct(Integer id);

    //ProductResponseDto fetchProduct(Long id);
}