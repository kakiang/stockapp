package com.omniverstech.stockapp.service;

import com.omniverstech.stockapp.entities.Product;
import com.omniverstech.stockapp.entities.projection.ProductRecord;
import com.omniverstech.stockapp.exceptions.DuplicateResourceException;
import com.omniverstech.stockapp.exceptions.ResourceNotFoundException;
import com.omniverstech.stockapp.repo.CategoryRepository;
import com.omniverstech.stockapp.repo.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public ProductRecord getProductRecordById(Long id) {
        return productRepository.findByIdWithCategory(id)
                .map(ProductRecord::new)
                .orElseThrow(()-> new ResourceNotFoundException("Product", "id", id));
    }

    @Transactional
    public ProductRecord createProduct(Product product, Long categoryId) {
        if (productRepository.existsByProductCode(product.getProductCode())) {
            throw new DuplicateResourceException("Product", "productCode", product.getProductCode());
        }
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        product.setCategory(category);
        return productRepository.save(product).toRecord();
    }

    @Transactional
    public List<Product> createProducts(List<Product> products) {
        return productRepository.saveAll(products);
    }

    @Transactional
    public Product updateProduct(Long id, Product productDetails, Long categoryId) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() ->  new ResourceNotFoundException("product", "id", id));
        existingProduct.setProductCode(productDetails.getProductCode());
        existingProduct.setProductName(productDetails.getProductName());
        if (categoryId != null) {
            categoryRepository.findById(categoryId).ifPresent(existingProduct::setCategory);
        }
        existingProduct.setCategory(productDetails.getCategory());
        return productRepository.save(existingProduct);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("No product found with  id=" + id);
        }
        productRepository.deleteById(id);
    }

    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategory_Id(categoryId);
    }

    public List<Product> getProductsByCategoryNom(String categoryNom) {
        return productRepository.findByCategory_CategoryName(categoryNom);
    }

    @Transactional
    public void deleteAllProducts() {
        productRepository.deleteAll();
        ;
    }


}
