package com.surjeet.productservice.service;

import com.surjeet.productservice.dto.ProductRequestDto;
import com.surjeet.productservice.dto.ProductResponseDto;
import com.surjeet.productservice.entity.Product;
import com.surjeet.productservice.exception.ProductNotFoundException;
import com.surjeet.productservice.mapper.ProductMapper;
import com.surjeet.productservice.repository.ProductRepository;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final Environment environment;

    public ProductServiceImpl(ProductRepository productRepository,
                              Environment environment) {
        this.productRepository = productRepository;
        this.environment = environment;
    }

    @Override
    public ProductResponseDto createProduct(ProductRequestDto request) {

        Product product = ProductMapper.toEntity(request);

        Product savedProduct = productRepository.save(product);

        return ProductMapper.toResponse(savedProduct);
    }

    @Override
    public ProductResponseDto getProductById(Integer id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found with id : " + id));

        //For Bulkhead
        /*
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        //Now Product Service waits 5 seconds.
        */

        ProductResponseDto response = ProductMapper.toResponse(product);

        response.setInstancePort(
                environment.getProperty("local.server.port")
        );

        return response;
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    @Override
    public ProductResponseDto updateProduct(Integer id,
                                            ProductRequestDto request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found with id : " + id));

        ProductMapper.updateEntity(request, product);

        Product updatedProduct = productRepository.save(product);

        return ProductMapper.toResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Integer id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found with id : " + id));

        productRepository.delete(product);
    }
}