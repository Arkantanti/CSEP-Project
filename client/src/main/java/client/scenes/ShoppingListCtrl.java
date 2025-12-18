package client.scenes;

import client.utils.Printer;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Ingredient;
import commons.RecipeIngredient;
import commons.Unit;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;


public class ShoppingListCtrl {

    @FXML
    private VBox ingredientListBox;

    private List<RecipeIngredient> ingredientsList;

    private final ServerUtils serverUtils;

    /**
     * Constructor for ShoppingListCtrl.
     *
     * @param server the server utility used for network communication
     */
    @Inject
    public ShoppingListCtrl(ServerUtils server) {
        this.serverUtils = server;
    }

    /**
     * called when the shopping list is opened
     */
    public void initialize() {
        ingredientsList = new ArrayList<>();
        List<Ingredient> allIngredients = serverUtils.getIngredients();
        ingredientsList.add(new RecipeIngredient(null, allIngredients.getFirst(), "", 1.0, Unit.GRAM));
        ingredientsList.add(new RecipeIngredient(null, allIngredients.getFirst(), "", 2.0, Unit.GRAM));
        ingredientsList.add(new RecipeIngredient(null, allIngredients.getFirst(), "", 3.0, Unit.LITER));
        ingredientsList.add(new RecipeIngredient(null, allIngredients.getLast(), "", 1.0, Unit.GRAM));
    }

    /**
     * clears the shopping list
     */
    public void clear(){

    }

    /**
     * exports the shopping list to pdf
     */
    public void print(){

    }
}
