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
    private final ConfigManager configManager;

    /**
     * Constructs a new FavoritesManager.
     *
     * @param config the config object containing the user specific configuration
     * @param serverUtils the object for communicating with the server
     * @param configManager the manager for saving configuration
     */
    @Inject
    public FavoritesManager(Config config, ServerUtils serverUtils, ConfigManager configManager) {
        this.config = config;
        this.serverUtils = serverUtils;
        this.configManager = configManager;
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
            configManager.save(config);
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
            configManager.save(config);
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
     * optimized to fetch all IDs from server once, and iterates over a copy
     * to avoid ConcurrentModificationException.
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

        // Fetch all existing recipe IDs from the server in one go
        List<Long> allServerIds = serverUtils.getAllRecipeIds();

        // If server is unreachable, we cannot validate. Return empty to avoid accidental deletion.
        if (allServerIds == null) {
            return new ArrayList<>();
        }

        List<Long> removedIds = new ArrayList<>();

        // Iterate over a COPY of the list to avoid ConcurrentModificationException
        // if the user interacts with the UI while this thread is running.
        List<Long> favoritesCopy = new ArrayList<>(favorites);

        for (Long recipeId : favoritesCopy) {
            if (!allServerIds.contains(recipeId)) {
                removedIds.add(recipeId);
            }
        }

        if (!removedIds.isEmpty()) {
            // Remove the invalid IDs from the LIVE list
            favorites.removeAll(removedIds);
            configManager.save(config);
        }

        return removedIds;
    }
}