package com.ecommerce.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecommerce.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

@Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND " +
       "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
       "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
Page<Product> searchProductsByCategoryAndTerm(@Param("categoryId") Long categoryId,
                                              @Param("searchTerm") String searchTerm,
                                              Pageable pageable);


    Page<Product> findByCategoryIdAndNameContaining(Long categoryId, String name, Pageable pageable);

    @Query(value = "SELECT * FROM products WHERE category_id = ?1 AND name LIKE %?2% LIMIT 10", nativeQuery = true)
    List<Product> findDistinctByCategoryIdAndExactWordMatch(Long categoryId, String name);
}








