package server.database;

import commons.Recipe;
import java.lang.reflect.Field;
import java.util.*;

public class InMemoryRecipeRepository {

    private final Map<Long, Recipe> store = new HashMap<>();
    private long nextId = 1;

    public List<Recipe> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Recipe> findById(long id) {
        return Optional.ofNullable(store.get(id));
    }

    public boolean existsById(long id) {
        return store.containsKey(id);
    }

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

    public void deleteById(long id) {
        store.remove(id);
    }

    public List<Recipe> findByNameContainingIgnoreCase(String name) {
        return store.values().stream()
                .filter(r -> r.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }
}
