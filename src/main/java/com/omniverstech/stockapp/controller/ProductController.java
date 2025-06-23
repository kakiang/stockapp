package com.omniverstech.stockapp.controller;

import java.util.List;

import com.omniverstech.stockapp.entities.Product;
import com.omniverstech.stockapp.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/")
    public ResponseEntity<List<Product>> getAllProductsWithCategories(){
        var all = productService.getAllProducts();
        return ResponseEntity.ok(all);
    }

}

