package com.example.repository;

import com.example.entity.Category;
import com.example.entity.Product;
import com.example.entity.ProductTagMapping;
import com.example.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductTagMappingRepository extends JpaRepository<ProductTagMapping, Long> {
    boolean existsByProductAndTag(Product product, Tag tag);
}