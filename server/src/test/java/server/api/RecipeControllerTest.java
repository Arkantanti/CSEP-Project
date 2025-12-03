package server.api;

import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import server.database.RecipeRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecipeControllerTest {

    private RecipeRepository repo;
    private RecipeController controller;

    private Recipe r1;
    private Recipe r2;

    @BeforeEach
    void setUp() {
        repo = new RecipeRepositoryTest();
        controller = new RecipeController(repo);// uncommit this to test

        r1 = new Recipe("Pancakes", 2, null);
        r2 = new Recipe("Tomato Soup", 4, null);

        repo.save(r1);
        repo.save(r2);
    }

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

    @Test
    void addRecipe_ValidRecipe_ReturnsSaved() {
        Recipe r3 = new Recipe("Salad", 2, List.of(""));

        ResponseEntity<Recipe> result = controller.add(r3);
        System.out.println(result);
        assertEquals(200, result.getStatusCodeValue());
        assertTrue(repo.existsById(result.getBody().getId()));
    }

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

    private RecipeIngredient validIngredient() {
        Ingredient ing = new Ingredient("Flour", 1, 1, 1);
        return new RecipeIngredient(r2, ing, "cup", 50, Unit.GRAM);
    }

    // @Test
    // void addRecipeIngredient_Valid_ReturnsOk() {
    //     var result = controller.addRecipeIngredient(r1.getId(), validIngredient());

    //     assertEquals(200, result.getStatusCodeValue());
    //     assertEquals(1, result.getBody().getIngredients().size());
    // }

    // @Test
    // void addRecipeIngredient_InvalidRecipeId_ReturnsBadRequest() {
    //     var result = controller.addRecipeIngredient(-1, validIngredient());
    //     assertEquals(400, result.getStatusCodeValue());
    // }

    // @Test
    // void addRecipeIngredient_RecipeNotFound_ReturnsNotFound() {
    //     var result = controller.addRecipeIngredient(999, validIngredient());
    //     assertEquals(404, result.getStatusCodeValue());
    // }

    // @Test
    // void addRecipeIngredient_InvalidIngredient_ReturnsBadRequest() {
    //     Ingredient badIng = new Ingredient("", 1, 1, 1);
    //     RecipeIngredient bad = new RecipeIngredient(r1, badIng, "cup", 50, Unit.GRAM);

    //     var result = controller.addRecipeIngredient(r1.getId(), bad);
    //     assertEquals(400, result.getStatusCodeValue());
    // }

    // @Test
    // void updateRecipeIngredient_Valid_ReturnsOk() {
    //     controller.addRecipeIngredient(r1.getId(), validIngredient());

    //     Ingredient sugar = new Ingredient("Sugar", 0, 0, 0);
    //     RecipeIngredient updated = new RecipeIngredient(r1, sugar, "tbsp", 10, Unit.GRAM);

    //     var result = controller.updateRecipeIngredient(r1.getId(), 0, updated);

    //     assertEquals(200, result.getStatusCodeValue());
    //     assertEquals("Sugar",
    //             result.getBody().getIngredients().get(0).getIngredient().getName());
    // }

    // @Test
    // void updateRecipeIngredient_InvalidRecipeId_ReturnsBadRequest() {
    //     var result = controller.updateRecipeIngredient(-1, 0, validIngredient());
    //     assertEquals(400, result.getStatusCodeValue());
    // }

    // @Test
    // void updateRecipeIngredient_RecipeNotFound_ReturnsNotFound() {
    //     var result = controller.updateRecipeIngredient(999, 0, validIngredient());
    //     assertEquals(404, result.getStatusCodeValue());
    // }

    // @Test
    // void updateRecipeIngredient_InvalidIngredient_ReturnsBadRequest() {
    //     Ingredient bad = new Ingredient("", 1, 1, 1);
    //     RecipeIngredient ri = new RecipeIngredient(r1, bad, "cup", 10, Unit.GRAM);

    //     var result = controller.updateRecipeIngredient(r1.getId(), 0, ri);
    //     assertEquals(400, result.getStatusCodeValue());
    // }

    // @Test
    // void deleteRecipeIngredient_Valid_ReturnsOk() {
    //     controller.addRecipeIngredient(r1.getId(), validIngredient());

    //     var result = controller.deleteRecipeIngredient(r1.getId(), 0);

    //     assertEquals(200, result.getStatusCodeValue());
    //     assertTrue(result.getBody().getIngredients().isEmpty());
    // }

    // @Test
    // void deleteRecipeIngredient_InvalidRecipeId_ReturnsBadRequest() {
    //     var result = controller.deleteRecipeIngredient(-1, 0);
    //     assertEquals(400, result.getStatusCodeValue());
    // }

    // @Test
    // void deleteRecipeIngredient_RecipeNotFound_ReturnsNotFound() {
    //     var result = controller.deleteRecipeIngredient(999, 0);
    //     assertEquals(404, result.getStatusCodeValue());
    // }
}
