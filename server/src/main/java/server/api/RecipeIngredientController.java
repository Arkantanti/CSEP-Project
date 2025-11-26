package server.api;

import commons.RecipeIngredient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.RecipeIngredientRepository;

import java.util.List;

/**
 * Controller responsible for handling HTTP requests related to {@link RecipeIngredient} entities.
 * Provides endpoints for:
 *     Retrieving all ingredient entries belonging to a specific recipe
 *     Adding a new recipe-ingredient relation
 *     Updating an existing recipe-ingredient entry
 *     Deleting a recipe-ingredient entry
 * This controller is mapped to the base path /api/recipeingredients/.
 */
@RestController
@RequestMapping("/api/recipeingredients/")
public class RecipeIngredientController {

    private final RecipeIngredientRepository repo;

    /**
     * Constructs a new {@code RecipeIngredientController} with the given repository.
     *
     * @param repo the {@link RecipeIngredientRepository} used for database operations
     */
    public RecipeIngredientController(RecipeIngredientRepository repo) {
        this.repo = repo;
    }

    /**
     * Retrieves all {@link RecipeIngredient} entries associated with a specific recipe.
     *
     * @param recipeId the ID of the recipe whose ingredient entries should be returned
     * @return {@code 400 Bad Request} if the ID is negative,
     *         otherwise {@code 200 OK} with a list of matching recipe-ingredient entries
     */
    @GetMapping("by-recipe/{recipeId}")
    public ResponseEntity<List<RecipeIngredient>> getByRecipeId(@PathVariable long recipeId) {
        if (recipeId < 0) {
            return ResponseEntity.badRequest().build();
        }
        List<RecipeIngredient> result = repo.findByRecipeId(recipeId);
        return ResponseEntity.ok(result);
    }

    /**
     * Creates a new {@link RecipeIngredient} entry on the server.
     * The provided object must reference both a recipe and an ingredient,
     * and must contain a non-negative amount value.
     *
     * @param ri the recipe-ingredient object to be created
     * @return {@code 400 Bad Request} if validation fails,
     *         otherwise {@code 200 OK} with the newly saved entry
     */
    @PostMapping("")
    public ResponseEntity<RecipeIngredient> add(@RequestBody RecipeIngredient ri) {

        if (ri == null
                || ri.getRecipe() == null
                || ri.getIngredient() == null
                || ri.getAmount() < 0) {
            return ResponseEntity.badRequest().build();
        }

        RecipeIngredient saved = repo.save(ri);
        return ResponseEntity.ok(saved);
    }

    /**
     * Updates an existing {@link RecipeIngredient} entry identified by its ID.
     * The provided object must reference both a recipe and an ingredient,
     * and must contain valid, non-negative amount data.
     *
     * @param id the ID of the recipe-ingredient entry to update
     * @param ri the updated recipe-ingredient data
     * @return {@code 400 Bad Request} if input is invalid,
     *         {@code 404 Not Found} if no entry with the given ID exists,
     *         otherwise {@code 200 OK} with the updated entry
     */
    @PutMapping("{id}")
    public ResponseEntity<RecipeIngredient> update(@PathVariable long id, @RequestBody RecipeIngredient ri) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }

        if (ri == null
                || ri.getRecipe() == null
                || ri.getIngredient() == null
                || ri.getAmount() < 0) {
            return ResponseEntity.badRequest().build();
        }

        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        RecipeIngredient saved = repo.save(ri);
        return ResponseEntity.ok(saved);
    }

    /**
     * Deletes a {@link RecipeIngredient} entry by its ID.
     *
     * @param id the ID of the recipe-ingredient entry to delete
     * @return {@code 400 Bad Request} if the ID is negative,
     *         {@code 404 Not Found} if no entry with the given ID exists,
     *         otherwise {@code 204 No Content} when deletion succeeds
     */
    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }

        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

