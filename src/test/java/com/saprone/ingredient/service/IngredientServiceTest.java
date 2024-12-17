package com.saprone.ingredient.service;

import com.saprone.ingredient.model.Ingredient;
import com.saprone.ingredient.repository.IngredientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @InjectMocks
    private IngredientService ingredientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(webClientBuilder.build()).thenReturn(webClient);
    }

    @Test
    void getAllIngredientsShouldReturnIngredients() {
        // Arrange
        Ingredient ingredient1 = new Ingredient();
        ingredient1.setId(1L);
        ingredient1.setName("Butter");

        Ingredient ingredient2 = new Ingredient();
        ingredient2.setId(2L);
        ingredient2.setName("Milk");

        List<Ingredient> ingredients = Arrays.asList(ingredient1, ingredient2);
        when(ingredientRepository.findAll()).thenReturn(ingredients);

        // Act
        Flux<Ingredient> result = ingredientService.getAllIngredients();

        // Assert
        assertThat(result.collectList().block()).hasSize(2);
        assertThat(result.collectList().block()).containsExactlyInAnyOrder(ingredient1, ingredient2);
        verify(ingredientRepository, times(1)).findAll();
    }

    @Test
    void getAllIngredientsShouldReturnEmptyWhenNoIngredients() {
        // Arrange
        when(ingredientRepository.findAll()).thenReturn(List.of());

        // Act
        Flux<Ingredient> result = ingredientService.getAllIngredients();

        // Assert
        assertThat(result.collectList().block()).isEmpty();
        verify(ingredientRepository, times(1)).findAll();
    }
}
