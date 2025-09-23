package com.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAdminResponseDTO {
    private Long id;
    private String productCode;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String category;
    private String brand;
    private String imageUrl;
    private Boolean isAvailable;
    private Boolean wholesaleAvailable;
    private Date createdAt;
    private Date updatedAt;
    private Integer minWholesaleQuantity;
    private BigDecimal wholesalePrice;
    private List<String> tags;

}