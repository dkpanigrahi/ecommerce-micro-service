package com.example.repository;

import com.example.entity.WholeSalePrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WholeSalePriceJpaRepository extends JpaRepository<WholeSalePrice, Long> {
}