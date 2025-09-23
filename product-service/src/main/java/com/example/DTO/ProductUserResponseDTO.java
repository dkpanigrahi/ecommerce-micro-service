package com.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUserResponseDTO {
    private Long id;
    private String productCode;
    private String name;
    private BigDecimal price;
    private String category;
    private String brand;
    private String imageUrl;
    private Boolean wholesaleAvailable;
    private Integer minWholesaleQuantity;
    private BigDecimal wholesalePrice;
}