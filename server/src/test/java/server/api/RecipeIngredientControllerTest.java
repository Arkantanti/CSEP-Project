package server.api;

import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.database.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecipeIngredientControllerTest {

    private RecipeIngredientController controller;
    private RecipeIngredientRepository repo;

    private Recipe r1;
    private Recipe r2;

    private Ingredient ing1;
    private Ingredient ing2;
    private Ingredient ing3;

    private RecipeIngredient ri1;
    private RecipeIngredient ri2;
    private RecipeIngredient ri3;

    @BeforeEach
    void setup() {
        repo = new RecipeIngredientRepositoryTest();
        controller  = new RecipeIngredientController(repo);
        RecipeRepository recipeRepo = new RecipeRepositoryTest();
        IngredientRepository ingredientRepo = new IngredientRepositoryTest();

        r1 = recipeRepo.save(new Recipe("Pancakes", 4, List.of("step 1","step 2")));
        r2 = recipeRepo.save(new Recipe("Spaghetti", 3, List.of("step 3","step 4")));

        ing1 = ingredientRepo.save(new Ingredient("Chilli", 3, 1, 1.2));
        ing2 = ingredientRepo.save(new Ingredient("Potato", 3, 1, 1.2));
        ing3 = ingredientRepo.save(new Ingredient("Oil", 10, 1, 1.2));

        ri1 = repo.save(new RecipeIngredient(r1, ing2, null, 4, Unit.GRAM));
        ri2 = repo.save(new RecipeIngredient(r2, ing1, null, 3, Unit.GRAM));
        ri3 = repo.save(new RecipeIngredient(r1, ing3, null, 1, Unit.GRAM));
        repo.save(new RecipeIngredient(r2, ing2, null, 312, Unit.LITER));

    }

    @Test
    void getByRecipeId() {
        ResponseEntity<List<RecipeIngredient>> response = controller.getByRecipeId(r1.getId());
        assertEquals(200, response.getStatusCode().value());
//        assertEquals(List.of(ri1,ri3),response.getBody());
    }

    @Test
    void add_validRecipeIngredient() {
        RecipeIngredient ri4 = new RecipeIngredient(r2, ing3, "a bit", 0, Unit.CUSTOM);
        ResponseEntity<RecipeIngredient> response = controller.add(ri4);
        assertEquals(200, response.getStatusCode().value());
        assertTrue(repo.existsById(ri4.getId()));
    }

    @Test
    void add_invalidRecipeIngredient() {
        RecipeIngredient ri4 = new RecipeIngredient(r2, ing3, "a bit", 0, null);
        ResponseEntity<RecipeIngredient> response = controller.add(ri4);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void update_validRecipeIngredient() {
        RecipeIngredient ri4 = new RecipeIngredient(r2, ing3, "a bit", 0, Unit.CUSTOM);
        ResponseEntity<RecipeIngredient> response = controller.update(ri1.getId(),ri4);
        assertEquals(200, response.getStatusCode().value());
//        assertEquals(ri4,repo.findById(ri1.getId()).orElse(null));
    }

    @Test
    void update_invalidRecipeIngredient() {
        RecipeIngredient ri4 = new RecipeIngredient(r2, ing3, "a bit", -1, Unit.CUSTOM);
        ResponseEntity<RecipeIngredient> response = controller.update(ri1.getId(),ri4);
        assertEquals(400, response.getStatusCode().value());
        assertNotEquals(ri4,repo.findById(ri1.getId()).orElse(null));
    }

    @Test
    void update_invalidId() {
        RecipeIngredient ri4 = new RecipeIngredient(r2, ing3, "a bit", -1, Unit.CUSTOM);
        ResponseEntity<RecipeIngredient> response = controller.update(-9, ri4);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void update_notfoundId() {
        RecipeIngredient ri4 = new RecipeIngredient(r2, ing3, "a bit", 0, Unit.CUSTOM);
        ResponseEntity<RecipeIngredient> response = controller.update(999, ri4);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void delete_validId() {
        ResponseEntity<Void> response = controller.delete(ri1.getId());
        assertEquals(204, response.getStatusCode().value());
        assertFalse(repo.existsById(ri1.getId()));
    }

    @Test
    void delete_invalidId() {
        ResponseEntity<Void> response = controller.delete(-10);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void delete_notfoundId() {
        ResponseEntity<Void> response = controller.delete(999);
        assertEquals(404, response.getStatusCode().value());
    }

//    @Test
//    void recipeCountSuccess() {
//        ResponseEntity<Long> response = controller.getRecipeCount(ing2.getId());
//        assertEquals(200, response.getStatusCode().value());
//        assertEquals(2,response.getBody());
//    }
}