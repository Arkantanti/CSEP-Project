package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.List;

public class RecipeIngredientCtrl {
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
    private Recipe recipe;

    private Runnable updateIngredientList;

    private final ServerUtils serverUtils;

    /**
     * constructor to be injected
     * @param serverUtils serverutils to load/delete/edit ingredients
     */
    @Inject
    public RecipeIngredientCtrl(ServerUtils serverUtils) {
        this.serverUtils = serverUtils;
    }

    /**
     * called by the RecipeView to initialize the cell
     * @param recipeIngredient the RecipeIngredient this cell corresponds to
     */
    public void initialize(RecipeIngredient recipeIngredient,
                           Recipe recipe,
                           Runnable updateIngredientList) {
        this.recipeIngredient = recipeIngredient;
        this.recipe = recipe;
        this.updateIngredientList = updateIngredientList;

        editView.setVisible(false);
        editView.setManaged(false);

        defaultView.setVisible(true);
        defaultView.setManaged(true);

        if (recipeIngredient != null) {
            textLabel.setText(recipeIngredient.formatIngredient());
        }
        textLabel.setVisible(true);
        textLabel.setManaged(true);

        ingredientComboBox.setCellFactory(list -> new ListCell<Ingredient>() {
            @Override
            protected void updateItem(Ingredient item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getName());
            }
        });

        ingredientComboBox.setButtonCell(new ListCell<Ingredient>() {
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
        editView.setVisible(true);
        editView.setManaged(true);

        defaultView.setVisible(false);
        defaultView.setManaged(false);

        // load data to show
        amountField.setText(String.valueOf(recipeIngredient.getAmount()));

        // Ingredient dropdown
        List<Ingredient> ingredients = serverUtils.getIngredients();
        ingredientComboBox.getItems().setAll(ingredients);

        int i = ingredients.indexOf(recipeIngredient.getIngredient());
        ingredientComboBox.getSelectionModel().select(i);

        // unit dropdown
        unitComboBox.getItems().setAll(Unit.values());

        unitComboBox.getSelectionModel().select(recipeIngredient.getUnit());
    }

    /**
     * deletes the current recipeIngredient
     */
    @FXML
    private void onDeleteClicked() {
        serverUtils.deleteRecipeIngredient(recipeIngredient.getId());
        this.updateIngredientList.run();
    }

    /**
     * called when the user confirms their edits
     */
    @FXML
    private void onConfirmClicked() {
        double amount = -1;
        try {
            amount = Double.parseDouble(amountField.getText());
        }
        catch (NumberFormatException _) {
        }

        Unit unit = unitComboBox.getSelectionModel().getSelectedItem();
        Ingredient ingredient = ingredientComboBox.getSelectionModel().getSelectedItem();

        if (amount <= 0 || unit == null || ingredient == null) {
            return;
        }

        if (recipeIngredient == null){
            recipeIngredient = serverUtils.addRecipeIngredient(
                    new RecipeIngredient(recipe, ingredient, "", amount, unit)
            );
        }
        else {
            recipeIngredient.setAmount(amount);
            recipeIngredient.setUnit(unit);
            recipeIngredient.setIngredient(ingredient);
            serverUtils.updateRecipeIngredient(recipeIngredient);
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
        editView.setVisible(false);
        editView.setManaged(false);

        defaultView.setVisible(true);
        defaultView.setManaged(true);
    }

    /**
     * called when creating a new RecipeIngredient from clicking the +
     */
    public void startEditingFromCtrl() {
        editView.setVisible(true);
        editView.setManaged(true);

        defaultView.setVisible(false);
        defaultView.setManaged(false);

        // load data to show
        amountField.setText("");

        // Ingredient dropdown
        List<Ingredient> ingredients = serverUtils.getIngredients();
        ingredientComboBox.getItems().setAll(ingredients);

        // unit dropdown
        unitComboBox.getItems().setAll(Unit.values());

        unitComboBox.getSelectionModel().select(Unit.GRAM);
    }
}
