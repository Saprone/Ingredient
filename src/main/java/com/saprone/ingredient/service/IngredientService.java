package com.saprone.ingredient.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saprone.ingredient.model.Ingredient;
import com.saprone.ingredient.repository.IngredientRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(IngredientService.class);
    private static final String URL_INGREDIENTS_MEAL_DB = "https://www.themealdb.com/api/json/v1/1/list.php?i=list";

    public IngredientService(IngredientRepository ingredientRepository, RestTemplate restTemplate) {
        this.ingredientRepository = ingredientRepository;
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void fetchAndSaveIngredients() {
        try {
            // Check if table ingredients in the database is empty
            if (ingredientRepository.count() == 0) {
                // Fetching the ingredients from the external API
                String response = restTemplate.getForObject(URL_INGREDIENTS_MEAL_DB, String.class);

                // Parsing the response and saving each ingredient
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response);
                JsonNode ingredientsNode = jsonNode.path("meals");

                if (ingredientsNode.isArray()) {
                    for (JsonNode ingredientNode : ingredientsNode) {
                        String ingredientName = ingredientNode.path("strIngredient").asText();

                        if (!ingredientName.isEmpty()) {
                            Ingredient ingredient = new Ingredient();
                            ingredient.setName(ingredientName);

                            ingredientRepository.save(ingredient);
                        }
                    }
                }

                logger.info("Ingredients fetched and saved successfully.");
            } else {
                logger.info("Database is not empty. Skipping fetching and saving ingredients.");
            }
        } catch (JsonProcessingException e) {
            logger.error("Error processing JSON response: {}", e.getMessage());
        }
    }

    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }
}
