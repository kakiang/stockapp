package com.omniverstech.stockapp;

import com.omniverstech.stockapp.entities.Category;
import com.omniverstech.stockapp.entities.Product;
import com.omniverstech.stockapp.entities.projection.ProductInputRecord;
import com.omniverstech.stockapp.entities.projection.ProductRecord;
import com.omniverstech.stockapp.repo.ProductRepository;
import com.omniverstech.stockapp.service.CategoryService;
import com.omniverstech.stockapp.service.ProductService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class ProductControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductRepository productRepository;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    );

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    List<Long> productIds = new ArrayList<>();
    List<Long> categoryIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        productService.deleteAllProducts();
        categoryService.deleteCategories();

        Category electronics = categoryService.createCategory(new Category("Electronics", "Gadgets and devices"));
        categoryIds.add(electronics.getId());
        Category books = categoryService.createCategory(new Category("Books", "All genres"));
        categoryIds.add(books.getId());

        Product laptop = new Product("LAP123", "Laptop", BigDecimal.valueOf(999.99));
        Product phone = new Product("PHO456", "Smartphone", BigDecimal.valueOf(699.99));
        Product novel = new Product("BOK789", "Novel", BigDecimal.valueOf(19.99));

        laptop.setCategory(electronics);
        phone.setCategory(electronics);
        novel.setCategory(books);

        productService.createProducts(List.of(laptop, phone, novel));

        var all = productService.getAllProducts();
        for (Product product : all) {
            System.out.println("===> " + product);
        }


    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        ResponseEntity<List> response = restTemplate.getForEntity("/api/products", List.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(3);
    }

    @Test
    void getProductsByCategoryId_ShouldReturnProductsByCategoryId() {
        var id = categoryService.getAllCategoryRecords().getFirst().id();
        ResponseEntity<List> response = restTemplate.getForEntity("/api/products?category_id=" + id, List.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void getProductsByCategoryId_WithInvalidCategoryId_ShouldReturnNotFound() {
        ResponseEntity<Object> response = restTemplate.getForEntity("/api/products?category_id=999", Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getProductById_ShouldReturnProductById() {
        var id = productService.getAllProducts().getFirst().getId();
        ResponseEntity<ProductRecord> response = restTemplate.getForEntity("/api/products/" + id, ProductRecord.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getProductById_WithInvalidId_ShouldReturnNotFound() {
        ResponseEntity<Object> response = restTemplate.getForEntity("/api/products/999", Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() {
        var id = categoryService.getAllCategoryRecords().getFirst().id();
        Product tablet = new Product("TBO01", "Tablet", BigDecimal.valueOf(899.99));
        ResponseEntity<Product> response = restTemplate.postForEntity("/api/products?category_id=" + id, tablet, Product.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getProductCode()).isEqualTo("TBO01");
    }

    @Test
    void createProduct_WithExistingCode_ShouldReturn409Conflict() {
        var code = productService.getAllProducts().getFirst().getProductCode();
        var id = categoryService.getAllCategoryRecords().getFirst().id();
        Product tablet = new Product(code, "Tablet", BigDecimal.valueOf(899.99));
        ResponseEntity<Object> response = restTemplate.postForEntity("/api/products?category_id=" + id, tablet, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void createProduct_WithInvalidCategoryId_ShouldReturnNotFound() {
        Product tablet = new Product("TBO01", "Tablet", BigDecimal.valueOf(899.99));
        ResponseEntity<Object> response = restTemplate.postForEntity("/api/products?category_id=999", tablet, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateProduct_WithValidIdAndData_ShouldReturnUpdatedProduct() {
        var existingproduct = productService.getAllProducts().getFirst();
        var categories = categoryService.getAllCategoryRecords();
        Long categoryId = null;

        for (var category : categories) {
            if (category.id() != existingproduct.getCategory().getId()) {
                categoryId = category.id();
            }
        }
        ProductInputRecord updatedDetails = new ProductInputRecord(
                "updated_code",
                "updated_name",
                BigDecimal.valueOf(999.99),
                categoryId);
        HttpEntity<ProductInputRecord> request = new HttpEntity<>(updatedDetails);

        ResponseEntity<ProductRecord> response = restTemplate.exchange(
                "/api/products/" + existingproduct.getId(),
                HttpMethod.PUT,
                request,
                ProductRecord.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().productCode()).isEqualTo("updated_code");
    }

    @Test
    void updateProduct_WithInvalidData_ShouldReturnBadRequest() {
        var existingproduct = productService.getAllProducts().getFirst();
        var categories = categoryService.getAllCategoryRecords();
        Long categoryId = null;

        for (var category : categories) {
            if (category.id() != existingproduct.getCategory().getId()) {
                categoryId = category.id();
            }
        }
        ProductInputRecord updatedDetails = new ProductInputRecord(
                "",
                "",
                BigDecimal.valueOf(999.99),
                categoryId);
        HttpEntity<ProductInputRecord> request = new HttpEntity<>(updatedDetails);

        ResponseEntity<ProductRecord> response = restTemplate.exchange(
                "/api/products/" + existingproduct.getId(),
                HttpMethod.PUT,
                request,
                ProductRecord.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateProduct_WithInvalidCategoryId_ShouldReturnNotFound() {
        var existingproduct = productService.getAllProducts().getFirst();
        ProductInputRecord updatedDetails = new ProductInputRecord(
                "updated_code",
                "updated_name",
                BigDecimal.valueOf(999.99),
                Long.valueOf(999));
        HttpEntity<ProductInputRecord> request = new HttpEntity<>(updatedDetails);
        ResponseEntity<ProductRecord> response = restTemplate.exchange(
                "/api/products/" + existingproduct.getId(),
                HttpMethod.PUT,
                request,
                ProductRecord.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteProduct_WithValidId_ShouldReturnNoContent() {
        var id = productService.getAllProducts().getFirst().getId();
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/products/" + id,
                HttpMethod.DELETE,
                null,
                Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(productRepository.existsById(id)).isFalse();
    }

    @Test
    void deleteProduct_WithInValidId_ShouldReturnNotFound() {
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/products/999",
                HttpMethod.DELETE,
                null,
                Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
