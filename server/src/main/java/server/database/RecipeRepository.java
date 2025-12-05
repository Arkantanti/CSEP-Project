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
     * Searches for recipes by name, preparation steps, OR ingredients.
     * * Since Recipe does not have a list of ingredients, we use a subquery
     * to find recipes referenced by matching RecipeIngredients.
     *
     * @param query the search string
     * @return a list of matching recipes
     */
    @Query("SELECT DISTINCT r FROM Recipe r " +
            "LEFT JOIN r.preparationSteps s " +
            "WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(s) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR r.id IN (" +
            "    SELECT ri.recipe.id FROM RecipeIngredient ri " +
            "    JOIN ri.ingredient i " +
            "    WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :query, '%'))" +
            ")")
    List<Recipe> search(@Param("query") String query);
}