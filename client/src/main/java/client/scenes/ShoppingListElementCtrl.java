package client.scenes;

import client.config.Config;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Ingredient;
import commons.RecipeIngredient;
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

    private RecipeIngredient recipeIngredient;

    private Runnable updateIngredientList;

    private final ServerUtils serverUtils;
    private final Config config;

    /**
     * constructor to be injected
     * @param serverUtils serverutils to load/delete/edit ingredients
     * @param config the config
     */
    @Inject
    public ShoppingListElementCtrl(ServerUtils serverUtils, Config config) {
        this.serverUtils = serverUtils;
        this.config = config;
    }

    /**
     * initializes the ShoppingElementCtrl
     * @param recipeIngredient A recipeIngredient containing information about the ingredient and its amount
     * @param updateIngredientList a function that is called whenever the list should be updated
     */
    public void initialize(RecipeIngredient recipeIngredient,
                           Runnable updateIngredientList) {
        this.recipeIngredient = recipeIngredient;
        this.updateIngredientList = updateIngredientList;

        defaultView.setVisible(true);
        defaultView.setManaged(true);

        editView.setVisible(false);
        editView.setManaged(false);

        if (recipeIngredient != null) {
            textLabel.setText(recipeIngredient.formatIngredient());
        }
        textLabel.setVisible(true);
        textLabel.setManaged(true);

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
     * starts editing the recipeIngredient
     */
    @FXML
    private void onEditClicked() {
        enterEditMode();

        // load data to show
        if (recipeIngredient.getUnit() != Unit.CUSTOM || recipeIngredient.getInformalUnit() == null) {
            amountField.setText(String.valueOf(recipeIngredient.getAmount()));
        }
        else {
            amountField.setText(recipeIngredient.getInformalUnit());
        }
        // Ingredient dropdown
        int i = ingredientComboBox.getItems().indexOf(recipeIngredient.getIngredient());
        ingredientComboBox.getSelectionModel().select(i);

        // unit dropdown
        unitComboBox.getSelectionModel().select(recipeIngredient.getUnit());
    }

    /**
     * deletes the current recipeIngredient
     */
    @FXML
    private void onDeleteClicked() {
        config.getShoppingList().remove(recipeIngredient);
        this.updateIngredientList.run();
    }

    /**
     * called when the user confirms their edits
     */
    @FXML
    private void onConfirmClicked() {
        int index = config.getShoppingList().indexOf(recipeIngredient);
        if (index == -1 && recipeIngredient != null) {
            updateIngredientList.run();
            return;
        }

        Unit unit = unitComboBox.getSelectionModel().getSelectedItem();

        String informalAmount = null;
        double amount = 0;

        if (unit == Unit.CUSTOM) {
            informalAmount = amountField.getText();
        }
        else{
            try {
                amount = Double.parseDouble(amountField.getText());
            }
            catch (Exception _) {
                amount = -1;
            }
        }

        Ingredient ingredient = ingredientComboBox.getSelectionModel().getSelectedItem();

        if ((amount <= 0 && unit != Unit.CUSTOM) ||
                ((informalAmount == null || informalAmount.isEmpty()) && unit == Unit.CUSTOM)) {
            amountField.styleProperty().set("-fx-text-box-border: red;");
            return;
        }
        else {
            amountField.styleProperty().set("-fx-text-box-border: lightgray;");
        }

        if (ingredient == null){
            ingredientComboBox.styleProperty().set("-fx-border-color: red; -fx-border-radius: 4;");
            return;
        }
        else{
            ingredientComboBox.styleProperty().set("-fx-border-color: lightgray;");
        }

        if (unit == null) { // user should not be able to set unit as null,
            return;         // so I will simply not allow that
        }

        if (recipeIngredient == null){
            recipeIngredient = new RecipeIngredient(null, ingredient, informalAmount, amount, unit);
            config.getShoppingList().add(recipeIngredient);
        }
        else {
            recipeIngredient.setAmount(amount);
            recipeIngredient.setUnit(unit);
            recipeIngredient.setInformalUnit(informalAmount);
            recipeIngredient.setIngredient(ingredient);
        }
        textLabel.setText(recipeIngredient.formatIngredient());

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
        if (recipeIngredient == null) {
            updateIngredientList.run(); // removes the recipeIngredient from the list
        }

        editView.setVisible(false);
        editView.setManaged(false);

        defaultView.setVisible(true);
        defaultView.setManaged(true);
    }

    /**
     * called when creating a new RecipeIngredient from clicking the +
     */
    public void startEditingFromCtrl() {
        // load data to show
        amountField.setText("");

        unitComboBox.getSelectionModel().select(Unit.GRAM);

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

    }

}
