package com.surjeet.orderservice.dto;

public class ProductResponseDto {

    private Integer id;

    private String name;

    private Double price;

    private Integer stock;

    private String instancePort;

    public ProductResponseDto(){}

    public ProductResponseDto(Integer id,
                              String name,
                              Double price,
                              Integer stock,
                              String instancePort) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
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

    public String getInstancePort() {
        return instancePort;
    }
    public void setInstancePort(String instancePort) {
        this.instancePort = instancePort;
    }
}
