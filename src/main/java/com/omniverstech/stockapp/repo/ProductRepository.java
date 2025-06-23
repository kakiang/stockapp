package com.omniverstech.stockapp.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.omniverstech.stockapp.entities.Product;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByProductCode(String code);

    Optional<Product> findByProductName(String productName);

    List<Product> findByCategory_Id(Long categoryId);

    List<Product> findByCategory_CategoryName(String categoryName);


}
