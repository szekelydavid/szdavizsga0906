package com.codecool.cookbook.data;

import com.codecool.cookbook.model.Ingredient;
import com.codecool.cookbook.model.Recipe;

import java.util.Collections;
import java.util.List;

import static com.codecool.cookbook.data.TestIngredient.*;

public interface TestRecipe {
    Recipe NON_DAIRY = new Recipe(null, "recipe 01", List.of(CHICKEN_BREAST, TOMATO, PASTA));
    Recipe VEGETARIAN = new Recipe(null, "recipe 02", List.of(TROUT, ROQUEFORT));
    Recipe VEGETARIAN_NON_DAIRY = new Recipe(null, "recipe 03", List.of(CELERY, PECAN_NUT));
    Recipe MIXED = new Recipe(null, "recipe 04", List.of(ANGUS_BEEF, KIWI, MILK));
    Recipe WITH_ID = new Recipe(42L, "with id", Collections.emptyList());
    Recipe WITH_NEW_INGREDIENT = new Recipe(null, "with new ingredient", List.of(new Ingredient()));
}
