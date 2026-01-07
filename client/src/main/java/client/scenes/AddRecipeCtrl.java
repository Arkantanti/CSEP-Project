package client.scenes;

import client.MyFXML;
import com.google.inject.Inject;
import client.utils.ServerUtils;
import commons.Recipe;
import commons.RecipeIngredient;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
// temporarily removed the adding as the design of adding
// ingredients make it so the recipe needs to be created first.


//import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddRecipeCtrl {
    @FXML
    private Label nameLabel;
    @FXML
    private VBox ingredientsContainer;
    @FXML
    private Button ingredientAddButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private MyFXML fxml;

    @FXML
    private TextField nameTextField;
    @FXML
    private TextField servingsArea;

    @FXML
    private TextArea preparationsArea;

    private final AppViewCtrl appViewCtrl;

    private List<RecipeIngredient> recipeIngredientList;

    private Recipe recipe;

    /**
     *  The constructor for the add recipeController
     * @param server the server it is linked to
     */
    @Inject
    public AddRecipeCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.appViewCtrl = mainCtrl.getAppViewCtrl();
    }

    /**
     * The stuff we need for that this begins so it can properly work.
     */
    public void initialize(MyFXML fxml){
        this.fxml = fxml;
        // Start with no recipe - will be created when needed
        this.recipe = null;
    }

    /**
     * function for adding recipeIngredients
     */
    @FXML
    private void onAddRecipeIngredient(){
        // If no recipe exists yet, create it from current fields
        if (recipe == null) {
            String name = nameTextField.getText().trim();
            if (name.isEmpty()) name = "New Recipe";

            int servings = 1;
            try {
                servings = Integer.parseInt(servingsArea.getText().trim());
            } catch (Exception e) {
                // Use default
            }

            List<String> steps = new ArrayList<>();
            if (!preparationsArea.getText().isEmpty()) {
                steps = Arrays.asList(preparationsArea.getText().split("\\r?\\n"));
            }

            recipe = server.add(new Recipe(name, servings, steps));
            System.out.println("DEBUG: Created recipe for first ingredient, ID: " + recipe.getId());
        }

        Pair<RecipeIngredientCtrl, Parent> item = fxml.load(RecipeIngredientCtrl.class,
                "client", "scenes", "RecipeIngredient.fxml");
        item.getKey().initialize(null, recipe, this::showIngredients);

        item.getValue().setUserData(item.getKey());
        ingredientsContainer.getChildren().add(item.getValue());
        item.getKey().startEditingFromCtrl();
    }

    /**
     * function to save the recipe
     */
    @FXML
    public void onSaveRecipe() {
        try {
            // Make sure the inputs are correct
            String name = nameTextField.getText().trim();
            if (name.isBlank()) {
                showError("Input Error", "Recipe name cannot be empty.");
                return;
            }

            int servings;
            try {
                servings = Integer.parseInt(servingsArea.getText().trim());
                if (servings < 1) {
                    showError("Input Error", "Servings must be at least 1.");
                    return;
                }
            } catch (NumberFormatException e) {
                showError("Input Error", "Servings must be a number.");
                return;
            }

            List<String> preparationSteps = Arrays.asList(
                    preparationsArea.getText().split("\\r?\\n"));
            if (preparationSteps.isEmpty() ||
                    (preparationSteps.size() == 1 && preparationSteps.get(0).isBlank())) {
                showError("Input Error", "Preparation steps cannot be empty.");
                return;
            }

            // Check if a recipe exists before adding the ingredients.
            if (recipe == null) {
                // If not first create the recipe.
                recipe = server.add(new Recipe(name, servings, preparationSteps));
            } else {
                // Update the existing recipe.
                recipe.setName(name);
                recipe.setServings(servings);
                recipe.setPreparationSteps(preparationSteps);
                recipe = server.updateRecipe(recipe);
            }

            // Now save all ingredients to the server.
            saveAllIngredientsToServer(recipe);

            appViewCtrl.loadRecipes();
            mainCtrl.showRecipe(recipe);

        } catch (Exception e) {
            showError("Error", "Could not save the recipe. There might be a problem with your server connection.");
        }
    }

    /**
     * Save all ingredients to the server.
     */
    private void saveAllIngredientsToServer(Recipe targetRecipe) {
        try {
            //Collect the ingredients.
            List<RecipeIngredient> ingredientsToSave = new ArrayList<>();

            for (javafx.scene.Node node : ingredientsContainer.getChildren()) {
                Object controller = node.getUserData();
                if (controller instanceof RecipeIngredientCtrl ctrl) {
                    RecipeIngredient ri = ctrl.getRecipeIngredient();

                    if (ri != null && ri.getIngredient() != null) {
                        ingredientsToSave.add(ri);
                        System.out.println(ri.getIngredient().getName());
                    }
                }
            }

            System.out.println("DEBUG: Total ingredients to save: " + ingredientsToSave.size());

            // Only delete old ingredients if we have new ones to save
            if (!ingredientsToSave.isEmpty()) {
                // Delete all existing ingredients to prevent duplicates
                List<RecipeIngredient> existing = server.getRecipeIngredients(targetRecipe.getId());
                if (existing != null) {
                    for (RecipeIngredient old : existing) {
                        server.deleteRecipeIngredient(old.getId());
                    }
                }

                // Input the recipes again
                for (RecipeIngredient ingredient : ingredientsToSave) {
                    RecipeIngredient fresh = new RecipeIngredient(
                            targetRecipe,
                            ingredient.getIngredient(),
                            ingredient.getInformalUnit(),
                            ingredient.getAmount(),
                            ingredient.getUnit()
                    );
                    server.addRecipeIngredient(fresh);
                }
            }
        } catch (Exception e) {
            System.out.println("There was an error in the saving of the ingredients.");
        }
    }

    /**
     * To show an error for if something goes wrong
     * @param header The head text of the error
     * @param content The main text of the error
     */
    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * the function to clone the recipe.
     * @param recipe the recipe information that needs be inputted for the clone
     */
    public void clone(Recipe recipe) {
        if (recipe == null) return;

        nameTextField.setText(recipe.getName());
        servingsArea.setText(String.valueOf(recipe.getServings()));
        preparationsArea.setText(String.join("\n", recipe.getPreparationSteps()));
    }

    /**
     * To cancel the add function
     */
    public void onCancel(){
        try{
            // if cancelled delete the recipe we created.
            System.out.println(recipe.getId());
            if(recipe != null){
                server.deleteRecipe(recipe.getId());
            }
        } catch(Exception e){
            System.out.println("Something went wrong");
        }

        if(mainCtrl.getFirstOpen()){
            mainCtrl.showAppView();
            appViewCtrl.loadRecipes();
            mainCtrl.showDefaultScreen();
        } else {
            mainCtrl.showAppView();
            appViewCtrl.loadRecipes();
        }
    }



    /**
     * This function is to show the ingredients for the adding of recipes.
     */
    private void showIngredients(){
        if(recipe == null || fxml == null){
            return;
        }
        // first up clear the ingredient Container.
        ingredientsContainer.getChildren().clear();

        this.recipeIngredientList = server.getRecipeIngredients(recipe.getId());

        if(recipeIngredientList == null){
            recipeIngredientList = new ArrayList<>();
        }

        for(RecipeIngredient ri : recipeIngredientList){
            //To make sure the ingredients are set on the correct recipe.
            ri.setRecipe(recipe);

            Pair<RecipeIngredientCtrl, Parent> item = fxml.load(RecipeIngredientCtrl.class,
                    "client", "scenes", "RecipeIngredient.fxml");
            item.getKey().initialize(ri, recipe, this::showIngredients);

            // STORE THE CONTROLLER REFERENCE
            item.getValue().setUserData(item.getKey());

            ingredientsContainer.getChildren().add(item.getValue());
        }
    }
}