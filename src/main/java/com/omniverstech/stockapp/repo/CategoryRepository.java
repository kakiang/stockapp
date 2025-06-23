package com.omniverstech.stockapp.repo;

import java.util.List;
import java.util.Optional;

import com.omniverstech.stockapp.entities.projection.CategoryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.omniverstech.stockapp.entities.Category;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{

    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.products")
    List<Category> findAllCategoriesAndProducts();

    @Query("SELECT c FROM Category c")
    List<CategoryRecord> findAllCategoryRecords();

    Optional<CategoryRecord> findCategoryRecordById(Long id);

//    @EntityGraph(attributePaths = "products")
//    @Query("SELECT c FROM Category c")
//    List<Category> findAllCategoriesWithProducts();

}
