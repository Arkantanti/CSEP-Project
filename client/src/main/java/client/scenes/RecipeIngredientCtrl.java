package client.scenes;

import client.services.IngredientService;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

public class RecipeIngredientCtrl {
    @FXML private HBox defaultView;
    @FXML private Label textLabel;
    @FXML private HBox editView;
    @FXML private TextField amountField;
    @FXML private ComboBox<Unit> unitComboBox;
    @FXML private ComboBox<Ingredient> ingredientComboBox;

    private RecipeIngredient recipeIngredient;
    private Recipe recipe;

    private Runnable updateIngredientList;
    private final MainCtrl mainCtrl;
    private final ServerUtils serverUtils;
    private final IngredientService ingredientService;

    /**
     * constructor to be injected
     * @param serverUtils serverutils to load/delete/edit ingredients
     */
    @Inject
    public RecipeIngredientCtrl(ServerUtils serverUtils,
                                MainCtrl mainCtrl, IngredientService ingredientService) {
        this.mainCtrl = mainCtrl;
        this.serverUtils = serverUtils;
        this.ingredientService = ingredientService;
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
        if (recipeIngredient.getUnit() != Unit.CUSTOM
                || recipeIngredient.getInformalUnit() == null) {
            amountField.setText(String.valueOf(recipeIngredient.getAmount()));
        }
        else {
            amountField.setText(recipeIngredient.getInformalUnit());
        }
        // Ingredient dropdown
        List<Ingredient> ingredients = ingredientService.getAllIngredients();
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
        // Fix: check if this is a local draft (ID 0) or a persisted recipe
        if (recipe.getId() == 0) {
            // Local deletion: remove from the recipe's list if it exists
            if (recipe.getRecipeIngredients() != null) {
                recipe.getRecipeIngredients().remove(recipeIngredient);
            }
        } else {
            // Server deletion
            serverUtils.deleteRecipeIngredient(recipeIngredient.getId());
        }
        this.updateIngredientList.run();
    }

    /**
     * called when the user confirms their edits
     */
    @FXML
    private void onConfirmClicked() {
        Unit unit = unitComboBox.getSelectionModel().getSelectedItem();
        if (unit == null) return;

        double amount = parseAmount(unit);
        String informalAmount = (unit == Unit.CUSTOM) ? amountField.getText() : null;
        Ingredient ingredient = ingredientComboBox.getSelectionModel().getSelectedItem();

        if (!validateInputs(amount, informalAmount, unit, ingredient)) {
            return;
        }

        saveIngredient(ingredient, amount, informalAmount, unit);
        updateUI();
    }

    /**
     * Parses the amount field based on the selected unit.
     */
    private double parseAmount(Unit unit) {
        if (unit == Unit.CUSTOM) return 0;
        try {
            return Double.parseDouble(amountField.getText());
        } catch (Exception ignored) {
            return -1;
        }
    }

    /**
     * Validates the user inputs and updates UI styles for errors.
     */
    private boolean validateInputs(double amount, String informalAmount,
                                   Unit unit, Ingredient ingredient) {
        boolean valid = true;

        if (!isAmountValid(amount, informalAmount, unit)) {
            amountField.styleProperty().set("-fx-text-box-border: red;");
            valid = false;
        } else {
            amountField.styleProperty().set("-fx-text-box-border: lightgray;");
        }

        if (ingredient == null) {
            ingredientComboBox.styleProperty().set(
                    "-fx-border-color: red; -fx-border-radius: 4;");
            valid = false;
        } else {
            ingredientComboBox.styleProperty().set("-fx-border-color: lightgray;");
        }

        return valid;
    }

    /**
     * Checks if the amount is valid for the given unit.
     */
    private boolean isAmountValid(double amount, String informalAmount, Unit unit) {
        if (unit == Unit.CUSTOM) {
            return informalAmount != null && !informalAmount.isEmpty();
        }
        return amount > 0;
    }

    /**
     * Routes the save operation to either local or server storage.
     */
    private void saveIngredient(Ingredient ingredient, double amount,
                                String informalAmount, Unit unit) {
        if (recipe.getId() == 0) {
            saveLocal(ingredient, amount, informalAmount, unit);
        } else {
            saveServer(ingredient, amount, informalAmount, unit);
        }
    }

    /**
     * Saves the ingredient to the local recipe object (not persisted yet).
     */
    private void saveLocal(Ingredient ingredient, double amount,
                           String informalAmount, Unit unit) {
        if (recipe.getRecipeIngredients() == null) {
            recipe.setRecipeIngredients(new ArrayList<>());
        }

        if (recipeIngredient == null) {
            recipeIngredient = new RecipeIngredient(recipe, ingredient,
                    informalAmount, amount, unit);
            recipe.getRecipeIngredients().add(recipeIngredient);
        } else {
            updateData(ingredient, amount, informalAmount, unit);
        }
    }

    /**
     * Saves the ingredient to the server.
     */
    private void saveServer(Ingredient ingredient, double amount,
                            String informalAmount, Unit unit) {
        if (recipeIngredient == null) {
            recipeIngredient = serverUtils.addRecipeIngredient(
                    new RecipeIngredient(recipe, ingredient, informalAmount, amount, unit)
            );
        } else {
            updateData(ingredient, amount, informalAmount, unit);
            serverUtils.updateRecipeIngredient(recipeIngredient);
        }
    }

    /**
     * Updates the data of the existing recipeIngredient object.
     */
    private void updateData(Ingredient ingredient, double amount,
                            String informalAmount, Unit unit) {
        recipeIngredient.setAmount(amount);
        recipeIngredient.setUnit(unit);
        recipeIngredient.setInformalUnit(informalAmount);
        recipeIngredient.setIngredient(ingredient);
    }

    /**
     * Updates the UI state after a successful save.
     */
    private void updateUI() {
        updateIngredientList.run();
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

    public RecipeIngredient getRecipeIngredient() {
        return recipeIngredient;
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
        List<Ingredient> ingredients = ingredientService.getAllIngredients();
        ingredientComboBox.getItems().setAll(ingredients);

        // unit dropdown
        unitComboBox.getItems().setAll(Unit.values());

        unitComboBox.getSelectionModel().select(Unit.GRAM);
    }

    /**
     * applies refactoring to the ingredients
     * @param factor the scaling factor
     */
    public void applyScaleFactor(double factor) {
        if (recipeIngredient == null) return;
        textLabel.setText(recipeIngredient.formatIngredientScaled(factor));
    }

    /**
     * Opens a new window to quickly add an ingredient.
     */
    public void onAddIngredient() {
        Ingredient newIngredient = mainCtrl.showAddIngredientsNewWindow();
        if(newIngredient != null){
            List<Ingredient> ingredients = ingredientService.getAllIngredients();
            ingredientComboBox.getItems().setAll(ingredients);
            ingredientComboBox.getSelectionModel().select(newIngredient);
        }
    }

    public HBox getDefaultView() {
        return defaultView;
    }

    public Label getTextLabel() {
        return textLabel;
    }

    public HBox getEditView() {
        return editView;
    }

    public TextField getAmountField() {
        return amountField;
    }

    public ComboBox<Unit> getUnitComboBox() {
        return unitComboBox;
    }

    public ComboBox<Ingredient> getIngredientComboBox() {
        return ingredientComboBox;
    }
}