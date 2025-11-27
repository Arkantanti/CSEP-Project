package server.database;

import commons.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
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
    List<RecipeIngredient> findByRecipeId(long recipeId);
}