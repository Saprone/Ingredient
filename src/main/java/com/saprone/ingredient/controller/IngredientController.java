package com.saprone.ingredient.controller;

import com.saprone.ingredient.model.Ingredient;
import com.saprone.ingredient.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@CrossOrigin(origins = "*")
public class IngredientController {

    private final IngredientService ingredientService;

    @Autowired
    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping("/ingredients")
    public Flux<Ingredient> getAllIngredients() {
        return ingredientService.getAllIngredients();
    }
}
