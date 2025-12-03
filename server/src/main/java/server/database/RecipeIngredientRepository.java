package server.database;

import commons.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {

    /**
     *Retrieves all {@link RecipeIngredient} entries that belong to a specific recipe.
     *
     * @param recipeId the ID of the recipe whose ingredient mappings should be returned;
     * @return a list of all {@link RecipeIngredient} objects linked to the specified recipe;
     */
    @Query("SELECT DISTINCT ri FROM RecipeIngredient ri " +
            "JOIN FETCH ri.ingredient i WHERE ri.recipe.id = :recipeId")
    List<RecipeIngredient> findByRecipeId(@Param("recipeId") long recipeId);
}