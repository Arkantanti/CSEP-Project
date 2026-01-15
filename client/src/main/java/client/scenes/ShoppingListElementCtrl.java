package client.scenes;

import client.services.ShoppingListService;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Ingredient;
import commons.ShoppingListItem;
import commons.Unit;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.List;

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

    private Runnable updateIngredientList;

    private final ServerUtils serverUtils;
    private final ShoppingListService shoppingListService;

    /**
     * constructor to be injected
     * @param serverUtils serverutils to load/delete/edit ingredients
     * @param shoppingListService the shopping list service
     */
    @Inject
    public ShoppingListElementCtrl(ServerUtils serverUtils, ShoppingListService shoppingListService) {
        this.serverUtils = serverUtils;
        this.shoppingListService = shoppingListService;
    }

    /**
     * initializes the ShoppingElementCtrl for an existing item
     * @param shoppingListItem A shopping list item
     * @param updateIngredientList a function that is called whenever the list should be updated
     */
    public void initialize(ShoppingListItem shoppingListItem,
                           Runnable updateIngredientList) {
        initializeInternal(shoppingListItem, updateIngredientList, shoppingListItem.isTextOnly());
    }

    /**
     * initializes the ShoppingElementCtrl for a new item
     * @param shoppingListItem A shopping list item (must be null for new items)
     * @param updateIngredientList a function that is called whenever the list should be updated
     * @param isTextMode true if this should be a text item
     */
    public void initialize(ShoppingListItem shoppingListItem,
                           Runnable updateIngredientList,
                           boolean isTextMode) {
        initializeInternal(shoppingListItem, updateIngredientList, isTextMode);
    }

    /**
     * Internal initialization method
     */
    private void initializeInternal(ShoppingListItem shoppingListItem,
                                    Runnable updateIngredientList,
                                    boolean isTextMode) {
        this.shoppingListItem = shoppingListItem;
        this.updateIngredientList = updateIngredientList;
        this.isTextMode = isTextMode;

        defaultView.setVisible(true);
        defaultView.setManaged(true);

        editView.setVisible(false);
        editView.setManaged(false);

        if (shoppingListItem != null) {
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
            shoppingListService.removeItem(shoppingListItem);
        }
        this.updateIngredientList.run();
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
                shoppingListService.addTextItem(text);
            } else {
                shoppingListItem.setText(text);
                shoppingListService.saveChanges();
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
                shoppingListService.addIngredientItem(
                        selectedIngredient.getId(),
                        selectedIngredient.getName(),
                        informalAmount,
                        amount,
                        unit,
                        null // no recipe name when manually adding
                );
                // Refresh to get the new item
                updateIngredientList.run();
                return;
            } else {
                shoppingListItem.setIngredientId(selectedIngredient.getId());
                shoppingListItem.setIngredientName(selectedIngredient.getName());
                shoppingListItem.setAmount(amount);
                shoppingListItem.setUnit(unit);
                shoppingListItem.setInformalUnit(informalAmount);
                shoppingListItem.setText(null); // clear text if it was a text item
                shoppingListService.saveChanges();
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
            updateIngredientList.run(); // removes the item from the list
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
