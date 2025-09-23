package com.example.repository;

import com.example.DTO.ProductAdminResponseDTO;
import com.example.DTO.ProductUserResponseDTO;
import com.example.entity.Product;
import com.example.entity.ProductTagMapping;
import com.example.entity.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ProductCriteriaRepository {

    @Autowired
    private EntityManager entityManager;

    public Map<String, Object> getProductsForUser(String category, Boolean wholesaleOnly, String search, List<String> tagList, Integer itemPerPage, Integer pageNumber) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // --- COUNT QUERY ---
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Product> countRoot = countQuery.from(Product.class);
        List<Predicate> countPredicates = buildCommonPredicates(cb, countRoot, category, wholesaleOnly, search, tagList);
        countQuery.select(cb.countDistinct(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        // --- DATA QUERY ---
        CriteriaQuery<Product> dataQuery = cb.createQuery(Product.class);
        Root<Product> root = dataQuery.from(Product.class);
        List<Predicate> dataPredicates = buildCommonPredicates(cb, root, category, wholesaleOnly, search, tagList);
        dataQuery.select(root).where(cb.and(dataPredicates.toArray(new Predicate[0]))).distinct(true);

        TypedQuery<Product> typedQuery = entityManager.createQuery(dataQuery);
        if (itemPerPage != null && pageNumber != null) {
            typedQuery.setMaxResults(itemPerPage);
            typedQuery.setFirstResult((pageNumber - 1) * itemPerPage);
        }

        List<ProductUserResponseDTO> data = typedQuery.getResultList().stream()
                .map(p -> new ProductUserResponseDTO(
                        p.getId(), p.getProductCode(), p.getName(), p.getPrice(),
                        p.getCategory().getName(), p.getBrand(), p.getImageUrl(),
                        p.getWholesaleAvailable(), p.getMinWholesaleQuantity(), p.getWholesalePricePerUnit()))
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("total", totalCount);
        return result;
    }


    public Map<String, Object> getProductsForAdmin(String category, Boolean wholesaleOnly, String search, List<String> tagList, Integer itemPerPage, Integer pageNumber) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // --- COUNT QUERY ---
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Product> countRoot = countQuery.from(Product.class);
        List<Predicate> countPredicates = buildCommonPredicates(cb, countRoot, category, wholesaleOnly, search, tagList);
        countQuery.select(cb.countDistinct(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        // --- DATA QUERY ---
        CriteriaQuery<Product> dataQuery = cb.createQuery(Product.class);
        Root<Product> root = dataQuery.from(Product.class);
        List<Predicate> dataPredicates = buildCommonPredicates(cb, root, category, wholesaleOnly, search, tagList);
        dataQuery.select(root).where(cb.and(dataPredicates.toArray(new Predicate[0]))).distinct(true);

        TypedQuery<Product> typedQuery = entityManager.createQuery(dataQuery);
        if (itemPerPage != null && pageNumber != null) {
            typedQuery.setMaxResults(itemPerPage);
            typedQuery.setFirstResult((pageNumber - 1) * itemPerPage);
        }

        List<Product> products = typedQuery.getResultList();
        List<Long> productIds = products.stream().map(Product::getId).toList();

        List<Object[]> tagResults = entityManager.createQuery("""
            SELECT ptm.product.id, t.name
            FROM ProductTagMapping ptm
            JOIN ptm.tag t
            WHERE ptm.product.id IN :productIds
        """, Object[].class)
                .setParameter("productIds", productIds)
                .getResultList();

        Map<Long, List<String>> productTagsMap = new HashMap<>();
        for (Object[] row : tagResults) {
            Long productId = (Long) row[0];
            String tagName = (String) row[1];
            productTagsMap.computeIfAbsent(productId, k -> new ArrayList<>()).add(tagName);
        }

        List<ProductAdminResponseDTO> data = products.stream()
                .map(p -> new ProductAdminResponseDTO(
                        p.getId(), p.getProductCode(), p.getName(), p.getDescription(),
                        p.getPrice(), p.getStockQuantity(), p.getCategory().getName(), p.getBrand(),
                        p.getImageUrl(), p.getIsAvailable(), p.getWholesaleAvailable(),
                        p.getCreatedAt(), p.getUpdatedAt(), p.getMinWholesaleQuantity(), p.getWholesalePricePerUnit(),
                        productTagsMap.getOrDefault(p.getId(), new ArrayList<>())
                ))
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("total", totalCount);
        return result;
    }



    private List<Predicate> buildCommonPredicates(CriteriaBuilder cb, Root<Product> root, String category, Boolean wholesaleOnly, String search, List<String> tagList) {
        List<Predicate> predicates = new ArrayList<>();

        if (category != null && !category.isEmpty()) {
            predicates.add(cb.equal(root.get("category").get("name"), category));
        }

        if (wholesaleOnly != null && wholesaleOnly) {
            predicates.add(cb.isTrue(root.get("wholesaleAvailable")));
        }

        if (search != null && !search.isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"));
        }

        if (tagList != null && !tagList.isEmpty()) {
            Subquery<Long> subquery = cb.createQuery().subquery(Long.class);
            Root<ProductTagMapping> ptmRoot = subquery.from(ProductTagMapping.class);
            Join<ProductTagMapping, Tag> tagJoin = ptmRoot.join("tag");
            subquery.select(ptmRoot.get("product").get("id"))
                    .where(tagJoin.get("name").in(tagList));
            predicates.add(root.get("id").in(subquery));
        }

        return predicates;
    }



}
