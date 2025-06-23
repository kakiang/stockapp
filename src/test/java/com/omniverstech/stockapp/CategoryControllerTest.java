package com.omniverstech.stockapp;

import com.omniverstech.stockapp.entities.Category;
import com.omniverstech.stockapp.entities.Product;
import com.omniverstech.stockapp.entities.projection.CategoryRecord;
import com.omniverstech.stockapp.repo.CategoryRepository;
import com.omniverstech.stockapp.service.CategoryService;
import com.omniverstech.stockapp.service.ProductService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class CategoryControllerTest {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(CategoryControllerTest.class);
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

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

    private Long testCategoryId = new Random().nextLong(18,25);
    private Long secondCategoryId = new Random().nextLong(26, 39);

    @BeforeEach
    void setUp() {
        categoryService.deleteCategories();
        Category testCategory = new Category("TEST", "Test Category");
        testCategoryId = categoryService.createCategory(testCategory).id();
        Category secondCategory = new Category("SECOND", "Second Category");
        secondCategoryId = categoryService.createCategory(secondCategory).id();
    }

    @Test
    void getAllCategories_ShouldReturnAllCategories() throws Exception {
        log.info("=== Test getAllCategories_ShouldReturnAllCategories ===");
        ResponseEntity<List> response = restTemplate.getForEntity("/api/categories", List.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }
    @Test
    void getAllCategories_ShouldReturnAllCategoriesAndProducts() throws Exception {
        ResponseEntity<List> response = restTemplate.getForEntity("/api/categories/all", List.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void createCategory_ShouldReturnCreatedCategory() throws Exception {
        Category newCategory = new Category("NEW", "New Category");
        ResponseEntity<Category> response = restTemplate.postForEntity("/api/categories", newCategory, Category.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getCategoryCode()).isEqualTo("NEW");
    }

    @Test
    void createCategory_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        Category invalidCategory = new Category("", "");
        ResponseEntity<Category> response = restTemplate.postForEntity("/api/categories", invalidCategory, Category.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getCategoryById_WithValidId_ShouldReturnCategory() throws Exception {
        ResponseEntity<Category> response = restTemplate.getForEntity("/api/categories/"+testCategoryId, Category.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCategoryCode()).isEqualTo("TEST");
    }

    @Test
    void getCategoryById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        Long invalidId = 999L;
        ResponseEntity<Category> response = restTemplate.getForEntity("/api/categories/"+invalidId, Category.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    void updateCategory_WithValidIdAndData_ShouldReturnUpdatedCategory() throws Exception {
        Category updatedDetails = new Category("UPDATED", "Updated Category");
        HttpEntity<Category> request = new HttpEntity<>(updatedDetails);

        ResponseEntity<Category> response = restTemplate.exchange(
                "/api/categories/"+testCategoryId,
                HttpMethod.PUT,
                request,
                Category.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCategoryCode()).isEqualTo("UPDATED");
    }

    @Test
    void updateCategory_WithInvalidId_ShouldReturnBadRequest() throws Exception {
        Long invalidId = 999L;
        Category updatedDetails = new Category("UPDATED", "Updated Category");
        HttpEntity<Category> request = new HttpEntity<>(updatedDetails);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/categories/"+invalidId,
                HttpMethod.PUT,
                request,
                String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateCategory_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        Category invalidCategory = new Category("", "");
        HttpEntity<Category> request = new HttpEntity<>(invalidCategory);
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/categories/"+testCategoryId,
                HttpMethod.PUT,
                request,
                String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void deleteCategory_WithValidId_ShouldReturnNoContent() throws Exception {
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/categories/"+testCategoryId,
                HttpMethod.DELETE,
                null,
                Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(categoryRepository.existsById(testCategoryId)).isFalse();
    }

    @Test
    void deleteCategory_WithProducts_ShouldReturnBadRequest() throws Exception {
        Category newCategory = new Category("CLOTH", "Clothing");
        Product shirt = new Product("SHI001", "T-Shirt", BigDecimal.valueOf(29.99));
        shirt.setCategory(newCategory);

        Set<Product> products = new HashSet<>();
        products.add(shirt);
        newCategory.setProducts(products);
        Long id = categoryRepository.save(newCategory).getId();

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/categories/"+id,
                HttpMethod.DELETE,
                null,
                Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

}
