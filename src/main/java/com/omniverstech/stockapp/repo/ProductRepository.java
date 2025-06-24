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

    List<Product> findByCategory_Id(Long categoryId);

    List<Product> findByCategory_CategoryName(String categoryName);

    boolean existsByProductCode(String productCode);

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.id = :id")
    Optional<Product> findByIdWithCategory(@Param("id") Long id);

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.id = :id")
    Optional<ProductRecord> findProductRecordById(@Param("id") Long id);

//    Optional<ProductRecord> findProductRecordByProductCode(String productCode);
//    List<ProductRecord> findProductRecordsByProductName(String productName);


}
