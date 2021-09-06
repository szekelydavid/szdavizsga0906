package com.codecool.cookbook.service;

import com.codecool.cookbook.model.Ingredient;
import com.codecool.cookbook.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IngredientService {
    @Autowired
    private IngredientRepository ingredientRepository;

    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    public Ingredient getIngredientById(Long id) {
        Optional<Ingredient> IngredientById = ingredientRepository.findById(id);
        if (IngredientById.isEmpty()) {
            throw new RuntimeException("There is no book with this id in the database");
        }
        return IngredientById.get();
    }

    public Ingredient addOrUpdateIngredient(Ingredient ingredient) {
        if (ingredient.getId()!=null) {
            throw new RuntimeException("No ids allowed in this request");
        }

        ingredientRepository.save(ingredient);
        return ingredient;
    }

    public void deleteIngredientById(Long id) {
        ingredientRepository.deleteById(id);
    }


}
