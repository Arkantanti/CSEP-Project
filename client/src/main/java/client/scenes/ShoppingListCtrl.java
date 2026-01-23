package client.scenes;

import client.MyFXML;
import client.services.ShoppingListService;
import client.utils.Printer;
import com.google.inject.Inject;
import client.model.ShoppingListItem;
import commons.IngredientCategory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.util.ResourceBundle;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class ShoppingListCtrl {

    @FXML
    private VBox ingredientListBox;

    @FXML
    private Button addIngredientButton;

    @FXML
    private Button addTextButton;

    @FXML
    private HBox addModeToggle;

    @FXML
    private Button toggleGroupingButton;

    private enum AddMode {
        INGREDIENT,
        TEXT
    }

    private AddMode currentAddMode = AddMode.INGREDIENT;
    private boolean groupedView = true;

    private final ShoppingListService shoppingListService;
    private final MainCtrl mainCtrl;
    private final Printer printer;
    private MyFXML fxml;

    private ResourceBundle bundle;


    /**
     * Constructor for ShoppingListCtrl.
     *
     * @param shoppingListService the service for managing the shopping list
     * @param mainCtrl the main controller for scene navigation
     * @param printer the printer utility for PDF generation
     */
    @Inject
    public ShoppingListCtrl(ShoppingListService shoppingListService,
                            MainCtrl mainCtrl, Printer printer) {
        this.shoppingListService = shoppingListService;
        this.mainCtrl = mainCtrl;
        this.printer = printer;
    }

    /**
     * called when the shopping list is opened
     */
    public void initialize(MyFXML fxml, ResourceBundle bundle) {
        this.bundle = bundle;
        this.fxml = fxml;
        setupAddModeButtons();
        updateGroupingButtonStyle();
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
        if (groupedView) {
            loadShoppingListGrouped();
        } else {
            loadShoppingListFlat();
        }
    }

    /**
     * loads the shopping list in grouped mode by category
     */
    private void loadShoppingListGrouped() {
        List<ShoppingListItem> allItems = shoppingListService.getShoppingList();

        for (IngredientCategory category : IngredientCategory.values()) {
            List<ShoppingListItem> categoryItems = new ArrayList<>();
            for (ShoppingListItem item : allItems) {
                IngredientCategory itemCategory = shoppingListService.getCategoryForItem(item);
                if (itemCategory == category) {
                    categoryItems.add(item);
                }
            }

            if (!categoryItems.isEmpty()) {
                Pair<ShoppingListCategorySectionCtrl, Parent> section = fxml.load(
                        ShoppingListCategorySectionCtrl.class, bundle,
                        "client", "scenes", "ShoppingListCategorySection.fxml");
                section.getKey().initialize(category, categoryItems, fxml, mainCtrl.getBundle(),
                        (_) -> {
                            loadShoppingList();
                            shoppingListService.saveChanges();
                            return null;
                        },
                        (ShoppingListItem itemToAdd) -> {
                            shoppingListService.addItem(itemToAdd);
                            shoppingListService.saveChanges();
                            return null;
                        },
                        (ShoppingListItem itemToRemove) -> {
                            shoppingListService.removeItem(itemToRemove);
                            shoppingListService.saveChanges();
                            return null;
                        },
                        currentAddMode == AddMode.TEXT);
                ingredientListBox.getChildren().add(section.getValue());
            }
        }
    }

    /**
     * loads the shopping list in flat mode
     */
    private void loadShoppingListFlat() {
        for (ShoppingListItem item : shoppingListService.getShoppingList()) {
            createListElement(item);
        }
    }

    /**
     * called when the user presses the add button
     */
    public void onAddShoppingListElement(){
        Pair<ShoppingListElementCtrl, Parent> element = createListElement(null);
        element.getKey().startEditingFromCtrl();
    }

    /**
     * helper function to create&init shoppinglistElements
     * @param baseItem the item to create for
     * @return the created ShoppingListElementCtrl/Parent pair
     */
    private Pair<ShoppingListElementCtrl, Parent> createListElement(ShoppingListItem baseItem) {
        Pair<ShoppingListElementCtrl, Parent> item =
                fxml.load(ShoppingListElementCtrl.class, bundle,
                "client", "scenes", "ShoppingListElement.fxml");
        boolean isTextMode = baseItem == null ?
                (currentAddMode == AddMode.TEXT) : baseItem.isTextOnly();
        item.getKey().initialize(baseItem,
                (_) -> {                        // onUpdate
                    loadShoppingList();
                    shoppingListService.saveChanges();
                    return null;
                },
                (ShoppingListItem itemToAdd) -> {    // onAddItem
                    shoppingListService.addItem(itemToAdd);
                    shoppingListService.saveChanges();
                    return null;
                },
                (ShoppingListItem itemToRemove) -> {    // onDeleteItem
                    shoppingListService.removeItem(itemToRemove);
                    return null;
                },
                isTextMode);
        ingredientListBox.getChildren().add(item.getValue());
        return item;
    }

    /**
     * clears the shopping list
     */
    public void clear(){
        shoppingListService.clear();
        loadShoppingList();
    }

    /**
     * toggles between grouped and flat view
     */
    @FXML
    public void toggleGrouping() {
        groupedView = !groupedView;
        updateGroupingButtonStyle();
        loadShoppingList();
    }

    /**
     * Updates the visual style of the grouping toggle button based on current state
     */
    private void updateGroupingButtonStyle() {
        if (groupedView) {
            toggleGroupingButton.getStyleClass().add("active");
            toggleGroupingButton.setText("Grouped");
        } else {
            toggleGroupingButton.getStyleClass().remove("active");
            toggleGroupingButton.setText("Group");
        }
    }

    /**
     * exports the printable shopping list to pdf
     */
    public void print(){
        Path path = mainCtrl.showFileChooser("ShoppingList.pdf");
        if (path == null) {
            return;
        }
        try {
            String markdown = printer.createShoppingListOutputString(
                    shoppingListService.getShoppingList(), shoppingListService);
            printer.markdownToPDF(path, markdown);
        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Saving error");
                alert.setHeaderText("Could not save the shopping list");
                alert.showAndWait();
            });
        }
    }
}
