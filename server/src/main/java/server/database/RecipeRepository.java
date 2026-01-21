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
     */
    List<Recipe> findByNameContainingIgnoreCase(String name);

    /**
     * Searches for recipes by name, preparation steps, ingredients, OR tags.
     * Updated to check boolean flags (cheap, fast, vegan) if the query matches those keywords.
     */
    @Query("SELECT DISTINCT r FROM Recipe r " +
            "LEFT JOIN r.preparationSteps s " +
            "WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(s) LIKE LOWER(CONCAT('%', :query, '%')) " +
            // --- NEW: Check boolean flags if query matches the keyword ---
            "OR (LOWER(:query) = 'cheap' AND r.cheap = true) " +
            "OR (LOWER(:query) = 'fast' AND r.fast = true) " +
            "OR (LOWER(:query) = 'vegan' AND r.vegan = true) " +
            // -------------------------------------------------------------
            "OR r.id IN (" +
            "    SELECT ri.recipe.id FROM RecipeIngredient ri " +
            "    JOIN ri.ingredient i " +
            "    WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :query, '%'))" +
            ")")
    List<Recipe> search(@Param("query") String query);

    /**
     * Retrieves all recipe IDs from the database.
     * Used for efficient validation of favorites.
     */
    @Query("SELECT r.id FROM Recipe r")
    List<Long> findAllIds();
}