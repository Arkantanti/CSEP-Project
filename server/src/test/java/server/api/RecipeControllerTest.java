package server.api;

import commons.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import server.database.RecipeRepository;
import server.database.RecipeRepositoryTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecipeControllerTest {

    private RecipeRepository repo;
    private RecipeController controller;

    private Recipe r1;
    private Recipe r2;
    private Recipe r3;

    @Test
    void searchRecipes_ValidQuery_ReturnsMatches() {
        // Setup: Add a specific recipe to search for
        Recipe applePie = new Recipe("Apple Pie", 4, List.of("Bake"), true, false, true);
        repo.save(applePie);

        // Test: Search for "Apple"
        ResponseEntity<List<Recipe>> result = controller.searchRecipes("Apple");

        // Verify
        assertEquals(200, result.getStatusCodeValue());
        assertFalse(result.getBody().isEmpty());
        assertEquals("Apple Pie", result.getBody().get(0).getName());
    }

    @Test
    void searchRecipes_EmptyQuery_ReturnsBadRequest() {
        // Test: Search with empty string or null
        ResponseEntity<List<Recipe>> result = controller.searchRecipes("");

        assertEquals(400, result.getStatusCodeValue());
    }

    @BeforeEach
    void setUp() {
        repo = new RecipeRepositoryTest();
        controller = new RecipeController(repo);// uncommit this to test

        r1 = new Recipe("Pancakes", 2, null, true, true, false);
        r2 = new Recipe("Tomato Soup", 4, null, true, true, true);
        r3 = new Recipe("Burrito", 3, null, false, true, false);

        repo.save(r1);
        repo.save(r2);
        repo.save(r3);
    }

    @Test
    void getRecipeById_ValidId_ReturnsRecipe() {
        ResponseEntity<Recipe> result = controller.getById(r1.getId());

        assertEquals(200, result.getStatusCode().value());
        assertEquals(r1, result.getBody());
    }

    @Test
    void getRecipeById_InvalidId_ReturnsNotFound() {
        ResponseEntity<Recipe> result = controller.getById(999);

        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    void addRecipe_ValidRecipe_ReturnsSaved() {
        // Change "" to "Salad"
        Recipe r4 = new Recipe("Salad", 2, List.of("step1"), false, false, false);

        ResponseEntity<Recipe> result = controller.add(r4);
        System.out.println(result);
        assertEquals(200, result.getStatusCode().value());
        assertTrue(repo.existsById(result.getBody().getId()));
    }

    @Test
    void addRecipe_InvalidRecipe_ReturnsBad() {
        Recipe r4 = new Recipe("", 2, List.of("step1"), false, false, false);

        ResponseEntity<Recipe> result = controller.add(r4);
        assertEquals(400, result.getStatusCode().value());
    }

    @Test
    void updateRecipe_Valid() {
        Recipe updated = new Recipe("Better Pancakes", 2, List.of("", "here"), true, true, false);
        ResponseEntity<Recipe> result = controller.update(r1.getId(), updated);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(updated, repo.findById(r1.getId()).orElse(null));
    }

    @Test
    void updateRecipe_InvalidId_ReturnsNotFound() {
        Recipe updated = new Recipe("X", 2, List.of("hre"), false, false, false);

        ResponseEntity<Recipe> result = controller.update(999, updated);

        assertEquals(404, result.getStatusCode().value());
    }

//    @Test
//    void updateRecipe_NullBody_ReturnsBadRequest() {
//        ResponseEntity<Recipe> result = controller.update(r1.getId(), null);
//
//        assertThrows(NullPointerException.class, () -> {result.getStatusCode().value();});
//    }

    @Test
    void deleteRecipe_Success() {
        ResponseEntity<Void> result = controller.delete(r1.getId());

        assertEquals(204, result.getStatusCode().value());
        assertFalse(repo.existsById(r1.getId()));
    }

    @Test
    void deleteRecipe_InvalidId_ReturnsBadRequest() {
        ResponseEntity<Void> result = controller.delete(-1);

        assertEquals(400, result.getStatusCode().value());
    }

    @Test
    void deleteRecipe_NotFound() {
        ResponseEntity<Void> result = controller.delete(999);

        assertEquals(404, result.getStatusCode().value());
    }
}
