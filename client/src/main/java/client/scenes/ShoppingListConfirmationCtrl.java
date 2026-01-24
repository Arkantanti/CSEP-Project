package client.scenes;

import client.MyFXML;
import client.services.ShoppingListService;
import com.google.inject.Inject;
import commons.RecipeIngredient;
import client.model.ShoppingListItem;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListConfirmationCtrl {
    @FXML
    private VBox ingredientListBox;

    private List<ShoppingListItem> items;
    private String recipeName;

    private MyFXML fxml;

    private final ShoppingListService shoppingListService;
    private final MainCtrl mainCtrl;

    private Stage confirmationStage;

    /**
     * injected constructor
     * @param shoppingListService the shoppinglistservice used when confirm is pressed
     */
    @Inject
    public ShoppingListConfirmationCtrl(
            ShoppingListService shoppingListService, MainCtrl mainCtrl) {
        this.shoppingListService = shoppingListService;
        this.mainCtrl = mainCtrl;
    }

    /**
     *  called when the shopping list confirmation menu is first opened
     * @param fxml the fxml loader
     */
    public void initialize(MyFXML fxml, Stage confirmationStage) {
        this.fxml = fxml;
        this.confirmationStage = confirmationStage;
    }

    /**
     * loads the confirmation window with a list of ingredients for the user to edit.
     * @param ingredients the list of ingredients to add
     * @param scalar a multiplier for the amount of each ingredient
     */
    public void loadList(List<RecipeIngredient> ingredients, double scalar, String recipeName) {
        items  = new ArrayList<>();
        this.recipeName = recipeName;
        for (RecipeIngredient ingredient : ingredients) {
            items.add(new ShoppingListItem(ingredient, scalar));
        }
        updateListBox();

    }

    /**
     * Called when the user presses the add (+) button
     */
    @FXML
    private void onAddElement(){
        Pair<ShoppingListElementCtrl, Parent> element = createListElement(null);
        element.getKey().startEditingFromCtrl();
    }

    /**
     * helper function to create&init shoppinglistElements
     * @param baseItem the item to create for
     * @return the created ShoppingListElementCtrl/Parent pair
     */
    private Pair<ShoppingListElementCtrl, Parent>
        createListElement(ShoppingListItem baseItem) {
        Pair<ShoppingListElementCtrl, Parent> item =
                fxml.load(ShoppingListElementCtrl.class, mainCtrl.getBundle(),
                "client", "scenes", "ShoppingListElement.fxml");
        item.getKey().initialize(baseItem,
                (ignored) -> {                        // onUpdate
                    updateListBox();
                    return null;
                },
                (ShoppingListItem itemToAdd) -> {    // onAddItem
                    items.add(itemToAdd);
                    return null;
                },
                (ShoppingListItem itemToRemove) -> {    // onDeleteItem
                    items.remove(itemToRemove);
                    return null;
                },
                false);
        ingredientListBox.getChildren().add(item.getValue());
        return item;
    }

    /**
     * loads creates the elements in the list.
     */
    private void updateListBox(){
        ingredientListBox.getChildren().clear();
        for (ShoppingListItem item : items) {
            createListElement(item);
        }
    }

    /**
     * Called when the user presses the "Add to Shopping List" button.
     * Adds all the elements in the current view to the shopping list
     */
    @FXML
    private void onConfirmPressed() {
        shoppingListService.addItems(items, recipeName);
        mainCtrl.reloadShoppingList();
        confirmationStage.close();
    }
}
