package client.scenes;

import com.google.inject.Inject;
import client.utils.ServerUtils;
import commons.Recipe;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

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

    @FXML
    private TextField nameTextField;
    @FXML
    private TextField servingsArea;

    @FXML
    private TextArea preparationsArea;

    private AppViewCtrl appViewCtrl;

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

            Recipe recipe = new Recipe(name, servings, preparationSteps);
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
     * gne
     */
    public void onCancel(){
        mainCtrl.showAppView();
        appViewCtrl.loadRecipes();
    }
}