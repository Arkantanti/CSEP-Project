package server.database;

import commons.Recipe;

import java.util.List;
import java.util.Optional;

public interface SimpleRecipeRepository {
        List<Recipe> findAll();
        Optional<Recipe> findById(Long id);
        boolean existsById(Long id);
        Recipe save(Recipe recipe);
        void deleteById(Long id);

}
