package com.saprone.ingredient.controller;

import com.saprone.ingredient.model.Ingredient;
import com.saprone.ingredient.repository.IngredientRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MySQLContainer;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class IngredientControllerIntegrationTest {

    @LocalServerPort
    private Integer port;

    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.40");

    @BeforeAll
    static void beforeAll() {
        mysqlContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mysqlContainer.stop();
    }

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private IngredientRepository ingredientRepository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        ingredientRepository.deleteAll();
    }

    @Test
    void getAllIngredientsReturnsEmptyWhenNoIngredientsPresent() {
        webTestClient.get()
            .uri("/ingredients")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Ingredient.class)
            .hasSize(0);
    }

    @Test
    void getAllIngredientsReturnsSavedIngredients() {
        // Arrange
        Ingredient ingredient1 = new Ingredient();
        ingredient1.setName("Butter");
        ingredientRepository.save(ingredient1);

        Ingredient ingredient2 = new Ingredient();
        ingredient2.setName("Milk");
        ingredientRepository.save(ingredient2);

        // Act & Assert
        webTestClient.get()
            .uri("/ingredients")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Ingredient.class)
            .hasSize(2)
            .consumeWith(response -> {
                List<Ingredient> ingredients = response.getResponseBody();
                assertThat(ingredients).extracting(Ingredient::getName).containsExactlyInAnyOrder("Butter", "Milk");
            });
    }
}
