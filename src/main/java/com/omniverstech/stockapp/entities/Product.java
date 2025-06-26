package com.omniverstech.stockapp.entities;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.omniverstech.stockapp.entities.projection.ProductRecord;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(name = "products")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @JsonProperty("product_code")
    @Column(length=20, nullable = false, unique = true)
    private String productCode;

    @NotBlank
    @JsonProperty("product_name")
    @Column(length = 100, nullable = false)
    private String productName;

    @DecimalMin("0.00")
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="category_id", nullable = false)
    @JsonBackReference
    private Category category;

    public Product(String productCode, String productName, BigDecimal price) {
        this.productCode = productCode;
        this.productName = productName;
        this.price = price;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Product other = (Product) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Product [id=" + id + ", productCode=" + productCode + ", productName=" + productName + ", price=" + price
                + ", categoryId=" + (category != null ? category.getId() : "null") +
               ", categoryName='" + (category != null ? category.getCategoryName() : "null") + '\'' +
               ']';
    }

    public ProductRecord toRecord() {
        return new ProductRecord(this);
    }

}
