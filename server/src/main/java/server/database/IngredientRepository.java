package server.database;

import commons.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    /**
     * Retrieves all ingredients sorted alphabetically by name.
     * Useful for the Ingredient Overview feature.
     *
     * @return A sorted list of all ingredients.
     */
    List<Ingredient> findAllByOrderByNameAsc();
}