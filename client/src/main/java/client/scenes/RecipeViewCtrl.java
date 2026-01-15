package client.scenes;

import client.MyFXML;
import client.services.ShoppingListService;
import client.utils.FavoritesManager;
import client.utils.Printer;
import client.utils.ServerUtils;
import commons.Ingredient;
import commons.Recipe;
import commons.Unit;
import commons.RecipeIngredient;
import commons.Unit;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import com.google.inject.Inject;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private Button deleteButton;
    @FXML
    private TextField servingsScalingInput;
    @FXML
    private Button resetServingsButton;
    @FXML
    private Button favoriteButton;
    @FXML
    private Label caloriesDisplay;

    private MyFXML fxml;
    private final ServerUtils server;
    private boolean editing = false;
    private final MainCtrl mainCtrl;
    private final Printer printer;
    private Recipe recipe;
    private List<RecipeIngredient> ingredients;
    private final AppViewCtrl appViewCtrl;
    private final FavoritesManager favoritesManager;
    private final ShoppingListService shoppingListService;

    private final List<RecipeIngredientCtrl> ingredientRowCtrls = new ArrayList<>();
    private int baseServings;
    private double targetServings;

    /**
     * Constructor for RecipeViewCtrl.
     *
     * @param server the server utility used for network communication
     */
    @Inject
    public RecipeViewCtrl(ServerUtils server,
                          MainCtrl mainCtrl,
                          Printer printer,
                          FavoritesManager favoritesManager,
                          ShoppingListService shoppingListService) {
        this.mainCtrl = mainCtrl;
        this.printer = printer;
        this.server = server;
        this.appViewCtrl = mainCtrl.getAppViewCtrl();
        this.favoritesManager = favoritesManager;
        this.shoppingListService = shoppingListService;
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

        servingsScalingInput.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                applyServingsFromField();
            }
        });
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
        if (recipe == null) return;

        baseServings = Math.max(1, recipe.getServings());
        targetServings = baseServings;
        setServingsField(targetServings);

        nameLabel.setText(recipe.getName());
        loadIngredients();
        loadPreparationSteps(recipe.getPreparationSteps());
        updateFavoriteButton();
        rerenderIngredientsScaled();
        updateCaloriesDisplay();
    }

    /**
     * Helper method to initialist the Target Servings field.
     *
     * @param servings amount of servings
     */
    private void setServingsField(double servings) {
        servingsScalingInput.setText(Double.toString(servings));
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
        ingredientRowCtrls.clear();
        this.ingredients = server.getRecipeIngredients(recipe.getId());
        if (ingredients == null || fxml == null) {
            return;
        }
        for (RecipeIngredient ri : ingredients) {
            Pair<RecipeIngredientCtrl, Parent> item = fxml.load(RecipeIngredientCtrl.class,
                    "client", "scenes", "RecipeIngredient.fxml");
            RecipeIngredientCtrl ctrl = item.getKey();
            ctrl.initialize(ri, recipe, this::loadIngredients);

            ingredientRowCtrls.add(ctrl);

            ingredientsContainer.getChildren().add(item.getValue());
        }
        updateCaloriesDisplay();
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
        updateCaloriesDisplay();
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
        mainCtrl.getAddRecipeCtrl().clone(recipe);
    }

    /**
     * this function will be used to delete recipes.
     * Also checks whether the recipe is favorited and deletes it from favorites if it is.
     */
    public void deleteRecipe(){
        try{
            long recipeId = this.recipe.getId();
            server.deleteRecipe(recipeId);

            // Remove from favorites if it was favorited
            if (favoritesManager.isFavorite(recipeId)) {
                favoritesManager.removeFavorite(recipeId);
            }

            appViewCtrl.loadRecipes();
        } catch (Exception e){
            System.out.println("something went wrong.");
        }
        mainCtrl.showDefaultView();

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

    /**
     * called when the user presses enter after inputting Target Servings
     */
    @FXML
    private void onServingsEnter() {
        applyServingsFromField();
    }

    /**
     * fetches the input from Target Services and handles invalid inputs
     */
    private void applyServingsFromField() {
        String text = servingsScalingInput.getText();
        if (text == null || text.isBlank()) return;

        try {
            double value = Double.parseDouble(text);
            if (value > 0) {
                targetServings = value;
            }
            else{
                setServingsField(targetServings);
            }
        } catch (NumberFormatException ignored) {
            setServingsField(targetServings);
        }

        rerenderIngredientsScaled();
    }

    /**
     * calculates the scaling factor and rerenders it again
     */
    private void rerenderIngredientsScaled() {
        double factor = (baseServings <= 0) ? 1.0 : (double) targetServings / baseServings;

        for (RecipeIngredientCtrl ctrl : ingredientRowCtrls) {
            ctrl.applyScaleFactor(factor);
        }
        updateCaloriesDisplay();
    }

    /**
     * called when the user clicks Reset button
     */
    @FXML
    public void resetServingsScaling() {
        if (recipe == null) return;

        targetServings = baseServings;
        setServingsField(targetServings);
        rerenderIngredientsScaled();
    }

    /**
     * called when the user clicks the Shop button
     */
    @FXML
    public void addToShoppingList(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Adding to shopping list");
        alert.setHeaderText("You are about to add the ingredients for " + targetServings + " servings of "
                + recipe.getName() + " to the shopping list.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            this.shoppingListService.addIngredients(server.getRecipeIngredients(this.recipe.getId()),
                    targetServings/baseServings, recipe.getName());
            Alert notif = new Alert(Alert.AlertType.INFORMATION);
            notif.setTitle("Success");
            notif.setHeaderText("Success");
            StringBuilder sb = new StringBuilder("Added:\n");
            for (RecipeIngredient ri : server.getRecipeIngredients(this.recipe.getId())){
                sb.append(ri.formatIngredientScaled(ri.getAmount()*targetServings/baseServings));
                sb.append("\n");
            }
            notif.setContentText(sb.toString());
            notif.show();
            mainCtrl.reloadShoppingList();
        }
    }

    //calories display

    /**
     * Updates the calories display based on the database's list of ingredients
     */
    protected void updateCaloriesDisplay(){
        StringBuilder textToDisplay = new StringBuilder();
        textToDisplay.append((int)calculateCaloriesForRecipe());
        textToDisplay.append(" kcal/100g");
        caloriesDisplay.setText(textToDisplay.toString());
    }

    /**
     * Logic for calculating the amount of calories for this Recipe.
     * This logic assumes that 1g = 1mL.
     * @return amount of calories or 0.0 in case of invalid ingredient's mass
     */
    private double calculateCaloriesForRecipe(){
        double totalCalories = 0;
        double totalMass = 0;
        for(RecipeIngredient ri: ingredients){
            if(ri == null) continue;
            if(ri.getIngredient() == null) continue;

            //String informal = ri.getInformalUnit();
            if (ri.getUnit() == Unit.CUSTOM) continue;

            double amount = ri.getAmount();
            Ingredient ingredient = ri.getIngredient();
            totalCalories +=
                    ri.getUnit() == Unit.GRAM ?
                            ingredient.calculateCalories()*amount/100 : ingredient.calculateCalories()*amount*10;
            totalMass += ri.getUnit() == Unit.GRAM ? amount : amount*1000;


        }
        if(totalMass <= 0.0) return 0.0;
        return 100*totalCalories/totalMass;
    }



}


