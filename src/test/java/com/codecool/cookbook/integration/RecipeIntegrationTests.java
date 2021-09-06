package com.codecool.cookbook.integration;

import com.codecool.cookbook.model.Ingredient;
import com.codecool.cookbook.model.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.codecool.cookbook.data.TestRecipe.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class RecipeIntegrationTests {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private int port;

    private String baseUrl;

    private Recipe postRecipe(Recipe recipe) {
        List<Ingredient> postedIngredients = new ArrayList<>();
        for (Ingredient ingredient : recipe.getIngredients()) {
            postedIngredients.add(testRestTemplate.postForObject("http://localhost:" + port + "/ingredient", ingredient, Ingredient.class));
        }
        Recipe recipeToPost = new Recipe(recipe.getId(), recipe.getName(), postedIngredients);
        return testRestTemplate.postForObject(baseUrl, recipeToPost, Recipe.class);
    }

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port + "/recipe";
    }

    @Test
    public void emptyDatabase_getAll_shouldReturnEmptyList() {
        assertEquals(Collections.emptyList(), List.of(testRestTemplate.getForObject(baseUrl, Recipe[].class)));
    }

    @Test
    public void emptyDatabase_addOne_shouldReturnAddedRecipe() {
        MIXED.getIngredients().forEach(ing -> System.out.println(ing.getId()));
        Recipe result = postRecipe(MIXED);
        assertEquals(MIXED.getName(), result.getName());
    }

    @Test
    public void someRecipesStored_getAll_shouldReturnAll() {
        List<Recipe> testData = List.of(MIXED, VEGETARIAN, NON_DAIRY);
        testData.forEach(this::postRecipe);

        Recipe[] result = testRestTemplate.getForObject(baseUrl, Recipe[].class);

        assertEquals(testData.size(), result.length);
        for (int i = 0; i < testData.size(); i++) {
            assertEquals(testData.get(i).getName(), result[i].getName());
        }
    }

    @Test
    public void oneRecipeStored_getOneById_shouldReturnCorrectRecipe() {
        Long id = postRecipe(MIXED).getId();
        Recipe result = testRestTemplate.getForObject(baseUrl + "/" + id, Recipe.class);
        assertEquals(MIXED.getName(), result.getName());
        for (int i = 0; i < MIXED.getIngredients().size(); i++) {
            assertEquals(MIXED.getIngredients().get(i).getName(), result.getIngredients().get(i).getName());
        }
    }

    @Test
    public void oneRecipeStored_getOneByWrongId_shouldRespondWithInternalServerError() {
        Long id = postRecipe(MIXED).getId();
        ResponseEntity<String> result = testRestTemplate.getForEntity(baseUrl + "/" + id + 42, String.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    public void someRecipesStored_deleteOne_getAllShouldReturnRemaining() {
        List<Recipe> testData = new ArrayList<>(List.of(MIXED, VEGETARIAN, NON_DAIRY));
        for (int i = 0; i < testData.size(); i++) {
            testData.set(i, postRecipe(testData.get(i)));
        }

        testRestTemplate.delete(baseUrl + "/" + testData.get(0).getId());
        testData.remove(testData.get(0));

        Recipe[] result = testRestTemplate.getForObject(baseUrl, Recipe[].class);

        assertEquals(testData.size(), result.length);
        for (int i = 0; i < testData.size(); i++) {
            assertEquals(testData.get(i).getName(), result[i].getName());
        }
    }

    @Test
    public void oneRecipeStored_deleteById_getAllShouldReturnEmptyList() {
        Recipe testRecipe = postRecipe(MIXED);

        testRestTemplate.delete(baseUrl + "/" + testRecipe.getId());

        Recipe[] result = testRestTemplate.getForObject(baseUrl, Recipe[].class);

        assertEquals(0, result.length);
    }

    @Test
    public void oneRecipeStored_updateIt_recipeShouldBeUpdated() {
        Recipe testRecipe = postRecipe(MIXED);

        testRecipe.setName(testRecipe.getName() + "update");
        testRestTemplate.put(baseUrl, testRecipe);

        Recipe result = testRestTemplate.getForObject(baseUrl + "/" + testRecipe.getId(), Recipe.class);

        assertEquals(testRecipe.getName(), result.getName());
    }

    @Test
    public void oneRecipeStored_updateWithWrongId_recipeShouldBeUnchanged() {
        Recipe testRecipe = postRecipe(MIXED);

        String originalName = testRecipe.getName();
        Long originalId = testRecipe.getId();

        testRecipe.setName(originalName + "update");
        testRecipe.setId(42L);
        testRestTemplate.put(baseUrl, testRecipe);

        Recipe result = testRestTemplate.getForObject(baseUrl + "/" + originalId, Recipe.class);

        assertEquals(originalName, result.getName());
    }

    @Test
    public void addNewRecipe_idProvided_shouldRespondWithInternalServerError() {
        ResponseEntity<String> result = testRestTemplate.postForEntity(baseUrl, WITH_ID, String.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    public void addNewRecipe_someIngredientNotYetSaved_shouldRespondWithInternalServerError() {
        ResponseEntity<String> result = testRestTemplate.postForEntity(baseUrl, WITH_NEW_INGREDIENT, String.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    public void someRecipesStored_getVegetarians_shouldReturnOnlyVegetarianRecipes() {
        List.of(VEGETARIAN, VEGETARIAN_NON_DAIRY, NON_DAIRY, MIXED).forEach(this::postRecipe);

        List<Recipe> result = List.of(testRestTemplate.getForObject(baseUrl + "/vegetarian", Recipe[].class));

        assertEquals(2, result.size());
        assertEquals(VEGETARIAN.getName(), result.get(0).getName());
        assertEquals(VEGETARIAN_NON_DAIRY.getName(), result.get(1).getName());
    }

    @Test
    public void someRecipesStored_getNonDairies_shouldReturnOnlyNonDairyRecipes() {
        List.of(VEGETARIAN, VEGETARIAN_NON_DAIRY, NON_DAIRY, MIXED).forEach(this::postRecipe);

        List<Recipe> result = List.of(testRestTemplate.getForObject(baseUrl + "/non-dairy", Recipe[].class));

        assertEquals(2, result.size());
        assertEquals(VEGETARIAN_NON_DAIRY.getName(), result.get(0).getName());
        assertEquals(NON_DAIRY.getName(), result.get(1).getName());
    }
}
