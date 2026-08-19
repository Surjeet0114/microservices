package com.surjeet.productservice.dto;

import com.surjeet.productservice.enums.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

//Swagger
@Schema(description = "Request object for creating or updating a product")

public class ProductRequestDto {

    @Schema(example = "Laptop")
    @NotBlank(message = "Product name is required")
    private String name;

    @Schema(example = "High Performance Gaming laptop")
    @NotBlank(message = "Description is required")
    @Size(max = 500)
    private String description;

    @Schema(example = "ELECTRONICS")
    @NotNull(message = "Category is required")
    private Category category;

    @Schema(example = "55000")
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private Double price;

    @Schema(example = "10")
    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    public ProductRequestDto(){

    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }
    public void setStock(Integer stock) {
        this.stock = stock;
    }
}