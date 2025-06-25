package com.omniverstech.stockapp.entities.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.omniverstech.stockapp.entities.Category;

public record CategoryRecord(
        Long id,
        @JsonProperty("category_code") String categoryCode,
        @JsonProperty("category_name") String categoryName) {

    public CategoryRecord(Category category) {
        this(category.getId(), category.getCategoryCode(), category.getCategoryName());
    }

    public Category toCategory() {
        return new Category(categoryCode,categoryName);
    }
}
