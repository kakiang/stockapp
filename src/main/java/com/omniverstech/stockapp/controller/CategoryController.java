package com.omniverstech.stockapp.controller;

import java.util.List;

import com.omniverstech.stockapp.entities.Category;
import com.omniverstech.stockapp.entities.projection.CategoryRecord;
import com.omniverstech.stockapp.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryRecord>> getAllCategories() {
        var all = categoryService.getAllCategoryRecords();
        return ResponseEntity.ok(all);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Category>> getAllCategoriesAndProducts() {
        var all = categoryService.getAllCategoriesAndProducts();
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryRecord> getCategoryRecordById(@PathVariable Long id) {
        return categoryService.getCategoryRecordById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CategoryRecord> createCategory(@RequestBody @Valid Category category) {
        var newCategory = categoryService.createCategory(category);
        return new ResponseEntity<>(newCategory.toRecord(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryRecord> updateCategory(@PathVariable Long id, @Valid @RequestBody Category categoryDetails) {
        return categoryService.updateCategory(id, categoryDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}

