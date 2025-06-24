package com.omniverstech.stockapp.controller;

import java.util.List;

import com.omniverstech.stockapp.entities.Product;
import com.omniverstech.stockapp.entities.projection.ProductRecord;
import com.omniverstech.stockapp.exceptions.DuplicateResourceException;
import com.omniverstech.stockapp.exceptions.ResourceNotFoundException;
import com.omniverstech.stockapp.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProductsWithCategories() {
        var all = productService.getAllProducts();
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductRecord> getProductRecordById(@PathVariable Long id) {
        return new ResponseEntity<>(productService.getProductRecordById(id), HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductRecord createProduct(
            @RequestBody @Valid Product product,
            @RequestParam(name = "category_id") Long categoryId) {
        return productService.createProduct(product, categoryId);
    }
}

