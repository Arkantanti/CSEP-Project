package server.database;

import commons.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    /**
     * Finds recipes where the name contains the given string (case-insensitive).
     * Useful for the "Search" feature.
     *
     * @param name The string to search for within recipe names.
     * @return A list of matching recipes.
     */
    List<Recipe> findByNameContainingIgnoreCase(String name);
}