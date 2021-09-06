package com.codecool.cookbook.data;

import com.codecool.cookbook.model.Ingredient;
import com.codecool.cookbook.model.IngredientType;

public interface TestIngredient {
    Ingredient CHICKEN_BREAST = new Ingredient(null, "chicken breast", IngredientType.MEAT);
    Ingredient ROQUEFORT = new Ingredient(null, "roquefort", IngredientType.DAIRY);
    Ingredient ANGUS_BEEF = new Ingredient(null, "Angus beef", IngredientType.MEAT);
    Ingredient TOMATO = new Ingredient(null, "tomato", IngredientType.FRUIT);
    Ingredient KIWI = new Ingredient(null, "kiwi", IngredientType.FRUIT);
    Ingredient CELERY = new Ingredient(null, "celery", IngredientType.VEGETABLE);
    Ingredient TROUT = new Ingredient(null, "trout", IngredientType.FISH);
    Ingredient MILK = new Ingredient(null, "milk", IngredientType.DAIRY);
    Ingredient PECAN_NUT = new Ingredient(null, "pecan nut", IngredientType.OTHER);
    Ingredient PASTA = new Ingredient(null, "pasta", IngredientType.OTHER);
}
