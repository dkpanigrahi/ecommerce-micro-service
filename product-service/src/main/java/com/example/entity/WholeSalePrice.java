package com.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "whole_sale_price")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WholeSalePrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer minWholesaleQuantity;

    @Column(precision = 10, scale = 2)
    private BigDecimal wholesalePrice;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
