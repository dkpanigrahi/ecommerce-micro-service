package com.example.DTO;

import jakarta.persistence.Column;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequestDTO {

    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String category;
    private String brand;
    private String imageUrl;
    private Boolean wholesaleAvailable;
    private Integer minWholesaleQuantity;
    private BigDecimal wholesalePrice;
    private Long categoryId;
}
