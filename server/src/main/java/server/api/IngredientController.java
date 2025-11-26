package server.api;

import commons.Ingredient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.IngredientRepository;

import java.util.List;

/**
 * Controller responsible for handling HTTP requests related to Ingredient entities.
 * Provides endpoints for:
 *     Retrieving all ingredients
 *     Retrieving an ingredient by its ID
 *     Adding a new ingredient
 *     Updating an existing ingredient
 *     Deleting an ingredient
 * This controller is mapped to the base path /api/ingredients/.
 */
@RestController
@RequestMapping("/api/ingredients/")
public class IngredientController {

    private final IngredientRepository repo;

    public IngredientController(IngredientRepository repo){
        this.repo = repo;
    }

    /**
     * Returns all ingredients stored on the server.
     *
     * @return a list of all Ingredient objects
     */
    @GetMapping("")
    public List<Ingredient> getAll(){
        return repo.findAll();
    }

    /**
     * Returns a single ingredient based on its id.
     *
     * @param id the id of the ingredient to retrieve
     * @return 400 Bad Request if the id is negative,
     *         404 Not Found if no ingredient with the given id exists,
     *         otherwise 200 OK with the ingredient data
     */
    @GetMapping("{id}")
    public ResponseEntity<Ingredient> getById(@PathVariable long id) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }
        if (!repo.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    /**
     * Creates a new ingredient and stores it on the server.
     * The ingredient must have a non-empty name, and all nutritional values must be non-negative.
     *
     * @param ing the ingredient object provided by the client
     * @return 400 Bad Request if validation fails,
     *         otherwise 200 OK with the saved ingredient
     */
    @PostMapping("")
    public ResponseEntity<Ingredient> add(@RequestBody Ingredient ing){

        if (ing == null
                || isNullOrEmpty(ing.getName())
                || ing.getFat() < 0
                || ing.getProtein() < 0
                || ing.getCarbs() < 0) {
            return ResponseEntity.badRequest().build();
        }

        Ingredient saved = repo.save(ing);
        return ResponseEntity.ok(saved);
    }

    /**
     * Updates an existing ingredient identified by its id.
     * The ingredient must already exist and the provided data must be valid.
     * Required fields include a non-empty name and non-negative nutritional values.
     *
     * @param id  the id of the ingredient to update
     * @param ing the updated ingredient data
     * @return 400 Bad Request if the id is invalid or the ingredient data is invalid,
     *         404 Not Found if the ingredient does not exist,
     *         otherwise 200 OK with the updated ingredient
     */
    @PutMapping("{id}")
    public ResponseEntity<Ingredient> update(@PathVariable long id, @RequestBody Ingredient ing){

        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }

        if (ing == null
                || isNullOrEmpty(ing.getName())
                || ing.getProtein() < 0
                ||  ing.getCarbs() < 0
                || ing.getFat() < 0) {
            return ResponseEntity.badRequest().build();
        }

        if (!repo.existsById(id)){
            return ResponseEntity.notFound().build();
        }

        Ingredient saved = repo.save(ing);
        return ResponseEntity.ok(saved);
    }

    /**
     * Deletes an ingredient identified by its id.
     *
     * @param id the id of the ingredient to delete
     * @return 400 Bad Request if the id is negative,
     *         404 Not Found if no ingredient with the given id exists,
     *         otherwise 204 No Content if deletion is successful
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
     * Checks whether a string is null or empty.
     *
     * @param s the string to check
     * @return true if the string is null or empty, false otherwise
     */
    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
