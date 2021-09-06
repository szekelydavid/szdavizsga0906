package com.codecool.cookbook.integration;

import com.codecool.cookbook.model.Ingredient;
import com.codecool.cookbook.model.IngredientType;
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

import static com.codecool.cookbook.data.TestIngredient.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class IngredientIntegrationTests {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private int port;

    private String baseUrl;

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port + "/ingredient";
    }

    @Test
    public void emptyDatabase_getAll_shouldReturnEmptyList() {
        assertEquals(Collections.emptyList(), List.of(testRestTemplate.getForObject(baseUrl, Ingredient[].class)));
    }

    @Test
    public void emptyDatabase_addOne_shouldReturnAddedIngredient() {
        Ingredient result = testRestTemplate.postForObject(baseUrl, TOMATO, Ingredient.class);
        assertEquals(TOMATO.getName(), result.getName());
    }

    @Test
    public void someIngredientsStored_getAll_shouldReturnAll() {
        List<Ingredient> testData = List.of(TOMATO, MILK, CHICKEN_BREAST);
        testData.forEach(ingredient -> testRestTemplate.postForObject(baseUrl, ingredient, Ingredient.class));

        Ingredient[] result = testRestTemplate.getForObject(baseUrl, Ingredient[].class);

        assertEquals(testData.size(), result.length);
        for (int i = 0; i < testData.size(); i++) {
            assertEquals(testData.get(i).getName(), result[i].getName());
        }
    }

    @Test
    public void oneIngredientStored_getOneById_shouldReturnCorrectIngredient() {
        Long id = testRestTemplate.postForObject(baseUrl, TOMATO, Ingredient.class).getId();
        Ingredient result = testRestTemplate.getForObject(baseUrl + "/" + id, Ingredient.class);
        assertEquals(TOMATO.getName(), result.getName());
    }

    @Test
    public void oneIngredientStored_getOneByWrongId_shouldRespondWithInternalServerError() {
        Long id = testRestTemplate.postForObject(baseUrl, TOMATO, Ingredient.class).getId();
        ResponseEntity<Ingredient> result = testRestTemplate.getForEntity(baseUrl + "/" + id + 42, Ingredient.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    public void someIngredientsStored_deleteOne_getAllShouldReturnRemaining() {
        List<Ingredient> testData = new ArrayList<>(List.of(TOMATO, MILK, CHICKEN_BREAST));
        for (int i = 0; i < testData.size(); i++) {
            testData.set(i, testRestTemplate.postForObject(baseUrl, testData.get(i), Ingredient.class));
        }

        testRestTemplate.delete(baseUrl + "/" + testData.get(0).getId());
        testData.remove(testData.get(0));

        Ingredient[] result = testRestTemplate.getForObject(baseUrl, Ingredient[].class);

        assertEquals(testData.size(), result.length);
        for (int i = 0; i < testData.size(); i++) {
            assertEquals(testData.get(i).getName(), result[i].getName());
        }
    }

    @Test
    public void oneIngredientStored_deleteById_getAllShouldReturnEmptyList() {
        Ingredient testIngredient = testRestTemplate.postForObject(baseUrl, TOMATO, Ingredient.class);

        testRestTemplate.delete(baseUrl + "/" + testIngredient.getId());

        Ingredient[] result = testRestTemplate.getForObject(baseUrl, Ingredient[].class);

        assertEquals(0, result.length);
    }

    @Test
    public void oneIngredientStoredUsedInRecipe_deleteById_IngredientShouldNotBeDeleted() {
        Ingredient testIngredient = testRestTemplate.postForObject(baseUrl, TOMATO, Ingredient.class);
        Recipe testRecipe = testRestTemplate.postForObject(
                "http://localhost:" + port + "/recipe",
                new Recipe(null, "with tomato", List.of(testIngredient)),
                Recipe.class
        );

        testRestTemplate.delete(baseUrl + "/" + testIngredient.getId());

        Ingredient result = testRestTemplate.getForObject(baseUrl + "/" + testIngredient.getId(), Ingredient.class);

        assertEquals(testIngredient.getName(), result.getName());
    }

    @Test
    public void oneIngredientStored_updateIt_ingredientShouldBeUpdated() {
        Ingredient testIngredient = testRestTemplate.postForObject(baseUrl, TOMATO, Ingredient.class);

        testIngredient.setName(testIngredient.getName() + "update");
        testRestTemplate.put(baseUrl, testIngredient);

        Ingredient result = testRestTemplate.getForObject(baseUrl + "/" + testIngredient.getId(), Ingredient.class);

        assertEquals(testIngredient.getName(), result.getName());
    }

    @Test
    public void oneIngredientStored_updateWithWrongId_ingredientShouldBeUnchanged() {
        Ingredient testIngredient = testRestTemplate.postForObject(baseUrl, TOMATO, Ingredient.class);

        String originalName = testIngredient.getName();
        Long originalId = testIngredient.getId();

        testIngredient.setName(originalName + "update");
        testIngredient.setId(42L);
        testRestTemplate.put(baseUrl, testIngredient);

        Ingredient result = testRestTemplate.getForObject(baseUrl + "/" + originalId, Ingredient.class);

        assertEquals(originalName, result.getName());
    }

    @Test
    public void addNewIngredient_idProvided_shouldRespondWithInternalServerError() {
        ResponseEntity<String> result = testRestTemplate.postForEntity(baseUrl, new Ingredient(42L, "with id", IngredientType.FISH), String.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }
}
