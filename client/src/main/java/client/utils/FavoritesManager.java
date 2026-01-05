package client.utils;

import client.config.Config;
import client.config.ConfigManager;
import com.google.inject.Inject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manager for handling favorite recipes stored in the local config file.
 */
public class FavoritesManager {

    private final Config config;
    private final ServerUtils serverUtils;

    /**
     * Constructs a new FavoritesManager.
     *
     * @param config the config object containing the user specific configuration
     * @param serverUtils the object for communicating with the server
     */
    @Inject
    public FavoritesManager(Config config, ServerUtils serverUtils) {
        this.config = config;
        this.serverUtils = serverUtils;
    }

    /**
     * Adds a recipe to favorites by its ID.
     *
     * @param recipeId the ID of the recipe to add
     * @throws IOException if saving the config fails
     */
    public void addFavorite(long recipeId) throws IOException {
        List<Long> favorites = config.getFavoriteRecipesIds();
        
        if (!favorites.contains(recipeId)) {
            favorites.add(recipeId);
            ConfigManager.save(config);
        }
    }

    /**
     * Removes a recipe from favorites using its ID.
     *
     * @param recipeId the ID of the recipe to remove
     * @throws IOException if saving the config fails
     */
    public void removeFavorite(long recipeId) throws IOException {
        List<Long> favorites = config.getFavoriteRecipesIds();
        if (favorites != null && favorites.contains(recipeId)) {
            favorites.remove(recipeId);
            ConfigManager.save(config);
        }
    }

    /**
     * Checks if a recipe is favorited.
     *
     * @param recipeId the ID of the recipe to check
     * @return true if the recipe is a favorite.
     */
    public boolean isFavorite(long recipeId) {
        List<Long> favorites = config.getFavoriteRecipesIds();
        return favorites != null && favorites.contains(recipeId);
    }

    /**
     * Validates favorite recipe IDs and removes invalid ones.
     *
     * @return list of recipe IDs that were removed because they no longer exist
     * @throws IOException if saving the config fails
     */
    public List<Long> validate() throws IOException {
        List<Long> favorites = config.getFavoriteRecipesIds();
        if (favorites == null) {
            throw new IllegalStateException("Favorite recipes list is not initialized");
        }
        if (favorites.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> removedIds = new ArrayList<>();
        List<Long> validIds = new ArrayList<>();

        for (Long recipeId : favorites) {
            if (serverUtils.getRecipeById(recipeId) != null) {
                validIds.add(recipeId);
            } else {
                removedIds.add(recipeId);
            }
        }

        if (!removedIds.isEmpty()) {
            config.setFavoriteRecipesIds(validIds);
            ConfigManager.save(config);
        }

        return removedIds;
    }
}

