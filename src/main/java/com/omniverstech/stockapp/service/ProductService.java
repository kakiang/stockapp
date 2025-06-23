package com.omniverstech.stockapp.service;

import com.omniverstech.stockapp.entities.Product;
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

    public Product getProductsByProductCode(String code) {
        return productRepository.findByProductCode(code)
                .orElseThrow(() -> new RuntimeException("No product found with  code=" + code));
    }

    public Product getProductsByProductName(String name) {
        return productRepository.findByProductName(name)
                .orElseThrow(() -> new RuntimeException("No product found with  nom=" + name));
    }

    @Transactional
    public Optional<Product> createProduct(Product product, Long categoryId) {
        return categoryRepository.findById(categoryId).map(category -> {
            product.setCategory(category);
            return productRepository.save(product);
        });
    }

    @Transactional
    public List<Product> createProducts(List<Product> products) {
        return productRepository.saveAll(products);
    }

    @Transactional
    public Product updateProduct(Long id, Product productDetails, Long categoryId) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No product found with  id=" + id));
        existingProduct.setProductCode(productDetails.getProductCode());
        existingProduct.setProductName(productDetails.getProductName());
        if(categoryId!=null){
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
    public void deleteAllProducts(){
        productRepository.deleteAll();;
    }


}
