package com.omniverstech.stockapp.entities.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.omniverstech.stockapp.entities.Product;

import java.math.BigDecimal;

public record ProductRecord(
        Long id,
        @JsonProperty("product_code") String productCode,
        @JsonProperty("product_name") String productName,
        @JsonProperty("price") BigDecimal price,
        @JsonProperty("category") CategoryRecord category) {

    public ProductRecord(Product product) {
        this(
                product.getId(),
                product.getProductCode(),
                product.getProductName(),
                product.getPrice(),
                new CategoryRecord(product.getCategory())
        );
    }

    public Product toProduct() {
        return new Product(id, productCode, productName, price, category.toCategory());
    }
}
