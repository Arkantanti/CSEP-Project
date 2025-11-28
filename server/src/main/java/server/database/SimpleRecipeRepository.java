package server.database;

import commons.Recipe;

import java.util.List;
import java.util.Optional;

public interface SimpleRecipeRepository {
    /**
     * looks for everything in the database.
     * @return all the items
     */
    List<Recipe> findAll();

    /**
     * function to find items with that id
     * @param id checks for the id it needs to have
     * @return the items with that id
     */
    Optional<Recipe> findById(Long id);

    /**
     * checks if that id exists
     * @param id the id it checks for
     * @return true or false depending on if the database has the id.
     */
    boolean existsById(Long id);

    /**
     * function to save the new value
     * @param recipe the new recipe it needs to save
     * @return saves the new value of the recipe to the database
     */
    Recipe save(Recipe recipe);

    /**
     * function to delete the id from the database
     * @param id the id of the item it should delete from the database.
     */
    void deleteById(Long id);

}
