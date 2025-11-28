package server.database;

import commons.Recipe;
import java.lang.reflect.Field;
import java.util.*;

public class InMemoryRecipeRepository {

    private final Map<Long, Recipe> store = new HashMap<>();
    private long nextId = 1;

    /**
     * tries to find all values in the database
     * @return Returns a list of all stored recipes.
     */
    public List<Recipe> findAll() {
        return new ArrayList<>(store.values());
    }

    /**
     * Finds a recipe by its ID.
     * @param id get the id.
     * @return return the id
     */
    public Optional<Recipe> findById(long id) {
        return Optional.ofNullable(store.get(id));
    }

    /**
     * Checks whether a recipe with the given ID exists.
     * @param id the id of the thing it checks
     * @return a true or false value
     */
    public boolean existsById(long id) {
        return store.containsKey(id);
    }

    /**
     * Saves a recipe. If the recipe has no ID, a new one is assigned automatically.
     * @param recipe the recipe it should take
     * @return the new version of the recipe.
     */
    public Recipe save(Recipe recipe) {
        if (recipe.getId() == 0) {
            try {
                Field f = Recipe.class.getDeclaredField("id");
                f.setAccessible(true);
                f.set(recipe, nextId++);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        store.put(recipe.getId(), recipe);
        return recipe;
    }

    /**
     * Deletes the recipe with the given ID.
     * @param id delete the id it finds.
     */
    public void deleteById(long id) {
        store.remove(id);
    }

    /**
     * Finds recipes whose name contains the given string, ignoring case.
     * @param name the name it should look for.
     * @return it returns every item with that name
     */
    public List<Recipe> findByNameContainingIgnoreCase(String name) {
        return store.values().stream()
                .filter(r -> r.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }
}
