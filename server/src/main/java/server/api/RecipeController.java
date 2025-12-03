package server.api;

import commons.Recipe;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.RecipeRepository;
import java.util.List;

/**
 * Controller responsible for handling all HTTP requests related to {@link Recipe} entities.
 * Provides endpoints for:
 *     Retrieving all recipes
 *     Retrieving a recipe by its ID
 *     Adding a new recipe
 *     Updating an existing recipe
 *     Deleting a recipe
 * This controller maps to the base path /api/recipes/.
 */
@RestController
@RequestMapping("/api/recipes/")
public class RecipeController {
    private final RecipeRepository repo;

    /**
     * Constructs a new {@code RecipeController} with the given repository.
     *
     * @param repo the {@link RecipeRepository} used for database operations
     */
    public RecipeController(RecipeRepository repo){
        this.repo = repo;
    }

    /**
     * Retrieves all recipes stored on the server.
     *
     * @return a list of all {@link Recipe} objects available in the system
     */
    @GetMapping( "")
    public List<Recipe> getAll() {
        return repo.findAll();
    }

    /**
     * Retrieves a single recipe by its ID.
     *
     * @param id the ID of the recipe to retrieve
     * @return {@code 400 Bad Request} if ID is negative,
     *         {@code 404 Not Found} if no recipe with the given ID exists,
     *         otherwise {@code 200 OK} with the requested recipe
     */
    @GetMapping("{id}")
    public ResponseEntity<Recipe> getById(@PathVariable long id) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }
        if (!repo.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    /**
     * Adds a new recipe to the system.
     * The following conditions must be met, otherwise a
     * {@code 400 Bad Request} response is returned:
     *     The recipe must have a non-empty name
     *     The number of servings must be at least 1
     *     The preparation steps must not be {@code null}
     *
     * @param recipe the recipe object to be added
     * @return {@code 400 Bad Request} if validation fails,
     *         otherwise {@code 200 OK} containing the saved recipe
     */
    @PostMapping("")
    public ResponseEntity<Recipe> add(@RequestBody Recipe recipe) {

        if (isNullOrEmpty(recipe.getName())
                || recipe.getServings() < 1
                || recipe.getPreparationSteps() == null) {
            return ResponseEntity.badRequest().build();
        }
        Recipe saved = repo.save(recipe);
        return ResponseEntity.ok(saved);
    }

    /**
     * Updates an existing recipe identified by its ID.
     * The following validation rules apply:
     *     ID must be non-negative
     *     The incoming recipe must not be {@code null}
     *     The recipe must have a valid name and serving size
     *     The preparation steps must not be {@code null}
     *     A recipe with the given ID must already exist
     *
     * @param id the ID of the recipe to update
     * @param recipe the new state of the recipe
     * @return {@code 400 Bad Request} if input is invalid,
     *         {@code 404 Not Found} if the recipe does not exist,
     *         otherwise {@code 200 OK} with the updated recipe
     */
    @PutMapping("{id}")
    public ResponseEntity<Recipe> update(@PathVariable long id, @RequestBody Recipe recipe) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }

        if (recipe == null
                || isNullOrEmpty(recipe.getName())
                || recipe.getServings() < 1
                || recipe.getPreparationSteps() == null) {
            return ResponseEntity.badRequest().build();
        }

        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Recipe saved = repo.save(recipe);
        return ResponseEntity.ok(saved);
    }

    /**
     * Deletes the recipe with the specified ID.
     *
     * @param id the ID of the recipe to delete
     * @return {@code 400 Bad Request} if ID is negative,
     *         {@code 404 Not Found} if no recipe with the given ID exists,
     *         otherwise {@code 204 No Content} when deletion succeeds
     */
    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }

        if (!repo.existsById(id)){
            return ResponseEntity.notFound().build();
        }

        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Utility method to check whether a given string is {@code null} or empty.
     *
     * @param s the string to evaluate
     * @return {@code true} if the string is {@code null} or empty,
     *         otherwise {@code false}
     */
    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
