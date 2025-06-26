package com.omniverstech.stockapp.controller;

import java.util.List;

import com.omniverstech.stockapp.entities.Product;
import com.omniverstech.stockapp.entities.projection.ProductInputRecord;
import com.omniverstech.stockapp.entities.projection.ProductRecord;
import com.omniverstech.stockapp.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductRecord>> getAllProducts(
            @RequestParam(name = "category_id", required = false) Long categoryId) {
        var all = (categoryId != null ?
                productService.getProductsByCategoryId(categoryId) :
                productService.getAllProducts()).stream()
                .map(ProductRecord::new)
                .toList();
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductRecord> getProductById(@PathVariable Long id) {
        return new ResponseEntity<>(
                productService.getProductRecordById(id).toRecord(),
                HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProductRecord> createProduct(
            @RequestBody @Valid Product product,
            @RequestParam(name = "category_id") Long categoryId) {
        return new ResponseEntity<>(
                productService.createProduct(product, categoryId).toRecord(),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductRecord> updateProduct(@PathVariable Long id, @RequestBody @Valid ProductInputRecord productDetails) {
        var updatedProduct = productService.updateProduct(id, productDetails);
        return new ResponseEntity<>(updatedProduct.toRecord(),
                HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}

