package client.scenes;

import client.MyFXML;
import client.config.Config;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Ingredient;
import commons.RecipeIngredient;
import commons.Unit;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;


public class ShoppingListCtrl {

    @FXML
    private VBox ingredientListBox;

    private final ServerUtils serverUtils;
    private final Config config;
    private MyFXML fxml;

    /**
     * Constructor for ShoppingListCtrl.
     *
     * @param server the server utility used for network communication
     * @param config the config file to load/save shopping list ingredients
     */
    @Inject
    public ShoppingListCtrl(ServerUtils server, Config config) {
        this.serverUtils = server;
        this.config = config;
    }

    /**
     * called when the shopping list is opened
     */
    public void initialize(MyFXML fxml) {
        this.fxml = fxml;
        List<Ingredient> allIngredients = serverUtils.getIngredients();
        if (config.getShoppingList() == null) {
            // Temporary values
            this.config.setShoppingList(new ArrayList<>());
            this.config.getShoppingList().add(new RecipeIngredient(null, allIngredients.getFirst(), "", 1.0, Unit.GRAM));
            this.config.getShoppingList().add(new RecipeIngredient(null, allIngredients.getFirst(), "", 2.0, Unit.GRAM));
            this.config.getShoppingList().add(new RecipeIngredient(null, allIngredients.getFirst(), "", 3.0, Unit.LITER));
            this.config.getShoppingList().add(new RecipeIngredient(null, allIngredients.getLast(), "", 1.0, Unit.GRAM));
        }
        // Temp values
        loadShoppingList();
    }

    /**
     * loads the shopping List
     */
    private void loadShoppingList() {
        ingredientListBox.getChildren().clear();
        for (RecipeIngredient ri : this.config.getShoppingList()) {
            Pair<ShoppingListElementCtrl, Parent> item = fxml.load(ShoppingListElementCtrl.class,
                    "client", "scenes", "ShoppingListElement.fxml");
            item.getKey().initialize(ri, this::loadShoppingList);
            ingredientListBox.getChildren().add(item.getValue());
        }
    }

    /**
     * called when the user presses the + button
     */
    public void onAddShoppingListElement(){
        Pair<ShoppingListElementCtrl, Parent> item = fxml.load(ShoppingListElementCtrl.class,
                "client", "scenes", "ShoppingListElement.fxml");
        item.getKey().initialize(null, this::loadShoppingList);
        ingredientListBox.getChildren().add(item.getValue());
        item.getKey().startEditingFromCtrl();
    }

    /**
     * clears the shopping list
     */
    public void clear(){
        this.config.getShoppingList().clear();
        loadShoppingList();
    }

    /**
     * exports the shopping list to pdf
     */
    public void print(){
        System.out.println("TODO: Print List");
    }
}
