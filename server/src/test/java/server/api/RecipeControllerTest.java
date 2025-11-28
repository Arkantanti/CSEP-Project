package server.api;

import commons.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import server.database.RecipeRepository;
import server.database.InMemoryRecipeRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecipeControllerTest {

    private InMemoryRecipeRepository repo;
    private RecipeController controller;

    private Recipe r1;
    private Recipe r2;

    @BeforeEach
    void setUp() {
        repo = new InMemoryRecipeRepository();
        controller = new RecipeController(repo);

        r1 = new Recipe("Pancakes", 2, null);
        r2 = new Recipe("Tomato Soup", 4, null);

        repo.save(r1);
        repo.save(r2);
    }

    // -----------------------------------------------------

    @Test
    void getRecipeById_ValidId_ReturnsRecipe() {
        ResponseEntity<Recipe> result = controller.getById(r1.getId());

        assertEquals(200, result.getStatusCodeValue());
        assertEquals("Pancakes", result.getBody().getName());
    }

    @Test
    void getRecipeById_InvalidId_ReturnsNotFound() {
        ResponseEntity<Recipe> result = controller.getById(999);

        assertEquals(404, result.getStatusCodeValue());
    }

    // -----------------------------------------------------

    @Test
    void addRecipe_ValidRecipe_ReturnsSaved() {
        Recipe r3 = new Recipe("Salad", 2, List.of(""));

        ResponseEntity<Recipe> result = controller.add(r3);
        System.out.println(result);
        assertEquals(200, result.getStatusCodeValue());
        assertTrue(repo.existsById(result.getBody().getId()));
    }

    // -----------------------------------------------------

    @Test
    void updateRecipe_Valid() {
        Recipe updated = new Recipe("Better Pancakes", 2, List.of("", "here"));
        ResponseEntity<Recipe> result = controller.update(r1.getId(), updated);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals("Pancakes",
                repo.findById(r1.getId()).get().getName());
    }

    @Test
    void updateRecipe_InvalidId_ReturnsNotFound() {
        Recipe updated = new Recipe("X", 2, List.of("hre"));

        ResponseEntity<Recipe> result = controller.update(999, updated);

        assertEquals(404, result.getStatusCodeValue());
    }

    @Test
    void updateRecipe_NullBody_ReturnsBadRequest() {
        ResponseEntity<Recipe> result = controller.update(r1.getId(), null);

        assertEquals(400, result.getStatusCodeValue());
    }

    // -----------------------------------------------------

    @Test
    void deleteRecipe_Success() {
        ResponseEntity<Void> result = controller.delete(r1.getId());

        assertEquals(204, result.getStatusCodeValue());
        assertFalse(repo.existsById(r1.getId()));
    }

    @Test
    void deleteRecipe_InvalidId_ReturnsBadRequest() {
        ResponseEntity<Void> result = controller.delete(-1);

        assertEquals(400, result.getStatusCodeValue());
    }

    @Test
    void deleteRecipe_NotFound() {
        ResponseEntity<Void> result = controller.delete(999);

        assertEquals(404, result.getStatusCodeValue());
    }
}
