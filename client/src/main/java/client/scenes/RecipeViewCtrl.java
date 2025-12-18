package client.scenes;

import client.MyFXML;
import client.utils.FavoritesManager;
import client.utils.Printer;
import client.utils.ServerUtils;
import commons.Recipe;
import commons.Unit;
import commons.RecipeIngredient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import com.google.inject.Inject;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class RecipeViewCtrl {

    @FXML
    private Button titleEditButton;
    @FXML
    private TextField nameTextField;
    @FXML
    private Label nameLabel;
    @FXML
    private VBox ingredientsContainer;
    @FXML
    private Button ingredientAddButton;
    @FXML
    private VBox preparationsContainer;
    @FXML
    private Button preparationAddButton;
    @FXML
    private Button printButton;
    @FXML
    private Button cloneButton;
    @FXML
    private Button favoriteButton;

    private MyFXML fxml;
    private final ServerUtils server;
    private boolean editing = false;
    private final MainCtrl mainCtrl;
    private final Printer printer;
    private Recipe recipe;
    private List<RecipeIngredient> ingredients;
    private final AppViewCtrl appViewCtrl;
    private final FavoritesManager favoritesManager;

    /**
     * Constructor for RecipeViewCtrl.
     *
     * @param server the server utility used for network communication
     */
    @Inject
    public RecipeViewCtrl(ServerUtils server, MainCtrl mainCtrl, Printer printer, FavoritesManager favoritesManager) {
        this.mainCtrl = mainCtrl;
        this.printer = printer;
        this.server = server;
        this.appViewCtrl = mainCtrl.getAppViewCtrl();
        this.favoritesManager = favoritesManager;
    }

    /**
     * The method that gets called internally when setting up the RecipeView.
     * This method initializes the base properties for the RecipeView
     */
    @FXML
    public void initialize() {
        // default state is label with text
        nameTextField.setVisible(false);
        nameTextField.setManaged(false);
    }

    /**
     * Sets the recipe to display in this view.
     *
     * @param recipe the recipe to display
     * @param fxml   the FXML loader for loading EditableItem components
     */
    public void setRecipe(Recipe recipe, MyFXML fxml) {
        this.fxml = fxml;
        this.recipe = recipe;
        if (recipe != null) {
            nameLabel.setText(recipe.getName());
            loadIngredients();
            loadPreparationSteps(recipe.getPreparationSteps());
            updateFavoriteButton();
        }
    }

    /**
     * Handles the edit button click for the recipe title.
     * Toggles between editing and viewing mode.
     */
    @FXML
    private void onEditClicked() {
        if (!editing) {
            startEditing();
        } else {
            finishEditing();
        }
    }

    /**
     * Starts editing mode for the recipe title.
     * Shows the text field and hides the label.
     */
    private void startEditing() {
        editing = true;
        nameTextField.setText(nameLabel.getText());

        nameLabel.setVisible(false);
        nameLabel.setManaged(false);
        nameTextField.setVisible(true);
        nameTextField.setManaged(true);

        nameTextField.requestFocus();
        nameTextField.selectAll();

        titleEditButton.setText("✔");
        titleEditButton.setTextFill(Color.GREEN);

        printButton.setVisible(false);
        printButton.setManaged(false);
    }

    /**
     * Finishes editing mode for the recipe title.
     * Shows the label and hides the text field.
     */
    private void finishEditing() {
        editing = false;
        String newName = nameTextField.getText();

        if (newName != null && !newName.isBlank()) {
            nameLabel.setText(newName.trim());
            // update new title to the server.
            if (recipe != null) {
                recipe.setName(newName);
                server.updateRecipe(recipe);
                appViewCtrl.loadRecipes();
            }
        }

        nameTextField.setVisible(false);
        nameTextField.setManaged(false);

        nameLabel.setVisible(true);
        nameLabel.setManaged(true);

        titleEditButton.setText("✏");
        titleEditButton.setTextFill(Color.web("#1e00ff"));

        printButton.setVisible(true);
        printButton.setManaged(true);
    }

    /**
     * Loads ingredients from the server into the ingredients container using EditableItem components.
     */
    private void loadIngredients() {
        ingredientsContainer.getChildren().clear();
        this.ingredients = server.getRecipeIngredients(recipe.getId());
        if (ingredients == null || fxml == null) {
            return;
        }
        for (RecipeIngredient ri : ingredients) {
            Pair<RecipeIngredientCtrl, Parent> item = fxml.load(RecipeIngredientCtrl.class,
                    "client", "scenes", "RecipeIngredient.fxml");
            item.getKey().initialize(ri, recipe, this::loadIngredients);
            ingredientsContainer.getChildren().add(item.getValue());
        }
    }

    /**
     * Formats a RecipeIngredient into a displayable string.
     *
     * @param ri the RecipeIngredient to format and display
     * @return a string representation of ingredient amount and unit from {@code RecipeIngredient}
     */
    private String formatIngredient(RecipeIngredient ri) {
        if (ri.getInformalUnit() != null && !ri.getInformalUnit().isEmpty()) {
            return ri.getInformalUnit() + " " + ri.getIngredient().getName();
        }
        String unitString = "";
        // TODO: EXTRA UNIT NORMALIZATION AND FORMATTING LOGIC
        if (ri.getUnit() != null) {
            if (ri.getUnit() == Unit.GRAM) {
                unitString = "g";
            } else if (ri.getUnit() == Unit.LITER) {
                unitString = "L";
            }
        }
        return ri.getAmount() + unitString + " " + ri.getIngredient().getName();
    }

    /**
     * Loads preparation steps into the preparations container using EditableItem components.
     *
     * @param steps the list of preparation steps to display
     */
    private void loadPreparationSteps(List<String> steps) {
        preparationsContainer.getChildren().clear();
        if (steps == null || fxml == null) {
            return;
        }

        for (int i = 0; i < steps.size(); i++) {
            String step = steps.get(i);

            Pair<EditableItemCtrl, Parent> item = fxml.load(
                    EditableItemCtrl.class,
                    "client", "scenes", "EditableItem.fxml"
            );

            EditableItemCtrl ctrl = item.getKey();
            ctrl.setText(step);

            ctrl.bindTo(
                    steps,
                    i,
                    () -> {
                        server.updateRecipe(recipe);
                        loadPreparationSteps(recipe.getPreparationSteps());
                    },
                    false
            );
            preparationsContainer.getChildren().add(item.getValue());
        }
    }

    /**
     * Adds an empty preparation step into the container for the preparation steps, so that
     * the user can input a new preparation step.
     *
     * @param actionEvent the click action to be handled
     */
    public void onAddPreparationStepClicked(ActionEvent actionEvent) {
        List<String> steps = recipe.getPreparationSteps();

        if (steps == null) {
            throw new IllegalStateException("Preparation steps array can " +
                    "not be null for a recipe");
        }

        steps.add("");
        int index = steps.size() - 1;

        Pair<EditableItemCtrl, Parent> item = fxml.load(EditableItemCtrl.class,
                "client", "scenes", "EditableItem.fxml");

        EditableItemCtrl ctrl = item.getKey();
        ctrl.setText("");

        ctrl.bindTo(
                steps,
                index,
                () -> {
                    // Only called for non-blank commit or delete on existing items
                    server.updateRecipe(recipe);
                    loadPreparationSteps(recipe.getPreparationSteps());
                },
                true // true in this case, because a new item is created
        );

        preparationsContainer.getChildren().add(item.getValue());
        ctrl.startEditingFromCtrl();
    }

    /**
     * called when the user presses the + under the ingredients list
     */
    @FXML
    private void onAddRecipeIngredient(){
        Pair<RecipeIngredientCtrl, Parent> item = fxml.load(RecipeIngredientCtrl.class,
                "client", "scenes", "RecipeIngredient.fxml");
        item.getKey().initialize(null, recipe, this::loadIngredients);
        ingredientsContainer.getChildren().add(item.getValue());
        item.getKey().startEditingFromCtrl();
    }

    /**
     * Saves the currently selected recipe as a markdown file in a location specified by the user.
     */
    public void recipePrint() {
        Path path = mainCtrl.showFileChooser("Recipe.pdf");
        if (recipe == null || path == null) {
            return;
        }
        try {
            String markdown = printer.recipePrint(recipe, ingredients);
            printer.markdownToPDF(path, markdown);
        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Saving error");
                alert.setHeaderText("Could not save the recipe");
                alert.showAndWait();
            });
        }
    }

    /**
     * This function will be implemented for adding recipes
     */
    public void cloneRecipe(){
        mainCtrl.showAddRecipe();
        AddRecipeCtrl addCtrl = mainCtrl.getAddRecipeCtrl();
        addCtrl.clone(recipe);
    }

    /**
     * Handles the favorite button clicks
     */
    @FXML
    private void onFavoriteClicked() {
        if (recipe == null) {
            throw new IllegalStateException("Recipe is not set");
        }
        try {
            if (favoritesManager.isFavorite(recipe.getId())) {
                favoritesManager.removeFavorite(recipe.getId());
            } else {
                favoritesManager.addFavorite(recipe.getId());
            }
            updateFavoriteButton();
            // Reload the recipes list after change in favorite recipes
            appViewCtrl.loadRecipes();
        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Favorite status update failed");
                alert.setHeaderText("Could not update favorite");
                alert.showAndWait();
            });
        }
    }

    /**
     * Updates the favorite button.
     * Uses the style class 'favorited' to visually indicate the status.
     */
    private void updateFavoriteButton() {
        if (recipe == null || favoriteButton == null) {
            return;
        }
        if (favoritesManager.isFavorite(recipe.getId())) {
            favoriteButton.getStyleClass().add("favorited");
        } else {
            favoriteButton.getStyleClass().remove("favorited");
        }
    }
}


