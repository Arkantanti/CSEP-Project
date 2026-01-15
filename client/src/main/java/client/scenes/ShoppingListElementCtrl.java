package client.scenes;

import client.services.IngredientService;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Ingredient;
import commons.ShoppingListItem;
import commons.Unit;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.function.Function;

public class ShoppingListElementCtrl {
    @FXML
    private HBox defaultView;

    @FXML
    private Label textLabel;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private HBox editView;

    @FXML
    private TextField amountField;

    @FXML
    private ComboBox<Unit> unitComboBox;

    @FXML
    private ComboBox<Ingredient> ingredientComboBox;

    @FXML
    private Button confirmButton;

    @FXML
    private Button cancelButton;

    private ShoppingListItem shoppingListItem;
    private boolean isTextMode;

    private Function<Void, Void> onUpdate;
    private Function<ShoppingListItem, Void> onDeleteIngredient;
    private Function<ShoppingListItem, Void> onAddIngredient;

    private final ServerUtils serverUtils;
    private final IngredientService ingredientService;

    /**
     * constructor to be injected
     * @param serverUtils serverutils to load/delete/edit ingredients
     */
    @Inject
    public ShoppingListElementCtrl(ServerUtils serverUtils, IngredientService ingredientService) {
        this.serverUtils = serverUtils;
        this.ingredientService = ingredientService;
    }

    /**
     * initializes the ShoppingElementCtrl for an existing item
     * @param shoppingListItem A shopping list item
     * @param onUpdate A function that is called whenever (an item in) the list has changed
     * @param onAddIngredient A function to call when adding a new element to the list
     * @param onDeleteIngredient A function to call when a specific element should be removed from the list
     */
    public void initialize(ShoppingListItem shoppingListItem,
                           Function<Void, Void> onUpdate,
                           Function<ShoppingListItem, Void> onAddIngredient,
                           Function<ShoppingListItem, Void> onDeleteIngredient) {
        initializeInternal(shoppingListItem, onUpdate, onAddIngredient, onDeleteIngredient, shoppingListItem.isTextOnly());
    }

    /**
     * initializes the ShoppingElementCtrl for a new item
     * @param shoppingListItem A shopping list item (must be null for new items)
     * @param onUpdate A function that is called whenever (an item in) the list has changed
     * @param onAddIngredient A function to call when adding a new element to the list
     * @param onDeleteIngredient A function to call when a specific element should be removed from the list
     * @param isTextMode true if this should be a text item
     */
    public void initialize(ShoppingListItem shoppingListItem,
                           Function<Void, Void> onUpdate,
                           Function<ShoppingListItem, Void> onAddIngredient,
                           Function<ShoppingListItem, Void> onDeleteIngredient,
                           boolean isTextMode) {
        initializeInternal(shoppingListItem, onUpdate, onAddIngredient, onDeleteIngredient, isTextMode);
    }

    /**
     * Internal initialization method
     */
    private void initializeInternal(ShoppingListItem shoppingListItem,
                                    Function<Void, Void> onUpdate,
                                    Function<ShoppingListItem, Void> onAddIngredient,
                                    Function<ShoppingListItem, Void> onDeleteIngredient,
                                    boolean isTextMode) {
        this.shoppingListItem = shoppingListItem;
        this.onUpdate = onUpdate;
        this.onAddIngredient = onAddIngredient;
        this.onDeleteIngredient = onDeleteIngredient;
        this.isTextMode = isTextMode;

        defaultView.setVisible(true);
        defaultView.setManaged(true);

        editView.setVisible(false);
        editView.setManaged(false);

        if (shoppingListItem != null) {
            if (!shoppingListItem.isTextOnly()) { // on opening, ingredientName would be Null for some reason.
                Ingredient i = ingredientService.getIngredientById(shoppingListItem.getIngredientId());
                if (i != null) {
                    shoppingListItem.setIngredientName(i.getName());
                }
            }
            textLabel.setText(shoppingListItem.formatItem());
        }
        textLabel.setVisible(true);
        textLabel.setManaged(true);

        setupIngredientComboBox();
    }

    /**
     * Sets up the ingredient combo box cell factories
     */
    private void setupIngredientComboBox() {
        ingredientComboBox.setButtonCell(new ListCell<Ingredient>() {
            @Override
            protected void updateItem(Ingredient item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getName());
            }
        });

