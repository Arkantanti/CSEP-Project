package client.scenes;

import client.MyFXML;
import client.model.ShoppingListItem;
import com.google.inject.Inject;
import commons.IngredientCategory;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;

public class ShoppingListCategorySectionCtrl {

    @FXML
    private Button toggleButton;

    @FXML
    private Label categoryLabel;

    @FXML
    private VBox itemsContainer;

    private IngredientCategory category;
    private boolean isExpanded = true;
    private List<ShoppingListItem> items;
    private MyFXML fxml;
    private Function<Void, Void> onUpdate;
    private Function<ShoppingListItem, Void> onAddItem;
    private Function<ShoppingListItem, Void> onDeleteItem;

    private ResourceBundle bundle;


    /**
     * constructor for ShoppingListCategorySectionCtrl.
     */
    @Inject
    public ShoppingListCategorySectionCtrl() {
    }

    /**
     * Initializes the category sections
     * @param category the category for this section
     * @param items the items in this category
     * @param fxml the FXML loader
     * @param bundle ResourceBundle loader
     * @param onUpdate callback when items are updated
     * @param onAddItem callback when an item is added
     * @param onDeleteItem callback when an item is deleted
     */
    public void initialize(IngredientCategory category,
                           List<ShoppingListItem> items,
                           MyFXML fxml,
                           ResourceBundle bundle,
                           Function<Void, Void> onUpdate,
                           Function<ShoppingListItem, Void> onAddItem,
                           Function<ShoppingListItem, Void> onDeleteItem) {
        this.category = category;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.fxml = fxml;
        this.bundle = bundle;
        this.onUpdate = onUpdate;
        this.onAddItem = onAddItem;
        this.onDeleteItem = onDeleteItem;

        String categoryName = category.name();
        categoryLabel.setText(categoryName.charAt(0) + categoryName.substring(1).toLowerCase());

        loadItems();
    }

    /**
     * loads the items into the container
     */
    private void loadItems() {
        itemsContainer.getChildren().clear();
        for (ShoppingListItem item : items) {
            createListElement(item);
        }
    }

    /**
     * creates a list element for a shopping list item
     * @param baseItem the item for which an element is created
     * @return the created ShoppingListElementCtrl/Parent pair
     */
    private Pair<ShoppingListElementCtrl, Parent> createListElement(ShoppingListItem baseItem) {
        Pair<ShoppingListElementCtrl, Parent> element =
                fxml.load(ShoppingListElementCtrl.class, bundle,
                        "client", "scenes", "ShoppingListElement.fxml");
        boolean isTextMode = baseItem.isTextOnly();
        element.getKey().initialize(baseItem,
                // Replaced '_' with 'ignored'
                (Void ignored) -> {
                    loadItems();
                    onUpdate.apply(null);
                    return null;
                },
                (ShoppingListItem itemToAdd) -> {
                    onAddItem.apply(itemToAdd);
                    return null;
                },
                (ShoppingListItem itemToRemove) -> {
                    items.remove(itemToRemove);
                    onDeleteItem.apply(itemToRemove);
                    return null;
                },
                isTextMode);
        itemsContainer.getChildren().add(element.getValue());
        return element;
    }

    /**
     * handles the toggle button for collapsing/expanding
     */
    @FXML
    private void onToggleClicked() {
        isExpanded = !isExpanded;
        itemsContainer.setVisible(isExpanded);
        itemsContainer.setManaged(isExpanded);
        toggleButton.setText(isExpanded ? "▼" : "▶");
    }

    /**
     * gets the category for this section
     * @return the category
     */
    public IngredientCategory getCategory() {
        return category;
    }

    /**
     * refreshes the items contained in this section .
     */
    public void refreshItems(List<ShoppingListItem> newItems) {
        this.items = new ArrayList<>(newItems);
        loadItems();
    }
}