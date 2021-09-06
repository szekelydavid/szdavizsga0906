package com.codecool.cookbook.repository;

import com.codecool.cookbook.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository  extends JpaRepository<Recipe,Long> {
}
