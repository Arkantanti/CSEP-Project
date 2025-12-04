package server.database;

import commons.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Searches for recipes by name, ingredient name, or preparation steps.
     *
     * @param query the search string
     * @return a list of matching recipes
     */
    @Query("SELECT DISTINCT r FROM Recipe r " +
            "LEFT JOIN r.ingredients ri " +
            "LEFT JOIN ri.ingredient i " +
            "LEFT JOIN r.preparationSteps s " +
            "WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(i.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(s) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Recipe> search(@Param("query") String query);
}