package client.scenes;

import client.MyFXML;
import client.services.ShoppingListService;
import com.google.inject.Inject;
import client.model.ShoppingListItem;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;


public class ShoppingListCtrl {

    @FXML
    private VBox ingredientListBox;

    @FXML
    private Button addIngredientButton;

    @FXML
    private Button addTextButton;

    @FXML
    private HBox addModeToggle;

    private enum AddMode {
        INGREDIENT,
        TEXT
    }

    private AddMode currentAddMode = AddMode.INGREDIENT;

    private final ShoppingListService shoppingListService;
    private MyFXML fxml;

    /**
     * Constructor for ShoppingListCtrl.
     *
     * @param shoppingListService the service for managing the shopping list
     */
    @Inject
    public ShoppingListCtrl(ShoppingListService shoppingListService) {
        this.shoppingListService = shoppingListService;
    }

    /**
     * called when the shopping list is opened
     */
    public void initialize(MyFXML fxml) {
        this.fxml = fxml;
        setupAddModeButtons();
        loadShoppingList();
    }

    /**
     * Sets up the add mode toggle buttons
     */
    private void setupAddModeButtons() {
        addIngredientButton.setOnAction(e -> switchAddMode(AddMode.INGREDIENT));
        addTextButton.setOnAction(e -> switchAddMode(AddMode.TEXT));
        updateAddModeButtons();
    }

    /**
     * Switches the add mode and updates button appearance
     */
    private void switchAddMode(AddMode mode) {
        currentAddMode = mode;
        updateAddModeButtons();
    }

    /**
     * Updates the visual state of the add mode toggle buttons
     */
    private void updateAddModeButtons() {
        boolean ingredientMode = (currentAddMode == AddMode.INGREDIENT);
        if (ingredientMode) {
            addIngredientButton.getStyleClass().add("active");
            addTextButton.getStyleClass().remove("active");
        } else {
            addTextButton.getStyleClass().add("active");
            addIngredientButton.getStyleClass().remove("active");
        }
    }

    /**
     * loads the shopping List
     */
    public void loadShoppingList() {
        ingredientListBox.getChildren().clear();
        for (ShoppingListItem item : shoppingListService.getShoppingList()) {
            Pair<ShoppingListElementCtrl, Parent> element = fxml.load(ShoppingListElementCtrl.class,
                    "client", "scenes", "ShoppingListElement.fxml");
            element.getKey().initialize(item, this::loadShoppingList);
            ingredientListBox.getChildren().add(element.getValue());
        }
    }

    /**
     * called when the user presses the add button
     */
    public void onAddShoppingListElement(){
        Pair<ShoppingListElementCtrl, Parent> item = fxml.load(ShoppingListElementCtrl.class,
                "client", "scenes", "ShoppingListElement.fxml");
        boolean isTextMode = (currentAddMode == AddMode.TEXT);
        item.getKey().initialize(null, this::loadShoppingList, isTextMode);
        ingredientListBox.getChildren().add(item.getValue());
        item.getKey().startEditingFromCtrl();
    }

    /**
     * clears the shopping list
     */
    public void clear(){
        shoppingListService.clear();
        loadShoppingList();
    }

    /**
     * exports the shopping list to pdf
     */
    public void print(){
        System.out.println("TODO: Print List");
    }
}
