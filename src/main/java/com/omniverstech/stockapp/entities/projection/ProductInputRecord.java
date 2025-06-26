package com.omniverstech.stockapp.entities.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductInputRecord(
       @NotBlank(message = "Product code cannot be empty")
       @JsonProperty("product_code")
       String productCode,

       @NotBlank(message = "Product name cannot be empty")
       @JsonProperty("product_name")
       String productName,

       @NotNull(message = "Product price cannot be null")
       @DecimalMin(value = "0.01", message = "Product price must be greater than 0")
       @JsonProperty("price")
       BigDecimal price,

       @NotNull(message = "Category ID cannot be null")
       @JsonProperty("category_id")
       Long categoryId
) {
}
