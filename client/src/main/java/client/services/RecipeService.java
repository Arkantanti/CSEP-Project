package client.services;

import client.utils.FavoritesManager;
import client.utils.ServerUtils;
import commons.Recipe;
import com.google.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeService {

    private final ServerUtils server;
    private final FavoritesManager favoritesManager;

    /**
     * Constructor for the RecipeService class.
     * @param server the connection to the server.
     * @param favoritesManager the favorites manager.
     */
    @Inject
    public RecipeService(ServerUtils server, FavoritesManager favoritesManager) {
        this.server = server;
        this.favoritesManager = favoritesManager;
    }

    /**
     * Gets all recipes, sorted alphabetically by name.
     */
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = server.getRecipes();
        if (recipes != null) {
            recipes.sort(Comparator.comparing(Recipe::getName));
        }
        return recipes;
    }

    /**
     * Searches for recipes, sorted alphabetically by name.
     */
    public List<Recipe> searchRecipes(String query) {
        List<Recipe> results = server.searchRecipes(query);
        if (results != null) {
            results.sort(Comparator.comparing(Recipe::getName));
        }
        return results;
    }

    /**
     * Gets only favorite recipes, sorted alphabetically by name.
     */
    public List<Recipe> getFavoriteRecipes() {
        List<Recipe> all = server.getRecipes();
        if (all == null) return List.of();

        return all.stream()
                .filter(r -> favoritesManager.isFavorite(r.getId()))
                .sorted(Comparator.comparing(Recipe::getName))
                .collect(Collectors.toList());
    }

    /**
     * Searches within favorite recipes, sorted alphabetically by name.
     */
    public List<Recipe> searchFavoriteRecipes(String query) {
        List<Recipe> results = server.searchRecipes(query);
        if (results == null) return List.of();

        return results.stream()
                .filter(r -> favoritesManager.isFavorite(r.getId()))
                .sorted(Comparator.comparing(Recipe::getName))
                .collect(Collectors.toList());
    }
}