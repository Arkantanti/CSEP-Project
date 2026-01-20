package client.services;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Ingredient;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class IngredientService {

    private final ServerUtils server;

    /**
     * inject the service class
     * @param server to the server
     */
    @Inject
    public IngredientService(ServerUtils server) {
        this.server = server;
    }

    /**
     * Gets all ingredients, sorted alphabetically by name.
     */
    public List<Ingredient> getAllIngredients() {
        List<Ingredient> ingredients = server.getIngredients();
        if (ingredients != null) {
            ingredients.sort(Comparator.comparing(Ingredient::getName));
        }
        return ingredients;
    }

    /**
     * Gets a specific ingredient by id, null if non-existant
     * @param id the id of the target ingredient
     * @return An ingredient with the requested id, else null
     */
    public Ingredient getIngredientById(long id) {
        Ingredient ingredient = server.getIngredientById(id);
        if (ingredient != null) {
            return ingredient;
        }
        return server.getIngredients().stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }

    /**
     * Searches ingredients (client-side filtering), sorted alphabetically.
     */
    public List<Ingredient> searchIngredients(String query) {
        List<Ingredient> all = getAllIngredients();
        if (all == null || query == null) return List.of();

        String lowerQuery = query.toLowerCase();
        return all.stream()
                .filter(i -> i.getName().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }
}