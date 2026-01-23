package client.services;

import client.config.Config;
import client.config.ConfigManager;
import com.google.inject.Inject;
import commons.Ingredient;
import commons.IngredientCategory;
import commons.RecipeIngredient;
import client.model.ShoppingListItem;
import commons.Unit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShoppingListService {
    private final Config config;
    private final IngredientService ingredientService;

    /**
     * Injected constructor for ShoppingListService
     * @param config the Config file
     * @param ingredientService the service for fetching ingredients
     */
    @Inject
    public ShoppingListService(Config config, IngredientService ingredientService) {
        this.config = config;
        this.ingredientService = ingredientService;
    }

    /**
     * returns the current shoppinglist
     * @return the shopping list
     */
    public List<ShoppingListItem> getShoppingList(){
        List<ShoppingListItem> list = config.getShoppingList();
        if (list == null) {
            list = new ArrayList<>();
            config.setShoppingList(list);
        }
        return list;
    }

    /**
     * sets the shopping list to whatever the user wishes
     * @param shoppingList the list to set as shoppinglist
     */
    public void setShoppingList(List<ShoppingListItem> shoppingList){
        config.setShoppingList(shoppingList);
        this.saveChanges();
    }

    /**
     * saves the shopping list to the config file
     */
    public void saveChanges(){
        try {
            ConfigManager.save(config);
        }
        catch (IOException _) {}
    }

    /**
     * adds a text-only item to the shopping list
     * @param text the text description of the item
     */
    public void addTextItem(String text) {
        if (text == null || text.isBlank()) {
            return;
        }
        getShoppingList().add(new ShoppingListItem(text.trim()));
        saveChanges();
    }

    /**
     * adds an ingredient-based item to the shopping list
     * @param ingredientId the ID of the ingredient
     * @param ingredientName the name of the ingredient
     * @param informalUnit the informal unit (e.g. "a pinch"), may be null
     * @param amount the numeric amount
     * @param unit the unit
     * @param recipeName the name of the recipe this comes from (optional, may be null)
     */
    public void addIngredientItem(Long ingredientId,
                                  String ingredientName,
                                  String informalUnit,
                                  double amount,
                                  Unit unit,
                                  String recipeName) {
        getShoppingList().add(new ShoppingListItem(ingredientId,
                ingredientName, informalUnit, amount, unit, recipeName));
        saveChanges();
    }

    /**
     * adds a ShoppingListItem to the shoppingList
     * @param item the item to add
     */
    public void addItem(ShoppingListItem item) {
        getShoppingList().add(item);
    }

    /**
     * Adds a list of items to the shopping list
     * @param items The list of items to add
     * @param recipeName the name of the recipe
     */
    public void addItems(List<ShoppingListItem> items, String recipeName){
        for (ShoppingListItem item : items) {
            item.setRecipeName(recipeName);
            getShoppingList().add(item);
        }
        saveChanges();
    }

    /**
     * adds a list of ingredients to the shopping list, multiplied by some amount.
     * Each ingredient is added as a separate item with the recipe name included.
     * @param ingredients the list of ingredients to add
     * @param amount the multiplier to multiply them by
     * @param recipeName the name of the recipe these ingredients come from
     */
    public void addIngredients(List<RecipeIngredient> ingredients,
                               double amount, String recipeName){
        for (RecipeIngredient i : ingredients) {
            Ingredient ingredient = i.getIngredient();
            if (ingredient == null) {
                continue;
            }

            // get the ingredient ID and name
            long ingredientId = ingredient.getId();
            String ingredientName = ingredient.getName();
            
            // If ingredient name is null, try to fetch it from the service
            if (ingredientName == null || ingredientName.isBlank()) {
                List<Ingredient> allIngredients = ingredientService.getAllIngredients();
                if (allIngredients != null) {
                    Ingredient fullIngredient = allIngredients.stream()
                            .filter(ing -> ing.getId() == ingredientId)
                            .findFirst()
                            .orElse(null);
                    if (fullIngredient != null) {
                        ingredientName = fullIngredient.getName();
                    }
                }
            }

            if (i.getUnit() == Unit.CUSTOM && i.getInformalUnit() != null) {
                // For custom units, keep the informal unit as is (amount multiplier doesn't apply)
                getShoppingList().add(new ShoppingListItem(
                        ingredientId,
                        ingredientName,
                        i.getInformalUnit(),
                        i.getAmount(),
                        Unit.CUSTOM,
                        recipeName));
            }
            else {
                getShoppingList().add(new ShoppingListItem(
                        ingredientId,
                        ingredientName,
                        i.getInformalUnit(),
                        amount * i.getAmount(),
                        i.getUnit(),
                        recipeName));
            }
        }

        saveChanges();
    }

    /**
     * removes an item from the shopping list
     * @param item the item to remove
     */
    public void removeItem(ShoppingListItem item) {
        List<ShoppingListItem> list = getShoppingList();
        if (list != null) {
            list.remove(item);
            saveChanges();
        }
    }

    /**
     * clears the entire shopping list
     */
    public void clear() {
        getShoppingList().clear();
        saveChanges();
    }

    /**
     * Gets the category for a shopping list item.
     * @param item the shopping list item
     * @return the category of the item
     */
    public IngredientCategory getCategoryForItem(ShoppingListItem item) {
        if (item.isTextOnly() || item.getIngredientId() == null) {
            return IngredientCategory.UNCATEGORIZED;
        }
        
        // Use the stored ingredient ID to get the ingredient from the server
        Ingredient ingredient = ingredientService.getIngredientById(item.getIngredientId());
        
        if (ingredient == null || ingredient.getCategory() == null) {
            return IngredientCategory.UNCATEGORIZED;
        }
        return ingredient.getCategory();
    }
}
