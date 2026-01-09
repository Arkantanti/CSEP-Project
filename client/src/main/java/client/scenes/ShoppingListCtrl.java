package client.scenes;

import client.MyFXML;
import client.config.Config;
import client.config.ConfigManager;
import com.google.inject.Inject;
import commons.RecipeIngredient;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.util.ArrayList;


public class ShoppingListCtrl {

    @FXML
    private VBox ingredientListBox;

    private final Config config;
    private MyFXML fxml;

    /**
     * Constructor for ShoppingListCtrl.
     *
     * @param config the config file to load/save shopping list ingredients
     */
    @Inject
    public ShoppingListCtrl(Config config) {
        this.config = config;
    }

    /**
     * called when the shopping list is opened
     */
    public void initialize(MyFXML fxml) {
        this.fxml = fxml;
        if (config.getShoppingList() == null) {
            this.config.setShoppingList(new ArrayList<>());
        }
        loadShoppingList();
    }

    /**
     * loads the shopping List
     */
    private void loadShoppingList() {
        try {
            ConfigManager.save(config);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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
