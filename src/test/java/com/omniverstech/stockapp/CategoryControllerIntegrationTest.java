package com.omniverstech.stockapp;

import com.omniverstech.stockapp.entities.Category;
import com.omniverstech.stockapp.entities.Product;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class CategoryControllerIntegrationTest {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(CategoryControllerIntegrationTest.class);
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @LocalServerPort
    private Integer port;

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

    @BeforeEach
    void setup() {
        categoryService.deleteCategories();
        productService.deleteAllProducts();

        Category electronics = new Category("Electronics", "Gadgets and devices");
        Category books = new Category("Books", "All genres");

        Product laptop = new Product("LAP123", "Laptop", BigDecimal.valueOf(999.99));
        Product phone = new Product("PHO456", "Smartphone", BigDecimal.valueOf(699.99));
        Product novel = new Product("BOK789", "Novel", BigDecimal.valueOf(19.99));

        laptop.setCategory(electronics);
        phone.setCategory(electronics);
        novel.setCategory(books);

        Set<Product> electronicProducts = new HashSet<>();
        electronicProducts.add(laptop);
        electronicProducts.add(phone);
        electronics.setProducts(electronicProducts);

        Set<Product> bookProducts = new HashSet<>();
        bookProducts.add(novel);
        books.setProducts(bookProducts);

        categoryService.createCategories(List.of(electronics, books));

//        Category electronics = categoryService.createCategory(
//                new Category("ELEC", "Electronics")
//        );
//        Category books = categoryService.createCategory(
//                new Category("BOOK", "Books")
//        );
//
//        log.info("... SETUP ...");
//        log.info("..."+books.getId() +"...");
//        log.info("..."+electronics.getId() +"...");
//
//        Product laptop = new Product("LAP123", "Laptop", BigDecimal.valueOf(999.99));
//        laptop.setCategory(electronics);
//
//        Product phone = new Product("PHO456", "Smartphone", BigDecimal.valueOf(699.99));
//        phone.setCategory(electronics);
//
//        Product novel = new Product("BOK789", "Novel", BigDecimal.valueOf(19.99));
//        novel.setCategory(books);

//        Product polo = new Product("POLO", "Polo Lacoste", BigDecimal.valueOf(199.99));
//        productService.createProduct(polo);

        var tab = productService.createProducts(List.of(laptop, phone, novel));
//        log.info("...products "+ tab.size() +"...");
//        for (Product p : tab){
//            log.info("... SETUP: "+p.toString());
//        }
    }

//    @Test
//    void getAllCategories_ShouldReturnAllCategories() {
//        ResponseEntity<List> response = restTemplate.getForEntity("/api/categories", List.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).hasSize(2);
//    }
//
//     @Test
//     void getCategoryById_WhenExists_ShouldReturnCategory() {
//         Category shirts = new Category("Textile", "Shirts");
//         Long id = categoryService.createCategory(shirts).getId();
//         ResponseEntity<Category> response = restTemplate.getForEntity("/api/categories/"+id.toString(), Category.class);
//         assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//         categoryService.deleteCategory(id);
//     }
//
//    @Test
//    void serviceLayer_createCategory_ShouldPersistInDatabase() {
//        Category newCategory = new Category("Clothing", "Apparel");
//        Category saved = categoryService.createCategory(newCategory);
//
//        assertThat(saved.getId()).isNotNull();
//        assertThat(categoryService.getCategoryById(saved.getId())).isPresent();
//    }
//
//    @Test
//    void serviceLayer_deleteCategory_ShouldNotRemoveFromDatabase() {
//        var id = categoryService.getAllCategories().getFirst().getId();
//        boolean deleted = categoryService.deleteCategory(id);
//        assertThat(deleted).isFalse();
//        assertThat(categoryService.getCategoryById(id)).isNotEmpty();
//    }
//
//    @Test
//    void serviceLayer_deleteCategory_ShouldRemoveFromDatabase() {
//        Category shirts = new Category("Textile", "Shirts");
//        Long id = categoryService.createCategory(shirts).getId();
//
//        boolean deleted = categoryService.deleteCategory(id);
//        assertThat(deleted).isTrue();
//        assertThat(categoryService.getCategoryById(id)).isEmpty();
//    }
//
//     @Test
//     void getCategoryById_ShouldIncludeProducts() {
//         var id = categoryService.getAllCategories().getFirst().getId();
//         ResponseEntity<Category> response = restTemplate.getForEntity("/api/categories/"+id.toString(), Category.class);
//
//         assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//         assertThat(response.getBody().getProducts()).hasSize(2);
//         assertThat(response.getBody().getProducts())
//             .extracting(Product::getProductCode)
//             .containsExactly("LAP123", "PHO456");
//     }
//
//     @Test
//     void createCategory_WithProducts_ShouldPersistBoth() {
//         Category newCategory = new Category("CLOTH", "Clothing");
//         Product shirt = new Product("SHI001", "T-Shirt", BigDecimal.valueOf(29.99));
//         shirt.setCategory(newCategory);
//
//         Set<Product> products = new HashSet<>();
//         products.add(shirt);
//         newCategory.setProducts(products);
//
//         ResponseEntity<Category> response = restTemplate.postForEntity(
//             "/api/categories",
//             newCategory,
//             Category.class
//         );
//
//         assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//         assertThat(response.getBody().getId()).isNotNull();
//
//         List<Product> savedProducts = productService.getProductsByCategoryId(response.getBody().getId());
//         assertThat(savedProducts).hasSize(1);
//         assertThat(savedProducts.get(0).getProductCode()).isEqualTo("SHI001");
//     }


}
