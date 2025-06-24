package com.omniverstech.stockapp.entities.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.omniverstech.stockapp.entities.Product;

public record ProductRecord(
        Long id,
        @JsonProperty("product_code") String productCode,
        @JsonProperty("product_name") String productName,
        @JsonProperty("category") CategoryRecord category) {

    public ProductRecord(Product product) {
        this(
                product.getId(),
                product.getProductCode(),
                product.getProductName(),
                new CategoryRecord(product.getCategory())
        );
    }
}
