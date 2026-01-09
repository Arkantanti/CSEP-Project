package client.services;

import client.config.Config;
import client.config.ConfigManager;
import com.google.inject.Inject;
import commons.RecipeIngredient;
import commons.Unit;
import org.jvnet.hk2.annotations.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ShoppingListService {
    private final Config config;

    /**
     * Injected constructor for ShoppingListService
     * @param config the Config file
     */
    @Inject
    public ShoppingListService(Config config) {
        this.config = config;
    }

    /**
     * returns the current shoppinglist
     * @return the shopping list
     */
    public List<RecipeIngredient> getShoppingList(){
        return config.getShoppingList();
    }

    /**
     * sets the shopping list to whatever the user wishes
     * @param shoppingList the list to set as shoppinglist
     */
    public void setShoppingList(List<RecipeIngredient> shoppingList){
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
     * adds a list of ingredients to the shopping list, multiplied by some amount
     * @param ingredients the list of ingredients to add
     * @param amount the multiplier to multiply them by
     */
    public void addIngredients(List<RecipeIngredient> ingredients, double amount){
        for (RecipeIngredient i : ingredients) {
            if (i.getUnit() == Unit.CUSTOM && i.getInformalUnit() != null) {
                String v = i.getInformalUnit();
                config.getShoppingList().add(new RecipeIngredient(null, i.getIngredient(),
                        v + "x"+amount, 0.0, Unit.CUSTOM));
            }
            else{
                config.getShoppingList().add(new RecipeIngredient(null, i.getIngredient(),
                        null, amount*i.getAmount(), i.getUnit()));
                // TODO: merge similar items in the shopping list?
            }
        }

        saveChanges();
    }

}
