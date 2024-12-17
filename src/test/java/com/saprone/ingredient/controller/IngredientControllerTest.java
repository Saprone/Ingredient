package com.saprone.ingredient.controller;

import com.saprone.ingredient.model.Ingredient;
import com.saprone.ingredient.service.IngredientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class IngredientControllerTest {

    @Mock
    private IngredientService ingredientService;

    @InjectMocks
    private IngredientController ingredientController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllIngredientsShouldReturnIngredients() {
        // Arrange
        Ingredient ingredient = new Ingredient();
        ingredient.setId(1L);
        ingredient.setName("Butter");
        when(ingredientService.getAllIngredients()).thenReturn(Flux.just(ingredient));

        // Act
        Flux<Ingredient> result = ingredientController.getAllIngredients();

        // Assert
        assertThat(result.collectList().block()).hasSize(1);
        verify(ingredientService, times(1)).getAllIngredients();
    }
}
