package com.omniverstech.stockapp.entities.projection;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CategoryRecord(
        Long id,
        @JsonProperty("category_code") String categoryCode,
        @JsonProperty("category_name") String categoryName) {

}
