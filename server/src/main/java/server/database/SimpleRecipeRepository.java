package server.database;

import commons.Recipe;

import java.util.List;
import java.util.Optional;

public interface SimpleRecipeRepository {
    /**
     *
     * @return
     */
    List<Recipe> findAll();

    /**
     *
     * @param id
     * @return
     */
    Optional<Recipe> findById(Long id);

    /**
     *
     * @param id
     * @return
     */
    boolean existsById(Long id);

    /**
     *
     * @param recipe
     * @return
     */
    Recipe save(Recipe recipe);

    /**
     *
     * @param id
     */
    void deleteById(Long id);

}
