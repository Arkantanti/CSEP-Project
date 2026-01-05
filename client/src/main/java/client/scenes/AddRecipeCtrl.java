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
     * gne
     */
    public void initialize(MyFXML fxml){
        this.fxml = fxml;
        this.recipe = server.add(
                new Recipe("temp", 1, new ArrayList<String>())
        );
    }

    /**
     * The function to save the recipes
     */
    @FXML
    public void onSaveRecipe() {
        try {
            List<String> preparationSteps = Arrays.asList(preparationsArea.getText().split("\\r?\\n"));
            String name = nameTextField.getText().trim();
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

            if (name.isBlank() || preparationSteps.isEmpty()) {
                showError("Input was invalid", "Please fill all fields correctly.");
                return;
            }

//            Recipe recipe = new Recipe(name, servings, preparationSteps);
            recipe.setName(name);
            recipe.setServings(servings);
            recipe.setPreparationSteps(preparationSteps);
            Recipe savedRecipe = server.add(recipe);
            appViewCtrl.loadRecipes();
            mainCtrl.showRecipe(savedRecipe);
        } catch (Exception e) {
            showError("Error", "Could not save the recipe. There might be a problem with your server connection.");
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
            System.out.println(recipe.getId());
            server.deleteRecipe(recipe.getId());
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
     * called when the user presses the + under the ingredients list
     */
    @FXML
    private void onAddRecipeIngredient(){
        Pair<RecipeIngredientCtrl, Parent> item = fxml.load(RecipeIngredientCtrl.class,
                "client", "scenes", "RecipeIngredient.fxml");
        item.getKey().initialize(null, recipe, this::showIngredients);
        ingredientsContainer.getChildren().add(item.getValue());
        item.getKey().startEditingFromCtrl();
    }


    /**
     * This function is to show the ingredients for the adding of recipes.
     */
    private void showIngredients(){
        ingredientsContainer.getChildren().clear();
        this.recipeIngredientList = server.getRecipeIngredients(recipe.getId());
        if(recipeIngredientList == null || fxml == null){
            return;
        }

        for(RecipeIngredient ri : recipeIngredientList){
            Pair<RecipeIngredientCtrl, Parent> item = fxml.load(RecipeIngredientCtrl.class,
                    "client", "scenes", "RecipeIngredient.fxml");
            item.getKey().initialize(ri, recipe, this::showIngredients);
            ingredientsContainer.getChildren().add(item.getValue());
        }
    }
}