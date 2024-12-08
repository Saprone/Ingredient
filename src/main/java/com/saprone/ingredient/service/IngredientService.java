package com.saprone.ingredient.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.saprone.ingredient.model.Ingredient;
import com.saprone.ingredient.repository.IngredientRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(IngredientService.class);
    private static final String URL_INGREDIENTS_MEAL_DB = "https://www.themealdb.com/api/json/v1/1/list.php?i=list";

    public IngredientService(IngredientRepository ingredientRepository, WebClient.Builder webClientBuilder) {
        this.ingredientRepository = ingredientRepository;
        this.webClient = webClientBuilder.build();
    }

    @PostConstruct
    public void fetchAndSaveIngredients() {
        try {
            // Check if table ingredients in the database is empty
            if (ingredientRepository.count() == 0) {
                // Fetching the ingredients from the external API
                JsonNode response = webClient.get()
                    .uri(URL_INGREDIENTS_MEAL_DB)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

                // Parse the response and save ingredients to the database
                if (response != null && response.has("meals")) {
                    for (JsonNode meal : response.get("meals")) {
                        String ingredientName = meal.get("strIngredient").asText();

                        // Create and save the ingredient
                        Ingredient ingredient = new Ingredient();
                        ingredient.setName(ingredientName);

                        ingredientRepository.save(ingredient);
                    }

                    logger.info("Ingredients fetched and saved successfully.");
                } else {
                    logger.warn("No ingredients found in the API response.");
                }
            } else {
                logger.info("Database is not empty. Skipping fetching and saving ingredients.");
            }
        } catch (Exception e) {
            logger.error("Error fetching and saving ingredients: {}", e.getMessage(), e);
        }
    }

    public Flux<Ingredient> getAllIngredients() {
        return Flux.fromIterable(ingredientRepository.findAll());
    }
}