        ingredientComboBox.setCellFactory(list -> new ListCell<Ingredient>() {
            @Override
            protected void updateItem(Ingredient item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getName());
            }
        });
    }

    /**
     * starts editing the shopping list item
     */
    @FXML
    private void onEditClicked() {
        enterEditMode();

        if (shoppingListItem == null) {
            return;
        }

        if (isTextMode) {
            amountField.setText(shoppingListItem.getText());
            updateUIForTextMode();
        } else {
            // load data to show
            if (shoppingListItem.getUnit() != Unit.CUSTOM || shoppingListItem.getInformalUnit() == null) {
                amountField.setText(String.valueOf(shoppingListItem.getAmount()));
            }
            else {
                amountField.setText(shoppingListItem.getInformalUnit());
            }
            updateUIForIngredientMode();

            // ingredient dropdown
            List<Ingredient> ingredients = serverUtils.getIngredients();
            ingredientComboBox.getItems().setAll(ingredients);
            if (shoppingListItem.getIngredientId() != null) {
                Ingredient matchingIngredient = null;
                for (Ingredient ing : ingredients) {
                    if (ing.getId() == shoppingListItem.getIngredientId()) {
                        matchingIngredient = ing;
                        break;
                    }
                }
                if (matchingIngredient != null) {
                    ingredientComboBox.getSelectionModel().select(matchingIngredient);
                }
            }

            // unit dropdown
            unitComboBox.getSelectionModel().select(shoppingListItem.getUnit());
        }
    }

    /**
     * deletes the current shopping list item
     */
    @FXML
    private void onDeleteClicked() {
        if (shoppingListItem != null) {
            onDeleteIngredient.apply(shoppingListItem);
        }
        this.onUpdate.apply(null);
    }

    /**
     * called when the user confirms their edits
     */
    @FXML
    private void onConfirmClicked() {
        String text = amountField.getText();
        if (text == null || text.isBlank()) {
            amountField.styleProperty().set("-fx-text-box-border: red;");
            return;
        }

        if (isTextMode) {
            // Text item
            if (shoppingListItem == null) {
                onAddIngredient.apply(new ShoppingListItem(text));
            } else {
                shoppingListItem.setText(text);
                onUpdate.apply(null);
            }
            textLabel.setText(text);
        } else {
            // Ingredient item
            Ingredient selectedIngredient = ingredientComboBox.getSelectionModel().getSelectedItem();
            if (selectedIngredient == null) {
                ingredientComboBox.styleProperty().set("-fx-border-color: red; -fx-border-radius: 4;");
                return;
            }
            else {
                ingredientComboBox.styleProperty().set("-fx-border-color: lightgray;");
            }

            Unit unit = unitComboBox.getSelectionModel().getSelectedItem();
            if (unit == null) {
                return;
            }

            String informalAmount = null;
            double amount = 0;

            if (unit == Unit.CUSTOM) {
                informalAmount = text;
            }
            else{
                try {
                    amount = Double.parseDouble(text);
                }
                catch (Exception _) {
                    amount = -1;
                }
            }

            if ((amount <= 0 && unit != Unit.CUSTOM) ||
                    ((informalAmount == null || informalAmount.isEmpty()) && unit == Unit.CUSTOM)) {
                amountField.styleProperty().set("-fx-text-box-border: red;");
                return;
            }
            else {
                amountField.styleProperty().set("-fx-text-box-border: lightgray;");
            }

            if (shoppingListItem == null) {
                onAddIngredient.apply(new ShoppingListItem(
                        selectedIngredient.getId(),
                        selectedIngredient.getName(),
                        informalAmount,
                        amount,
                        unit,
                        null));
                // Refresh to get the new item
                onUpdate.apply(null);
                return;
            } else {
                shoppingListItem.setIngredientId(selectedIngredient.getId());
                shoppingListItem.setIngredientName(selectedIngredient.getName());
                shoppingListItem.setAmount(amount);
                shoppingListItem.setUnit(unit);
                shoppingListItem.setInformalUnit(informalAmount);
                shoppingListItem.setText(null); // clear text if it was a text item
                onUpdate.apply(null);
            }
            textLabel.setText(shoppingListItem.formatItem());
        }

        editView.setVisible(false);
        editView.setManaged(false);

        defaultView.setVisible(true);
        defaultView.setManaged(true);
    }

    /**
     * called when the user cancels their edits
     */
    @FXML
    private void onCancelClicked() {
        if (shoppingListItem == null) {
            onUpdate.apply(null); // removes the item from the list
        }

        editView.setVisible(false);
        editView.setManaged(false);

        defaultView.setVisible(true);
        defaultView.setManaged(true);
    }

    /**
     * called when creating a new ShoppingListItem from clicking the +
     */
    public void startEditingFromCtrl() {
        // load data to show
        amountField.setText("");

        if (isTextMode) {
            amountField.setPromptText("Enter text");
            updateUIForTextMode();
        } else {
            amountField.setPromptText("Enter amount");
            unitComboBox.getSelectionModel().select(Unit.GRAM);
            updateUIForIngredientMode();
        }

        enterEditMode();
    }

    /**
     * enters edit mode
     */
    public void enterEditMode(){
        defaultView.setVisible(false);
        defaultView.setManaged(false);

        editView.setVisible(true);
        editView.setManaged(true);

        // Ingredient dropdown
        List<Ingredient> ingredients = serverUtils.getIngredients();
        ingredientComboBox.getItems().setAll(ingredients);

        // unit dropdown
        unitComboBox.getItems().setAll(Unit.values());

        // Update UI based on current mode
        if (isTextMode) {
            updateUIForTextMode();
        } else {
            updateUIForIngredientMode();
        }
    }

    /**
     * Updates UI to show text mode (hides ingredient and unit controls)
     */
    private void updateUIForTextMode() {
        unitComboBox.setVisible(false);
        unitComboBox.setManaged(false);
        ingredientComboBox.setVisible(false);
        ingredientComboBox.setManaged(false);
        amountField.setPromptText("Enter text");
    }

    /**
     * Updates UI to show ingredient mode (shows all controls)
     */
    private void updateUIForIngredientMode() {
        unitComboBox.setVisible(true);
        unitComboBox.setManaged(true);
        ingredientComboBox.setVisible(true);
        ingredientComboBox.setManaged(true);
        amountField.setPromptText("Enter amount");
    }

}
