package client.services;

import client.utils.FavoritesManager;
import client.utils.ServerUtils;
import commons.Language;
import commons.Recipe;
import com.google.inject.Inject;
import java.util.Comparator;
import java.util.List;
import static java.util.stream.Collectors.toList;

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
     * Gets all recipes related to the language.
     */
    public List<Recipe> getAllRecipesWithLanguage(boolean english, boolean polish, boolean dutch){

        List<Recipe> recipes =
                new java.util.
                        ArrayList<>(server.getRecipes().stream().filter((Recipe recipeItem) -> {
                            if (english && dutch && polish) {
                                return true;
                            }
                            if (english && dutch) {
                                return (recipeItem.
                                        getLanguage().equals(Language.English)
                                        || recipeItem.getLanguage().equals(Language.Dutch));
                            }
                            if (english && polish) {
                                return (recipeItem.
                                        getLanguage().equals(Language.English)
                                        || recipeItem.getLanguage().equals(Language.Polish));
                            }
                            if (dutch && polish) {
                                return (recipeItem.
                                        getLanguage().equals(Language.Dutch)
                                        || recipeItem.getLanguage().equals(Language.Polish));
                            }
                            if (english) {
                                return recipeItem.getLanguage().equals(Language.English);
                            }
                            if (polish) {
                                return recipeItem.getLanguage().equals(Language.Polish);
                            }
                            if (dutch) {
                                return recipeItem.getLanguage().equals(Language.Dutch);
                            }
                            return false;
                        })
                .toList());

        recipes.sort(Comparator.comparing(Recipe::getName));

        return recipes;
    }

    /**
     * Searches for recipes, sorted alphabetically by name.
     */
    public List<Recipe> searchRecipes(String query,
                                      boolean english, boolean polish, boolean dutch) {
        List<Recipe> results = server.searchRecipes(query);
        if (results == null) {
            return null;
        }

        results.sort(Comparator.comparing(Recipe::getName));
        return filterByLanguage(results, english, polish, dutch);
    }

    /**
     * Filters the list of recipes based on the selected languages.
     * @param results The list of recipes to filter.
     * @param english Whether to include English recipes.
     * @param polish Whether to include Polish recipes.
     * @param dutch Whether to include Dutch recipes.
     * @return A list of filtered recipes.
     */
    private List<Recipe> filterByLanguage(List<Recipe> results,
                                          boolean english, boolean polish, boolean dutch) {
        return results.stream()
                .filter(recipe -> isLanguageAllowed(recipe, english, polish, dutch))
                .toList();
    }

    /**
     * Checks if a specific recipe is allowed based on the language flags.
     * @param recipe The recipe to check.
     * @param english Whether English is allowed.
     * @param polish Whether Polish is allowed.
     * @param dutch Whether Dutch is allowed.
     * @return true if the recipe's language matches a selected flag.
     */
    private boolean isLanguageAllowed(Recipe recipe,
                                      boolean english, boolean polish, boolean dutch) {
        Language lang = recipe.getLanguage();
        if (lang == null) {
            return false;
        }

        return (english && lang == Language.English)
                || (dutch && lang == Language.Dutch)
                || (polish && lang == Language.Polish);
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
                .collect(toList());
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
                .collect(toList());
    }

    /**
     * function for checking if the name from the recipe is already in the list of all the recipes
     * @param recipeList the list of all the recipes where the names need to be checked
     * @param s the name of the recipe
     * @return true or false depending on if the list contains the recipe name
     */
    public boolean recipeNameChecker(List<Recipe> recipeList, String s, Recipe recipeOne){
        if(recipeOne != null){
            for(Recipe recipeName : recipeList){
                if (recipeName.getName().trim().equalsIgnoreCase(s.trim()) &&
                        (recipeOne.getId() != recipeName.getId())) {
                    return true;
                }

            }
        } else{
            for(Recipe recipeName : recipeList){
                if (recipeName.getName().trim().equalsIgnoreCase(s.trim())) {
                    return true;
                }

            }
        }
        return false;
    }
}