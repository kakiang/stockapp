package com.omniverstech.stockapp.service;

import com.omniverstech.stockapp.entities.Category;
import com.omniverstech.stockapp.entities.Product;
import com.omniverstech.stockapp.entities.projection.ProductInputRecord;
import com.omniverstech.stockapp.exceptions.DuplicateResourceException;
import com.omniverstech.stockapp.exceptions.ResourceNotFoundException;
import com.omniverstech.stockapp.repo.CategoryRepository;
import com.omniverstech.stockapp.repo.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAllWithCategories();
    }

    public List<Product> getProductsByCategoryId(Long categoryId) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        return productRepository.findAllByCategory_Id(categoryId);
    }

    public Product getProductRecordById(Long id) {
        return productRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    @Transactional
    public Product createProduct(Product product, Long categoryId) {
        if (productRepository.existsByProductCode(product.getProductCode())) {
            throw new DuplicateResourceException("Product", "product_code", product.getProductCode());
        }
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        product.setCategory(category);
        return productRepository.save(product);
    }

    @Transactional
    public List<Product> createProducts(List<Product> products) {
        return productRepository.saveAll(products);
    }

    @Transactional
    public Product updateProduct(Long id, ProductInputRecord productDetails) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("product", "id", id));

        Category newCategory = categoryRepository.findById(productDetails.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productDetails.categoryId()));

        existingProduct.setProductCode(productDetails.productCode());
        existingProduct.setProductName(productDetails.productName());
        existingProduct.setPrice(productDetails.price());
        existingProduct.setCategory(newCategory);

        return productRepository.save(existingProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", "id", id);
        }
        productRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllProducts() {
        productRepository.deleteAll();
    }


}
