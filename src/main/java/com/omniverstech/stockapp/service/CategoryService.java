package com.omniverstech.stockapp.service;

import com.omniverstech.stockapp.entities.Category;
import com.omniverstech.stockapp.entities.projection.CategoryRecord;
import com.omniverstech.stockapp.exceptions.ResourceNotFoundException;
import com.omniverstech.stockapp.repo.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    public List<CategoryRecord> getAllCategoryRecords() {
        return categoryRepository.findAllCategoryRecords();
    }

    public List<Category> getAllCategoriesAndProducts() {
        return categoryRepository.findAllCategoriesAndProducts();
    }

    public Optional<CategoryRecord> getCategoryRecordById(Long id) {
        return categoryRepository.findCategoryRecordById(id);
    }

    @Transactional
    public Category createCategory(Category Category) {
        return categoryRepository.save(Category);
    }

    @Transactional
    public List<Category> createCategories(List<Category> categories) {
        return categoryRepository.saveAll(categories);
    }

    @Transactional
    public Optional<CategoryRecord> updateCategory(Long id, Category categoryDetails) {
        return categoryRepository.findById(id)
                .map(category -> {
                    category.setCategoryCode(categoryDetails.getCategoryCode());
                    category.setCategoryName(categoryDetails.getCategoryName());
                    return toRecord(categoryRepository.save(category));
                });
    }

    @Transactional
    public boolean deleteCategory(Long id) {
         return categoryRepository.findById(id).map(Category -> {
            if (!Category.getProducts().isEmpty()) {
                 throw new DataIntegrityViolationException("Cannot delete category with associated products.");
            }
            categoryRepository.deleteById(id);
            return true;
        }).orElseThrow(()->new ResourceNotFoundException("Category", "id", id));
    }

    @Transactional
    public void deleteCategories(){
        categoryRepository.deleteAll();
    }

    private CategoryRecord toRecord(Category category){
        return new CategoryRecord(category.getId(), category.getCategoryCode(), category.getCategoryName());
    }
}
