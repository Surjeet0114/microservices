package com.surjeet.productservice.controller;

import com.surjeet.productservice.dto.ProductRequestDto;
import com.surjeet.productservice.dto.ProductResponseDto;
import com.surjeet.productservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")

//Swagger
@Tag(name = "Product APIs", description = "Operations related to Products")

public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /*
    @GetMapping("/fetchProduct/{id}")
    public ProductResponseDto getProductById(@PathVariable Integer id) {

        return productService.getProductById(id);
    }*/
    //Swagger
    @Operation(
            summary = "Get Product by ID",
            description = "Fetches a product using its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/fetchProduct/{id}")
    public ProductResponseDto getProductById(@PathVariable Integer id) {
        return productService.getProductById(id);
    }

    @PostMapping("/addProduct")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponseDto createProduct(
            @Valid @RequestBody ProductRequestDto request) {
        return productService.createProduct(request);
    }



    @GetMapping("/fetchProducts")
    public List<ProductResponseDto> getAllProducts() {
        return productService.getAllProducts();
    }

    @PutMapping("/updateProduct/{id}")
    public ProductResponseDto updateProduct(@PathVariable Integer id,
                                            @Valid @RequestBody ProductRequestDto request) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/deleteProduct/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
    }


}