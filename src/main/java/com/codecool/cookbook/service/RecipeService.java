package com.codecool.cookbook.service;

import com.codecool.cookbook.model.Ingredient;
import com.codecool.cookbook.model.IngredientType;
import com.codecool.cookbook.model.Recipe;
import com.codecool.cookbook.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public Recipe getRecipeById(Long id) {
        Optional<Recipe> RecipeById = recipeRepository.findById(id);
        if (RecipeById.isEmpty()) {
            throw new RuntimeException("There is no recipe with this id in the database");
        }
        return RecipeById.get();
    }

    public Recipe addRecipe(Recipe recipe) {
        if (recipe.getId() != null) {
            throw new RuntimeException("No ids allowed in this request");
        }
        recipeRepository.save(recipe);
        return recipe;
    }

    public Recipe updateRecipe(Recipe recipe) {
        recipeRepository.save(recipe);
        return recipe;
    }

    public void deleteRecipeById(Long id) {
        recipeRepository.deleteById(id);
    }

    public List<Recipe> getVegetarianRecipes() {

        List<Recipe> recipesList = getAllRecipes();
        ArrayList<Recipe> recipesReturnList = new ArrayList<Recipe>();
        for (Recipe r : recipesList) {
            List<Ingredient> ingredientTempList = r.getIngredients();
            for (int i = 0; i < ingredientTempList.size(); i++) {
                if (ingredientTempList.get(i).getIngredientType() == IngredientType.MEAT) {
                    break;
                } else {
                    if (i == ingredientTempList.size() - 1) {
                        recipesReturnList.add(r);
                    }
                }
            }
        }
        return recipesReturnList;
    }

    public List<Recipe> getNonDairyRecipes() {
        List<Recipe> recipesList = getAllRecipes();
        ArrayList<Recipe> recipesReturnList = new ArrayList<Recipe>();
        for (Recipe r : recipesList) {
            List<Ingredient> ingredientTempList = r.getIngredients();
            for (int i = 0; i < ingredientTempList.size(); i++) {
                if (ingredientTempList.get(i).getIngredientType() == IngredientType.DAIRY) {
                    break;
                } else {
                    if (i == ingredientTempList.size() - 1) {
                        recipesReturnList.add(r);
                    }
                }
            }
        }
        return recipesReturnList;
    }
}
