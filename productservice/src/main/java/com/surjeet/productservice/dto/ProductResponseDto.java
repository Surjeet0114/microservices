package com.surjeet.productservice.dto;

import com.surjeet.productservice.enums.Category;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

//Swagger
@Schema(description = "Product Response")

public class ProductResponseDto {

    @Schema(example = "1")
    private Integer id;

    @Schema(example = "Laptop")
    private String name;

    private String description;
    private Category category;

    @Schema(example = "55000")
    private Double price;

    @Schema(example = "10")
    private Integer stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //for instance port to be shown
    private String instancePort;


    public ProductResponseDto(){
    }

    public ProductResponseDto(Integer id,
                              String name,
                              String description,
                              Category category,
                              Double price,
                              Integer stock,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt,
                              String instancePort) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

        this.instancePort = instancePort;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getInstancePort() {
        return instancePort;
    }
    public void setInstancePort(String instancePort) {
        this.instancePort = instancePort;
    }
}
