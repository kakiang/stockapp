package com.omniverstech.stockapp.repo;

import com.omniverstech.stockapp.entities.projection.ProductRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import com.omniverstech.stockapp.entities.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByProductCode(String code);

    Optional<Product> findByProductName(String productName);

    boolean existsByProductCode(String productCode);

    @Query("SELECT DISTINCT p FROM Product p JOIN FETCH p.category WHERE p.id = :id")
    Optional<Product> findByIdWithCategory(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Product p JOIN FETCH p.category")
    List<Product> findAllWithCategories();

    @Query("SELECT DISTINCT p FROM Product p JOIN FETCH p.category WHERE p.category.id = :id")
    List<Product> findAllByCategory_Id(@Param("id")  Long categoryId);

    @Query("SELECT p FROM Product p JOIN FETCH p.category ORDER BY p.price DESC LIMIT :limit")
    List<Product> findTopByPrice(@Param("limit") Integer limit);

}
